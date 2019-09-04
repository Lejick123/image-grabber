package portal.component;

import com.google.gson.Gson;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import portal.model.FileUploadResult;
import portal.model.ImageJsonObj;
import portal.model.ImageModelImpl;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UploadController {
    private static Map<String, ImageModelImpl> filesMap = new HashMap<>();
    private static Map<String, ImageModelImpl> previewMap = new HashMap<>();


    @GetMapping("/")
    public String index(Model model) {
        ArrayList<ImageModelImpl> imagesList = new ArrayList<>();
        for (String key : previewMap.keySet()) {
            ImageModelImpl imageModel = previewMap.get(key);
            imagesList.add(imageModel);
        }
        model.addAttribute("images", imagesList);
        return "uploadForm";
    }

    @PostMapping("/uploadForm")
    public String imagesUpload(@RequestParam("files") MultipartFile[] files,
                               @RequestParam("json") String json,
                               @RequestParam("url") String url,
                               RedirectAttributes redirectAttributes) {

        FileUploadResult filesResult = processFiles(files);
        String jsonResult = processJson(json);
        String urlResult = processUrl(url);
        redirectAttributes.addFlashAttribute("message_json", jsonResult);
        redirectAttributes.addFlashAttribute("message_file", filesResult.getSuccess());
        redirectAttributes.addFlashAttribute("message_file_error", filesResult.getError());
        redirectAttributes.addFlashAttribute("message_url", urlResult);
        return "redirect:/uploadStatus";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

    @RequestMapping(value = "/images/{id:.+}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(@PathVariable("id") String id) throws IOException {
        BufferedImage image = previewMap.get(id).getImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageInByte);
    }


    private String processJson(String json) {
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
                e.printStackTrace();
                result = "Error while uploaded json ";
            }
        }
        return result;
    }

    private FileUploadResult processFiles(MultipartFile[] files) {
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
                    e.printStackTrace();
                    errorMessage += fileName + "; ";
                }
            }
        }
        return new FileUploadResult(successMessage, errorMessage);
    }

    private String processUrl(String url) {
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
                e.printStackTrace();
                result = "Error while uploaded url";
            }
        }
        return result;
    }


    private void putInMemory(BufferedImage image, String fileName) {

        ImageModelImpl model = new ImageModelImpl();
        model.setImage(image);
        model.setName(fileName);
        model.setMd5(md5(fileName));
        filesMap.put(md5(fileName), model);

        BufferedImage newImage = resize(image, 100, 100);
        model.setImage(newImage);
        model.setName(fileName);
        model.setMd5(md5(fileName));
        previewMap.put(md5(fileName), model);
    }


    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }

    private String md5(String password) {
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());

            byte byteData[] = md.digest();


            for (int i = 0; i < byteData.length; i++)
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));

        } catch (NoSuchAlgorithmException e) {
            //    LOGGER.error(e.getMessage());
        }
        return sb.toString();
    }


}