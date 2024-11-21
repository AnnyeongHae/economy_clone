<script setup>
import { ref, onMounted } from 'vue';
import dailyInterestApi from '@/api/dailyInterestApi'; // API 호출 모듈 import

// 상태 관리
const totalInterest = ref(''); // 오늘까지 받은 총 이자
const selectedYear = ref(new Date().getFullYear()); // 기본값: 현재 연도
const interestHistory = ref({}); // 연도별 이자 데이터
const filteredData = ref([]); // 선택된 연도의 필터링된 데이터

// 월별 데이터를 연도별로 그룹화
const formatMonthlyData = (monthlyData) => {
  return monthlyData.reduce((acc, item) => {
    const year = new Date(item.todayDate).getFullYear(); // API 응답의 날짜 필드에서 연도 추출
    const month = new Date(item.todayDate).getMonth() + 1; // 월 추출 (0부터 시작하므로 +1)

    if (!acc[year]) acc[year] = []; // 해당 연도가 없으면 초기화
    acc[year].push({ month, amount: item.monthlyInterest }); // 월과 이자 금액 추가

    return acc;
  }, {});
};

// 선택된 연도의 이자 내역 필터링
const filterInterestData = () => {
  filteredData.value = interestHistory.value[selectedYear.value] || [];
};

// 데이터 가져오기
const fetchInterestData = async () => {
  try {
    // API 호출: 총 이자와 월별 데이터를 병렬로 가져오기
    const [total, monthlyData] = await Promise.all([
      dailyInterestApi.getTotal(), // 총 이자 API 호출
      dailyInterestApi.getMonthly(), // 월별 이자 API 호출
    ]);

    // 데이터 설정
    totalInterest.value = total; // 총 이자
    interestHistory.value = formatMonthlyData(monthlyData); // 연도별 데이터로 정리

    // 초기 필터링 실행
    filterInterestData();
  } catch (error) {
    console.error('이자 데이터를 가져오는 중 오류 발생:', error);
  }
};

// 컴포넌트 로드시 데이터 가져오기
onMounted(() => {
  fetchInterestData();
});
</script>

<template>
  <div class="interest-history">
    <!-- 상단 이자 정보 -->
    <div class="highlight-section text-center">
      <p class="subtitle">오늘까지 받은 이자 <span class="emoji">🌟</span></p>
      <h1 class="total-amount">{{ totalInterest }}원</h1>
    </div>

    <!-- 연도 선택 캘린더 -->
    <div class="year-selector text-center">
      <label for="year-select">연도 선택:</label>
      <input
        id="year-select"
        type="number"
        v-model="selectedYear"
        @change="dailyMonthlyInterestObject"
        min="2000"
        max="2024"
        placeholder="연도를 입력하세요"
      />
    </div>

    <!-- 테이블 영역 -->
    <div class="interest-table-container">
      <h3 class="year-title">{{ selectedYear }}년</h3>
      <table class="interest-table" v-if="dailyMonthlyInterestObject.length > 0">
        <thead>
          <tr>
            <th>월</th>
            <th>이자 금액</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(entry, index) in dailyMonthlyInterestObject" :key="index">
            <td>{{ entry.todayDate.slice(5, 7) }}월</td>
            <td>{{ entry.monthlyInterest +  entry.todayInterest}}원</td>
          </tr>
        </tbody>
      </table>
      <p v-else class="empty-message">이자 내역이 없습니다.</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import dailyInterestApi from '@/api/dailyInterestApi';

// 상태 변수
const dailyMonthlyInterestObject = ref([]);
//const myDailyMonthlyInterest = computed(() => dailyMonthlyInterestObject.value);


// const totalInterest = ref(0); // 오늘까지 받은 총 이자
// const selectedYear = ref(new Date().getFullYear()); // 기본값: 현재 연도
// const interestHistory = ref({}); // 연도별 이자 데이터
// const filteredData = ref([]); // 선택된 연도의 필터링된 데이터

