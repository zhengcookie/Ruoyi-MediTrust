import userStore from "@/stores/user";

/**
 * 判定用户是否具有权限
 * @param permission
 * @returns boolean
 */
function authPermission(permission: string) {
  const all_permission = "*:*:*";
  const uStore = userStore();
  const permissions = uStore.permissions;
  if (permission && permission.length > 0) {
    return permissions.some((v) => {
      return all_permission === v || v === permission;
    });
  } else {
    return false;
  }
}

/**
 * 判定用户是否具有权限
 * @param role
 * @returns boolean
 */
function authRole(role: string) {
  const super_admin = "admin";
  const uStore = userStore();
  const roles = uStore.roles;
  if (role && role.length > 0) {
    return roles.some((v) => {
      return super_admin === v || v === role;
    });
  } else {
    return false;
  }
}

export default {
  // 验证用户是否具备某权限
  hasPermi(permission: string) {
    return authPermission(permission);
  },
  // 验证用户是否含有指定权限，只需包含其中一个
  hasPermiOr(permissions: string[]) {
    return permissions.some((item) => {
      return authPermission(item);
    });
  },
  // 验证用户是否含有指定权限，必须全部拥有
  hasPermiAnd(permissions: string[]) {
    return permissions.every((item) => {
      return authPermission(item);
    });
  },
  // 验证用户是否具备某角色
  hasRole(role: string) {
    return authRole(role);
  },
  // 验证用户是否含有指定角色，只需包含其中一个
  hasRoleOr(roles: string[]) {
    return roles.some((item) => {
      return authRole(item);
    });
  },
  // 验证用户是否含有指定角色，必须全部拥有
  hasRoleAnd(roles: string[]) {
    return roles.every((item) => {
      return authRole(item);
    });
  },
};
