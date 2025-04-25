package com.farmdora.farmdoraproduct.service;


import java.io.InputStream;
import java.io.OutputStream;

public interface StorageService {

    void upload(String filePath, InputStream fileIn);
    void download(String filePath, OutputStream fileOut);
    void delete(String filePath);
    String getObjectStorageImageUrl(String objectName);
    String getThumbnailUrl(String objectName);
    String getStreamUrl(String objectName);
}
