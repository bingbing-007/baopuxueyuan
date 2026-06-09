package com.baopu.learning.dingtalk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "baopu.dingtalk")
record DingtalkProperties(String appKey, String appSecret, String corpId, Long agentId) {}

@Component
public class DingtalkClient {
  private static final Logger log = LoggerFactory.getLogger(DingtalkClient.class);
  private static final String BASE = "https://oapi.dingtalk.com";
  private static final Duration TIMEOUT = Duration.ofSeconds(10);

  private final DingtalkProperties props;
  private final HttpClient http;
  private final ObjectMapper mapper;
  private volatile String cachedToken;
  private volatile long tokenExpiresAt;

  public DingtalkClient(DingtalkProperties props, ObjectMapper mapper) {
    this.props = props;
    this.mapper = mapper;
    this.http = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
  }

  public DingtalkUser resolveUser(String authCode) {
    String token = getToken();
    String userId = getUserIdByCode(token, authCode);
    JsonNode detail = getUserDetail(token, userId);
    return new DingtalkUser(
        userId,
        detail.path("name").asText(""),
        detail.path("mobile").asText(""),
        detail.path("avatar").asText(""),
        detail.path("email").asText(""),
        detail.path("org_email").asText(""));
  }

  private synchronized String getToken() {
    if (cachedToken != null && System.currentTimeMillis() < tokenExpiresAt - 60000) {
      return cachedToken;
    }
    try {
      var req = HttpRequest.newBuilder()
          .uri(URI.create(BASE + "/gettoken?appkey=" + props.appKey() + "&appsecret=" + props.appSecret()))
          .GET().timeout(TIMEOUT).build();
      var res = http.send(req, HttpResponse.BodyHandlers.ofString());
      var body = mapper.readTree(res.body());
      checkError(body);
      cachedToken = body.get("access_token").asText();
      tokenExpiresAt = System.currentTimeMillis() + body.get("expires_in").asLong() * 1000;
      return cachedToken;
    } catch (Exception e) {
      log.error("DingTalk gettoken failed", e);
      throw new RuntimeException("DingTalk access token failed", e);
    }
  }

  private String getUserIdByCode(String token, String authCode) {
    try {
      var req = HttpRequest.newBuilder()
          .uri(URI.create(BASE + "/topapi/v2/user/getuserinfo?access_token=" + token))
          .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(Map.of("code", authCode))))
          .header("Content-Type", "application/json").timeout(TIMEOUT).build();
      var res = http.send(req, HttpResponse.BodyHandlers.ofString());
      var body = mapper.readTree(res.body());
      checkError(body);
      return body.get("result").get("userid").asText();
    } catch (Exception e) {
      log.error("DingTalk getuserinfo failed", e);
      throw new RuntimeException("DingTalk auth code invalid", e);
    }
  }

  private JsonNode getUserDetail(String token, String userId) {
    try {
      var req = HttpRequest.newBuilder()
          .uri(URI.create(BASE + "/topapi/v2/user/get?access_token=" + token))
          .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(Map.of("userid", userId))))
          .header("Content-Type", "application/json").timeout(TIMEOUT).build();
      var res = http.send(req, HttpResponse.BodyHandlers.ofString());
      var body = mapper.readTree(res.body());
      checkError(body);
      return body.get("result");
    } catch (Exception e) {
      log.error("DingTalk user/get failed", e);
      throw new RuntimeException("DingTalk user detail failed", e);
    }
  }

  private void checkError(JsonNode body) {
    if (body.has("errcode") && body.get("errcode").asInt() != 0) {
      throw new RuntimeException("DingTalk API error: " + body.path("errmsg").asText());
    }
  }

  public record DingtalkUser(String userId, String name, String mobile, String avatar, String email, String orgEmail) {}
}
