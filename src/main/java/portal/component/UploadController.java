package portal.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import portal.model.DataService;
import portal.model.FileUploadResult;
import portal.model.ImageModelImpl;
import java.util.List;

@Controller
public class UploadController {

    private final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);
    public static final String UPLOAD_FORM = "uploadForm";
    @Autowired
    DataService dataService;

    @GetMapping("/")
    public String index(Model model) {
        List<ImageModelImpl> previewList = dataService.getAllPreview();
        model.addAttribute("images", previewList);
        return UPLOAD_FORM;
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

    @PostMapping("/uploadForm")
    public String imagesUpload(@RequestParam(name="files",required=false) MultipartFile[] files ,
                               @RequestParam(name="json",required=false) String json,
                               @RequestParam(name="url",required=false) String url,
                               RedirectAttributes redirectAttributes) {

        FileUploadResult filesResult = dataService.putFiles(files);
        String jsonResult = dataService.putJson(json);
        String urlResult = dataService.putUrl(url);
        redirectAttributes.addFlashAttribute("message_json", jsonResult);
        redirectAttributes.addFlashAttribute("message_file", filesResult.getSuccess());
        redirectAttributes.addFlashAttribute("message_file_error", filesResult.getError());
        redirectAttributes.addFlashAttribute("message_url", urlResult);
        LOGGER.info("Upload files Finished");
        return "redirect:/uploadStatus";
    }

    @RequestMapping(value = "/images/{id:.+}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(@PathVariable("id") String id) {
        LOGGER.info("Getting images preview");
        byte[] imageInByte = dataService.getPreviewBytes(id);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageInByte);
    }

}