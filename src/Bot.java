/**
 * Created by Eugene on 22.07.2017.
 */
import java.io.*;
import java.util.ArrayList;

public class Bot {
    public static void main (String[] args) {
        //Подгрузка ключей должна происходить каждый раз при обработке новой новости? Что бы можно было добвлять новые ключи
        News news = new News("Погода в москве без изменений", "Билеты в кремль дорожают интеРнЕтов.нет", "", 10);
        Keys.loadKeys();

        System.out.println(news.getWeight());
    }
}
