<template>
  <div class="app-container">
    <el-row :gutter="20" class="mgb20">
      <el-col :span="6" :xs="24">
        <el-card class="box-card">
          <template #header>
            <div class="clearfix">
              <span>个人信息</span>
            </div>
          </template>
          <div>
            <div class="text-center">
              <userAvatar />
            </div>
            <ul class="list-group list-group-striped">
              <li class="list-group-item">
                <el-icon>
                  <User />
                </el-icon>
                用户名称
                <div class="pull-right">{{ form.user.userName }}</div>
              </li>
              <li class="list-group-item">
                <el-icon>
                  <Cellphone />
                </el-icon>
                手机号码
                <div class="pull-right">{{ form.user.phonenumber }}</div>
              </li>
              <li class="list-group-item">
                <el-icon><Message /></el-icon>
                用户邮箱
                <div class="pull-right">{{ form.user.email }}</div>
              </li>
              <li class="list-group-item">
                <el-icon><Management /></el-icon>
                所属部门
                <div v-if="form.user.dept" class="pull-right">{{ form.user.dept?.deptName }} / {{ form.postGroup }}</div>
              </li>
              <li class="list-group-item">
                <el-icon><Avatar /></el-icon>
                所属角色
                <div class="pull-right">{{ form.roleGroup }}</div>
              </li>
              <li class="list-group-item">
                <el-icon><Calendar /></el-icon>
                创建日期
                <div class="pull-right">{{ form.user.createTime }}</div>
              </li>
            </ul>
          </div>
        </el-card>
      </el-col>

      <el-col :span="18" :xs="24">
        <el-card>
          <template #header>
            <div class="clearfix">
              <span>基本资料</span>
            </div>
          </template>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本资料" name="userinfo">
              <userInfo :user="form.user" />
            </el-tab-pane>
            <el-tab-pane label="修改密码" name="resetPwd">
              <resetPwd />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts" name="ProfilePage">
import userAvatar from "./userAvatar.vue";
import userInfo from "./userInfo.vue";
import resetPwd from "./resetPwd.vue";
import { getUserProfile } from "@/api/system/user";
import type { UserInfoObj } from "@/api/system/user";
import { ref } from "vue";

const form = ref<{ user: UserInfoObj; roleGroup: string; postGroup: string }>({
  user: { userId: "" },
  roleGroup: "",
  postGroup: "",
});
const activeTab = ref("userinfo");
function getUser() {
  getUserProfile().then((rsp) => {
    form.value.user = rsp.data;
    form.value.roleGroup = rsp.roleGroup;
    form.value.postGroup = rsp.postGroup;
  });
}

getUser();
</script>
