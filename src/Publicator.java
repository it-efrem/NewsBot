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
                //Написать реальную публикацию
                //System.out.println(tmpNews.subId + " | " + tmpNews.getTitle());
                DataBase.putLastNews(tmpNews);
            }
       }

        //FOR DEBUG
        /*HashMap<Integer, List<News>> tmp = new HashMap<>();
        for (News tmpNews : dequeWaitPublication) {
            List<News> tmpList = new ArrayList<>();
            tmpList.add(tmpNews);
            if (tmp.containsKey(tmpNews.subId))
                tmpList.addAll(tmp.get(tmpNews.subId));
            tmp.put(tmpNews.subId, tmpList);
        }*/
    }
}
