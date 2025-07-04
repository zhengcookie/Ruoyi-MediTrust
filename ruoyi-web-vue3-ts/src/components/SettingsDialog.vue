<template>
  <el-dialog v-model="dialogVisible" title="设置" width="500px">
    <el-form :model="form" label-width="100px">
      <el-form-item label="头像">
        <el-upload
          class="avatar-uploader"
          action="#"
          :auto-upload="false"
          :show-file-list="false"
          :on-change="handleAvatarChange"
          :before-upload="beforeAvatarUpload">
          <img v-if="savedPreview" :src="savedPreview" class="avatar" />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="性别">
        <el-select v-model="form.gender">
          <el-option label="男" value="男" />
          <el-option label="女" value="女" />
        </el-select>
      </el-form-item>
      <el-form-item label="年龄">
        <el-input v-model="form.age" />
      </el-form-item>

      <el-form-item label="邮箱">
        <el-input v-model="form.email" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSettings">保存</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { getUserDetails, updateInfo } from '../api/medical';
// import axios from 'axios';

const props = defineProps<{
  visible: boolean;
}>();

const emit = defineEmits(['update:visible', 'save']);

const dialogVisible = ref(props.visible);
const imageUrl = ref('');
const selectedFile = ref<File | null>(null);
const form = ref({
  avatar: localStorage.getItem("avatar") || "",
  name: localStorage.getItem("name") || "",
  gender: '',
  age: 0,
  email: ''
});

watch(() => props.visible, (val) => {
  dialogVisible.value = val;
});

watch(dialogVisible, (val) => {
  emit('update:visible', val);
});
const savedPreview = localStorage.getItem("avatarPreview");
  
onMounted(() => {
  // 加载头像路径

  
  getUserDetails(userId).then(res => {
    // Access data directly from res instead of res.data
    form.value.name = res.name;
    form.value.gender = res.gender;
    form.value.age = res.age;
    form.value.email = res.email;
    
    // 如果后端返回了头像信息且本地没有，则使用后端的
    if (!form.value.avatar && res.avatar) {
      form.value.avatar = res.avatar;
      
      // 尝试使用服务器图片路径
      imageUrl.value = `/api/images/${res.avatar}`;
      
      // 保存到localStorage
      localStorage.setItem("avatar", res.avatar);
    }
  });
});

const handleAvatarChange = (file: any) => {
  // 保存文件对象
  selectedFile.value = file.raw;
  
  // 将文件转换为base64用于预览
  const reader = new FileReader();
  reader.onload = (e) => {
    // 获取base64用于预览
    const base64 = e.target?.result as string;
    
    // 仅用于预览的base64
    imageUrl.value = base64;
    
    // 保存文件路径到form.avatar，这将发送给后端
    form.value.avatar = file.raw.name;
    
    // 同时在localStorage中保存base64和文件路径
    localStorage.setItem("avatarPreview", base64);
    
    console.log('已选择文件:', file.raw.name);
  };
  
  // 读取文件内容为base64
  reader.readAsDataURL(file.raw);
};

const beforeAvatarUpload = (file: File) => {
  const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPGOrPNG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!');
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!');
  }
  return isJPGOrPNG && isLt2M;
};
const userId = parseInt(localStorage.getItem("userId") || "0");
const saveSettings = async () => {
  try {
    // 发送用户信息和头像文件路径
    const res = await updateInfo({
      userId: userId,
      name: form.value.name,
      gender: form.value.gender,
      age: form.value.age,
      email: form.value.email,
      avatar: form.value.avatar  // 文件路径，非base64数据
    });
    
    if (res.code === 200) {
      // 保存文件路径到localStorage
      localStorage.setItem("avatar", form.value.avatar);
      localStorage.setItem("name", form.value.name);
      
      // 如果此时有预览图，也保存它
      if (imageUrl.value && imageUrl.value.startsWith('data:')) {
        localStorage.setItem("avatarPreview", imageUrl.value);
      }
      
      emit('save', form.value);
      dialogVisible.value = false;
      ElMessage.success('设置已保存');
    } else {
      ElMessage.error('设置保存失败');
    }
  } catch (error) {
    console.error('保存设置时出错:', error);
    ElMessage.error('保存设置失败');
  }
};

defineExpose({
  dialogVisible,
  form,
  imageUrl,
  handleAvatarChange,
  beforeAvatarUpload,
  saveSettings
});
</script>

<style scoped>
.avatar-uploader {
  text-align: center;
}

.avatar-uploader .avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  transition: border-color 0.3s;
}

.avatar-uploader-icon:hover {
  border-color: var(--primary-color);
}
</style> 