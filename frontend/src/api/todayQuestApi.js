import api from '@/api';
const BASE_URL = '/quest';

export default {
    // 진행중인 퀘스트 api
    // 전체 퀘스트 조회
    async getTotalToday() {
        const { data } = await api.get(`${BASE_URL}/active`);
        return data;
    },

    // 일일 퀘스트 조회
    async getDailyToday() {
        const { data } = await api.get(`${BASE_URL}/active/daily`);
        return data;
    },

    // 주간 퀘스트 조회
    async getWeeklyToday() {
        const { data } = await api.get(`${BASE_URL}/active/weekly`);
        return data;
    },

    // 월간 퀘스트 조회
    async getMonthlyToday() {
        const { data } = await api.get(`${BASE_URL}/active/monthly`);
        return data;
    },

    //거래내역으로 인증
    async processQuestAchievements(memberNo) {
        const { data } = await api.post(`${BASE_URL}/process`, memberNo);
        return data;
    },
};