// // 월별 데이터를 연도별로 그룹화
// const formatMonthlyData = (monthlyData) => {
//   return monthlyData.reduce((acc, item) => {
//     const year = new Date(item.date).getFullYear(); // 연도 추출
//     const month = new Date(item.date).getMonth() + 1; // 월 추출 (0부터 시작하므로 +1)

//     if (!acc[year]) acc[year] = []; // 해당 연도가 없으면 초기화
//     acc[year].push({ month, amount: item.amount });

//     return acc;
//   }, {});
// };

// // 선택된 연도의 이자 내역 필터링
// const filterInterestData = () => {
//   filteredData.value = interestHistory.value[selectedYear.value] || [];
// };

// // API 호출: 누적 이자 및 월별 내역 가져오기
// const fetchInterestData = async () => {
//   try {
//     const [total, monthlyData] = await Promise.all([
//       dailyInterestApi.getTotal(),
//       dailyInterestApi.getMonthly(),
//     ]);

//     // 데이터 설정
//     totalInterest.value = total; // 총 이자
//     interestHistory.value = formatMonthlyData(monthlyData); // 연도별 데이터 정리

//     // 초기 필터링
//     filterInterestData();
//   } catch (error) {
//     console.error('Error fetching interest data:', error);
//   }
// };

// // 컴포넌트 로드시 데이터 가져오기
// onMounted(async () => {
//   await fetchInterestData();
// });

// // selectedYear 변경 시 필터링된 데이터 업데이트
// watch(selectedYear, () => {
//   filterInterestData();
// });

const load = async () => {
  try {
    const dailyMonthlyInterestData = await dailyInterestApi.getMonthly();
    dailyMonthlyInterestObject.value = dailyMonthlyInterestData;
    console.log('dailyMonthlyInterestObject: ', dailyMonthlyInterestObject.value);
    //console.log('myDailyTotalInterest: ', myDailyMonthlyInterest);
  } catch (error) {
    console.error('Error finding account: ', error);
  }
};

load();
</script>


<style scoped>
/* 전체 컨테이너 */
.interest-history {
  font-family: 'Arial', sans-serif;
  margin: 0 auto;
  max-width: 900px;
  padding: 20px;
}

/* 상단 이자 정보 */
.highlight-section {
  color: #333; /* 텍스트 색상 */
  margin-bottom: 30px;
}

.subtitle {
  font-size: 1.2rem;
  margin-bottom: 10px;
}
.emoji {
  font-size: 1.5rem;
}
.total-amount {
  font-size: 2.8rem;
  font-weight: bold;
  margin: 0;
}

/* 연도 선택 캘린더 */
.year-selector {
  margin-bottom: 20px;
}
.year-selector label {
  font-size: 1rem;
  margin-right: 10px;
}
.year-selector input {
  font-size: 1rem;
  padding: 5px 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

/* 테이블 컨테이너 */
.interest-table-container {
  background-color: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

/* 연도 제목 */
.year-title {
  font-size: 1.8rem;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

/* 테이블 스타일 */
.interest-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

.interest-table thead {
  background-color: #40a578; /* 헤더 배경색 */
  color: white;
}

.interest-table th,
.interest-table td {
  padding: 12px 15px;
  border-bottom: 1px solid #ddd;
  font-size: 1rem;
}

.interest-table tr:nth-child(even) {
  background-color: #f9f9f9; /* 짝수 행 색상 */
}

.interest-table tr:hover {
  background-color: #f1f1f1; /* 행 Hover 효과 */
}

/* 비어있는 메시지 스타일 */
.empty-message {
  text-align: center;
  font-size: 1.2rem;
  color: #999;
  margin: 20px 0;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .total-amount {
    font-size: 2rem;
  }
  .interest-table th,
  .interest-table td {
    font-size: 0.9rem;
  }
}
</style>
