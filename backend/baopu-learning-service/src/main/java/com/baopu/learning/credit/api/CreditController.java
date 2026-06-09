package com.baopu.learning.credit.api;

import com.baopu.learning.credit.service.CreditService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/credits")
public class CreditController {
  private final CreditService creditService;

  public CreditController(CreditService creditService) { this.creditService = creditService; }

  @GetMapping("/rules")
  List<Map<String, Object>> rules() { return creditService.listRules(); }

  @GetMapping("/my-account")
  Map<String, Object> myAccount(HttpServletRequest req) {
    return creditService.getAccount((Long) req.getAttribute("userId"));
  }

  @GetMapping("/my-records")
  List<Map<String, Object>> myRecords(HttpServletRequest req, @RequestParam(defaultValue = "20") int limit) {
    return creditService.getRecords((Long) req.getAttribute("userId"), limit);
  }
}
