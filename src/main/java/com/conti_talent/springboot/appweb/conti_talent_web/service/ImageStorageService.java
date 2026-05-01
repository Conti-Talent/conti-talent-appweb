package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.response.ImageUploadResponse;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ImageStorageService {

    private final Path uploadRoot;
    private final long maxFileSize;
    private final Set<String> allowedContentTypes;

    public ImageStorageService(@Value("${app.upload.dir:uploads}") String uploadDir,
                               @Value("${app.upload.max-file-size:2097152}") long maxFileSize,
                               @Value("${app.upload.allowed-content-types:image/jpeg,image/png,image/webp,image/gif}")
                               String allowedContentTypes) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.maxFileSize = maxFileSize;
        this.allowedContentTypes = Arrays.stream(allowedContentTypes.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toSet());
    }

    public ImageUploadResponse store(MultipartFile file) {
        validate(file);
        try {
            Files.createDirectories(uploadRoot);
            String extension = extensionFrom(file.getOriginalFilename(), file.getContentType());
            String filename = UUID.randomUUID() + extension;
            Path target = uploadRoot.resolve(filename).normalize();

            if (!target.startsWith(uploadRoot)) {
                throw new BusinessException("Nombre de archivo invalido");
            }

            file.transferTo(target);
            return new ImageUploadResponse(filename, "/uploads/" + filename, file.getSize(), file.getContentType());
        } catch (IOException ex) {
            throw new BusinessException("No se pudo guardar la imagen");
        }
    }

    public Resource load(String filename) {
        try {
            Path file = uploadRoot.resolve(filename).normalize();
            if (!file.startsWith(uploadRoot) || !Files.exists(file) || !Files.isRegularFile(file)) {
                throw new ResourceNotFoundException("Imagen no encontrada");
            }
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("Imagen no encontrada");
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("Imagen no encontrada");
        }
    }

    public MediaType mediaType(String filename) {
        try {
            String detected = Files.probeContentType(uploadRoot.resolve(filename).normalize());
            if (detected != null && allowedContentTypes.contains(detected)) {
                return MediaType.parseMediaType(detected);
            }
        } catch (IOException ignored) {
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("La imagen es obligatoria");
        }
        if (file.getSize() > maxFileSize) {
            throw new BusinessException("La imagen supera el tamanio maximo permitido");
        }
        if (file.getContentType() == null || !allowedContentTypes.contains(file.getContentType())) {
            throw new BusinessException("Tipo de imagen no permitido");
        }
    }

    private String extensionFrom(String originalFilename, String contentType) {
        String cleanName = StringUtils.cleanPath(originalFilename == null ? "" : originalFilename);
        int lastDot = cleanName.lastIndexOf('.');
        if (lastDot >= 0 && lastDot < cleanName.length() - 1) {
            return cleanName.substring(lastDot).toLowerCase();
        }
        return switch (contentType) {
            case MediaType.IMAGE_JPEG_VALUE -> ".jpg";
            case MediaType.IMAGE_PNG_VALUE -> ".png";
            case "image/webp" -> ".webp";
            case MediaType.IMAGE_GIF_VALUE -> ".gif";
            default -> "";
        };
    }
}
