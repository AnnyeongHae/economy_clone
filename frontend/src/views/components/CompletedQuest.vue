<script setup>
import moment from 'moment';
import { ref, computed } from 'vue';

// 샘플 퀘스트 데이터
const quests = ref([
    { questNo: 1, questType: 0, questContent: '일간 퀘스트 1', questPoint: 10, completedDate: '2024-11-20' },
    { questNo: 2, questType: 1, questContent: '주간 퀘스트 1', questPoint: 20, completedDate: '2024-11-21' },
    { questNo: 3, questType: 2, questContent: '월간 퀘스트 1', questPoint: 30, completedDate: '2024-11-22' },
    { questNo: 4, questType: 0, questContent: '일간 퀘스트 2', questPoint: 15, completedDate: '2024-11-21' },
    { questNo: 5, questType: 1, questContent: '주간 퀘스트 2', questPoint: 25, completedDate: '2024-11-23' },
]);

const questTypeImages = {
    0: require('@/assets/img/team-2.jpg'), // 일간 퀘스트 이미지
    1: require('@/assets/img/team-3.jpg'), // 주간 퀘스트 이미지
    2: require('@/assets/img/team-4.jpg'), // 월간 퀘스트 이미지
    default: require('@/assets/img/team-5.jpg'), // 기본 이미지
};

// 날짜 상태 관리
const startDate = ref(''); // 필터 시작 날짜
const endDate = ref(''); // 필터 종료 날짜

// 완료된 퀘스트 필터링
const filteredQuests = computed(() => {
    if (!startDate.value && !endDate.value) {
        // 시작/종료 날짜가 없으면 모든 퀘스트 표시
        return quests.value;
    }
    return quests.value.filter((quest) => {
        const completedDate = moment(quest.completedDate);
        const isAfterStartDate = startDate.value ? completedDate.isSameOrAfter(moment(startDate.value)) : true;
        const isBeforeEndDate = endDate.value ? completedDate.isSameOrBefore(moment(endDate.value)) : true;
        return isAfterStartDate && isBeforeEndDate;
    });
});
</script>

<template>
    <div class="card">
        <div class="card-header pb-0 d-flex justify-content-between align-items-center">
            <h6>완료된 퀘스트</h6>
            <!-- 날짜 필터 -->
            <div class="d-flex align-items-center">
                <div class="me-2">
                    <label for="start-date" class="form-label">시작 날짜:</label>
                    <input id="start-date" type="date" v-model="startDate" class="form-control" />
                </div>
                <div>
                    <label for="end-date" class="form-label">종료 날짜:</label>
                    <input id="end-date" type="date" v-model="endDate" class="form-control" />
                </div>
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
                            <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7"></th>
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
                                <button class="badge bg-gradient-success border-0">완료</button>
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
</style>
