import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Eugene on 21.08.2017.
 */
public class MyMyStem {
    private final static String pathToDictGrammar = "Dict/DictGrammarInvert";
    private static HashSet<String> dictEnding = new HashSet<>();
    private static HashMap<String, Word> dictGrammar = new HashMap<>();

    private static String toInvertForm (String str) {
        StringBuilder tmpArr = new StringBuilder();
        for (int i = str.length(); i > 0; i--) {
            tmpArr.append(str.charAt(i-1));
        }
        return new String(tmpArr);
    }

    private static void loadDictGrammar ()
            throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pathToDictGrammar)));
        String tmpLine = new String();
        while ((tmpLine = br.readLine()) != null) {
            Matcher lineMath = Pattern.compile("([^\\]]*)(\\[)([^\\]]*)(\\])(.*)").matcher(tmpLine);
            if (lineMath.find()) {
                String lema = tmpLine.substring(0, lineMath.end(1));
                String normalForm = tmpLine.substring(lineMath.start(3), lineMath.end(3));
                String endsStr = tmpLine.substring(lineMath.start(5));
                String[] endsArr = endsStr.split(",");

                HashSet<String> tmpTreeEnds = new HashSet<>();
                for (String str : endsArr)
                    tmpTreeEnds.add(str);

                Word word = new Word(lema, normalForm, tmpTreeEnds);
                dictEnding.addAll(tmpTreeEnds);
                dictGrammar.put(lema, word);
            }
        }
    }

    private static void loadDict () {
        try {
            if (dictGrammar.size() == 0) loadDictGrammar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean containsEndInWord (String[] ending, String endOfWord) {
        for (String str : ending) {
            if (str.equals(endOfWord))
                return true;
        }
        return false;
    }

    public static String normalization (String noNormWord) {
        loadDict();
        String invNoNormWord = toInvertForm(noNormWord);
        String normalForm = new String();
        boolean wordInDict = true;
        for (int countChar = 0; countChar <= invNoNormWord.length(); countChar++) {
            String endsTmp = invNoNormWord.substring(0, countChar);
            if (!wordInDict || dictEnding.contains(endsTmp)) {
                String lemaTmp = invNoNormWord.substring(countChar);
                if (dictGrammar.containsKey(lemaTmp)) {
                    Word word = dictGrammar.get(lemaTmp);
                    if (!wordInDict || containsEndInWord(word.getEndings(), endsTmp)) {
                        normalForm = dictGrammar.get(lemaTmp).getNormForm() + dictGrammar.get(lemaTmp).getLema();
                        return toInvertForm(normalForm);
                    }
                }
                if ((!wordInDict && dictEnding.contains(endsTmp))) {
                    normalForm = invNoNormWord.substring(countChar);
                    return toInvertForm(normalForm);
                }
            }
            if (countChar == invNoNormWord.length() && wordInDict) {
                wordInDict = false;
                countChar = 1;
            }
        }
        return noNormWord;
    }
}