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
        String successMessage = "You successfully uploaded images: ";
        String errorMessage = "";

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
                    errorMessage = "Can't upload images ";
                    LOGGER.error(errorMessage, e.getCause());
                    errorMessage += fileName + "; ";
                }
            }
        }
        return new FileUploadResult(successMessage, errorMessage);
    }

    public String putJson(String json) {
        String result = "Please select a json to upload";
        if (json.isEmpty()) {

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
                result = "You successfully uploaded json ";
            } catch (Exception e) {
                result = "Error while uploaded json ";
                LOGGER.error(result, e);
            }
        }
        return result;
    }

    public String putUrl(String url) {
        String result = "Please select a url to upload";
        if (!url.isEmpty()) {
            try {
                URL formedUrl = new URL(url);
                BufferedImage c = ImageIO.read(formedUrl);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(c, "jpg", baos);
                baos.flush();
                baos.close();
                putInMemory(c, url);
                result = "You successfully uploaded url";
            } catch (IOException e) {
                result = "Error while uploaded url";
                LOGGER.error(result, e.getMessage());
            }
        }

        return result;
    }


}
