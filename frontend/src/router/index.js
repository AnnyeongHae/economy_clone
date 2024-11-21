import { createRouter, createWebHistory } from 'vue-router';
import Dashboard from '../views/Dashboard.vue';
import Tables from '../views/Tables.vue';
import Billing from '../views/Billing.vue';
import Signup from '../views/Signup.vue';
import Signin from '../views/Signin.vue';
import Mypage from '../views/Mypage.vue';
import Quest from '../views/Quest.vue';
import Interest from '../views/Interest.vue';
import Report from '../views/Report.vue';
import About from '../views/About.vue';
import Community from '../views/Community.vue';
import Quiz from '../views/Quiz.vue';
import Vision from '../views/OurVision.vue';
import QrCode from '../views/QrCode.vue';
import QrSession from '../views/QrSession.vue';

const routes = [
  {
    path: '/',
    name: '/',
    redirect: '/dashboard-default',
  },
  {
    path: '/dashboard-default',
    name: 'Dashboard',
    component: Dashboard,
  },
  {
    path: '/tables',
    name: 'Tables',
    component: Tables,
  },
  {
    path: '/billing',
    name: 'Billing',
    component: Billing,
  },

  {
    path: '/signin',
    name: 'Signin',
    component: Signin,
  },
  {
    path: '/signup',
    name: 'Signup',
    component: Signup,
  },
  {
    path: '/mypage',
    name: 'Mypage',
    component: Mypage,
  },
  {
    path: '/quest',
    name: 'Quest',
    component: Quest,
  },
  {
    path: '/interest',
    name: 'Interest',
    component: Interest,
  },
  {
    path: '/report',
    name: 'Report',
    component: Report,
  },
  {
    path: '/about',
    name: 'About',
    component: About,
  },
  {
    path: '/community',
    name: 'Community',
    component: Community,
  },
  {
    path: '/quiz',
    name: 'quiz',
    component: Quiz,
  },
  {
    path: '/ourvision',
    name: 'OurVision',
    component: Vision,
  },
  {
    path: '/qrcode',
    name: 'qrcode',
    component: QrCode, // Vue 컴포넌트로 설정
  },
  {
    path: '/qrsession',
    name: 'qrsession',
    component: QrSession, // Vue 컴포넌트로 설정
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
  linkActiveClass: 'active',
});

export default router;
