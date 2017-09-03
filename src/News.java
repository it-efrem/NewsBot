import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.security.MessageDigest;

/**
 * Created by Eugene on 22.07.2017.
 */
public class News {
    public class MetaData {
        MetaData (byte[] metaUrl, byte[] metaTitle, byte[] metaDesc, int length) {
            this.metaUrl = metaUrl;
            this.metaTitle = metaTitle;
            this.metaDesc = metaDesc;
            this.length = length;
        }
        byte[] metaUrl, metaTitle, metaDesc;
        int length;
        public List<byte[]> shinglesList;
        public byte[] getMetaUrl() { return metaUrl; }
        public byte[] getMetaTitle() { return metaTitle; }
        public byte[] getMetaDesc() { return metaDesc; }
        public int getLength() { return length; }
    }
    public MetaData metaData;
    public int subId;
    private String title, description, url, date, canText;
    private HashSet<String> tagsList = new HashSet<String>();
    private HashSet<String> imageLinks = new HashSet<String>();

    News (String title, String description, String url, String date, HashSet<String> imageLinks, HashSet<String> tagsList)
            throws NoSuchAlgorithmException {
        this.title = title;
        this.description = description;
        this.url = url;
        this.date = date;
        this.imageLinks = imageLinks;
        this.tagsList = tagsList;
        metaData = generateMetaData();
    }

    public String getTitle () { return title; }
    public String getDescription () { return description; }
    public String getUrl () { return url; }
    public String getDate () { return date; }
    public String getCanText () { return canText; }
    public String getTags () {
        return  hashSetToString(tagsList);
    }
    public String getImages () {
        return  hashSetToString(imageLinks);
    }

    public void setCanText (String str) {
        canText = str;
    }

    private String hashSetToString (HashSet hs) {
        Iterator<String> iter = hs.iterator();
        String tmpStr = "";
        while (iter.hasNext()){
            tmpStr += "[" + iter.next() + "]";
        }
        return  tmpStr;
    }
    private MetaData generateMetaData ()
            throws NoSuchAlgorithmException {
        MessageDigest hashCode = MessageDigest.getInstance("SHA1");
        return new MetaData (
                hashCode.digest(url.getBytes()),
                hashCode.digest(title.getBytes()),
                hashCode.digest(description.getBytes()),
                description.length()
        );
    }
}