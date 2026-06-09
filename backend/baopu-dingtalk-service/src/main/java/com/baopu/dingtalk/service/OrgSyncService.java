package com.baopu.dingtalk.service;

import com.baopu.dingtalk.client.DingtalkApiClient;
import com.baopu.dingtalk.repository.DingtalkSyncRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OrgSyncService {
  private static final Logger log = LoggerFactory.getLogger(OrgSyncService.class);
  private static final long DEFAULT_TENANT_ID = 1L;

  private final DingtalkApiClient apiClient;
  private final DingtalkSyncRepository syncRepo;

  public OrgSyncService(DingtalkApiClient apiClient, DingtalkSyncRepository syncRepo) {
    this.apiClient = apiClient;
    this.syncRepo = syncRepo;
  }

  @Scheduled(cron = "${baopu.dingtalk.sync-cron:0 0 3 * * ?}")
  public void fullSync() {
    log.info("Starting DingTalk org full sync");
    try {
      syncDepartments();
      syncUsers();
      log.info("DingTalk org sync completed");
    } catch (Exception e) {
      log.error("DingTalk org sync failed", e);
    }
  }

  public void syncDepartments() {
    var depts = apiClient.listDepartments();
    log.info("Syncing {} departments from DingTalk", depts.size());
    for (var dept : depts) {
      long deptId = dept.get("dept_id").asLong();
      long parentId = dept.has("parent_id") ? dept.get("parent_id").asLong() : 0L;
      String name = dept.get("name").asText();
      syncRepo.upsertDepartment(DEFAULT_TENANT_ID, deptId, parentId == 0 ? null : parentId, name);
    }
  }

  public void syncUsers() {
    String token = apiClient.getAccessToken();
    var deptIds = syncRepo.listDingtalkDeptIds();
    int count = 0;
    for (Long deptId : deptIds) {
      var userIds = apiClient.listDeptUserIds(token, deptId);
      for (Long dingtalkUserId : userIds) {
        syncRepo.upsertUser(DEFAULT_TENANT_ID, String.valueOf(dingtalkUserId), "钉钉用户" + dingtalkUserId, null);
        count++;
      }
    }
    log.info("Synced {} users from DingTalk", count);
  }
}
