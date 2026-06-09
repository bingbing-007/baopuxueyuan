package com.baopu.dingtalk.client;

import com.baopu.dingtalk.config.DingtalkProperties;
import com.baopu.dingtalk.model.DingtalkAccessToken;
import com.baopu.dingtalk.model.DingtalkUserInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DingtalkApiClient {
  private static final Logger log = LoggerFactory.getLogger(DingtalkApiClient.class);
  private static final String BASE_URL = "https://oapi.dingtalk.com";
  private static final Duration TIMEOUT = Duration.ofSeconds(10);

  private final DingtalkProperties props;
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;
  private final ConcurrentHashMap<String, DingtalkAccessToken> tokenCache = new ConcurrentHashMap<>();

  public DingtalkApiClient(DingtalkProperties props, ObjectMapper objectMapper) {
    this.props = props;
    this.objectMapper = objectMapper;
    this.httpClient = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
  }

  public String getAccessToken() {
    return tokenCache.compute(props.appKey(), (key, cached) -> {
      if (cached != null && cached.expiresIn() > System.currentTimeMillis() / 1000 + 60) {
        return cached;
      }
      return fetchAccessToken();
    });
  }

  private DingtalkAccessToken fetchAccessToken() {
    try {
      var request = HttpRequest.newBuilder()
          .uri(URI.create(BASE_URL + "/gettoken?appkey=" + props.appKey() + "&appsecret=" + props.appSecret()))
          .GET()
          .timeout(TIMEOUT)
          .build();
      var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      var body = objectMapper.readTree(response.body());
      if (body.has("errcode") && body.get("errcode").asInt() != 0) {
        log.error("DingTalk gettoken failed: {}", body);
        throw new RuntimeException("DingTalk gettoken failed: " + body.get("errmsg").asText());
      }
      String token = body.get("access_token").asText();
      long expiresIn = body.get("expires_in").asLong();
      return new DingtalkAccessToken(token, System.currentTimeMillis() / 1000 + expiresIn);
    } catch (Exception e) {
      log.error("Failed to fetch DingTalk access token", e);
      throw new RuntimeException("DingTalk access token fetch failed", e);
    }
  }

  public DingtalkUserInfo getUserInfoByAuthCode(String authCode) {
    String accessToken = getAccessToken();
    try {
      var request = HttpRequest.newBuilder()
          .uri(URI.create(BASE_URL + "/topapi/v2/user/getuserinfo?access_token=" + accessToken))
          .POST(HttpRequest.BodyPublishers.ofString("{\"code\":\"" + authCode + "\"}"))
          .header("Content-Type", "application/json")
          .timeout(TIMEOUT)
          .build();
      var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      var body = objectMapper.readTree(response.body());
      if (body.get("errcode").asInt() != 0) {
        log.error("DingTalk getuserinfo failed: {}", body);
        throw new RuntimeException("DingTalk getuserinfo failed: " + body.get("errmsg").asText());
      }
      JsonNode result = body.get("result");
      String userId = result.get("userid").asText();

      var detail = getUserDetail(accessToken, userId);
      return new DingtalkUserInfo(
          userId,
          detail.get("name").asText(""),
          detail.has("mobile") ? detail.get("mobile").asText("") : "",
          detail.has("avatar") ? detail.get("avatar").asText("") : "",
          detail.has("email") ? detail.get("email").asText("") : "",
          detail.has("org_email") ? detail.get("org_email").asText("") : "");
    } catch (Exception e) {
      log.error("Failed to get DingTalk user info", e);
      throw new RuntimeException("DingTalk user info fetch failed", e);
    }
  }

  private JsonNode getUserDetail(String accessToken, String userId) {
    try {
      var request = HttpRequest.newBuilder()
          .uri(URI.create(BASE_URL + "/topapi/v2/user/get?access_token=" + accessToken))
          .POST(HttpRequest.BodyPublishers.ofString("{\"userid\":\"" + userId + "\"}"))
          .header("Content-Type", "application/json")
          .timeout(TIMEOUT)
          .build();
      var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      var body = objectMapper.readTree(response.body());
      if (body.get("errcode").asInt() != 0) {
        log.error("DingTalk user/get failed: {}", body);
        throw new RuntimeException("DingTalk user/get failed: " + body.get("errmsg").asText());
      }
      return body.get("result");
    } catch (Exception e) {
      log.error("Failed to get DingTalk user detail", e);
      throw new RuntimeException("DingTalk user detail fetch failed", e);
    }
  }
}
