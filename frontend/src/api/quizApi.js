import api from '@/api';

const BASE_URL = '/quiz';

export default {
  // 오늘의 퀴즈 데이터 가져오기
  getDailyQuiz() {
    return api.get(`${BASE_URL}/daily`);
  },

  // 퀴즈 옵션 가져오기
  getQuizOptions(quizPk) {
    return api.get(`${BASE_URL}/options/${quizPk}`);
  },

  // 퀴즈 완료 여부 확인
  checkQuizCompletion(memberNo) {
    return api.get(`${BASE_URL}/check-completion`, {
      params: { memberNo },
    });
  },

  // 답안 제출
  submitQuizAnswer(payload) {
    return api.post(`${BASE_URL}/submit`, payload, {
      headers: {
        'Content-Type': 'application/json',
      },
    });
  },
};
