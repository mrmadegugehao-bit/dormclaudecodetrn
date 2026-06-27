<template>
  <div class="login-wrapper">
    <!-- 背景装饰球 -->
    <div class="blob blob-1" />
    <div class="blob blob-2" />
    <div class="blob blob-3" />

    <div class="login-card">
      <!-- 头部 -->
      <div class="login-header">
        <div class="logo-circle">🏠</div>
        <h1 class="system-title">社員寮管理システム</h1>
        <p class="system-subtitle">Dormitory Management System</p>
      </div>

      <!-- 表单 -->
      <el-form ref="formRef" :model="form" :rules="rules" class="login-form" size="large" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="ユーザー名" :prefix-icon="User" clearable />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="パスワード"
            :prefix-icon="Lock" show-password clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">
            ログイン
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-hint">
        <el-icon><InfoFilled /></el-icon>
        デモ：admin / admin123
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, InfoFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ username: 'admin', password: 'admin123' })

const rules = {
  username: [{ required: true, message: 'ユーザー名を入力してください', trigger: 'blur' }],
  password: [{ required: true, message: 'パスワードを入力してください', trigger: 'blur' }],
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    setTimeout(() => {
      loading.value = false
      ElMessage.success('ログインしました ✨')
      router.push('/dashboard')
    }, 600)
  })
}
</script>

<style scoped>
.login-wrapper {
  height: 100vh;
  width: 100vw;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
}

/* 装饰背景球 */
.blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(70px);
  opacity: 0.35;
  animation: float 8s ease-in-out infinite;
}
.blob-1 {
  width: 420px; height: 420px;
  background: #a78bfa;
  top: -120px; left: -100px;
}
.blob-2 {
  width: 320px; height: 320px;
  background: #f9a8d4;
  bottom: -80px; right: -60px;
  animation-delay: -3s;
}
.blob-3 {
  width: 200px; height: 200px;
  background: #7dd3fc;
  top: 40%; left: 60%;
  animation-delay: -5s;
}
@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); }
  50%       { transform: translateY(-24px) scale(1.04); }
}

/* 登录卡片 */
.login-card {
  position: relative;
  z-index: 1;
  width: 420px;
  background: rgba(255,255,255,0.92);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 44px 40px 36px;
  box-shadow: 0 24px 60px rgba(99,29,190,0.25), 0 0 0 1px rgba(255,255,255,0.5);
}

/* 头部 */
.login-header { text-align: center; margin-bottom: 32px; }

.logo-circle {
  width: 70px; height: 70px;
  border-radius: 20px;
  background: linear-gradient(135deg, #6366f1, #a78bfa);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 34px;
  box-shadow: 0 8px 24px rgba(99,102,241,0.35);
  margin-bottom: 14px;
}

.system-title {
  font-size: 22px;
  font-weight: 700;
  color: #1e1b4b;
  letter-spacing: 2px;
  margin-bottom: 6px;
}

.system-subtitle {
  font-size: 12px;
  color: #a5b4fc;
  letter-spacing: 2px;
  text-transform: uppercase;
}

/* 表单 */
.login-form { margin-top: 4px; }

.login-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  border-radius: 12px !important;
  background: linear-gradient(135deg, #6366f1, #7c3aed) !important;
  border: none !important;
  box-shadow: 0 6px 20px rgba(99,102,241,0.40);
  transition: transform 0.2s, box-shadow 0.2s !important;
}
.login-btn:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 10px 28px rgba(99,102,241,0.50) !important;
}

/* 提示 */
.login-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  margin-top: 16px;
  font-size: 12px;
  color: #a5b4fc;
}
</style>
