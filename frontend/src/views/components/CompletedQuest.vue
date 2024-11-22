<script setup>
import flatpickr from 'flatpickr';
import 'flatpickr/dist/flatpickr.min.css';
import moment from 'moment';
import { ref, onMounted } from 'vue';
import questApi from '@/api/achieveQuestApi';
// import Swal from 'sweetalert2';

// 완료된 퀘스트 상태
const quests = ref([]); // API로부터 가져올 데이터
const filteredQuests = ref([]); // 필터링된 데이터
const selectedDateRange = ref([]); // 선택된 날짜 범위

// 샘플 퀘스트 데이터
// const quests = ref([
//     { questNo: 1, questType: 0, questContent: '일간 퀘스트 1', questPoint: 10, completedDate: '2024-11-20' },
//     { questNo: 2, questType: 1, questContent: '주간 퀘스트 1', questPoint: 20, completedDate: '2024-11-21' },
//     { questNo: 3, questType: 2, questContent: '월간 퀘스트 1', questPoint: 30, completedDate: '2024-11-22' },
//     { questNo: 4, questType: 0, questContent: '일간 퀘스트 2', questPoint: 15, completedDate: '2024-11-21' },
//     { questNo: 5, questType: 1, questContent: '주간 퀘스트 2', questPoint: 25, completedDate: '2024-11-23' },
// ]);

//퀘스트 타입 이미지 맵
const questTypeImages = {
    0: require('@/assets/img/dailyQuest.png'), // 일간 퀘스트 이미지
    1: require('@/assets/img/weeklyQuest.png'), // 주간 퀘스트 이미지
    2: require('@/assets/img/monthlyQuest.png'), // 월간 퀘스트 이미지
    default: require('@/assets/img/team-5.jpg'), // 기본 이미지
};

//questApi에서 전체 완료된 퀘스트 불러오기
const loadQuests = async () => {
    try {
        const data = await questApi.getTotalAchieve();
        console.log("data : ", data);
        quests.value = data; // 전체 퀘스트 저장
        filteredQuests.value = data; //기본적으로 모든 데이터 표시
    } catch (error) {
        console.error('퀘스트 로드 실패:', error);
        // Swal.fire('에러', '완료된 퀘스트 데이터를 불러오는 데 실패했습니다.', 'error');
    }
};

//moment로 날짜 필터링
const applyFilters = () => {
    if (!selectedDateRange.value.length) {
        filteredQuests.value = quests.value; // 선택된 날짜가 없으면 모든 퀘스트 표시
        return;
    }

    const [startDate, endDate] = selectedDateRange.value.map((date) => moment(date));
    filteredQuests.value = quests.value.filter((quest) => {
        const completedDate = moment(quest.completedDate);
        return completedDate.isBetween(startDate, endDate, undefined, '[]'); // 선택한 범위 내 날짜만 포함
    });
};

const initDateRangePicker = () => {
    flatpickr('#date-range-picker', {
        mode: 'range', // 범위 선택 모드
        dateFormat: 'Y-m-d',
        onChange: (selectedDates) => {
            selectedDateRange.value = selectedDates.map((date) => moment(date).format('YYYY-MM-DD'));
            applyFilters();
        },
        onDayCreate: (dObj, dStr, fp, dayElem) => {
            const today = moment().format('YYYY-MM-DD'); // 오늘 날짜
            const dayDate = moment(dayElem.dateObj).format('YYYY-MM-DD');

            // 오늘 날짜 스타일 적용
            if (dayDate === today) {
                dayElem.classList.add('today'); // 기본 flatpickr 클래스 활용
            }
        },
    });
};

onMounted(() => {
    loadQuests();
    initDateRangePicker();
});
</script>

<template>
    <div class="card">
        <div class="card-header pb-0 d-flex justify-content-between align-items-center">
            <h6>완료된 퀘스트</h6>
            <!-- 범위 선택 달력 -->
            <div>
                <input id="date-range-picker" type="text" placeholder="날짜 범위를 선택하세요" class="form-control" />
            </div>
        </div>
        <div class="card-body px-0 pt-0 pb-2">
            <div class="table-responsive p-0">
                <table class="table align-items-center mb-0">
                    <thead>
                        <tr>
                            <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">퀘스트</th>
                            <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">타입</th>
                            <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">포인트</th>
                            <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">완료일</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="quest in filteredQuests" :key="quest.questNo">
                            <td>
                                <div class="d-flex px-2 py-1">
                                    <div>
                                        <!-- 퀘스트 종류에 따른 이미지 -->
                                        <img :src="questTypeImages[quest.questType] || questTypeImages.default" class="avatar avatar-sm me-3" alt="quest" />
                                    </div>
                                    <div class="d-flex flex-column justify-content-center">
                                        <!-- 퀘스트 내용 -->
                                        <h6 class="mb-0 text-sm">{{ quest.questContent }}</h6>
                                    </div>
                                </div>
                            </td>
                            <td>
                                <p class="text-xs font-weight-bold mb-0 text-secondary">
                                    {{ quest.questType === 0 ? '일간' : quest.questType === 1 ? '주간' : '월간' }}
                                </p>
                            </td>
                            <td class="align-middle text-center">
                                <span class="text-secondary text-xs font-weight-bold">{{ quest.questPoint }}P</span>
                            </td>
                            <td class="align-middle text-center text-sm">
                                <span class="text-secondary text-xs font-weight-bold">{{ quest.completedDate }}</span>
                            </td>
                        </tr>
                        <tr v-if="filteredQuests.length === 0">
                            <td colspan="4" class="text-center">해당 조건에 완료된 퀘스트가 없습니다.</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</template>

<style>
/* 테이블 및 폼 스타일링 */
.form-label {
    margin-right: 10px;
}

/* 달력 스타일 */
#date-range-picker {
    max-width: 300px;
    margin-bottom: 15px;
}

/* 오늘 날짜 스타일 */
.flatpickr-day.today:not(.flatpickr-disabled) {
    background-color: #40a578 !important; /* 초록색 배경 */
    color: white !important; /* 흰색 글자 */
    border-radius: 50% !important; /* 원형 */
    font-weight: bold;
}

/* 선택된 날짜 스타일 */
.flatpickr-day.selected:not(.flatpickr-disabled) {
    background-color: #3085d6 !important; /* 파란색 배경 */
    color: white !important; /* 흰색 글자 */
    border-radius: 50%; /* 원형 */
}
</style>
