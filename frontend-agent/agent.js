#!/usr/bin/env node
/**
 * 前端工程师 Agent
 * 使用方式: node agent.js
 * 支持 MODEL_PROVIDER=claude | deepseek | ollama
 */

require('dotenv').config()
const Anthropic = require('@anthropic-ai/sdk')
const readline = require('readline')
const fs = require('fs')
const path = require('path')

const MODEL_PROVIDER = process.env.MODEL_PROVIDER || 'claude'

// ─── 读取 Claude Code 的 OAuth token ────────────────────────────────────────
function getToken() {
  const credsPath = path.join(process.env.USERPROFILE || process.env.HOME, '.claude', '.credentials.json')
  try {
    const creds = JSON.parse(fs.readFileSync(credsPath, 'utf8'))
    return creds.claudeAiOauth?.accessToken
  } catch {
    return process.env.ANTHROPIC_API_KEY
  }
}

function createAnthropicClient() {
  if (MODEL_PROVIDER === 'deepseek') {
    if (!process.env.DEEPSEEK_API_KEY) {
      console.error('未找到 DEEPSEEK_API_KEY 环境变量')
      process.exit(1)
    }
    return new Anthropic({
      apiKey: process.env.DEEPSEEK_API_KEY,
      baseURL: 'https://api.deepseek.com/anthropic'
    })
  }

  const token = getToken()
  if (!token) {
    console.error('未找到认证 token，请设置 ANTHROPIC_API_KEY 环境变量')
    process.exit(1)
  }
  return new Anthropic({ authToken: token })
}

// ─── Agent 配置 ──────────────────────────────────────────────────────────────
function getModel() {
  if (MODEL_PROVIDER === 'deepseek') return process.env.DEEPSEEK_MODEL || 'deepseek-v4-pro'
  if (MODEL_PROVIDER === 'ollama') return process.env.OLLAMA_MODEL || 'gemma3:4b'
  return 'claude-haiku-4-5'
}

const AGENT_CONFIG = {
  name: '前端工程师助手',
  model: getModel(),
  system: `你是一位资深前端工程师助手，拥有丰富的实战经验。

## 技术专长
- **框架**: Vue 3 (Composition API / <script setup>)、React 18、Vite、Next.js
- **语言**: TypeScript、JavaScript ES2024+
- **样式**: CSS3、Tailwind CSS、SCSS、CSS Variables、CSS Grid/Flexbox
- **工具链**: Git、pnpm/npm、ESLint、Prettier、Vitest/Jest
- **构建**: Vite、Webpack 5、Rollup、esbuild
- **状态管理**: Pinia、Zustand、Jotai、Redux Toolkit
- **动画**: GSAP、Framer Motion、CSS Animations、@vueuse/motion
- **UI 库**: Element Plus、Ant Design、shadcn/ui、Radix UI
- **其他**: WebSocket、Canvas/WebGL 基础、PWA、性能优化

## 工作原则
- **代码优先**: 直接给出可运行代码，必要时才附说明
- **现代实践**: 使用最新稳定 API，避免过时写法
- **简洁清晰**: 命名有意义，注释只写关键逻辑
- **考虑复用**: 组件设计注重可复用性和可维护性
- **主动优化**: 发现性能问题会主动指出改进方向

## 回答风格
- 默认中文回答
- 代码块明确标注语言
- 复杂问题先给思路再给代码
- 遇到多种方案时列出优劣`,
  tools: [
    {
      name: 'read_file',
      description: '读取本地文件内容',
      input_schema: {
        type: 'object',
        properties: {
          path: { type: 'string', description: '文件路径（相对或绝对路径）' }
        },
        required: ['path']
      }
    },
    {
      name: 'write_file',
      description: '写入文件内容',
      input_schema: {
        type: 'object',
        properties: {
          path: { type: 'string', description: '文件路径' },
          content: { type: 'string', description: '文件内容' }
        },
        required: ['path', 'content']
      }
    },
    {
      name: 'list_dir',
      description: '列出目录文件',
      input_schema: {
        type: 'object',
        properties: {
          path: { type: 'string', description: '目录路径，默认当前目录' }
        },
        required: []
      }
    },
    {
      name: 'run_command',
      description: '执行 shell 命令（npm、git 等）',
      input_schema: {
        type: 'object',
        properties: {
          command: { type: 'string', description: '要执行的命令' },
          cwd: { type: 'string', description: '工作目录，默认当前目录' }
        },
        required: ['command']
      }
    }
  ]
}

