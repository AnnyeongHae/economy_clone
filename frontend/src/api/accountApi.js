import api from '@/api';

const BASE_URL1 = '/account'; // http://localohst:8080/api/account

export default {
  async findAccount() {
    // { withCredentials: true } : session을 넘기지 않고, 쿠키가 자동으로 포함되도록 요청
    const { data } = await api.get(`${BASE_URL1}/find`);
    console.log('find Account');
    return data;
  },

  async deposit(amount) {
    const { data } = await api.post(`${BASE_URL1}/deposit`, amount);
    console.log('deposit');
    return data;
  },

  async withdraw(amount) {
    const { data } = await api.post(`${BASE_URL1}/withdraw`, amount);
    console.log('withdraw');
    return data;
  },
};
