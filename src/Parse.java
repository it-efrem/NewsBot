import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Eugene on 08.08.2017.
 */
public class Parse extends Thread {
    public Parse () {
        start();
    }

    private final static Pattern isXmlPat = Pattern.compile("(?i)(<\\?xml)(.*)(<rss)(.*)(<channel)");
    private final static Pattern delHtmlTags = Pattern.compile("(<(/?[^>]+)>)");
    private final static Pattern cdataPat = Pattern.compile("(?i)(<!\\[cdata\\[)([^\\]]*)(\\]\\]>)");
    private final static Pattern itemStartPat = Pattern.compile("(?i)(<item>)|((<item)([^>]{0,32})(>))");
    private final static Pattern itemEndPat = Pattern.compile("(?i)</item>");
    private final static Pattern imgPat = Pattern.compile("(?i)((src=\")|(url=\")|(href=\"))(.[^ ]{5,256})(.)((\\.jpg)|(\\.jpeg)|(\\.png)|(\\.gif)|(\\.bmp))(\")");
    private final static Pattern linkPat = Pattern.compile("(?i)(http|https)(://)([\\w.][^/ ]{5,256})");
    private final static Pattern categoryPat = Pattern.compile("(?i)(<category)([^<!\\[\\]]{0,256})(>)([^/]{2,64})(</category>)");

    private static boolean isXML(String document) {
        Matcher match = isXmlPat.matcher(document);
        return match.find();
    }

    private static String getBodyOfTag (String tag, String str) {
        Matcher body = Pattern.compile("(?i)(<"+tag+">(.*)</"+tag+">)").matcher(str);
        if (body.find())
            return body.group(1);
        return "";
    }

    public static String deleteHtmlTags (String text) {
        Matcher mat = delHtmlTags.matcher(text);
        return mat.replaceAll("");
    }

    private static String removeAllGarbage (String str) {
        Matcher strCdataTag = cdataPat.matcher(str);
        if (strCdataTag.find())
            str = str.substring(strCdataTag.start(2), strCdataTag.end(2));

        str = deleteHtmlTags(str);
        return str;
    }

    private static List<String> getListOfItem (String bodyChannel) {
        List<String> listItem = new ArrayList<>();
        int iterator = 0;
        boolean hasNext = true;

        while (hasNext) {
            Matcher startItem = itemStartPat.matcher(bodyChannel);
            Matcher endItem = itemEndPat.matcher(bodyChannel);
            hasNext = startItem.find(iterator);
            if (!hasNext)
                break;
            endItem.find(iterator);
            iterator = endItem.end();
            listItem.add(bodyChannel.substring(startItem.end(), endItem.start()));
        }
        return listItem;
    }

    private static HashSet<String> getImageSet (String bodyChannel, String url) {
        HashSet<String> hashSetImg = new HashSet<>();
        int iterator = 0;
        boolean hasNext = true;

        while (hasNext) {
            Matcher imgTag = imgPat.matcher(bodyChannel);
            hasNext = imgTag.find(iterator);
            if (!hasNext)
                break;
            iterator = imgTag.end();
            String urlToImg = bodyChannel.substring(imgTag.start(5), imgTag.end(7));

            if (!urlToImg.contains("http")) {
                Matcher urlOnlySite = linkPat.matcher(url);
                if (urlOnlySite.find()) {
                    url = url.substring(urlOnlySite.start(), urlOnlySite.end());
                    urlToImg = url + urlToImg;
                } else {
                    urlToImg = "";
                    Bot.log.add(Log.Type.Error,"Image without address, can not extract address. \t URL: " + url);
                }
            }

            hashSetImg.add(urlToImg);
        }
        return hashSetImg;
    }