// ─── 工具执行 ────────────────────────────────────────────────────────────────
const { execSync } = require('child_process')

function executeTool(name, input) {
  try {
    switch (name) {
      case 'read_file': {
        const content = fs.readFileSync(input.path, 'utf8')
        return content.length > 8000
          ? content.slice(0, 8000) + '\n...[文件过长，已截断]'
          : content
      }
      case 'write_file': {
        const dir = path.dirname(input.path)
        fs.mkdirSync(dir, { recursive: true })
        fs.writeFileSync(input.path, input.content, 'utf8')
        return `✓ 已写入 ${input.path}`
      }
      case 'list_dir': {
        const dir = input.path || '.'
        const entries = fs.readdirSync(dir, { withFileTypes: true })
        return entries
          .map(e => `${e.isDirectory() ? '📁' : '📄'} ${e.name}`)
          .join('\n')
      }
      case 'run_command': {
        const result = execSync(input.command, {
          cwd: input.cwd || process.cwd(),
          encoding: 'utf8',
          timeout: 30000,
          stdio: ['pipe', 'pipe', 'pipe']
        })
        return result.trim() || '(命令执行成功，无输出)'
      }
      default:
        return `未知工具: ${name}`
    }
  } catch (err) {
    return `错误: ${err.message}`
  }
}

// ─── 会话历史持久化 ──────────────────────────────────────────────────────────
// Ollama 用 OpenAI 格式消息，与 Anthropic 格式不兼容，各自用独立 session 文件
const HISTORY_FILE = MODEL_PROVIDER === 'ollama'
  ? path.join(__dirname, '.session-ollama.json')
  : path.join(__dirname, '.session.json')

function loadHistory() {
  try {
    return JSON.parse(fs.readFileSync(HISTORY_FILE, 'utf8'))
  } catch {
    return []
  }
}

function saveHistory(messages) {
  const recent = messages.slice(-40)
  fs.writeFileSync(HISTORY_FILE, JSON.stringify(recent, null, 2))
}

// ─── Anthropic / DeepSeek 路径 ───────────────────────────────────────────────
async function chat(client, messages, userInput) {
  messages.push({ role: 'user', content: userInput })

  let history = [...messages]

  for (let turn = 0; turn < 8; turn++) {
    const response = await client.messages.create({
      model: AGENT_CONFIG.model,
      max_tokens: 8096,
      system: AGENT_CONFIG.system,
      tools: AGENT_CONFIG.tools,
      messages: history
    })

    let hasText = false
    for (const block of response.content) {
      if (block.type === 'text' && block.text) {
        if (!hasText) process.stdout.write('\n\x1b[36m助手:\x1b[0m ')
        process.stdout.write(block.text)
        hasText = true
      }
    }
    if (hasText) process.stdout.write('\n')

    if (response.stop_reason === 'end_turn') {
      messages.push({ role: 'assistant', content: response.content })
      break
    }

    if (response.stop_reason === 'tool_use') {
      const toolResults = []
      for (const block of response.content) {
        if (block.type === 'tool_use') {
          process.stdout.write(`\n\x1b[33m⚙ 调用工具: ${block.name}\x1b[0m`)
          if (Object.keys(block.input).length) {
            process.stdout.write(` ${JSON.stringify(block.input).slice(0, 80)}`)
          }
          const result = executeTool(block.name, block.input)
          process.stdout.write(` → \x1b[32m完成\x1b[0m\n`)
          toolResults.push({ type: 'tool_result', tool_use_id: block.id, content: result })
        }
      }
      history = [
        ...history,
        { role: 'assistant', content: response.content },
        { role: 'user', content: toolResults }
      ]
      messages.push({ role: 'assistant', content: response.content })
      messages.push({ role: 'user', content: toolResults })
    } else {
      messages.push({ role: 'assistant', content: response.content })
      break
    }
  }

  return messages
}

// ─── Ollama 路径（OpenAI 兼容格式）─────────────────────────────────────────
const OLLAMA_BASE = process.env.OLLAMA_BASE_URL || 'http://localhost:11434'

