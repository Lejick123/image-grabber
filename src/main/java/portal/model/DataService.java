package portal.model;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import portal.Util;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataService {
    private Map<String, ImageModelImpl> imagesMap = new HashMap<>();
    private Map<String, ImageModelImpl> previewMap = new HashMap<>();
    private final Logger LOGGER = LoggerFactory.getLogger(DataService.class);
    public static final String URL_UPLOAD_ERROR = "Error while uploaded url";
    public static final String URL_UPLOAD_SUCCESS = "You successfully uploaded url";
    public static final String JSON_UPLOAD_ERROR = "Error while uploaded json";
    public static final String JSON_UPLOAD_SUCCESS = "You successfully uploaded json";
    public static final String FILE_UPLOAD_ERROR = "Can't upload images";
    public static final String FILE_UPLOAD_SUCCESS = "You successfully uploaded images: ";

    public List<ImageModelImpl> getAllPreview() {
        ArrayList<ImageModelImpl> imagesList = new ArrayList<>();
        for (String key : previewMap.keySet()) {
            ImageModelImpl imageModel = previewMap.get(key);
            imagesList.add(imageModel);
        }
        return imagesList;
    }

    public byte[] getPreviewBytes(String key) {
        byte[] imageInByte = null;
        BufferedImage image = previewMap.get(key).getImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", baos);
            baos.flush();
            imageInByte = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return imageInByte;
    }

    private void putInMemory(BufferedImage image, String fileName) {

        ImageModelImpl model = new ImageModelImpl();
        model.setImage(image);
        model.setName(fileName);
        model.setMd5(Util.md5(fileName));
        imagesMap.put(Util.md5(fileName), model);

        BufferedImage newImage = Util.resize(image, 100, 100);
        model.setImage(newImage);
        model.setName(fileName);
        model.setMd5(Util.md5(fileName));
        previewMap.put(Util.md5(fileName), model);
    }


    public FileUploadResult putFiles(MultipartFile[] files) {
        String successMessage = FILE_UPLOAD_SUCCESS;
        String errorMessage = "";
        if (files == null) {
            return new FileUploadResult("", FILE_UPLOAD_ERROR);
        }
        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                successMessage = "Please select a file to upload";
            } else {
                String fileName = file.getOriginalFilename();
                try {
                    BufferedImage image = ImageIO.read(file.getInputStream());
                    putInMemory(image, fileName);
                    successMessage += fileName + "; ";
                } catch (Exception e) {
                    errorMessage =FILE_UPLOAD_ERROR ;
                    LOGGER.error(errorMessage, e.getCause());
                    errorMessage += fileName + "; ";
                }
            }
        }
        return new FileUploadResult(successMessage, errorMessage);
    }

    public String putJson(String json) {
        String result = "Please select a json to upload";
        if (json==null || json.isEmpty()) {

        } else {
            try {
                Gson gson = new Gson();
                ImageJsonObj imageJsonObj = gson.fromJson(json, ImageJsonObj.class);
                BufferedImage image = null;
                byte[] imageByte = null;
                BASE64Decoder decoder = new BASE64Decoder();
                imageByte = decoder.decodeBuffer(imageJsonObj.getData());
                ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
                image = ImageIO.read(bis);
                bis.close();
                putInMemory(image, imageJsonObj.getName());
                result = JSON_UPLOAD_SUCCESS;
            } catch (Exception e) {
                result = JSON_UPLOAD_ERROR;
                LOGGER.error(result, e);
            }
        }
        return result;
    }

    public String putUrl(String url) {
        String result = "Please select a url to upload";
        if (url==null || !url.isEmpty()) {
            try {
                URL formedUrl = new URL(url);
                BufferedImage c = ImageIO.read(formedUrl);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(c, "jpg", baos);
                baos.flush();
                baos.close();
                putInMemory(c, url);
                result = URL_UPLOAD_SUCCESS;
            } catch (IOException e) {
                result = URL_UPLOAD_ERROR;
                LOGGER.error(result, e.getMessage());
            }
        }

        return result;
    }


}
