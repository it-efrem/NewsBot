import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eugene on 22.07.2017.
 */
public class News {
    private String caption, text, link;
    private List<String> imageLinks = new ArrayList<String>();
    private List<String> arrayWords = new ArrayList<String>();
    private int time, weight;

    News (String caption, String text, String link, int time) {
        this.caption = caption;
        this.text = text;
        this.link = link;
        this.time = time;
    }

    void addImage (String imageLink) {
        this.imageLinks.add(imageLink);
    }

    String[] getImages () {
        String[] arrayImages = new String[this.imageLinks.size()];
        return arrayImages;
    }

    String getCaption () { return this.caption; }
    String getText () { return this.text; }
    String getLink () { return this.link; }
    int getTime () { return this.time; }

    int getWeight () {
        //Array of words
        if (arrayWords.isEmpty()){
            String[] wordsCaption = caption.replaceAll("\\w ", "").toLowerCase().split(" ");
            String[] wordsText = text.replaceAll("\\w ", "").toLowerCase().split(" ");

            for (String word:
                    wordsCaption) {
                arrayWords.add(word);
            }

            for (String word:
                    wordsText) {
                arrayWords.add(word);
            }
        }

        //Calc weight
        for (String word:
             arrayWords) {
            weight += Keys.get(word);
        }

        return this.weight;
    }
}