// 将 Anthropic tool 定义转换为 OpenAI function 定义
const ollamaTools = AGENT_CONFIG.tools.map(t => ({
  type: 'function',
  function: {
    name: t.name,
    description: t.description,
    parameters: t.input_schema
  }
}))

async function chatOllama(messages, userInput) {
  messages.push({ role: 'user', content: userInput })

  for (let turn = 0; turn < 8; turn++) {
    let resp
    try {
      resp = await fetch(`${OLLAMA_BASE}/v1/chat/completions`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          model: AGENT_CONFIG.model,
          messages: [
            { role: 'system', content: AGENT_CONFIG.system },
            ...messages
          ],
          tools: ollamaTools,
          stream: false
        })
      })
    } catch (e) {
      throw new Error(`无法连接 Ollama（${OLLAMA_BASE}），请确认 Ollama 正在运行：${e.message}`)
    }

    if (!resp.ok) {
      const text = await resp.text()
      throw new Error(`Ollama 返回错误 ${resp.status}: ${text}`)
    }

    const data = await resp.json()
    const choice = data.choices[0]
    const msg = choice.message
    const finishReason = choice.finish_reason

    if (msg.content) {
      process.stdout.write('\n\x1b[36m助手:\x1b[0m ' + msg.content + '\n')
    }

    // 存入历史（OpenAI 格式）
    messages.push(msg)

    if (!msg.tool_calls || msg.tool_calls.length === 0 || finishReason === 'stop') {
      break
    }

    // 处理工具调用
    for (const tc of msg.tool_calls) {
      const name = tc.function.name
      const input = typeof tc.function.arguments === 'string'
        ? JSON.parse(tc.function.arguments)
        : tc.function.arguments

      process.stdout.write(`\n\x1b[33m⚙ 调用工具: ${name}\x1b[0m`)
      if (Object.keys(input).length) {
        process.stdout.write(` ${JSON.stringify(input).slice(0, 80)}`)
      }
      const result = executeTool(name, input)
      process.stdout.write(` → \x1b[32m完成\x1b[0m\n`)

      messages.push({
        role: 'tool',
        tool_call_id: tc.id,
        content: String(result)
      })
    }
  }

  return messages
}

// ─── 主逻辑 ──────────────────────────────────────────────────────────────────
async function main() {
  const isOllama = MODEL_PROVIDER === 'ollama'
  const client = isOllama ? null : createAnthropicClient()

  let messages = loadHistory()

  const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout,
    terminal: true
  })

  const providerLabel = isOllama
    ? `\x1b[33mOllama\x1b[0m \x1b[90m(${AGENT_CONFIG.model})\x1b[0m`
    : MODEL_PROVIDER === 'deepseek'
      ? `\x1b[34mDeepSeek\x1b[0m \x1b[90m(${AGENT_CONFIG.model})\x1b[0m`
      : `\x1b[35mClaude\x1b[0m \x1b[90m(${AGENT_CONFIG.model})\x1b[0m`

  console.log('\x1b[35m╔══════════════════════════════════════╗\x1b[0m')
  console.log('\x1b[35m║      前端工程师 Agent 已就绪 ✦        ║\x1b[0m')
  console.log('\x1b[35m╚══════════════════════════════════════╝\x1b[0m')
  console.log(`模型: ${providerLabel}`)
  if (messages.length > 0) {
    console.log(`\x1b[90m已恢复 ${Math.floor(messages.length / 2)} 条历史对话\x1b[0m`)
  }
  console.log('\x1b[90m输入 /clear 清空历史，/exit 退出\x1b[0m\n')

  const ask = () => {
    rl.question('\x1b[32m你:\x1b[0m ', async (input) => {
      input = input.trim()
      if (!input) { ask(); return }

      if (input === '/exit') { saveHistory(messages); rl.close(); return }
      if (input === '/clear') {
        messages = []
        fs.existsSync(HISTORY_FILE) && fs.unlinkSync(HISTORY_FILE)
        console.log('\x1b[90m历史已清空\x1b[0m\n')
        ask(); return
      }

      try {
        messages = isOllama
          ? await chatOllama(messages, input)
          : await chat(client, messages, input)
        saveHistory(messages)
      } catch (err) {
        console.error('\x1b[31m错误:', err.message, '\x1b[0m')
      }

      if (!rl.closed) ask()
    })
  }

  ask()
}

main()
