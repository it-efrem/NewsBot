import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Eugene on 19.08.2017.
 */
public class Comparator {
    private static String canonization (String text) {
        return text.toLowerCase().replaceAll(
                "(\\s)((без)|(близ)|(в)|(вместо)|(вне)|(для)|(до)|(за)|(из)|(из-за)|(из-под)|(к)|(кроме)|(между)" +
                        "|(на)|(над)|(о)|(от)|(перед)|(пo)|(под)|(при)|(про)|(ради)|(с)|(сквозь)|(среди)|(через)" +
                        "|(возле)|(вокруг)|(вдоль)|(поперек)|(со)|(около)|(у)|(по)|(после)|(накануне)|(ввиду)" +
                        "|(вследствие)|(в силу)|(благодаря)|(согласно)|(в целях)|(с целью)|(против)|(вопреки)|(и)|(да)" +
                        "|(и)|(ни-ни)|(тоже)|(также)|(а)|(но)|(да )|(но)|(зато)|(однако)|(же)|(или)|(либо)|(то-то)" +
                        "|(то ли )|(то ли)|(не то )|(не то)|(как)|(чтобы)|(что)|(будто)|(когда)|(как)|(как только)" +
                        "|(между тем как)|(лишь)|(лишь только)|(едва лишь)|(пока)|(ибо)|(потому что)|(оттого что)" +
                        "|(так как)|(из-за того что)|(благодаря тому что)|(вследствие того что)|(в связи с тем что)" +
                        "|(чтобы )|(чтоб)|(дабы)|(для того чтобы)|(с тем чтобы)|(если)|(если бы)|(ежели)|(ежели бы)" +
                        "|(коли )|(коль)|(когда)|(когда бы)|(раз)|(хотя )|(хоть)|(хотя бы)|(пусть)|(даром что)" +
                        "|(несмотря на то что)|(невзирая на то что)|(как)|(как бы)|(как будто)|(будто)|(будто бы)" +
                        "|(словно)|(словно как)|(точно)|(так что))(\\s)"
                        , " ")
                .replaceAll("\\p{Punct}|«|»", "");
    }

    private static boolean contains (byte[] compared, byte[] comparable) {
        if (Arrays.equals(compared, comparable))
            return true;
        return false;
    }

    private static List<byte[]> getShingles (String text, int wordsInShingle)
            throws Exception {
        MessageDigest hashCode = MessageDigest.getInstance("SHA-256"); //MD5, SHA-1
        List<byte[]> shingleList = new ArrayList<>();
        String[] arrayWords = text.split("\\s[^\\S]*");

        //Что если кол-во слов меньше чем длина шингла?
        /*if (wordsInShingle > arrayWords.length)
            wordsInShingle = (int) Math.ceil(arrayWords.length / (float)wordsInShingle);*/

        if (arrayWords.length - wordsInShingle < 7)
            wordsInShingle = 1;

        for (int i = 0; i < arrayWords.length - wordsInShingle + 1; i++) {
            String shingle = "";
            for (int shi = 0; shi < wordsInShingle; shi++)
                shingle += arrayWords[i+shi] + " ";
            shingleList.add(hashCode.digest(shingle.getBytes()));
        }

        return shingleList;
    }

    private static float comparisonShingles (String compared, String comparable, int wordsInShingle)
            throws Exception {
        List<byte[]> comparedList = getShingles(compared, wordsInShingle);
        List<byte[]> comparableList =  getShingles(comparable, wordsInShingle);
        int coincidences = 0;
        for (byte[] line1:
                comparedList) {
            for (byte[] line2:
                    comparableList) {
                if (contains(line1, line2))
                    coincidences++;
            }
        }
        return ((float)coincidences / Math.max(comparedList.size(), comparableList.size())) * 100;
    }

    public static boolean comparison (String compared, String comparable)
            throws Exception {
        compared = canonization(compared);
        comparable = canonization(comparable);

        float index = comparisonShingles (compared, comparable, 3);

        return false;
    }
}
