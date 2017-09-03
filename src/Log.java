import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eugene on 13.08.2017.
 */
public class Log {

    enum Type { AbsentWords, Program, Error }
    private final static String pathToLogsWordsFile = "Logs/WordsNotInDictionary.txt";
    private final static String pathToLogsProgram = "Logs/Program.txt";
    private final static String pathToLogsErrors = "Logs/Errors.txt";
    private static Writer bwerror, bwprog;
    private static HashMap<String, String> ErrorWordsNotInDictionary = new HashMap<>();
    public static PrintWriter bwErrorTrace;

    public Log () {
        try {
            bwerror = new FileWriter(pathToLogsErrors, true);
            bwErrorTrace = new PrintWriter(bwerror, true);
            bwprog = new BufferedWriter(new FileWriter(pathToLogsProgram, true));
        } catch (Exception e) {
            e.printStackTrace(bwErrorTrace);
        }
    }

    public static void add (Type type, String message) {
        try {
            switch (type) {
                case AbsentWords:
                    ErrorWordsNotInDictionary.put(message, Console.currentDate());
                    break;
                case Error:
                    bwerror.write("\r\n["+Console.currentDate()+"]\t" + message + "\r\n");
                    break;
                case Program:
                    bwprog.write("\r\n["+Console.currentDate()+"]\t" + message + "\r\n");
                    break;
            }
        } catch (Exception e) {
            Console.emergencyExit();
            e.printStackTrace(bwErrorTrace);
        }
    }
    public static void emergencyClosed (Exception e, String message) {
        Console.emergencyExit();
        add(Log.Type.Error,message+"\t" + e.getMessage());
        e.printStackTrace(Log.bwErrorTrace);
    }
    public static void writeErrorWordsNotInDictionary ()
            throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(pathToLogsWordsFile, true));
        for (Map.Entry word : ErrorWordsNotInDictionary.entrySet()) {
            bw.write(word.getKey() + "\t|\t" + word.getValue() + "\r\n");
        }
        bw.close();
    }
}
