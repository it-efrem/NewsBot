import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Eugene on 22.07.2017.
 */
public class News {
    private String title, description, url, date;
    private HashSet<String> tagsList = new HashSet<String>();
    private HashSet<String> imageLinks = new HashSet<String>();
    private int subId;

    News (String title, String description, String url, String date, HashSet<String> imageLinks, HashSet<String> tagsList) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.date = date;
        this.imageLinks = imageLinks;
        this.tagsList = tagsList;
    }

    public String getTitle () { return deleteHtmlTags(this.title); }
    public String getDescription () { return deleteHtmlTags(this.description); }
    public String getUrl () { return deleteHtmlTags(this.url); }
    public String getDate () { return deleteHtmlTags(this.date); }

    public String getTags () {
        return  deleteHtmlTags(setToString(tagsList));
    }
    public String getImages () {
        return  deleteHtmlTags(setToString(imageLinks));
    }

    private String setToString (HashSet hs) {
        Iterator<String> iter = hs.iterator();
        String tmpStr = "";
        while (iter.hasNext()){
            tmpStr += "[" + iter.next() + "]";
        }
        return  tmpStr;
    }
    private String deleteHtmlTags (String text) {
        return text.replaceAll("(\\<(/?[^>]+)>)","");
    }
}
