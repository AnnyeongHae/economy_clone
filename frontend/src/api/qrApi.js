import api from '@/api';

const BASE_URL = '/qr'; // Spring Boot API 서버 URL

export default {
  // QR 코드 생성
  async generateQrCode(params) {
    try {
      const { data } = await api.get(
        `${BASE_URL}/generate`,
        {
          params,
          responseType: 'arraybuffer', // 바이너리 데이터를 받기 위함
        }
      );
      return data; // 바이너리 데이터 반환
    } catch (error) {
      console.error('Failed to generate QR Code:', error);
      throw error; // 에러를 상위로 전달
    }
  },
  // Session 생성 API 호출
  async createSession() {
    try {
      const { data } = await api.post('/session/create');
      console.log('QR Session Key:', data);
      return data; // QR 세션 키
    } catch (error) {
      console.error(
        'Failed to create QR session:',
        error.response.data
      );
    }
  },
  async validateSession(sessionKey) {
    try {
      const { data } = await api.get('/session/validate', {
        params: { sessionKey },
      });
      console.log('Session is valid:', data);
      return data; // true 또는 false
    } catch (error) {
      console.error(
        'Failed to validate QR session:',
        error.response.data
      );
    }
  },
  async getSessionTimeout(sessionKey) {
    try {
      const { data } = await api.get('/session/timeout', {
        params: { sessionKey },
      });
      return data; // 남은 시간 (초)
    } catch (error) {
      console.error(
        'Failed to fetch session timeout:',
        error.response.data
      );
      throw error;
    }
  },
};
