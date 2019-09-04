
package portal.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.net.URL;

import portal.model.DataService;

import static org.testng.AssertJUnit.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileUploadTests {
    @Autowired
    private DataService dataService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void putErrorUrl() {
        String failUrl = "https://ichef.bbci.co.uk/news/624/cpsprodpb/11DB3/production/_102693137_281befe0-99d8-4d72-9dd8-beeafa6ce8c2333.jpg";
        String result = dataService.putUrl(failUrl);
        assertEquals("Error while uploaded url",result);
    }

    @Test
    public void putSuccessUrl() {
        String failUrl = "https://ichef.bbci.co.uk/news/624/cpsprodpb/11DB3/production/_102693137_281befe0-99d8-4d72-9dd8-beeafa6ce8c2.jpg";
        String result = dataService.putUrl(failUrl);
        assertEquals("You successfully uploaded url",result);
    }

    @Test
    public void getHello() throws Exception {

        ResponseEntity<String> response = restTemplate.getForEntity(
                new URL("http://localhost:" + port + "/").toString(), String.class);
        assertEquals("Hello Controller", response.getBody());

    }

}
