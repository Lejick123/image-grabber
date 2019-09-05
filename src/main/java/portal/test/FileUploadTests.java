
package portal.test;

import org.junit.Test;
import portal.model.DataService;
import sun.misc.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static portal.model.DataService.*;
public class FileUploadTests {
    private DataService dataService = new DataService();

    @Test
    public void putErrorUrl() {
        String failUrl = "https://ichef.bbci.co.uk/news/624/cpsprodpb/11DB3/production/_102693137_281befe0-99d8-4d72-9dd8-beeafa6ce8c2333.jpg";
        String result = dataService.putUrl(failUrl);
        assertEquals(URL_UPLOAD_ERROR, result);
    }

    @Test
    public void putSuccessUrl() {
        String failUrl = "https://ichef.bbci.co.uk/news/624/cpsprodpb/11DB3/production/_102693137_281befe0-99d8-4d72-9dd8-beeafa6ce8c2.jpg";
        String result = dataService.putUrl(failUrl);
        assertEquals(URL_UPLOAD_SUCCESS, result);
    }

    @Test
    public void putErrorJSON() {
        String failUrl = "Errrrr";
        String result = dataService.putJson(failUrl);
        assertEquals(JSON_UPLOAD_ERROR, result);
    }

    @Test
    public void putSuccessJSON() {
        String successJson = readString("src/main/data/json_cat.json");
        String result = dataService.putJson(successJson);
        assertEquals(JSON_UPLOAD_SUCCESS, result);
    }

    private String readString(String filePath) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {

            // read line by line
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }

       return  sb.toString();


    }
}
