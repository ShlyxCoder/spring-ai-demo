<template>
  <div class="chat-app">
    <div class="chat-container">
      <!-- 头部 -->
      <div class="chat-header">
        <h1>Spring AI 聊天助手</h1>
        <p>智能对话，随时为您服务</p>
        
        <!-- 模式选择 -->
        <div class="mode-buttons">
          <button 
            @click="switchMode('normal')"
            :class="['mode-btn', { active: chatMode === 'normal' }]"
          >
            📞 普通对话
          </button>
          <button 
            @click="switchMode('rag')"
            :class="['mode-btn', { active: chatMode === 'rag' }]"
          >
            📚 RAG对话
          </button>
          <button 
            @click="switchMode('stream')"
            :class="['mode-btn', { active: chatMode === 'stream' }]"
          >
            ⚡ 流式对话
          </button>
          <button 
            @click="switchMode('ragStream')"
            :class="['mode-btn', { active: chatMode === 'ragStream' }]"
          >
            🔄 RAG流式
          </button>
        </div>

        <!-- RAG配置 -->
        <div v-if="showRagConfig" class="rag-config">
          <input 
            v-model="ragTag" 
            type="text" 
            placeholder="请输入RAG标签"
          >
          <label>
            <input v-model="isAllow" type="checkbox">
            <span>允许使用模型自身信息库</span>
          </label>
        </div>
      </div>

      <!-- 聊天主体 -->
      <div class="chat-body">
        <!-- 消息区域 -->
        <div class="messages-area" ref="messagesContainer">
          <!-- 空状态 -->
          <div v-if="messages.length === 0" class="empty-state">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path>
            </svg>
            <h3>开始您的对话吧！</h3>
            <p>我是您的AI助手，随时为您服务</p>
            <p>当前模式: {{ getModeLabel(chatMode) }}</p>
          </div>

          <!-- 消息列表 -->
          <div v-for="msg in messages" :key="msg.id" :class="['message', msg.type]">
            <div class="message-avatar">
              <svg v-if="msg.type === 'user'" width="20" height="20" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
              </svg>
              <svg v-else width="20" height="20" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path>
              </svg>
            </div>
            <div class="message-content">
              <div v-if="msg.type === 'user'">{{ msg.content }}</div>
              <div v-else>
                <div v-if="msg.isStreaming" class="streaming-text">{{ msg.content }}</div>
                <div v-else class="markdown-content" v-html="renderMarkdown(msg.content)"></div>
              </div>
              <div class="message-time">{{ formatTime(msg.timestamp) }}</div>
            </div>
          </div>

          <!-- 加载指示器 -->
          <div v-if="isLoading" class="message assistant">
            <div class="message-avatar">
              <svg width="20" height="20" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path>
              </svg>
            </div>
            <div class="message-content">
              <div class="loading-message">
                <div class="loading-dots">
                  <div class="loading-dot"></div>
                  <div class="loading-dot"></div>
                  <div class="loading-dot"></div>
                </div>
                <span>AI正在思考...</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="input-area">
          <div class="input-container">
            <textarea 
              ref="messageInputElement"
              v-model="messageInput" 
              @keydown="handleKeydown"
              @input="adjustTextareaHeight"
              placeholder="输入您的消息..." 
              class="input-field"
              rows="1"
            ></textarea>
            <button 
              @click="sendMessage" 
              :disabled="isLoading || !canSend"
              class="send-button"
            >
              <svg width="20" height="20" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"></path>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'

// 响应式数据
const chatMode = ref('normal')
const ragTag = ref('')
const isAllow = ref(false)
const messageInput = ref('')
const messages = ref([])
const isLoading = ref(false)
const controller = ref(null)
const messagesContainer = ref(null)
const messageInputElement = ref(null)

// 计算属性
const showRagConfig = computed(() => {
  return chatMode.value === 'rag' || chatMode.value === 'ragStream'
})

const canSend = computed(() => {
  return messageInput.value && 
         typeof messageInput.value === 'string' && 
         messageInput.value.trim().length > 0
})

// 方法
const switchMode = (mode) => {
  if (isLoading.value) return
  chatMode.value = mode
}

const getModeLabel = (mode) => {
  const modeMap = {
    'normal': '普通对话',
    'rag': 'RAG对话',
    'stream': '流式对话',
    'ragStream': 'RAG流式'
  }
  return modeMap[mode] || mode
}

