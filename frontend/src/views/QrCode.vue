<template>
  <div>
    <h1>QR Code Generator</h1>
    <button @click="generateQrCode">
      Generate QR Code
    </button>
    <div v-if="qrCodeUrl">
      <img :src="qrCodeUrl" alt="Generated QR Code" />
    </div>
    <p v-if="errorMessage" class="error">
      {{ errorMessage }}
    </p>
  </div>
</template>

<script>
import qrApi from '@/api/qrApi.js';

export default {
  data() {
    return {
      qrCodeUrl: null, // QR 코드 이미지 URL
      errorMessage: null, // 에러 메시지
    };
  },
  methods: {
    async generateQrCode() {
      try {
        this.errorMessage = null; // 에러 초기화
        const sessionId = 'example-session-id'; // 예제 SessionID
        const qrData = await qrApi.generateQrCode({
          sessionId,
        });

        // Blob으로 변환하여 URL 생성
        const blob = new Blob([qrData], {
          type: 'image/png',
        });
        this.qrCodeUrl = URL.createObjectURL(blob);
      } catch (error) {
        this.errorMessage =
          'Failed to generate QR Code. Please try again.';
      }
    },
  },
};
</script>

<style>
.error {
  color: red;
  margin-top: 10px;
}
</style>
