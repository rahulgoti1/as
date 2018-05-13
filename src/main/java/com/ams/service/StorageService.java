package com.ams.service;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by grahul on 10-05-2018.
 */
@Service
public class StorageService {


  Logger log = LoggerFactory.getLogger(this.getClass().getName());
  private final Path rootLocation = Paths.get("upload-dir");

  public boolean validateFiles(List<MultipartFile> files) {

    if (files == null) {
      return true;
    }

    for (MultipartFile file : files) {
      if (file == null || file.getSize() == 0 || file.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  public boolean saveUploadedFile(MultipartFile file) throws IOException {

    if (!this.validateFiles(Arrays.asList(file))) {
      return false;
    }

    byte[] bytes = file.getBytes();
    Path path = Paths.get(String.valueOf(rootLocation), file.getOriginalFilename());
    Files.write(path, bytes);
    return true;
  }

  public boolean saveUploadedFiles(List<MultipartFile> files) throws IOException {

    if (files == null) {
      return false;
    }

    for (MultipartFile file : files) {
      if (!this.saveUploadedFile(file)) {
        return false;
      }
    }
    return true;
  }

  public boolean saveUploadedFile(MultipartFile file, String rootFolder, String fileName) throws IOException {

    if (!this.validateFiles(Arrays.asList(file))) {
      return false;
    }

    /**
     * Create Directory
     */
    File directory = new File(String.valueOf(rootLocation) + "/" + rootFolder);
    if (!directory.exists()) {
      directory.mkdir();
    }


    byte[] bytes = file.getBytes();
    Path path = Paths.get(String.valueOf(rootLocation), rootFolder, fileName);
    Files.write(path, bytes);
    return true;
  }

  public Resource loadFile(String path, String filename) {
    try {
      Path file = rootLocation.resolve(path).resolve(filename);
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new RuntimeException("FAIL!");
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException("FAIL!");
    }
  }
}
