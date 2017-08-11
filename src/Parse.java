import java.io.IOException;
import java.io.StringReader;
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
    private List<String> links;

    public static void main (String[] args){
        try{
            parseSite("");
        } catch (DOMException | XPathExpressionException | IOException | ParserConfigurationException | SAXException ex) {
        ex.printStackTrace(System.out);
    }
    }

    private static void parseSite (String url)
            throws DOMException, XPathExpressionException, IOException, ParserConfigurationException, SAXException {

        //Подключение к сайту
        //Client connect = new Client();
        //String strRSS = connect.getString(Client.Protocol.HTTPS, "nkj.ru/rss");
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document docRSS = documentBuilder.parse("file.xml");

        //Выделение определенной новости
        NodeList newsList = getNodeList(docRSS, "//item");

        //Обход всех новостей
        for (int iNL = 0; iNL < newsList.getLength(); iNL++) {
            //Сбор нужной информации по новости
            News news = getNewsInfo(newsList.item(iNL), docRSS);
            //Добавление в очередь новостей
            System.out.println("title: " + news.getCaption() + " - desc: " + news.getText());
        }
    }

    private static NodeList getNodeList (Node node, String expression)
            throws XPathExpressionException {
        XPathFactory pathFactory = XPathFactory.newInstance();
        XPath xpath = pathFactory.newXPath();
        XPathExpression expr = xpath.compile(expression);
        return (NodeList) expr.evaluate(node, XPathConstants.NODESET);
    }

    private static News getNewsInfo (Node nodeOfNews, Document doc)
            throws XPathExpressionException {
        News news = new  News (
                getNodeList(nodeOfNews, "title").item(0).getTextContent(),
                getNodeList(nodeOfNews, "description").item(0).getTextContent(),
                getNodeList(nodeOfNews, "link").item(0).getTextContent(),
                //Integer.parseInt(getNodeList(doc, "//pubDate").item(0).getTextContent())
                0
        );
        return news;
    }
}
