package com.conti_talent.springboot.appweb.conti_talent_web.service;

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
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CvStorageService {

    private static final Map<String, String> EXTENSIONS_BY_CONTENT_TYPE = Map.of(
            MediaType.APPLICATION_PDF_VALUE, ".pdf",
            "application/msword", ".doc",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx",
            MediaType.IMAGE_JPEG_VALUE, ".jpg",
            MediaType.IMAGE_PNG_VALUE, ".png",
            "image/webp", ".webp"
    );

    private final Path uploadRoot;
    private final long maxFileSize;
    private final Set<String> allowedContentTypes;

    public CvStorageService(@Value("${app.cv.upload-dir:uploads/cv}") String uploadDir,
                            @Value("${app.cv.max-file-size:5242880}") long maxFileSize,
                            @Value("${app.cv.allowed-content-types:application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/jpeg,image/png,image/webp}")
                            String allowedContentTypes) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.maxFileSize = maxFileSize;
        this.allowedContentTypes = Arrays.stream(allowedContentTypes.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toSet());
    }

    public String store(MultipartFile file) {
        validate(file);
        try {
            Files.createDirectories(uploadRoot);
            String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "cv" : file.getOriginalFilename());
            String extension = resolveExtension(originalName, file.getContentType());
            String baseName = safeBaseName(originalName);
            String storedFilename = UUID.randomUUID() + "-" + baseName + extension;
            Path target = uploadRoot.resolve(storedFilename).normalize();

            if (!target.startsWith(uploadRoot)) {
                throw new BusinessException("Nombre de archivo invalido");
            }

            file.transferTo(target);
            return storedFilename;
        } catch (IOException ex) {
            throw new BusinessException("No se pudo guardar el CV");
        }
    }

    public Resource load(String storedFilename) {
        try {
            Path file = resolveStoredPath(storedFilename);
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("CV no encontrado");
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("CV no encontrado");
        }
    }

    public MediaType mediaType(String storedFilename) {
        try {
            String detected = Files.probeContentType(resolveStoredPath(storedFilename));
            if (detected != null && allowedContentTypes.contains(detected)) {
                return MediaType.parseMediaType(detected);
            }
        } catch (IOException ignored) {
        }
        return mediaTypeFromExtension(storedFilename);
    }

    public boolean shouldOpenInline(String storedFilename) {
        MediaType mediaType = mediaType(storedFilename);
        return MediaType.APPLICATION_PDF.equals(mediaType)
                || MediaType.IMAGE_JPEG.equals(mediaType)
                || MediaType.IMAGE_PNG.equals(mediaType)
                || "image/webp".equals(mediaType.toString());
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("El CV es obligatorio");
        }
        if (file.getSize() > maxFileSize) {
            throw new BusinessException("El CV supera el tamanio maximo permitido");
        }
        String contentType = file.getContentType();
        if (contentType == null || !allowedContentTypes.contains(contentType)) {
            throw new BusinessException("Tipo de archivo CV no permitido");
        }

        String originalName = file.getOriginalFilename();
        String extension = extensionOf(originalName);
        String expectedExtension = EXTENSIONS_BY_CONTENT_TYPE.get(contentType);
        if (extension.isBlank() || expectedExtension == null || !isCompatibleExtension(contentType, extension)) {
            throw new BusinessException("Extension de CV no permitida");
        }
    }

    private Path resolveStoredPath(String storedFilename) {
        if (storedFilename == null || storedFilename.isBlank()) {
            throw new ResourceNotFoundException("CV no encontrado");
        }
        Path file = uploadRoot.resolve(StringUtils.cleanPath(storedFilename)).normalize();
        if (!file.startsWith(uploadRoot) || !Files.exists(file) || !Files.isRegularFile(file)) {
            throw new ResourceNotFoundException("CV no encontrado");
        }
        return file;
    }

    private String resolveExtension(String originalName, String contentType) {
        String extension = extensionOf(originalName);
        if (!extension.isBlank() && isCompatibleExtension(contentType, extension)) {
            return extension;
        }
        return EXTENSIONS_BY_CONTENT_TYPE.getOrDefault(contentType, "");
    }

    private static boolean isCompatibleExtension(String contentType, String extension) {
        return switch (contentType) {
            case MediaType.APPLICATION_PDF_VALUE -> ".pdf".equals(extension);
            case "application/msword" -> ".doc".equals(extension);
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> ".docx".equals(extension);
            case MediaType.IMAGE_JPEG_VALUE -> ".jpg".equals(extension) || ".jpeg".equals(extension);
            case MediaType.IMAGE_PNG_VALUE -> ".png".equals(extension);
            case "image/webp" -> ".webp".equals(extension);
            default -> false;
        };
    }

    private static String extensionOf(String filename) {
        String cleanName = StringUtils.cleanPath(filename == null ? "" : filename);
        int lastDot = cleanName.lastIndexOf('.');
        if (lastDot < 0 || lastDot == cleanName.length() - 1) {
            return "";
        }
        return cleanName.substring(lastDot).toLowerCase(Locale.ROOT);
    }

    private static String safeBaseName(String originalName) {
        String cleanName = StringUtils.cleanPath(originalName == null ? "cv" : originalName);
        int lastDot = cleanName.lastIndexOf('.');
        String withoutExtension = lastDot > 0 ? cleanName.substring(0, lastDot) : cleanName;
        String normalized = Normalizer.normalize(withoutExtension, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return normalized.isBlank() ? "cv" : normalized.substring(0, Math.min(normalized.length(), 60));
    }

    private static MediaType mediaTypeFromExtension(String filename) {
        String extension = extensionOf(filename);
        return switch (extension) {
            case ".pdf" -> MediaType.APPLICATION_PDF;
            case ".doc" -> MediaType.parseMediaType("application/msword");
            case ".docx" -> MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            case ".jpg", ".jpeg" -> MediaType.IMAGE_JPEG;
            case ".png" -> MediaType.IMAGE_PNG;
            case ".webp" -> MediaType.parseMediaType("image/webp");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}
