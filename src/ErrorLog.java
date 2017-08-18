import java.util.Date;

/**
 * Created by Eugene on 13.08.2017.
 */
public class ErrorLog {

    ErrorLog (String message) { System.out.println("\t" + message + "\t datetime: " + new Date()); }

    //Переопределить стандартный поток ошибок
}
