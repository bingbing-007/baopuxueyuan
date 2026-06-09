package com.baopu.dingtalk.service;

import com.baopu.dingtalk.client.DingtalkApiClient;
import com.baopu.dingtalk.config.DingtalkProperties;
import com.baopu.dingtalk.model.DingtalkUserInfo;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DingtalkAuthService {
  private final DingtalkApiClient apiClient;
  private final DingtalkProperties props;

  public DingtalkAuthService(DingtalkApiClient apiClient, DingtalkProperties props) {
    this.apiClient = apiClient;
    this.props = props;
  }

  public DingtalkUserInfo authByCode(String authCode) {
    return apiClient.getUserInfoByAuthCode(authCode);
  }

  public Map<String, Object> getConfig() {
    return Map.of(
        "corpId", props.corpId(),
        "agentId", props.agentId() != null ? props.agentId() : 0L);
  }
}