    private static HashSet<String> getTagSet (String bodyChannel, String url) {
        HashSet<String> hashSetTag = new HashSet<>();
        int iterator = 0;
        boolean hasNext = true;

        while (hasNext) {
            Matcher categoryTag = categoryPat.matcher(bodyChannel);
            hasNext = categoryTag.find(iterator);
            if (!hasNext)
                break;
            iterator = categoryTag.end(4);
            String textOfTag = bodyChannel.substring(categoryTag.start(4), categoryTag.end(4));
                if (textOfTag.toLowerCase().contains("cdata")){
                    categoryTag = cdataPat.matcher(textOfTag);
                    if (categoryTag.find()) {
                        textOfTag = textOfTag.substring(categoryTag.start(2), categoryTag.end(2));
                    } else {
                        textOfTag = "";
                        Bot.log.add(Log.Type.Error,"Tag with [CDATA], can not extract text of tag. \t URL: " + url);
                    }
                }
            hashSetTag.add(textOfTag);
        }
        return hashSetTag;
    }

    public static List<News> getListOfNewsFromSite(Client.Protocol protocol, String url) {
        try {
            List<News> listNews = new ArrayList<News>();
            String bodyXML;

            //Подключение к сайту
            Client connect = new Client(protocol, url);
            if ((bodyXML = connect.getXML()) != null)
                bodyXML = bodyXML.replaceAll("(\\r)|(\\n)", " ");
            else {
                Bot.log.add(Log.Type.Error,"File is NULL \t Url: " + url);
                return null;
            }

            //Проверка XML
            if (isXML(bodyXML)) {
                bodyXML = getBodyOfTag("channel", bodyXML);
                List<String> listOfItem = getListOfItem(bodyXML);
                String urlAndProt = protocol + "://" + url;
                for (String itemBlock : listOfItem) {
                    String descTmp = getBodyOfTag("description", itemBlock);
                    descTmp = removeAllGarbage(descTmp);
                    String contentEncoded = removeAllGarbage(getBodyOfTag("content:encoded", itemBlock));
                    String description = (descTmp.length() >= contentEncoded.length()) ? descTmp : contentEncoded;

                    News tmp = new News(
                            removeAllGarbage(getBodyOfTag("title", itemBlock)),
                            description,
                            removeAllGarbage(getBodyOfTag("link", itemBlock)),
                            removeAllGarbage(getBodyOfTag("pubdate", itemBlock)),
                            getImageSet(itemBlock, urlAndProt),
                            getTagSet(itemBlock, urlAndProt));

                    //Unique of news
                    if (!DataBase.containsInMetaData(tmp.metaData))
                        listNews.add(tmp);
                }

                return listNews;
            } else {
                Bot.log.add(Log.Type.Error,"File not XML \t Url: " + url);
                return null;
            }
        } catch (Exception e) {
            Bot.log.emergencyClosed(e, "Parse:getListOfNewsFromSite\turl:"+url);
        }
        return null;
    }

    private static boolean strIsLinkToSite (String str) {
        //Скомпилировать рег.выражение
        if (str.length() > 5 && str.matches("(?i)([\\w.-][^/_]{2,64})([.]{1})([a-z]{2,10})(/)(.*)"))
            return true;
        return false;
    }

    public void run () {
        while (Console.getStatusProgram()) {
            String currentLine = "";
            try {
                BufferedReader listSitesHTTPS = new BufferedReader(new FileReader("listSitesHTTPS"));
                BufferedReader listSitesHTTP = new BufferedReader(new FileReader("listSitesHTTP"));

                while ((currentLine = listSitesHTTP.readLine()) != null) {
                    if (strIsLinkToSite(currentLine)) {
                        List<News> tmp = getListOfNewsFromSite(Client.Protocol.HTTP, currentLine);
                        if (tmp != null)
                            DataBase.putDequeClasterization(tmp);
                    }
                }

                while ((currentLine = listSitesHTTPS.readLine()) != null) {
                    if (strIsLinkToSite(currentLine)) {
                        List<News> tmp = getListOfNewsFromSite(Client.Protocol.HTTPS, currentLine);
                        if (tmp != null)
                            DataBase.putDequeClasterization(tmp);
                    }
                }
                listSitesHTTP.close();
                listSitesHTTPS.close();
            } catch (IOException e) {
                Bot.log.emergencyClosed(e, "Parse:run\tcurrentLine:"+currentLine);
            }
        }
    }
}