const formatTime = (timestamp) => {
  return timestamp.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const renderMarkdown = (content) => {
  if (!content || typeof content !== 'string') return ''
  
  // 简化的 Markdown 渲染，专门优化代码块显示
  let html = content
    // 代码块处理（三个反引号）
    .replace(/```(\w+)?\n?([\s\S]*?)```/g, (match, lang, code) => {
      return `<pre><code class="language-${lang || 'text'}">${code.trim()}</code></pre>`
    })
    
    // 行内代码
    .replace(/`([^`\n]+)`/g, '<code>$1</code>')
    
    // 标题
    .replace(/^### (.*$)/gm, '<h3>$1</h3>')
    .replace(/^## (.*$)/gm, '<h2>$1</h2>')
    .replace(/^# (.*$)/gm, '<h1>$1</h1>')
    
    // 粗体和斜体
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.*?)\*/g, '<em>$1</em>')
    
    // 链接
    .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" target="_blank">$1</a>')
    
    // 列表项
    .replace(/^- (.*$)/gm, '<li>$1</li>')
    .replace(/^(\d+)\. (.*$)/gm, '<li>$2</li>')
    
    // 换行
    .replace(/\n\n/g, '</p><p>')
    .replace(/\n/g, '<br>')
  
  // 包装列表
  html = html.replace(/(<li>.*?<\/li>)/s, '<ul>$1</ul>')
  
  // 包装段落
  if (!html.includes('<p>') && !html.includes('<h') && !html.includes('<pre>')) {
    html = '<p>' + html + '</p>'
  }
  
  return html
}

const addMessage = (type, content, isStreaming = false) => {
  const message = {
    type,
    content: content || '',
    timestamp: new Date(),
    id: Date.now() + Math.random(),
    isStreaming: isStreaming || false
  }
  
  messages.value.push(message)
  scrollToBottom()
  return message
}

const updateMessage = (messageId, content, isStreaming = false) => {
  const message = messages.value.find(m => m.id === messageId)
  if (message) {
    message.content = content || ''
    message.isStreaming = isStreaming
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const adjustTextareaHeight = () => {
  nextTick(() => {
    if (messageInputElement.value) {
      messageInputElement.value.style.height = 'auto'
      messageInputElement.value.style.height = Math.min(messageInputElement.value.scrollHeight, 120) + 'px'
    }
  })
}

const handleKeydown = (e) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

const handleStreamResponse = async (response) => {
  if (!response.body) {
    throw new Error('Response body is null')
  }

  const reader = response.body.getReader()
  const decoder = new TextDecoder()
  
  const assistantMessage = addMessage('assistant', '', true)
  let accumulatedContent = ''

  try {
    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      // 直接解码接收到的文本数据，无需查找 data: 前缀
      const chunk = decoder.decode(value, { stream: true })
      
      if (chunk) {
        accumulatedContent += chunk
        updateMessage(assistantMessage.id, accumulatedContent, true)
        scrollToBottom()
      }
    }

    // 流式传输完成，更新消息状态
    updateMessage(assistantMessage.id, accumulatedContent, false)

  } catch (streamError) {
    console.error('流式读取错误:', streamError)
    updateMessage(assistantMessage.id, accumulatedContent + '\n\n[流式传输中断]', false)
  }
}

const sendMessage = async () => {
  if (!messageInput.value || typeof messageInput.value !== 'string') return
  
  const message = messageInput.value.trim()
  if (!message || isLoading.value) return

  try {
    addMessage('user', message)
    messageInput.value = ''
    isLoading.value = true
    adjustTextareaHeight()

    let url = 'http://localhost:8090'
    const params = new URLSearchParams()

    switch (chatMode.value) {
      case 'normal':
        url += '/chat'
        params.append('message', message)
        break
      case 'rag':
        url += '/chat/withRag'
        params.append('message', message)
        params.append('ragTag', ragTag.value || '')
        if (isAllow.value) {
          params.append('isAllow', 'true')
        }
        break
      case 'stream':
        url += '/chat/stream'
        params.append('message', message)
        break
      case 'ragStream':
        url += '/chat/withRagStream'
        params.append('message', message)
        params.append('ragTag', ragTag.value || '')
        if (isAllow.value) {
          params.append('isAllow', 'true')
        }
        break
    }

    controller.value = new AbortController()

    const response = await fetch(`${url}?${params.toString()}`, {
      signal: controller.value.signal
    })

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    if (chatMode.value === 'stream' || chatMode.value === 'ragStream') {
      await handleStreamResponse(response)
    } else {
      const data = await response.json()
      if (data.code && data.code !== 200) {
        addMessage('assistant', `服务异常 (${data.code})：${data.message || '未知错误'}`)
      } else {
        addMessage('assistant', data.data || '抱歉，我暂时无法回答这个问题。')
      }
    }
  } catch (error) {
    console.error('发送消息错误:', error)
    if (error.name !== 'AbortError') {
      addMessage('assistant', '抱歉，发生了错误，请稍后再试。')
    }
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  console.log('Spring AI 聊天助手已加载')
})
</script>