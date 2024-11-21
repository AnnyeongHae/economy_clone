<template>
  <div>
    <h1>QR Session Timer</h1>
    <button @click="createSession">
      Create QR Session
    </button>

    <p v-if="sessionKey">Session Key: {{ sessionKey }}</p>
    <p v-if="remainingTime > 0">
      Time Remaining: {{ remainingTime }} seconds
    </p>
    <p v-if="remainingTime === 0">Session Expired</p>

    <div v-if="qrCodeUrl">
      <h3>Your QR Code:</h3>
      <img :src="qrCodeUrl" alt="Generated QR Code" />
    </div>

    <button
      v-if="sessionKey && remainingTime > 0"
      @click="validateSession"
    >
      Validate Session
    </button>
    <p v-if="validationMessage">{{ validationMessage }}</p>
    <p v-if="errorMessage" class="error">
      {{ errorMessage }}
    </p>
  </div>
</template>

<script>
import api from '@/api/qrApi';

export default {
  data() {
    return {
      sessionKey: null, // 세션 키
      qrCodeUrl: null, // QR 코드 이미지 URL
      remainingTime: 0, // 남은 시간
      timer: null, // 타이머 ID
      syncTimer: null, // 분 단위 동기화 타이머
      validationMessage: null,
      errorMessage: null,
    };
  },
  methods: {
    // QR 세션 생성
    async createSession() {
      try {
        this.errorMessage = null;
        this.validationMessage = null;

        // 세션 생성
        const sessionKey = await api.createSession();
        this.sessionKey = sessionKey;

        // QR 코드 생성
        this.generateQrCode();

        // 초기 남은 시간 설정 및 타이머 시작
        this.syncWithServer();
        this.startLocalTimer();
        this.startSyncTimer();
      } catch (error) {
        this.errorMessage = 'Failed to create session.';
      }
    },
    // QR 코드 생성
    async generateQrCode() {
      try {
        const qrData = await api.generateQrCode({
          sessionId: this.sessionKey,
        });
        const blob = new Blob([qrData], {
          type: 'image/png',
        });
        this.qrCodeUrl = URL.createObjectURL(blob);
      } catch (error) {
        this.errorMessage = 'Failed to generate QR Code.';
      }
    },
    // 초 단위 타이머 시작
    startLocalTimer() {
      if (this.timer) clearInterval(this.timer); // 기존 타이머 정리
      this.timer = setInterval(() => {
        if (this.remainingTime > 0) {
          this.remainingTime -= 1; // 1초 감소
        } else {
          clearInterval(this.timer); // 시간이 0이 되면 타이머 정지
        }
      }, 1000);
    },
    // 1분마다 서버와 동기화
    startSyncTimer() {
      if (this.syncTimer) clearInterval(this.syncTimer); // 기존 동기화 타이머 정리
      this.syncTimer = setInterval(() => {
        this.syncWithServer(); // 서버와 동기화
      }, 60000); // 1분마다 실행
    },
    // 서버와 동기화
    async syncWithServer() {
      try {
        const remainingTime = await api.getSessionTimeout(
          this.sessionKey
        );
        this.remainingTime = remainingTime;
        if (remainingTime <= 0) {
          clearInterval(this.timer); // 만료된 경우 타이머 정지
          clearInterval(this.syncTimer); // 동기화 타이머도 정지
        }
      } catch (error) {
        this.errorMessage = 'Failed to sync with server.';
        clearInterval(this.syncTimer); // 서버 통신 실패 시 동기화 타이머 정지
      }
    },

    // 세션 검증
    async validateSession() {
      try {
        this.validationMessage = null;
        const isValid = await api.validateSession(
          this.sessionKey
        );
        this.validationMessage = isValid
          ? 'Session is valid!'
          : 'Session is invalid or expired.';
      } catch (error) {
        this.errorMessage = 'Failed to validate session.';
      }
    },
  },
  beforeUnmount() {
    if (this.timer) clearInterval(this.timer); // 컴포넌트 파괴 시 타이머 정리
  },
};
</script>

<style>
.error {
  color: red;
  margin-top: 10px;
}
</style>
