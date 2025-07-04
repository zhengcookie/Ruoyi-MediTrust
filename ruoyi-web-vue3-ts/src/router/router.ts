import "vue-router";
type TagsTree = {
  icon?: string;
  title?: string;
};
declare module "vue-router" {
  interface RouteMeta {
    // 是可选的
    title?: string;
    // 路由权限
    permissions?: string[];
    // 路由角色权限
    roles?: string[];
    // 父级地址
    parentPath?: string;
    // 全路由地址
    fullPath?: string;
    // 父级地址
    hidden?: boolean;
    // icon
    icon?: string;
    // tags 路径
    tagsTree?: TagsTree[];
    // 是否缓存
    noCache?: boolean;
    // 是否缓存
    query?: string;
    // 链接
    link?: string;
  }
}
