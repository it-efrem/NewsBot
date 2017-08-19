import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;

public class Client {

    enum Protocol { HTTP, HTTPS }
    private Protocol protocol;
    private String url;

    public Client (Protocol protocol, String url) {
        this.protocol = protocol;
        this.url = url;
    }

    public String getXML () {
        return getBodyDocument(connectToSite());
    }

    private URLConnection connectToSite () {
        URL urlNew;
        try {
            switch (protocol) {
                case HTTPS:
                    url = "https://" + url;
                    urlNew = new URL(url);

                    HttpsURLConnection connectHttps = (HttpsURLConnection)urlNew.openConnection();

                    if (connectHttps.getResponseCode() == 200)
                        return connectHttps;
                    new ErrorLog("ResponseCode: \t" + connectHttps.getResponseCode() + "\t Url: " + url);
                break;
                case HTTP:
                    url = "http://" + url;
                    urlNew = new URL(url);

                    HttpURLConnection connectHttp = (HttpURLConnection)urlNew.openConnection();

                    if (connectHttp.getResponseCode() == 200)
                        return connectHttp;
                    new ErrorLog("ResponseCode: \t" + connectHttp.getResponseCode() + "\t Url: " + url);
                break;
            }
        } catch (MalformedURLException e) {
            new ErrorLog(e.getMessage());
        } catch (IOException e) {
            new ErrorLog(e.getMessage());
        }
        return null;
    }

    private String searchCharset (String charsetRegex, String whereToLook, int posGroup) {
        Matcher conMatch = Pattern.compile(charsetRegex).matcher(whereToLook);
        if (conMatch.find()) {
            String charset = conMatch.group(posGroup);
            if (charset.length() > 3)
                return charset;
        }
        return null;
    }

    private String getCharset (String contentType, ByteArrayOutputStream byteArray) {
        String charset = null;

        //Search for encoding in headers
        charset = searchCharset ("(?i)(charset=)(.{3,16})", contentType.replaceAll("(\")", ""), 2);

        if (charset == null) {
            //Search for encoding in the body of the document
            byte[] lineBuf = new byte[1024];
            byte[] byteArrayNew = byteArray.toByteArray();
            if (byteArrayNew.length > lineBuf.length) {
                System.arraycopy(byteArrayNew, 0, lineBuf, 0, lineBuf.length);
                charset = searchCharset ("(?i)(encoding=\")(.{3,16})(\")", new String(lineBuf), 2);
            } else {
                new ErrorLog("The content of the document is too small. \t URL: \t" + url);
            }
        }

        return charset;
    }

    private String getBodyDocument(URLConnection con){
        if(con != null){
            try {
                byte[] byteLineBuf = new byte[16384];
                int countByteInLine = 0;
                InputStream stream = con.getInputStream();
                ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

                while ((countByteInLine = stream.read(byteLineBuf)) > 0) {
                    byteArray.write(byteLineBuf, 0, countByteInLine);
                }

                String charset = getCharset (con.getHeaderField("Content-Type"), byteArray);
                if (charset != null)
                    return new String(byteArray.toByteArray(), charset);
                else
                    new ErrorLog("Undefined encoding \t Url: \t" + url);
            } catch (IOException e) {
                new ErrorLog(e.getMessage());
            }
        }
        new ErrorLog("Connection NULL: \t" + url);
        return null;
    }
}