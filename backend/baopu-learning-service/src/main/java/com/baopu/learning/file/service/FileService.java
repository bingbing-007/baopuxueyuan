package com.baopu.learning.file.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.baopu.learning.file.config.OssProperties;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class FileService {
  private final OssProperties props;

  public FileService(OssProperties props) { this.props = props; }

  public String upload(String originalName, InputStream stream, long size, String contentType) {
    OSS client = new OSSClientBuilder().build(props.endpoint(), props.accessKeyId(), props.accessKeySecret());
    try {
      String ext = "";
      int dot = originalName.lastIndexOf('.');
      if (dot > 0) ext = originalName.substring(dot);
      String key = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/" + UUID.randomUUID() + ext;

      var meta = new com.aliyun.oss.model.ObjectMetadata();
      meta.setContentType(contentType);
      meta.setContentLength(size);
      client.putObject(new PutObjectRequest(props.bucket(), key, stream, meta));

      String domain = props.cdnDomain() != null && !props.cdnDomain().isBlank()
          ? props.cdnDomain() : "https://" + props.bucket() + "." + props.endpoint();
      return domain + "/" + key;
    } finally {
      client.shutdown();
    }
  }

  public Map<String, String> uploadPolicy(String dir) {
    long expireTime = System.currentTimeMillis() / 1000 + (props.expireSeconds() != null ? props.expireSeconds() : 300);
    String key = (dir != null ? dir + "/" : "") + UUID.randomUUID();
    return Map.of(
        "endpoint", "https://" + props.bucket() + "." + props.endpoint(),
        "key", key,
        "expireAt", String.valueOf(expireTime),
        "maxSize", String.valueOf(10 * 1024 * 1024)
    );
  }
}
