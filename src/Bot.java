/**
 * Created by Eugene on 22.07.2017.
 */
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Bot {
    static List<News> listNews = new ArrayList<News>();

    public static void main (String[] args) {

        //Парсинг RSS
        getListOfNews(listNews);
        System.out.print("");
        //Проверка на дубли

        //Постинг
        /*for (int i = 0; i < listNews.size(); i ++) {
            try {
                File file = new File("News.txt");

                FileWriter fr = null;
                fr = new FileWriter(file, true);
                String data = "[" + i + "] "
                        + listNews.get(i).getTitle()
                        + " \t "
                        + listNews.get(i).getDescription()
                        + " \t "
                        + listNews.get(i).getTags()
                        //+ " \t "
                        //+ listNews.get(i).getImages()
                        + " \t "
                        + listNews.get(i).getUrl()
                        + " \t "
                        + listNews.get(i).getDate()
                        + "\n";

                        fr.write(data);
                        fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    private static boolean strIsLinkToSite (String str) {
        if (str.length() > 5 && str.matches("([\\w.-][^/_]{2,64})([.]{1})([a-z]{2,10})(/)(.*)"))
            return true;
        return false;
    }

    private static void getListOfNews (List<News> listNews) {
        String currentLine;
        Parse parse = new Parse();
        try {
            BufferedReader listSitesHTTPS = new BufferedReader(new FileReader("listSitesHTTPS"));
            BufferedReader listSitesHTTP = new BufferedReader(new FileReader("listSitesHTTP"));

            while ((currentLine = listSitesHTTP.readLine()) != null) {
                if (strIsLinkToSite(currentLine)) {
                    List<News> tmp = parse.getListOfNewsFromSite(Client.Protocol.HTTP, currentLine);
                    if (tmp != null)
                        listNews.addAll(tmp);
                }
            }

            while ((currentLine = listSitesHTTPS.readLine()) != null) {
                if (strIsLinkToSite(currentLine)) {
                    List<News> tmp = parse.getListOfNewsFromSite(Client.Protocol.HTTPS, currentLine);
                    if (tmp != null)
                        listNews.addAll(tmp);
                }
            }

        } catch (IOException e) {
            new ErrorLog(e.getMessage());
        }
    }

}