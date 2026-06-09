package com.baopu.dingtalk.service;

import com.baopu.dingtalk.client.DingtalkApiClient;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
  private final DingtalkApiClient apiClient;

  public MessageService(DingtalkApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public void sendCourseReminder(String dingtalkUserId, String courseTitle, String courseUrl) {
    apiClient.sendWorkNotice(dingtalkUserId, "课程提醒",
        "### 学习提醒\n\n您报名的课程 **" + courseTitle + "** 等待您继续学习，点击查看详情。", courseUrl);
  }

  public void sendExamNotice(String dingtalkUserId, String examTitle, String examUrl) {
    apiClient.sendWorkNotice(dingtalkUserId, "考试通知",
        "### 考试通知\n\n您有一场考试 **" + examTitle + "** 即将开始，请及时参加。", examUrl);
  }

  public void sendCertificateNotice(String dingtalkUserId, String certTitle, String certUrl) {
    apiClient.sendWorkNotice(dingtalkUserId, "证书发放",
        "### 恭喜获得证书\n\n您已完成课程学习并获得 **" + certTitle + "** 证书！", certUrl);
  }
}
