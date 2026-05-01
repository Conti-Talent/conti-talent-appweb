package com.conti_talent.springboot.appweb.conti_talent_web.dto.response;

public class ImageUploadResponse {

    private String filename;
    private String url;
    private long size;
    private String contentType;

    public ImageUploadResponse() {
    }

    public ImageUploadResponse(String filename, String url, long size, String contentType) {
        this.filename = filename;
        this.url = url;
        this.size = size;
        this.contentType = contentType;
    }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
}
