<template>
  <el-form ref="userRef" :model="form" :rules="rules" label-width="80px">
    <el-form-item label="用户昵称" prop="nickName">
      <el-input v-model="form.nickName" maxlength="30" />
    </el-form-item>
    <el-form-item label="手机号码" prop="phonenumber">
      <el-input v-model="form.phonenumber" maxlength="11" />
    </el-form-item>
    <el-form-item label="邮箱" prop="email">
      <el-input v-model="form.email" maxlength="50" />
    </el-form-item>
    <el-form-item label="性别">
      <el-radio-group v-model="form.sex">
        <el-radio label="0">男</el-radio>
        <el-radio label="1">女</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="submit">保存</el-button>
      <el-button type="danger" @click="close">关闭</el-button>
    </el-form-item>
  </el-form>
</template>

<script lang="ts" setup name="userifnfoPage">
import { updateUserProfile } from "@/api/system/user";
import type { UserInfoObj } from "@/api/system/user";
import { ElMessage } from "element-plus";
import { ref, watch } from "vue";
import useStore from "@/stores/user";
import tagsStore from "@/stores/tagsView";
import type { ElForm } from "@/api/form";

const uStore = useStore();
const tStore = tagsStore();

const props = withDefaults(
  defineProps<{
    user: UserInfoObj;
  }>(),
  {
    user: () => {
      return { userId: "" };
    },
  }
);

const userRef = ref<ElForm>();
const form = ref<UserInfoObj>({ userId: "" });
const rules = ref({
  nickName: [{ required: true, message: "用户昵称不能为空", trigger: "blur" }],
  email: [
    { required: true, message: "邮箱地址不能为空", trigger: "blur" },
    {
      type: "email",
      message: "请输入正确的邮箱地址",
      trigger: ["blur", "change"],
    },
  ],
  phonenumber: [
    { required: true, message: "手机号码不能为空", trigger: "blur" },
    {
      pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
      message: "请输入正确的手机号码",
      trigger: "blur",
    },
  ],
});

/** 提交按钮 */
function submit() {
  userRef.value?.validate((valid) => {
    if (valid) {
      updateUserProfile(form.value).then(() => {
        ElMessage.success("修改成功");
        uStore.phonenumber = form.value.phonenumber || "";
        uStore.email = form.value.email || "";
      });
    }
  });
}

/** 关闭按钮 */
function close() {
  tStore.closePage();
}

// 回显当前登录用户信息
watch(
  () => props.user,
  (user) => {
    if (user) {
      form.value = {
        userId: user.userId,
        nickName: user.nickName,
        phonenumber: user.phonenumber,
        email: user.email,
        sex: user.sex,
      };
    }
  },
  { immediate: true }
);
</script>
