import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.net.ssl.HttpsURLConnection;

public class Client {

    enum Protocol { HTTP, HTTPS }

    public String getString (Protocol protocol, String url) {
        URL urlNew;
        URLConnection connect;
        String content = "Empty";

        try {
            switch (protocol) {
                case HTTPS:
                    url = "https://" + url;
                    urlNew = new URL(url);

                    connect = (HttpsURLConnection)urlNew.openConnection();
                    content = getBody(connect);
                break;
                case HTTP:
                    url = "http://" + url;
                    urlNew = new URL(url);

                    connect = (HttpURLConnection)urlNew.openConnection();
                    content = getBody(connect);
                break;
            }
            return content;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    private String getBody(URLConnection con){
        if(con!=null){
            try {
                BufferedReader br =
                        new BufferedReader(
                                new InputStreamReader(con.getInputStream()));
                String tempLine, outString = "";
                while ((tempLine = br.readLine()) != null){
                    outString += tempLine + "\n";
                }
                br.close();
                return outString;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "Error connection";
    }
}