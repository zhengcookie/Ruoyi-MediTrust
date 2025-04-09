<template>
    <div class="permission-management">
      <!-- 表格头部 -->
      <div class="table-header">
        <div class="header-item">权限ID</div>
        <div class="header-item">权限名称</div>
        <div class="header-item">权限性质</div>
        <div class="header-item">权限描述</div>
        <div class="header-item">状态</div>
        <div class="header-item">操作</div>
      </div>

      <!-- 表格内容 -->
      <div class="table-content">
        <div v-for="(item, index) in permissions" :key="index" class="table-row">
          <div class="cell">{{ item.id }}</div>
          <div class="cell">{{ item.name }}</div>
          <div class="cell">{{ item.type }}</div>
          <div class="cell">{{ item.description }}</div>
          <div class="cell">
            <el-radio-group v-model="item.status">
              <el-radio :label="true">开</el-radio>
              <el-radio :label="false">关</el-radio>
            </el-radio-group>
          </div>
          <div class="cell">
            <el-button type="text" @click="handleEdit(item)">编辑</el-button>
            <el-button type="text" @click="handleDelete(item)">删除</el-button>
          </div>
        </div>
      </div>
    </div>
  </template>

  <script setup lang="ts">
  import { ref } from 'vue'

  interface Permission {
    id: string
    name: string
    type: string
    description: string
    status: boolean
  }

  const permissions = ref<Permission[]>([
    {
      id: '01',
      name: '查看内容',
      type: '基本权限',
      description: '权限内容',
      status: true
    },
    {
      id: '02', 
      name: '发布内容',
      type: '操作权限',
      description: '权限内容',
      status: true
    },
    {
      id: '03',
      name: '编辑内容', 
      type: '操作权限',
      description: '权限内容',
      status: true
    },
    {
      id: '04',
      name: '删除内容',
      type: '操作权限', 
      description: '权限内容',
      status: true
    },
    {
      id: '05',
      name: '下载内容',
      type: '操作权限',
      description: '权限内容', 
      status: true
    }
  ])

  const handleEdit = (item: Permission) => {
    console.log('编辑', item)
  }

  const handleDelete = (item: Permission) => {
    console.log('删除', item)
  }
  </script>

  <style scoped>
  .permission-management {
    padding: 20px;
  }

  .table-header {
    display: grid;
    grid-template-columns: repeat(6, 1fr);
    background: #f5f7fa;
    padding: 10px 0;
  }

  .header-item {
    text-align: center;
    font-weight: bold;
  }

  .table-content {
    border: 1px solid #ebeef5;
  }

  .table-row {
    display: grid;
    grid-template-columns: repeat(6, 1fr);
    padding: 10px 0;
    border-bottom: 1px solid #ebeef5;
  }

  .cell {
    text-align: center;
  }
  </style>