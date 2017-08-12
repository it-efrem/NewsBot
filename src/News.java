import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eugene on 22.07.2017.
 */
public class News {
    private String title, description, text, url, tags, date;
    private List<String> imageLinks = new ArrayList<String>();
    private List<String> arrayWords = new ArrayList<String>();
    private int subId;

    News (String title, String description, String url, String date) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.date = date;
    }

    void addImage (String imageLink) {
        this.imageLinks.add(imageLink);
    }

    String[] getImages () {
        String[] arrayImages = new String[this.imageLinks.size()];
        return arrayImages;
    }

    String getTitle () { return this.title; }
    String getDescription () { return this.description; }
    String getText () { return this.text; }
    String getUrl () { return this.url; }
    String getTags () { return this.tags; }
    String getDate () { return this.date; }
}
