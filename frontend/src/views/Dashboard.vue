<script setup>
import DashQuiz from './components/DashQuiz.vue';
import MiniStatisticsCard from '@/examples/Cards/MiniStatisticsCard.vue';
import Carousel from './components/Carousel.vue';
import TodayQuest from './components/TodayQuest.vue';
import accountApi from '@/api/accountApi';
import memberApi from '@/api/memberApi';
import { ref, onMounted, computed } from 'vue';
import InterestChart from '@/examples/Charts/InterestChart.vue';

const now = new Date();
const hours = now.getHours();
const minutes = now.getMinutes();

// 계정 관련 상태
const accountObject = ref({
    accountBalance: 0,
    accountRate: 0,
});
const myAccount = computed(() => accountObject.value);

// 회원 관련 상태
const memberObject = ref({
    memberName: '',
});
const myMember = computed(() => memberObject.value);

const myRate = computed(() => myAccount.value.accountRate);

// 이자 계산 관련
const myTotalInterest = computed(() => myAccount.value.accountBalance * (myAccount.value.accountRate / 100 / 365));
const myCurrentInterest = computed(() => myAccount.value.accountBalance * (myAccount.value.accountRate / 100 / 365 / (24 * 60 * 60)) * ((hours * 60 + minutes) * 60));
const myInterest = computed(() => myAccount.value.accountBalance * (myAccount.value.accountRate / 100 / 365 / (24 * 60 * 60)));
const countInterest = ref(0);

// 이자 카운트 시작
const startInterestCount = () => {
    const interval = setInterval(() => {
        if (countInterest.value < myTotalInterest.value) {
            countInterest.value += myInterest.value;
        } else {
            clearInterval(interval);
        }
    }, 1000);
};

// 팁 관련
const tips = [
    {
        tip: '물 절약 실천',
        description: '양치컵 사용하기로 하루 12L 절약!',
    },
    {
        tip: '전기 절약',
        description: '사용하지 않는 콘센트 뽑기로 대기전력 차단',
    },
    {
        tip: '수도꼭지 절수',
        description: '절수형 수도꼭지로 교체하면 30% 절약',
    },
    {
        tip: '냉장고 관리',
        description: '냉장실 60%, 냉동실 70% 채우기가 최적',
    },
    {
        tip: '빨래 모아하기',
        description: '세탁물 한번에 모아서 하면 물 80L 절약',
    },
    {
        tip: '친환경 장바구니',
        description: '비닐봉지 대신 장바구니 사용하기',
    },
    {
        tip: 'LED 전구 교체',
        description: '일반 전구보다 최대 90% 전기 절약',
    },
    {
        tip: '적정 온도 유지',
        description: '여름 26도, 겨울 20도가 최적 온도',
    },
    {
        tip: '물 재활용하기',
        description: '세탁 헹굼물로 화장실 청소하기',
    },
    {
        tip: '친환경 이동',
        description: '가까운 거리는 자전거나 도보 이용하기',
    },
];

const getRandomTip = () => {
    const today = new Date().setHours(0, 0, 0, 0);
    const seed = today % tips.length;
    return tips[seed];
};

const dailyTip = ref(getRandomTip());

// 데이터 로드
const load = async () => {
    try {
        // 계정 정보 로드
        const accountData = await accountApi.findAccount();
        accountObject.value = accountData;
        console.log('accountObject: ', accountObject.value);
        console.log('accountObject.accountBalance: ', myAccount.value.accountRate);

        // 회원 정보 로드
        const memberData = await memberApi.getMember();
        memberObject.value = {
            memberName: memberData.name || memberData.memberName,
        };
        console.log('memberObject: ', memberObject.value);
        console.log('member name: ', myMember.value.memberName);

        // 이자 초기화
        countInterest.value = myCurrentInterest.value;
    } catch (error) {
        console.error('Error loading data: ', error);
    }
};

onMounted(() => {
    load();
    startInterestCount();
    dailyTip.value = getRandomTip();
});
</script>

<template>
    <div class="py-4 container-fluid">
        <div class="row">
            <div class="col-lg-12">
                <!-- 상단 카드들 -->
                <div class="row">
                    <div class="col-lg-4 col-md-6 col-12">
                        <mini-statistics-card
                            title="오늘의 절약 Tip!"
                            :value="dailyTip.tip"
                            :description="dailyTip.description"
                            :icon="{
                                component: 'ni ni-bulb-61',
                                background: 'bg-gradient-success',
                                shape: 'rounded-circle',
                            }"
                        />
                    </div>
                    <div class="col-lg-4 col-md-6 col-12">
                        <mini-statistics-card
                            title="금일 벌어들인 이자"
                            :value="`+${countInterest.toFixed(4)}원`"
                            :description="`<span class='text-md font-weight-bolder text-danger'>현재 이자율 +${myRate}%</span> 퀘스트로 더 높은 이율을 받아보세요!`"
                            :icon="{
                                component: 'ni ni-paper-diploma',
                                background: 'bg-gradient-success',
                                shape: 'rounded-circle',
                            }"
                        />
                    </div>
                    <div class="col-lg-4 col-md-6 col-12">
                        <mini-statistics-card
                            :title="myMember.memberName ? `${myMember.memberName} 님은` : '회원님은'"
                            value="수도세 절약"
                            description="을(를) 가장 많이 수행하셨습니다!"
                            :icon="{
                                component: 'ni ni-cart',
                                background: 'bg-gradient-success',
                                shape: 'rounded-circle',
                            }"
                        />
                    </div>
                </div>

                <!-- 중단 차트와 캐러셀 -->
                <div class="row" style="margin-top: 25px">
                    <div class="col-lg-5 col-md-">
                        <div class="card z-index-2 chart-card">
                            <dash-quiz />
                        </div>
                    </div>
                    <div class="col-lg-7">
                        <carousel class="h-100" />
                    </div>
                </div>

                <!-- 하단 카드들 -->
                <div class="row mt-4">
                    <div class="col-lg-6">
                        <div class="card h-100" style="margin-bottom: 0">
                            <InterestChart />
                        </div>
                    </div>
                    <div class="col-lg-6">
                        <div class="card h-100" style="margin-bottom: 0">
                            <today-quest />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.card {
    height: 100%;
}

/* 특정 차트 카드에만 높이 100% 적용 */
.chart-card {
    height: 100%;
}

/* carousel 컴포넌트가 부모 높이를 상속받도록 설정 */
:deep(.carousel) {
    height: 100%;
}

/* 차트 컨테이너가 카드 높이를 채우도록 설정 */
:deep(.chart-container) {
    height: 100% !important;
}
</style>
