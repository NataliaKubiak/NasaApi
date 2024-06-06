import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static final String TOKEN = "THYIKivMEbEspBe6d9cEzbKEh1V6RNhc8fhLhHTm";
    public static final String URL = "https://api.nasa.gov/planetary/apod?api_key=" + TOKEN;
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setUserAgent("Natalie's Test Program")
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet infoRequest = new HttpGet(URL);

        CloseableHttpResponse infoResponse = httpClient.execute(infoRequest);

        Post post = mapper.readValue(infoResponse.getEntity().getContent(), new TypeReference<Post>() {});
        String[] parts = post.getHdurl().split("/");
        String pictureFileName = parts[parts.length - 1];

        HttpGet pictureRequest = new HttpGet(post.getHdurl());
        CloseableHttpResponse pictureResponse = httpClient.execute(pictureRequest);
        byte[] pictureBytes = pictureResponse.getEntity().getContent().readAllBytes();

        try (FileOutputStream fos = new FileOutputStream(pictureFileName)) {
            fos.write(pictureBytes);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //записала в файл explanation в качестве повторения
        String explanationFileName = post.getTitle();
        String explanation = post.getExplanation();
        try (FileWriter writer = new FileWriter(explanationFileName + ".txt", false)) {
            writer.write(explanation);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
