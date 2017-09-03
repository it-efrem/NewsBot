import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

public class DataBase {
    private static ArrayDeque<News> lastNews = new ArrayDeque<News>();
    private static ArrayDeque<News.MetaData> listOfMetaData = new ArrayDeque<News.MetaData>();
    private static ArrayDeque<News> dequeClasterization = new ArrayDeque<News>();
    private static ArrayDeque<News> dequeWaitPublication = new ArrayDeque<News>();

    public static synchronized boolean containsInMetaData (News.MetaData data) {
        for (News.MetaData md : listOfMetaData) {
            if (Arrays.equals(md.metaUrl, data.metaUrl))
                return true;
        }
        return false;
    }
    public static synchronized void putLastNews (News news) {
        lastNews.addLast(news);
        if (lastNews.size() > 15000) {
            lastNews.removeFirst();
            listOfMetaData.removeFirst();
        }
    }
    public static synchronized void putDequeClasterization (News news) {
        dequeClasterization.addLast(news);
    }
    public static synchronized void putDequeClasterization (List<News> news) {
        dequeClasterization.addAll(news);
    }
    public static synchronized void putDequeWaitPublication (News news) {
        dequeWaitPublication.addLast(news);
    }
    public static synchronized News getLastNews () {
        return lastNews.pollFirst();
    }
    public static synchronized ArrayDeque<News> getAllLastNews () {
        return lastNews;
    }
    public static synchronized News.MetaData getListOfMetaData () {
        return listOfMetaData.pollFirst();
    }
    public static synchronized News getDequeClasterization () {
        return dequeClasterization.pollFirst();
    }
    public static synchronized News getWaitPublication () {
        return dequeWaitPublication.pollFirst();
    }
    public static synchronized ArrayDeque<News> getAllWaitPublication () {
        return dequeWaitPublication;
    }

    public static synchronized int sizeLastNews () { return lastNews.size(); }
    public static synchronized int sizeListOfMetaData () { return listOfMetaData.size(); }
    public static synchronized int sizeDequeClasterization () { return dequeClasterization.size(); }
    public static synchronized int sizeDequeWaitPublication () { return dequeWaitPublication.size(); }
}