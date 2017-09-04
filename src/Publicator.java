/**
 * Created by Eugene on 02.09.2017.
 */
public class Publicator extends Thread {

    public Publicator () {
        start();
    }

    public void run () {
        while (Console.getStatusProgram()) {
            News tmpNews =  DataBase.getWaitPublication();
            if (tmpNews != null) {
                /*
                Secret Section
                */
                DataBase.putLastNews(tmpNews);
            }
       }
    }
}
