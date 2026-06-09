package com.baopu.iam.api;

import com.baopu.iam.model.Department;
import com.baopu.iam.model.Role;
import com.baopu.iam.model.Tenant;
import com.baopu.iam.model.UserInfo;
import com.baopu.iam.service.IamService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/iam")
public class IamController {
  private final IamService iamService;

  public IamController(IamService iamService) { this.iamService = iamService; }

  @GetMapping("/tenants")
  List<Tenant> tenants() { return iamService.listTenants(); }

  @PostMapping("/tenants")
  Tenant createTenant(@RequestBody Map<String, String> body) {
    return iamService.createTenant(body.get("name"), body.get("code"));
  }

  @GetMapping("/tenants/{tenantId}/departments")
  List<Department> departments(@PathVariable Long tenantId) { return iamService.listDepartments(tenantId); }

  @PostMapping("/tenants/{tenantId}/departments")
  Department createDepartment(@PathVariable Long tenantId, @RequestBody Map<String, Object> body) {
    Long parentId = body.get("parentId") instanceof Number n ? n.longValue() : null;
    return iamService.createDepartment(tenantId, parentId, (String) body.get("name"),
        body.get("sortOrder") instanceof Number n ? n.intValue() : 0);
  }

  @GetMapping("/tenants/{tenantId}/roles")
  List<Role> roles(@PathVariable Long tenantId) { return iamService.listRoles(tenantId); }

  @PostMapping("/tenants/{tenantId}/roles")
  Role createRole(@PathVariable Long tenantId, @RequestBody Map<String, String> body) {
    return iamService.createRole(tenantId, body.get("name"), body.get("code"));
  }

  @GetMapping("/tenants/{tenantId}/users")
  List<UserInfo> users(@PathVariable Long tenantId) { return iamService.listUsers(tenantId); }

  @GetMapping("/users/{userId}")
  UserInfo user(@PathVariable Long userId) { return iamService.getUser(userId); }
}
