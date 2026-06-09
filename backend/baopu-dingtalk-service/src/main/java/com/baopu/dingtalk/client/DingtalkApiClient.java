package com.baopu.dingtalk.client;

import com.baopu.dingtalk.config.DingtalkProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DingtalkApiClient {
  private static final Logger log = LoggerFactory.getLogger(DingtalkApiClient.class);
  private static final String BASE = "https://oapi.dingtalk.com";
  private static final Duration TIMEOUT = Duration.ofSeconds(15);

  private final DingtalkProperties props;
  private final HttpClient http;
  private final ObjectMapper mapper;
  private final ConcurrentHashMap<String, TokenCache> tokenCache = new ConcurrentHashMap<>();

  public DingtalkApiClient(DingtalkProperties props, ObjectMapper mapper) {
    this.props = props;
    this.mapper = mapper;
    this.http = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
  }

  // ── Access Token ──

  public String getAccessToken() {
    return tokenCache.compute(props.appKey(), (k, v) -> {
      if (v != null && System.currentTimeMillis() < v.expiresAt - 60000) return v;
      return fetchToken();
    }).token;
  }

  private TokenCache fetchToken() {
    try {
      var req = HttpRequest.newBuilder()
          .uri(URI.create(BASE + "/gettoken?appkey=" + props.appKey() + "&appsecret=" + props.appSecret()))
          .GET().timeout(TIMEOUT).build();
      var res = http.send(req, HttpResponse.BodyHandlers.ofString());
      var body = mapper.readTree(res.body());
      check(body);
      return new TokenCache(body.get("access_token").asText(),
          System.currentTimeMillis() + body.get("expires_in").asLong() * 1000);
    } catch (Exception e) {
      throw new RuntimeException("DingTalk token failed", e);
    }
  }

  // ── User ──

  public JsonNode getUserInfoByCode(String authCode) {
    String token = getAccessToken();
    var body = post(token, "/topapi/v2/user/getuserinfo", Map.of("code", authCode));
    String userId = body.get("result").get("userid").asText();
    return post(token, "/topapi/v2/user/get", Map.of("userid", userId)).get("result");
  }

  public List<Long> listDeptUserIds(String token, Long deptId) {
    List<Long> ids = new ArrayList<>();
    int cursor = 0;
    while (true) {
      var body = post(token, "/topapi/user/listid",
          Map.of("dept_id", deptId, "cursor", cursor, "size", 100));
      var result = body.get("result");
      for (var n : result.get("list")) ids.add(n.asLong());
      if (!result.has("has_more") || !result.get("has_more").asBoolean()) break;
      cursor = result.get("next_cursor").asInt();
    }
    return ids;
  }

  // ── Department ──

  public List<JsonNode> listDepartments() {
    String token = getAccessToken();
    return listSubDepts(token, 1L);
  }

  private List<JsonNode> listSubDepts(String token, Long parentId) {
    List<JsonNode> all = new ArrayList<>();
    var body = post(token, "/topapi/v2/department/listsub",
        Map.of("dept_id", parentId));
    for (var dept : body.get("result")) {
      all.add(dept);
      all.addAll(listSubDepts(token, dept.get("dept_id").asLong()));
    }
    return all;
  }

  // ── Message ──

  public void sendWorkNotice(String userId, String title, String content, String url) {
    String token = getAccessToken();
    try {
      var body = Map.of(
          "agent_id", props.agentId(),
          "userid_list", userId,
          "msg", Map.of(
              "msgtype", "markdown",
              "markdown", Map.of("title", title, "text", content + "\n\n[查看详情](" + url + ")")));
      var res = postRaw(token, "/topapi/message/corpconversation/asyncsend_v2", body);
      check(res);
    } catch (Exception e) {
      log.error("DingTalk sendWorkNotice failed: userId={}", userId, e);
    }
  }

  // ── Helpers ──

  private JsonNode post(String token, String path, Map<String, Object> body) {
    var res = postRaw(token, path, body);
    check(res);
    return res;
  }

  private JsonNode postRaw(String token, String path, Map<String, Object> body) {
    try {
      var req = HttpRequest.newBuilder()
          .uri(URI.create(BASE + path + "?access_token=" + token))
          .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
          .header("Content-Type", "application/json").timeout(TIMEOUT).build();
      return mapper.readTree(http.send(req, HttpResponse.BodyHandlers.ofString()).body());
    } catch (Exception e) {
      throw new RuntimeException("DingTalk API call failed: " + path, e);
    }
  }

  private void check(JsonNode body) {
    if (body.has("errcode") && body.get("errcode").asInt() != 0) {
      throw new RuntimeException("DingTalk error: " + body.path("errmsg").asText());
    }
  }

  private record TokenCache(String token, long expiresAt) {}
}
