import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.print.Doc;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.xpath.*;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Created by Eugene on 08.08.2017.
 */
public class Parse {
    public static void main (String[] args) {}

    private NodeList getNodeList (Node node, String expression)
            throws XPathExpressionException {
        XPathFactory pathFactory = XPathFactory.newInstance();
        XPath xpath = pathFactory.newXPath();
        XPathExpression expr = xpath.compile(expression);
        return (NodeList) expr.evaluate(node, XPathConstants.NODESET);
    }

    private News getNewsInfo (Node nodeOfNews)
            throws XPathExpressionException {
        News news = new  News (
                getNodeList(nodeOfNews, "title").item(0).getTextContent(),
                getNodeList(nodeOfNews, "description").item(0).getTextContent(),
                getNodeList(nodeOfNews, "link").item(0).getTextContent(),
                getNodeList(nodeOfNews, "pubDate").item(0).getTextContent()
        );
        return news;
    }

    public List<News> parseSite (Client.Protocol protocol, String url)
            throws DOMException, XPathExpressionException, IOException, ParserConfigurationException, SAXException {
        List<News> listNews = new ArrayList<News>();
        //Подключение к сайту
        Client connect = new Client();
        String strRSS = connect.getString(protocol, url);
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document docRSS = documentBuilder.parse(new InputSource(new ByteArrayInputStream(strRSS.getBytes("utf-8"))));

        //Собрать все контейнеры с новостями
        NodeList newsList = getNodeList(docRSS, "//item");

        //Добавить все новости в список
        for (int iNL = 0; iNL < newsList.getLength(); iNL++)
            listNews.add(getNewsInfo(newsList.item(iNL)));

        return listNews;
    }
}
