import java.io.BufferedReader;
import java.io.FileReader;
import java.security.SecureRandom;
import java.util.*;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Eugene on 19.08.2017.
 */
public class Comparator extends  Thread {
    public Comparator (int wordsInShingleLink) {
        wordsInShingle = wordsInShingleLink;
        start();
    }

    private static int wordsInShingle;
    private final static String pathToDictAdverbs = "Dict/DictAdverbs";
    private final static String pathToDictServiceVocabulary = "Dict/DictServiceVocabulary";
    private final static String pathToDictSyn = "Dict/SynDepth2-TABLE.txt";
    private static Pattern delGarbPatt = null, delSpacePatt = null, delWordsPatt = null;
    private static HashMap<String, String> dictSyn = new HashMap<>();

    private static void loadSynFromFile (String pathToFile) {
        String tmpString = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(pathToFile));

            while ((tmpString = br.readLine()) != null) {
                String[] arrTmp = tmpString.split("=");
                if (arrTmp.length == 2) {
                    String word = arrTmp[0];
                    String numGroup = arrTmp[1];
                    dictSyn.put(word, numGroup);
                } else {
                    Bot.log.add(Log.Type.Error,"Comparator:loadSynFromFile \tpathToFile:\t" + pathToFile + "\ttmpString:" + tmpString);
                }
            }
            br.close();
        } catch (Exception e) {
            Bot.log.emergencyClosed(e, "Comparator:loadSynFromFile \tpathToFile:"+pathToFile+"\ttmpString:"+tmpString);
        }
    }

    private static String loadRegexFromFile (String pathToFile) {
        String tmpString = "", regex = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(pathToFile));

            while ((tmpString = br.readLine()) != null)
                regex += "(" + tmpString + ")|";
            regex = regex.substring(0, regex.length()-1);
            br.close();
        } catch (Exception e) {
            Bot.log.emergencyClosed(e, "Comparator:loadRegexFromFile \t pathToFile:"+pathToFile+"\tregex:"+regex+"\ttmpString:"+tmpString);
        }
        return regex;
    }

    private static void loadDict () {
        if ((delGarbPatt == null) | (delSpacePatt == null) | (delWordsPatt == null)) {
            String wordsRemovePatt = "\\b(?iu)(" +
                    loadRegexFromFile(pathToDictAdverbs)
                    + "|" + loadRegexFromFile(pathToDictServiceVocabulary)
                    + ")\\b";
            delWordsPatt = Pattern.compile(wordsRemovePatt);
            delGarbPatt = Pattern.compile("(?iu)([\\s]{2,})|[^а-яa-z\\s]|(\\b([^\\s]{0,3})\\b)");
            delSpacePatt = Pattern.compile("(?iu)((^[\\s]*)|([\\s]*)$)");
        }
        if (dictSyn.size() == 0) loadSynFromFile(pathToDictSyn);
    }

    private static String wordToSynGroup (String word) {
        if (dictSyn.containsKey(word))
            return dictSyn.get(word).toString();
        else {
            Bot.log.add(Log.Type.AbsentWords, "Syn:\t" + word);
            return word;
        }
    }

    private static String normalization (String text) {
        String[] textArr = text.split(" ");
        text = new String();
        for (String word : textArr) {
            text += MyMyStem.normalization(word);
        }
        return text;
    }

    private static String canonization (String text) {
        loadDict();

        Matcher delWordsMat = delWordsPatt.matcher(text);
        text = delWordsMat.replaceAll("");

        Matcher delGarbMat = delGarbPatt.matcher(text);
        text = delGarbMat.replaceAll("");

        Matcher delSpaceMat = delSpacePatt.matcher(text);
        text = delSpaceMat.replaceAll("");

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
        List<byte[]> hashList = new ArrayList<>();
        String[] arrayWords = text.split("\\s[^\\S]*");

        if (arrayWords.length - wordsInShingle < 7)
            wordsInShingle = 1;

        for (int i = 0; i < arrayWords.length - wordsInShingle + 1; i++) {
            String shingle = "";
            for (int shi = 0; shi < wordsInShingle; shi++)
                shingle += wordToSynGroup(normalization(arrayWords[i + shi]));
            hashList.add(hashCode.digest(shingle.getBytes()));
        }

        return hashList;
    }

    private static float algorithmShingles (List<byte[]> comparedList, List<byte[]> comparableList)
            throws Exception {
        int coincidences = 0;
        for (byte[] line1 : comparedList) {
            for (byte[] line2 : comparableList)
                if (contains(line1, line2))
                    coincidences++;
        }
        float difference = (float)Math.min(comparedList.size(), comparableList.size()) / (float)Math.max(comparedList.size(), comparableList.size());
        return (((float)coincidences / Math.min(comparedList.size(), comparableList.size())) * 100) * difference;
    }

    private static void existShinglesList (News news)
            throws Exception {
        if (news.metaData.shinglesList == null) {
            news.setCanText(canonization(news.getTitle() + " " + news.getDescription()));
            news.metaData.shinglesList = getShingles(news.getCanText(), wordsInShingle);
        }
    }

    private static boolean comparison (News compared, News comparable)
            throws Exception {

        existShinglesList(compared);
        existShinglesList(comparable);

        float index = algorithmShingles (compared.metaData.shinglesList, comparable.metaData.shinglesList);

        if (index > 20)
            return true;
        else
            return false;
    }

    public void run () {
        while (Console.getStatusProgram()) {
            try {
                News news = DataBase.getDequeClasterization();
                ArrayDeque<News> allLastNews = DataBase.getAllLastNews();
                ArrayDeque<News> allWaitPublication = DataBase.getAllWaitPublication();
                if (news != null) {
                    int subId = 0;
                    //Проверить наличие такой темы в уже опубликованных новостях
                    if (allLastNews != null) {
                        for (News newsLast : allLastNews) {
                            if (comparison(news, newsLast)) {
                                subId = newsLast.subId;
                                news.subId = subId;
                                DataBase.putDequeWaitPublication(news);
                                break;
                            }
                        }
                    }
                    //Проверить в новостях ожидающих публикации
                    if (allWaitPublication != null) {
                        for (News newsWaitP : allWaitPublication) {
                            if (comparison(news, newsWaitP)) {
                                subId = newsWaitP.subId;
                                news.subId = subId;
                                DataBase.putDequeWaitPublication(news);
                                break;
                            }
                        }
                    }
                    //Если тема новости новая
                    if (subId == 0) {
                        SecureRandom sr = new SecureRandom();
                        subId = sr.nextInt(1073741824);
                        news.subId = subId;
                        DataBase.putDequeWaitPublication(news);
                    }
                }
            } catch (Exception e) {
                Bot.log.emergencyClosed(e, "Comparator:run");
            }
        }
    }
}
