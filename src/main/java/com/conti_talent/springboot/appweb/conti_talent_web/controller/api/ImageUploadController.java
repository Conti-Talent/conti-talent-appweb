package com.conti_talent.springboot.appweb.conti_talent_web.controller.api;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.response.ApiResponse;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.response.ImageUploadResponse;
import com.conti_talent.springboot.appweb.conti_talent_web.service.ImageStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@RestController
public class ImageUploadController {

    private final ImageStorageService imageStorageService;

    public ImageUploadController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @PostMapping("/api/uploads/images")
    public ApiResponse<ImageUploadResponse> uploadImage(@RequestParam("image") MultipartFile image) {
        return ApiResponse.ok(imageStorageService.store(image));
    }

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> showImage(@PathVariable String filename) {
        return ResponseEntity.ok()
                .contentType(imageStorageService.mediaType(filename))
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
                .body(imageStorageService.load(filename));
    }
}
