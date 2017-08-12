/**
 * Created by Eugene on 22.07.2017.
 */
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Bot {
    static List<News> listNews = new ArrayList<News>();

    public static void main (String[] args) {

        //Парсинг RSS
        getListNews(listNews);

        //Парсинг сайтов, дополнение новостей
        for (News n : listNews
             ) {
            System.out.println(listNews.size() + " = " + n.getTitle() + " - " + n.getDescription());
        }

        //Проверка на дубли

        //Постинг
    }

    public static void getListNews (List<News> ln) {
        String currentLine;
        Parse parse = new Parse();
        try {
            BufferedReader listSitesHTTPS = new BufferedReader(new FileReader("listSitesHTTPS"));
            BufferedReader listSitesHTTP = new BufferedReader(new FileReader("listSitesHTTP"));

            while ((currentLine = listSitesHTTPS.readLine()) != null)
                listNews.addAll(parse.parseSite(Client.Protocol.HTTPS, currentLine));

            while ((currentLine = listSitesHTTP.readLine()) != null)
                listNews.addAll(parse.parseSite(Client.Protocol.HTTP, currentLine));

        } catch (DOMException | XPathExpressionException | IOException | ParserConfigurationException | SAXException ex) {
            ex.printStackTrace();
        }
    }

}
