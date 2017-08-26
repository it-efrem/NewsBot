import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Eugene on 25.08.2017.
 */
public class Tests {
    public static void main (String[] args) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
            new ErrorLog(e.getMessage());
        }
    }

    private static void notDictionaryWords () {
        System.out.println("заберкать = " + MyMyStem.normalization("заберкать"));       //заберка
        System.out.println("букерки = " + MyMyStem.normalization("букерки"));           //букер
        System.out.println("ништяков = " + MyMyStem.normalization("ништяков"));         //ништяк
        System.out.println("дочернолями = " + MyMyStem.normalization("дочернолями"));   //дочерноля
        System.out.println("шмалезябры = " + MyMyStem.normalization("шмалезябры"));     //шмалезяб
        System.out.println("нагибатор = " + MyMyStem.normalization("нагибатор"));       //нагибат
    }

    private static void testSpeedOfFile ()
            throws IOException {
        List<String> wordsList = new ArrayList<>();
        List<String> normalList = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("Tests/wordsNoNormForm.txt")));
        BufferedWriter bw = new BufferedWriter(new FileWriter("Tests/resultNormalization.txt"));
        String lineTmp = new String();
        while ((lineTmp = br.readLine()) != null) {
            wordsList.add(lineTmp);
        }

        long start, end;
        start = System.currentTimeMillis();
        {
            for (String word : wordsList)
                normalList.add(MyMyStem.normalization(word));
        }
        end = System.currentTimeMillis();
        System.out.println(((end-start))+"ms");

        for (int i = 0; i < normalList.size(); i++) {
            bw.write(
                    wordsList.get(i) + "\t{"
                    + normalList.get(i) + "}\n"
            );
        }
    }
}
