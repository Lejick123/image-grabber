
package portal.test;

import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import portal.model.DataService;
import portal.model.FileUploadResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static portal.model.DataService.*;
public class UnitTests {
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
    @Test
    public void putErrorFile() throws IOException {
        MultipartFile[] mpFiles = readWrongFiles("src/main/data");
        FileUploadResult result = dataService.putFiles(mpFiles);
        assertTrue( result.getError().startsWith(FILE_UPLOAD_ERROR));
    }

    @Test
    public void putSuccessFile() throws IOException {
        MultipartFile[] mpFiles = readFiles("src/main/data");
        FileUploadResult result = dataService.putFiles(mpFiles);
        assertTrue(result.getSuccess().startsWith(FILE_UPLOAD_SUCCESS));
    }


    public static String readString(String filePath) {
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

    public static MultipartFile[] readFiles(String filePath) throws IOException {
        File file=new File(filePath+"/file_cat_1.jpg");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file", input);
        MultipartFile[] files= {multipartFile};
        return  files;

    }
    public static MultipartFile[] readWrongFiles(String filePath) throws IOException {
        File file=new File(filePath+"/json_cat.json");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file", input);
        MultipartFile[] files= {multipartFile};
        return  files;
    }
}
