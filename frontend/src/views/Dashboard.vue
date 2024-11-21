<script setup>
import MiniStatisticsCard from '@/examples/Cards/MiniStatisticsCard.vue';
import GradientLineChart from '@/examples/Charts/GradientLineChart.vue';
import Carousel from './components/Carousel.vue';
// import CategoriesList from './components/CategoriesList.vue';

import TodayQuest from './components/TodayQuest.vue';
import accountApi from '@/api/accountApi';
import dailyInterestApi from '@/api/dailyInterestApi';
import { ref, onMounted, computed } from 'vue';

const now = new Date(); // 현재 시각
const hours = now.getHours(); // 시 (0 ~ 23)
const minutes = now.getMinutes(); // 분 (0 ~ 59)

const accountObject = ref({
  accountBalance: 0, // 기본값
  accountRate: 0, // 기본값
});
const myAccount = computed(() => accountObject.value);
//const formattedInterest = ref(0);
//const myBalance = computed(() => myAccount.value.accountBalance);
const myRate = computed(() => myAccount.value.accountRate);

// const monthlyInterestObject = ref({});
// const monthlyInterest = computed(() => monthlyInterestObject.value);
// const monthlyInterestMonthListObject = ref({});
// const monthlyInterestMonthList = computed(() => monthlyInterestMonthListObject.value);
const dailyMonthlyInterestObject = ref([]);


// 이자 초기값 설정
const myTotalInterest = computed(
  () =>
    myAccount.value.accountBalance * (myAccount.value.accountRate / 100 / 365)
);
const myCurrentInterest = computed(
  () =>
    myAccount.value.accountBalance *
    (myAccount.value.accountRate / 100 / 365 / (24 * 60 * 60)) *
    ((hours * 60 + minutes) * 60)
);
const myInterest = computed(
  () =>
    myAccount.value.accountBalance *
    (myAccount.value.accountRate / 100 / 365 / (24 * 60 * 60))
);
const countInterest = ref(0);


// 매초마다 이자금을 더함
const startInterestCount = () => {
  const interval = setInterval(() => {
    if (countInterest.value < myTotalInterest.value) {
      countInterest.value += myInterest.value;
    } else {
      clearInterval(interval); // 카운트가 myTotalInterest에 도달하면 타이머 종료
    }
  }, 1000); // 1000ms(1초)마다 실행
};



const tips = [
    { 
        tip: "물 절약 실천",
        description: "양치컵 사용하기로 하루 12L 절약!"
    },
    {
        tip: "전기 절약",
        description: "사용하지 않는 콘센트 뽑기로 대기전력 차단"
    },
    {
        tip: "수도꼭지 절수",
        description: "절수형 수도꼭지로 교체하면 30% 절약"
    },
    {
        tip: "냉장고 관리",
        description: "냉장실 60%, 냉동실 70% 채우기가 최적"
    },
    {
        tip: "빨래 모아하기",
        description: "세탁물 한번에 모아서 하면 물 80L 절약"
    },
    {
        tip: "친환경 장바구니",
        description: "비닐봉지 대신 장바구니 사용하기"
    },
    {
        tip: "LED 전구 교체",
        description: "일반 전구보다 최대 90% 전기 절약"
    },
    {
        tip: "적정 온도 유지",
        description: "여름 26도, 겨울 20도가 최적 온도"
    },
    {
        tip: "물 재활용하기",
        description: "세탁 헹굼물로 화장실 청소하기"
    },
    {
        tip: "친환경 이동",
        description: "가까운 거리는 자전거나 도보 이용하기"
    }
];

const getRandomTip = () => {
    const today = new Date().setHours(0, 0, 0, 0);
    const seed = today % tips.length;
    return tips[seed];
};

const dailyTip = ref(getRandomTip());


// 차트 데이터
const chartData = ref({
  labels: [],
  datasets: [
    {
      label: "월별 이자",
      data: []
    }
  ]
});

