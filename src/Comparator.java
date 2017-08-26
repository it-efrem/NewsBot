import jdk.nashorn.internal.runtime.regexp.joni.Regex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Eugene on 19.08.2017.
 */
public class Comparator {
    private final static String pathToDictAdverbs = "Dict/DictAdverbs";
    private final static String pathToDictServiceVocabulary = "Dict/DictServiceVocabulary";
    private static String dictAdverbs = "";
    private static String serviceVocabulary = "";

    private static String loadRegexFromFile (String pathToFile) {
        String tmpString, regex = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(pathToFile));

            while ((tmpString = br.readLine()) != null)
                regex += "(" + tmpString + ")|";
            regex = regex.substring(0, regex.length()-1);
            regex = "\\b(?iu)(" + regex + ")\\b";
            br.close();
        } catch (Exception e) {
            new ErrorLog("Error access to file: \t" + pathToFile + "\t Detals: \t" + e.getMessage());
        }
        return regex;
    }

    private static void loadDict () {
        if (dictAdverbs.length() == 0) dictAdverbs = loadRegexFromFile(pathToDictAdverbs);
        if (serviceVocabulary.length() == 0) serviceVocabulary = loadRegexFromFile(pathToDictServiceVocabulary);
    }

    private static String canonization (String text) {
        loadDict();

        text = text.toLowerCase() //Почему не работает (?i) ?
            .replaceAll(dictAdverbs, "")
            .replaceAll(serviceVocabulary, "")
            .replaceAll("(?iu)(\\b([^\\s]{0,3})\\b)", "")
            .replaceAll("(?iu)[^а-яa-z\\s]", " ")
            .replaceAll("(?iu)([\\s]{2,})", " ")
            .replaceAll("(?iu)(^[ ])", "");

        //Normal form
        String[] textArr = text.split(" ");
        text = new String();
        for (String word : textArr) {
            text += MyMyStem.normalization(word) + " ";
        }
        return text;
    }

    private static boolean contains (byte[] compared, byte[] comparable) {
        if (Arrays.equals(compared, comparable))
            return true;
        return false;
    }

    private static List<byte[]> getShingles (String text, int wordsInShingle)
            throws Exception {
        MessageDigest hashCode = MessageDigest.getInstance("SHA1");
        List<byte[]> shingleList = new ArrayList<>();
        String[] arrayWords = text.split("\\s[^\\S]*");

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

        float index = comparisonShingles (compared, comparable, 2);
        //Debug
            System.out.println(index);

        if (index > 12)
            return true;
        else
            return false;
    }
}
