import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Eugene on 08.08.2017.
 */
public class Parse {
    public static void main (String[] args) {}

    private boolean isXML(String document) {
        Matcher match = Pattern.compile(
                "(<\\?xml)(.*)" +
                "(<rss)(.*)" +
                "(<channel)")
                .matcher(document);

        return match.find();
    }

    private String getBodyOfTag (String tag, String str) {
        Matcher body = Pattern.compile("<"+tag+">(.*)</"+tag+">").matcher(str);
        if (body.find())
            return body.group(1);
        return "";
    }

    private List<String> getListOfItem (String bodyChannel) {
        List<String> listItem = new ArrayList<>();
        int iterator = 0;
        boolean hasNext = true;

        while (hasNext) {
            Matcher startItem = Pattern.compile("(<item>)|((<item)(.{0,32})(>))").matcher(bodyChannel);
            Matcher endItem = Pattern.compile("</item>").matcher(bodyChannel);
            hasNext = startItem.find(iterator);
            if (!hasNext)
                break;
            endItem.find(iterator);
            iterator = endItem.end();
            listItem.add(bodyChannel.substring(startItem.end(), endItem.start()));
        }
        return listItem;
    }

    private HashSet<String> getImageSet (String bodyChannel, String url) {
        HashSet<String> hashSetImg = new HashSet<>();
        int iterator = 0;
        boolean hasNext = true;

        while (hasNext) {
            Matcher imgTag = Pattern.compile("((src=\")|(url=\")|(href=\"))(.[^ ]{5,256})(.)((\\.jpg)|(\\.jpeg)|(\\.png)|(\\.gif)|(\\.bmp))(\")").matcher(bodyChannel);
            hasNext = imgTag.find(iterator);
            if (!hasNext)
                break;
            iterator = imgTag.end();
            String urlToImg = bodyChannel.substring(imgTag.start(5), imgTag.end(7));

            if (!urlToImg.contains("http")) {
                Matcher urlOnlySite = Pattern.compile("(http|https)(://)([\\w.][^/ ]{5,256})").matcher(url.toLowerCase());
                if (urlOnlySite.find()) {
                    url = url.substring(urlOnlySite.start(), urlOnlySite.end());
                    urlToImg = url + urlToImg;
                } else {
                    urlToImg = "";
                    new ErrorLog("Image without address, can not extract address. \t URL: " + url);
                }
            }

            hashSetImg.add(urlToImg);
        }
        return hashSetImg;
    }

    private HashSet<String> getTagSet (String bodyChannel, String url) {
        HashSet<String> hashSetTag = new HashSet<>();
        int iterator = 0;
        boolean hasNext = true;

        while (hasNext) {
            Matcher categoryTag = Pattern.compile("(<category)([^<!\\[\\]]{0,256})(>)([^/]{2,64})(</category>)").matcher(bodyChannel);
            hasNext = categoryTag.find(iterator);
            if (!hasNext)
                break;
            iterator = categoryTag.end(4);
            String textOfTag = bodyChannel.substring(categoryTag.start(4), categoryTag.end(4));
                if (textOfTag.contains("cdata")){
                    categoryTag = Pattern.compile("(<!\\[cdata\\[)([^\\]]{2,64})(\\]\\]>)").matcher(textOfTag);
                    if (categoryTag.find()) {
                        textOfTag = textOfTag.substring(categoryTag.start(2), categoryTag.end(2));
                    } else {
                        textOfTag = "";
                        new ErrorLog("Image with [CDATA], can not extract text of tag. \t URL: " + url);
                    }
                }
            hashSetTag.add(textOfTag);
        }
        return hashSetTag;
    }

    public List<News> getListOfNewsFromSite(Client.Protocol protocol, String url) {
        List<News> listNews = new ArrayList<News>();
        String bodyXML;

        //Подключение к сайту
        Client connect = new Client(protocol, url);
        if ((bodyXML = connect.getXML()) != null)
            bodyXML = bodyXML.replaceAll("(\\r)|(\\n)", "").toLowerCase();
        else {
            new ErrorLog("File is NULL \t Url: " + url);
            return null;
        }

        //Проверка XML
        if (isXML(bodyXML)) {
            bodyXML = getBodyOfTag("channel", bodyXML);
            List<String> listOfItem = getListOfItem(bodyXML);
            String urlAndProt = protocol+"://"+url;
            for (String itemBlock:
                 listOfItem) {
                listNews.add(
                        new News(
                                getBodyOfTag("title", itemBlock),
                                getBodyOfTag("description", itemBlock),
                                getBodyOfTag("link", itemBlock),
                                getBodyOfTag("pubdate", itemBlock),
                                getImageSet(itemBlock, urlAndProt),
                                getTagSet(itemBlock, urlAndProt)
                        )
                );
            }

            return listNews;
        } else {
            new ErrorLog("File not XML \t Url: " + url);
            return null;
        }
    }
}
