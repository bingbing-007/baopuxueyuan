package com.baopu.learning.file.api;

import com.baopu.learning.file.service.FileService;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileController {
  private final FileService fileService;

  public FileController(FileService fileService) { this.fileService = fileService; }

  @PostMapping("/upload")
  Map<String, String> upload(@RequestParam("file") MultipartFile file) throws Exception {
    String url = fileService.upload(file.getOriginalFilename(), file.getInputStream(), file.getSize(), file.getContentType());
    return Map.of("url", url);
  }

  @PostMapping("/upload-policy")
  Map<String, String> uploadPolicy(@RequestParam(defaultValue = "uploads") String dir) {
    return fileService.uploadPolicy(dir);
  }
}
