import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Eugene on 02.09.2017.
 */
public class Console extends Thread {
    private static Parse parse;
    private static Comparator comparator;
    private static Publicator publicator;
    private static Scanner comandScaner;
    private static enum  Comands { help, start, stop, restart, status, lastcomand, lastevent, exit }
    private static ArrayDeque<String> eventsList = new ArrayDeque<>();
    private static ArrayDeque<Comands> comandList = new ArrayDeque<>();
    private static long timeStart;
    private static boolean activeProgram, activeThreads;

    public Console () {
        activeProgram = true;
        timeStart = System.currentTimeMillis();
        comandScaner = new Scanner(System.in);
        start();
        progStart();
    }

    public void run () {
        try {
            while (activeProgram) {
                controller();
            }
        } catch (Exception e) {
            Bot.log.emergencyClosed(e,"Console:run");
        }
    }
    public static boolean getStatusProgram () { return activeThreads; }
    public static String currentDate () {
        Long time = System.currentTimeMillis();
        DateFormat format = new SimpleDateFormat("d-M-Y HH:mm:ss") ;
        format.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        return format.format(new Date(time));
    }
    private static void controller () throws IOException {
        String[] arrComand = comandScaner.nextLine().split("\\s");
        for (String comand : arrComand) {
            switch (comand) {
                case "help" :
                    printHelp();
                    comandList.addFirst(Comands.help);
                    break;
                case "start":
                    progStart();
                    comandList.addFirst(Comands.start);
                    break;
                case "stop":
                    progStop();
                    comandList.addFirst(Comands.stop);
                    break;
                case "restart":
                    progRestart();
                    comandList.addFirst(Comands.restart);
                    break;
                case "status":
                    printStatus();
                    comandList.addFirst(Comands.status);
                    break;
                case "lastcomand":
                    printLastCommand();
                    comandList.addFirst(Comands.lastcomand);
                    break;
                case "lastevent":
                    printLastEvent();
                    comandList.addFirst(Comands.lastevent);
                    break;
                case "exit":
                    exit();
                    comandList.addFirst(Comands.exit);
                    break;
                case "": break;
                default:
                    System.out.println(comand + " - UNDEFINED COMMAND! Use help");
            }
        }
    }
    private static void printStatus () {
        String active = activeThreads ? "Active" : "Stopped";
        System.out.println("\r" + active + "\t" + timeWork() + "\t" + basicInformation());
    }
    private static void printHelp () {
        System.out.println("Command list: " + Arrays.toString(Comands.values()));
    }
    private static String basicInformation () {
        return "MetaData: " + DataBase.sizeListOfMetaData() + "\t"
                + "DequeClasterization: " + DataBase.sizeDequeClasterization() + "\t"
                + "DequeWaitPublication: " + DataBase.sizeDequeWaitPublication() + "\t"
                + "LastNews: " + DataBase.sizeLastNews();
    }
    private static String timeWork () {
        long timeEnd = System.currentTimeMillis();
        Long time = (timeEnd - timeStart) / 1000;
        short day = (short)(time / 86400);
        short hour = (short)((time-day*86400) / 3600);
        short min = (short)((time-hour*3600) / 60);
        short sec = (short)(time-min*60);

        return day+"day "+hour+":"+min+":"+sec;
    }
    private static void printLastCommand () {
        if (!comandList.isEmpty()) {
            for (Comands comand : comandList)
                System.out.println("\t" + comand);
        } else
            System.out.println("\t Comand list is empty");
    }
    private static void printLastEvent () {
        if (!eventsList.isEmpty()) {
            for (String event : eventsList)
                System.out.println("\t" + event);
        } else
            System.out.println("\t Event list is empty");
    }

    private static void progStart () {
        System.out.println("\rStarting program...");
        eventsList.addFirst("Start" + " - " + currentDate());
        activeThreads = true;
        parse = new Parse();
        comparator = new Comparator(2);
        publicator = new Publicator();
    }
    private static void progStop () throws IOException {
        Log.writeErrorWordsNotInDictionary();
        System.out.println("\rStop program...");
        eventsList.addFirst("Stop" + " - " + currentDate());
        activeThreads = false;
        parse = null;
        comparator = null;
        publicator = null;
    }
    private static void progRestart () throws IOException {
        System.out.println("\rRestarting program...");
        eventsList.addFirst("Restart" + " - " + currentDate());
        progStop ();
        progStart ();
    }
    private static void exit () throws IOException {
        boolean selection = false;
        System.out.println("Do you really want to end the program? [Y/N]");
        Scanner tmpScaner = new Scanner(System.in);
        while (!selection) {
            switch (tmpScaner.nextLine()) {
                case "Y":
                    progStop();
                    selection = true;
                    activeProgram = false;
                    break;
                case "N":
                    selection = true;
                    break;
                default:
                    System.out.println("[Y/N]?");
            }
        }
    }
    public static void emergencyExit () {
        activeProgram = false;
        activeThreads = false;
        parse = null;
        comparator = null;
        publicator = null;
    }
}
