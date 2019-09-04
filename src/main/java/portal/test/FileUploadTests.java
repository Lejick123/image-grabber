
package portal.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import portal.AppConfig;
import portal.model.DataService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {AppConfig.class})
public class FileUploadTests {
    @Autowired
    private DataService dataService = new DataService();

    @Test
    public void putErrorUrl() throws Exception {
        String failUrl = "https://ichef.bbci.co.uk/news/624/cpsprodpb/11DB3/production/_102693137_281befe0-99d8-4d72-9dd8-beeafa6ce8c2333.jpg";
        String result = dataService.putUrl(failUrl);
        assertEquals("Error while uploaded url",result);
    }

    @Test
    public void putSuccessUrl() throws Exception {
        String failUrl = "https://ichef.bbci.co.uk/news/624/cpsprodpb/11DB3/production/_102693137_281befe0-99d8-4d72-9dd8-beeafa6ce8c2.jpg";
        String result = dataService.putUrl(failUrl);
        assertEquals("You successfully uploaded url",result);
    }

}
