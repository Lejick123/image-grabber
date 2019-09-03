package portal.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import portal.Upload;

@Controller
public class ImageUploader {

    @GetMapping("/upload")
    public String uploadingForm(Model model) {
        model.addAttribute("upload", new Upload());
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadingSubmit(@ModelAttribute Upload upload) {
        return "result";
    }

}