/**
 * Created by Eugene on 22.07.2017.
 */
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Bot {
    static List<News> listNews = new ArrayList<News>();

    public static void main (String[] args) {
        try {
            getListOfNews(listNews);

            for (int i = 0; i < listNews.size(); i++){
                for (int j = 0; j < listNews.size(); j++){
                    if (i != j) {
                        long timerStart = System.nanoTime();
                        Comparator.comparison(
                                listNews.get(i).getTitle(),
                                listNews.get(j).getTitle());
                        long timerEnd = System.nanoTime();
                        System.out.println("Time: " + (timerEnd - timerStart) / 1000000000 + " sec");
                    }
                }
            }
        } catch (Exception e) {
            new ErrorLog(e.getMessage());
        }

        //Постинг
    }

    private static boolean strIsLinkToSite (String str) {
        if (str.length() > 5 && str.matches("(?i)([\\w.-][^/_]{2,64})([.]{1})([a-z]{2,10})(/)(.*)"))
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