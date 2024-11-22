import api from '@/api';

const BASE_URL = '/card';

export default {
    async getCount() {
        // { withCredentials: true } : session을 넘기지 않고, 쿠키가 자동으로 포함되도록 요청
        const response = await api.get(`${BASE_URL}/count`);
        console.log('find Account');
        return response.data;
    },
};