const updateChartData = async () => {
  try {
    const monthlyData = await dailyInterestApi.getMonthly();
    console.log('Monthly Data:', monthlyData);

    // 각 월별 최신 데이터만 사용
    const monthlyMap = new Map();
    monthlyData.forEach(item => {
      const month = new Date(item.todayDate).format("YYYY-MM-DD");
      // 같은 월의 데이터가 있으면 항상 새로운 데이터로 덮어씀
      monthlyMap.set(month, item.monthlyInterest);
    });

    // 데이터 배열로 변환
    const months = Array.from(monthlyMap.keys()).sort((a, b) => a - b);
    const amounts = months.map(month => monthlyMap.get(month));
    const labelsss = months.map(month => `${month + 1}월`);

    console.log('Mapped months:', months);
    console.log('Mapped amounts:', amounts);
    console.log('Mapped labels:', labelsss);

    // 차트 데이터 업데이트
    chartData.value = {
      labels: labelsss,
      datasets: [
        {
          label: "월별 이자",
          data: amounts,
        }
      ]
    };
    
  } catch (error) {
    console.error('Error updating chart data:', error);
  }
};

const load = async () => {
  try {
    const accountData = await accountApi.findAccount();
    accountObject.value = accountData;
    console.log('accountObject: ', accountObject.value);
    console.log('accountObject.accountBalance: ', myAccount.value.accountRate);

    // 데이터 로드 후 countInterest를 초기화
    countInterest.value = myCurrentInterest.value;
    //formattedInterest.value = `+${(1234.5678).toFixed(4)}원`;

    const dailyMonthlyInterestData = await dailyInterestApi.getMonthly();
    dailyMonthlyInterestObject.value = dailyMonthlyInterestData;
    console.log('dailyMonthlyInterestObject: ', dailyMonthlyInterestObject.value);
  } catch (error) {
    console.error('Error finding account: ', error);
  }
};

onMounted(() => {
    load();
    startInterestCount();
    dailyTip.value = getRandomTip();
    updateChartData();
    console.log('chartData: ', chartData);
});
</script>
<template>
    <div class="py-4 container-fluid">
        <div class="row">
            <div class="col-lg-12">
                <div class="row">
                    <div class="col-lg-3 col-md-6 col-12">
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
                    <div class="col-lg-3 col-md-6 col-12">
                        <mini-statistics-card
                            title="찜한 퀘스트"
                            value="n개"
                            description="회원님이 찜한 퀘스트가 기다리고 있어요!"
                            :icon="{
                                component: 'ni ni-favourite-28',
                                background: 'bg-gradient-danger',
                                shape: 'rounded-circle',
                            }"
                        />
                    </div>
                    <div class="col-lg-3 col-md-6 col-12">
                        <mini-statistics-card
                            title="금일 벌어들인 이자"
                            :value="`+${countInterest.toFixed(4)}원`"
                            :description="`<span class='text-md font-weight-bolder text-danger'>+${myRate}%</span>  &nbsp  퀘스트로 더 높은 이율을 받아보세요!`"
                            :icon="{
                                component: 'ni ni-paper-diploma',
                                background: 'bg-gradient-success',
                                shape: 'rounded-circle',
                            }"
                        />
                    </div>
                    <!-- 가장 많이 완료한 퀘스트 알림 -->
                    <div class="col-lg-3 col-md-6 col-12">
                        <mini-statistics-card
                            title="김예은 님은"
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
                <div class="row">
                    <div class="col-lg-7 mb-lg">
                        <!-- line chart -->
                        <div class="card z-index-2">
                            <gradient-line-chart
                                id="chart-line"
                                title="Sales Overview"
                                description="<i class='fa fa-arrow-up text-success'></i>
      <span class='font-weight-bold'>4% more</span> in 2021"
                                :chart="{
                                    labels: ['Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
                                    datasets: [
                                        {
                                            label: 'Mobile Apps',
                                            data: [247, 231, 248, 240, 249, 233, 243, 244, 236,245, 237, 246],
                                        },
                                    ],
                                }"
                            />
                        </div>
                    </div>
                    <div class="col-lg-5">
                        <carousel />
                    </div>
                </div>
                <div class="row mt-4">
                    <div class="col-12 mb-lg-0 mb-4">
                        <div class="card">
                            <today-quest />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
