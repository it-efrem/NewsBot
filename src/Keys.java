import java.io.*;
import java.util.ArrayList;

/**
 * Created by Eugene on 22.07.2017.
 */
public class Keys implements Serializable {
    private static ArrayList<Key> keys = new ArrayList<Key>();
    private static String pathFile = "Keys.dat";

    private static void saveKeysPr ()
            throws Exception {
        ObjectOutputStream oos1 = new ObjectOutputStream(new FileOutputStream(pathFile));
        oos1.writeObject(keys);
        oos1.close();
    };

    private static void loadKeysPr ()
            throws Exception {
        ObjectInputStream oos = new ObjectInputStream(new FileInputStream(pathFile));
        keys = (ArrayList<Key>)oos.readObject();
        oos.close();
    }

    public static void saveKeys (){
        try {
            Keys.saveKeysPr();
        } catch (Exception e) {
            System.out.println("Error in \"void saveKeys\": "+e.getMessage());
        }
    }

    public static void loadKeys (){
        try {
            Keys.loadKeysPr();
        } catch (Exception e) {
            System.out.println("Error in \"void loadKeys\": "+e.getMessage());
        }
    }

    public static void add (String key, int weight) {
        keys.add(new Key(key, weight));
    };

    public static int get (String word) {
        for (Key key:
             keys)
            if (word.contains(key.word))
                return key.weight;
        return 0;
    }
}

class Key implements Serializable {
    String word;
    int weight;

    Key (String word, int weight) {this.word = word; this.weight = weight;}
}
