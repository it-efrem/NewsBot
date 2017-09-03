/**
 * Created by Eugene on 22.07.2017.
 */

public class Bot {
    public static Log log = new Log();
    public static void main (String[] args) {
        try {
            Console console = new Console();
        } catch (Exception e) {
            log.emergencyClosed(e, "Bot:main");
        }
    }
}