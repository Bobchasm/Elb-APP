<template>
  <div class="notifications-container">
    <div class="header">
      <h1 class="title">消息与通知</h1>
    </div>

    <div class="notification-list">
      <div v-for="message in messages" :key="message.id" class="notification-item">
        <div class="icon-wrapper" :class="{ 'unread': message.unread }">
          <svg class="icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M22 6.5C22 7.32843 21.3284 8 20.5 8H3.5C2.67157 8 2 7.32843 2 6.5V3.5C2 2.67157 2.67157 2 3.5 2H20.5C21.3284 2 22 2.67157 22 3.5V6.5ZM22 17.5C22 18.3284 21.3284 19 20.5 19H3.5C2.67157 19 2 18.3284 2 17.5V14.5C2 13.6716 2.67157 13 3.5 13H20.5C21.3284 13 22 13.6716 22 14.5V17.5ZM2 10.5C2 9.67157 2.67157 9 3.5 9H20.5C21.3284 9 22 9.67157 22 10.5V11.5C22 12.3284 21.3284 13 20.5 13H3.5C2.67157 13 2 12.3284 2 11.5V10.5Z"></path>
          </svg>
        </div>
        <div class="content">
          <div class="message-text" :class="{ 'bold': message.unread }">{{ message.text }}</div>
          <div class="timestamp">{{ message.time }}</div>
        </div>
        <div v-if="message.unread" class="dot"></div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NotificationsPage',
  data() {
    return {
      messages: [
        {
          id: 1,
          text: '您申请成为商家的审核已通过',
          time: '2025-09-20 14:30',
          unread: true // 未读
        },
        {
          id: 2,
          text: '您的账户已被解除禁用',
          time: '2025-09-18 10:05',
          unread: false // 已读
        },
        {
          id: 3,
          text: '系统维护通知：为了提供更好的服务，我们将在 23:00-次日 02:00 进行系统升级，期间可能会影响部分功能使用。',
          time: '2025-09-17 18:00',
          unread: false
        },
      ]
    };
  }
};
</script>

<style scoped>
.notifications-container {
  max-width: 600px;
  margin: 0 auto;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  color: #333;
}

.header {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #e0e0e0;
}

.title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #1a73e8; /* 标题蓝色 */
}

.notification-list {
  padding: 0;
  margin: 0;
}

.notification-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s ease;
  cursor: pointer;
}

.notification-item:hover {
  background-color: #f5f8ff; /* 浅蓝色背景 */
}

.notification-item:last-child {
  border-bottom: none;
}

.icon-wrapper {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 15px;
  background-color: #e6f0ff; /* 浅蓝色 */
}

.icon-wrapper.unread {
  background-color: #c9e2ff; /* 针对未读消息的更深的蓝色 */
}

.icon {
  width: 20px;
  height: 20px;
  color: #1a73e8; /* 图标蓝色 */
}

.content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.message-text {
  font-size: 16px;
  line-height: 1.5;
  color: #555;
}

.message-text.bold {
  font-weight: 600;
  color: #1a73e8; /* 未读消息文本蓝色 */
}

.timestamp {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.dot {
  width: 8px;
  height: 8px;
  background-color: #ff4d4f; /* 未读小红点 */
  border-radius: 50%;
  margin-left: 10px;
  flex-shrink: 0;
}
</style>