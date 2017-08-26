import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Eugene on 22.08.2017.
 */
public class Word {
    private String lema;
    private String normForm;
    private HashSet<String> endings;

    Word (String lema) { this.lema = lema; }
    Word (String lema, String normForm) { this.lema = lema; this.normForm = normForm; }
    Word (String lema, String normForm, HashSet<String> endings)
    { this.lema = lema; this.normForm = normForm; this.endings = endings; }

    public String getLema () { return lema; }
    public String getNormForm () { return normForm; }
    public String[] getEndings () {
        Iterator<String> iter = endings.iterator();
        String[] arrEnd = new String[endings.size()];
        for (int i = 0; iter.hasNext(); i++) {
            arrEnd[i] = iter.next();
        }
        return arrEnd;
    }
}
