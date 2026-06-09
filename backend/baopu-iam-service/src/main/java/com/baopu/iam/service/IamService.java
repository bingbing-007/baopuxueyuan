package com.baopu.iam.service;

import com.baopu.iam.model.Department;
import com.baopu.iam.model.Role;
import com.baopu.iam.model.Tenant;
import com.baopu.iam.model.UserInfo;
import com.baopu.iam.repository.DepartmentRepository;
import com.baopu.iam.repository.RoleRepository;
import com.baopu.iam.repository.TenantRepository;
import com.baopu.iam.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IamService {
  private final TenantRepository tenantRepo;
  private final DepartmentRepository deptRepo;
  private final RoleRepository roleRepo;
  private final UserRepository userRepo;

  public IamService(TenantRepository tr, DepartmentRepository dr, RoleRepository rr, UserRepository ur) {
    this.tenantRepo = tr; this.deptRepo = dr; this.roleRepo = rr; this.userRepo = ur;
  }

  public List<Tenant> listTenants() { return tenantRepo.findAll(); }

  public Tenant getTenant(Long id) {
    return tenantRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
  }

  @Transactional
  public Tenant createTenant(String name, String code) {
    Tenant t = tenantRepo.create(name, code);
    roleRepo.create(t.id(), "管理员", "admin");
    roleRepo.create(t.id(), "讲师", "lecturer");
    roleRepo.create(t.id(), "学员", "student");
    return t;
  }

  public List<Department> listDepartments(Long tenantId) { return deptRepo.findByTenant(tenantId); }

  public Department createDepartment(Long tenantId, Long parentId, String name, int sortOrder) {
    return deptRepo.create(tenantId, parentId, name, sortOrder);
  }

  public List<Role> listRoles(Long tenantId) { return roleRepo.findByTenant(tenantId); }

  public Role createRole(Long tenantId, String name, String code) {
    return roleRepo.create(tenantId, name, code);
  }

  public void assignRole(Long userId, Long roleId) { roleRepo.assignUser(userId, roleId); }

  public List<UserInfo> listUsers(Long tenantId) { return userRepo.findByTenant(tenantId); }

  public UserInfo getUser(Long userId) {
    return userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
  }

  public UserInfo upsertUser(Long tenantId, String dingtalkUserId, String name, String mobile) {
    return userRepo.upsert(tenantId, dingtalkUserId, name, mobile);
  }
}
