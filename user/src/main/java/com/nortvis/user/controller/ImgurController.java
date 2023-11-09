package com.nortvis.user.controller;

import com.nortvis.user.service.ImgurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/image")
public class ImgurController {
    @Autowired
    private ImgurService imgurService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile image) {

        String imgurLink = imgurService.uploadImage(image);
        return ResponseEntity.ok(imgurLink);
    }


    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteImage() {
        // Call imgurService to delete the image from Imgur
        imgurService.deleteImage();
        return ResponseEntity.ok("Image deleted from Imgur");
    }
}
