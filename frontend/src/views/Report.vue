<template>
    <div class="report-container">
        <!-- 필터 섹션 -->
        <div class="filter-section">
            <div class="period-buttons">
                <button
                    v-for="period in periods"
                    :key="period.value"
                    :class="['period-btn', { active: selectedPeriod === period.value }]"
                    :disabled="isLoading"
                    @click="changePeriod(period.value)"
                >
                    {{ period.label }}
                </button>
            </div>

            <div class="date-range">
                <div class="date-input">
                    <input type="date" v-model="customStartDate" class="custom-date" />
                    <span class="date-separator">~</span>
                    <input type="date" v-model="customEndDate" class="custom-date" />
                </div>
                <button @click="applyCustomDateRange" class="apply-date-btn" :disabled="isLoading">기간 적용</button>
            </div>
        </div>

        <!-- 멤버 정보 섹션 -->
        <div class="member-info" v-if="memberData && memberData.length > 0">
            <div class="member-header">
                <div class="member-title">
                    <h1>{{ memberData[0][0] }}님의 환경 리포트</h1>
                    <div class="member-grade">
                        <span class="grade-badge">{{ getGradeText(memberData[0][1]) }}</span>
                        <span class="score-badge">{{ formatCurrency(memberData[0][2]) }}점</span>
                    </div>
                </div>
            </div>

            <div class="environmental-stats">
                <div class="stat-card">
                    <div class="stat-icon">🌱</div>
                    <p>{{ environmentalImpact(memberData[0][2]) }}톤의<br />이산화탄소 감소</p>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">🦋</div>
                    <p>{{ environmentalImpact(memberData[0][2], 'wildlife') }}마리의<br />생물 보호</p>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">🌳</div>
                    <p>{{ environmentalImpact(memberData[0][2], 'forest') }}m²의<br />숲 보존</p>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">🪹</div>
                    <p>{{ environmentalImpact(memberData[0][2], 'nest') }}개의<br />새둥지 보호</p>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">💧</div>
                    <p>{{ environmentalImpact(memberData[0][2], 'water') }}L의<br />물 절약</p>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">🌊</div>
                    <p>{{ environmentalImpact(memberData[0][2], 'plastic') }}g의<br />해양 플라스틱 제거</p>
                </div>
            </div>
        </div>

        <!-- 차트 섹션 -->
        <div class="charts-grid">
            <div class="chart-card">
                <h3>누적 점수 추이</h3>
                <div class="chart-wrapper">
                    <button @click="navigateChart('score', 'prev')" :disabled="!hasMorePrev('score')" class="nav-btn prev">←</button>
                    <LineChart v-if="visibleScoreChartData" :chart-data="visibleScoreChartData" :options="scoreChartOptions" />
                    <button @click="navigateChart('score', 'next')" :disabled="!hasMoreNext('score')" class="nav-btn next">→</button>
                </div>
            </div>

            <div class="chart-card">
                <h3>이자 수익 추이</h3>
                <div class="chart-wrapper">
                    <button @click="navigateChart('interest', 'prev')" :disabled="!hasMorePrev('interest')" class="nav-btn prev">←</button>
                    <LineChart v-if="visibleInterestChartData" :chart-data="visibleInterestChartData" :options="interestChartOptions" />
                    <button @click="navigateChart('interest', 'next')" :disabled="!hasMoreNext('interest')" class="nav-btn next">→</button>
                </div>
            </div>

            <div class="chart-card">
                <h3>퀘스트 달성</h3>
                <div class="chart-wrapper">
                    <button @click="navigateChart('quest', 'prev')" :disabled="!hasMorePrev('quest')" class="nav-btn prev">←</button>
                    <BarChart v-if="visibleQuestChartData" :chart-data="visibleQuestChartData" :options="questChartOptions" />
                    <button @click="navigateChart('quest', 'next')" :disabled="!hasMoreNext('quest')" class="nav-btn next">→</button>
                </div>
            </div>

            <div class="chart-card">
                <h3>계좌 잔액 추이</h3>
                <div class="chart-wrapper">
                    <button @click="navigateChart('balance', 'prev')" :disabled="!hasMorePrev('balance')" class="nav-btn prev">←</button>
                    <LineChart v-if="visibleBalanceChartData" :chart-data="visibleBalanceChartData" :options="balanceChartOptions" />
                    <button @click="navigateChart('balance', 'next')" :disabled="!hasMoreNext('balance')" class="nav-btn next">→</button>
                </div>
            </div>
        </div>

        <!-- 이자 내역 테이블 -->
        <div class="history-section">
            <h3>이자 상세 내역</h3>
            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th>날짜</th>
                            <th>계좌 잔액</th>
                            <th>이자 금액</th>
                            <th>이자율</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="item in interestHistory" :key="item.todayDate" @click="fetchQuestDetails(item.todayDate)" class="table-row">
                            <td>{{ formatDate(item.todayDate) }}</td>
                            <td>{{ formatCurrency(item.todayBalance) }}원</td>
                            <td>{{ formatCurrency(item.todayInterest) }}원</td>
                            <td>{{ formatRate(item.todayRate) }}%</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div class="pagination">
                <button :disabled="currentPage <= 1" @click="loadPreviousPage" class="page-btn">이전</button>
                <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
                <button :disabled="currentPage >= totalPages" @click="loadNextPage" class="page-btn">다음</button>
            </div>
        </div>

        <!-- 퀘스트 상세 모달 -->
        <div v-if="showQuestDetails" class="modal-overlay" @click.self="closeQuestDetails">
            <div class="modal-content">
                <div class="modal-header">
                    <h4>{{ selectedDate ? formatDate(selectedDate) : '' }} 달성 퀘스트</h4>
                    <button @click="closeQuestDetails" class="close-btn">×</button>
                </div>

                <div class="modal-body">
                    <div v-if="questDetails && questDetails.length > 0" class="quest-table">
                        <table>
                            <thead>
                                <tr>
                                    <th>퀘스트명</th>
                                    <th>점수</th>
                                    <th>달성시간</th>
                                    <th>유형</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="(quest, index) in questDetails" :key="index">
                                    <td>{{ quest.questName || '정보 없음' }}</td>
                                    <td>{{ quest.questPoint || 0 }}점</td>
                                    <td>{{ formatQuestDateTime(quest.achieveDateTime) }}</td>
                                    <td>
                                        <span class="quest-type">{{ getQuestType(quest.questType) }}</span>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    <div v-else class="no-data">
                        <p>해당 날짜에 달성한 퀘스트가 없습니다</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { ref, onMounted, computed, nextTick } from 'vue';
import axios from 'axios';
import { format } from 'date-fns';
import LineChart from '../components/LineChart.vue';
import BarChart from '../components/BarChart.vue';
import api from '@/api';
export default {
    name: 'ReportView',
    components: {
        LineChart,
        BarChart,
    },
    setup() {
        // 상태 관리
        const memberData = ref([]);
        const selectedPeriod = ref('daily');
        const interestChartData = ref(null);
        const questChartData = ref(null);
        const balanceChartData = ref(null);
        const scoreChartData = ref(null);
        const interestHistory = ref([]);
        const isLoading = ref(false);
        const currentPage = ref(1);
        const totalPages = ref(0);
        const dateRangeCache = ref(null);
        const questDetails = ref([]);
        const selectedDate = ref(null);
        const showQuestDetails = ref(false);
        const accountNo = ref(null);
        let currentRequest = null;
        // 차트 디스플레이 상태
        const displayIndices = ref({
            interest: 0,
            quest: 0,
            balance: 0,
            score: 0,
        });
        const displayCount = 7;
        // 커스텀 날짜 범위
        const customStartDate = ref(null);
        const customEndDate = ref(null);
        // 기간 옵션
        const periods = [
            { label: '일간', value: 'daily' },
            { label: '주간', value: 'weekly' },
            { label: '월간', value: 'monthly' },
        ];
        // 차트 옵션
        const scoreChartOptions = {
            responsive: true,
            maintainAspectRatio: false,
            animation: { duration: 0 },
            scales: {
                y: {
                    ticks: {
                        callback: (value) =>
                            `${new Intl.NumberFormat('ko-KR', {
                                maximumFractionDigits: 0,
                            }).format(value)}점`,
                    },
                },
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: (context) => `${new Intl.NumberFormat('ko-KR').format(context.raw)}점`,
                    },
                },
            },
        };
        const interestChartOptions = {
            responsive: true,
            maintainAspectRatio: false,
            animation: { duration: 0 },
            scales: {
                y: {
                    ticks: {
                        callback: (value) =>
                            `${new Intl.NumberFormat('ko-KR', {
                                maximumFractionDigits: 0,
                            }).format(value)}원`,
                    },
                },
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: (context) => `${new Intl.NumberFormat('ko-KR').format(context.raw)}원`,
                    },
                },
            },
        };
        const questChartOptions = {
            responsive: true,
            maintainAspectRatio: false,
            animation: { duration: 0 },
            scales: {
                y: {
                    min: 0,
                    ticks: {
                        stepSize: 1,
                        callback: (value) => `${Math.floor(value)}건`,
                    },
                },
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: (context) => `${Math.floor(context.raw)}건`,
                    },
                },
            },
        };
        const balanceChartOptions = {
            responsive: true,
            maintainAspectRatio: false,
            animation: { duration: 0 },
            scales: {
                y: {
                    ticks: {
                        callback: (value) =>
                            `${new Intl.NumberFormat('ko-KR', {
                                maximumFractionDigits: 0,
                            }).format(value)}원`,
                    },
                },
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: (context) => `${new Intl.NumberFormat('ko-KR').format(context.raw)}원`,
                    },
                },
            },
        };
        // 차트 데이터 처리
        const createVisibleChartData = (chartData, type) => {
            if (!chartData) return null;
            const start = displayIndices.value[type];
            const end = start + displayCount;
            return {
                labels: chartData.labels.slice(start, end),
                datasets: [
                    {
                        ...chartData.datasets[0],
                        data: chartData.datasets[0].data.slice(start, end),
                    },
                ],
            };
        };
        const visibleScoreChartData = computed(() => createVisibleChartData(scoreChartData.value, 'score'));
        const visibleInterestChartData = computed(() => createVisibleChartData(interestChartData.value, 'interest'));
        const visibleQuestChartData = computed(() => createVisibleChartData(questChartData.value, 'quest'));
        const visibleBalanceChartData = computed(() => createVisibleChartData(balanceChartData.value, 'balance'));
        // 차트 네비게이션
        const hasMorePrev = (type) => displayIndices.value[type] > 0;
        const hasMoreNext = (type) => {
            const chartData =
                type === 'score' ? scoreChartData.value : type === 'interest' ? interestChartData.value : type === 'quest' ? questChartData.value : balanceChartData.value;
            return displayIndices.value[type] + displayCount < (chartData?.labels.length || 0);
        };
        const navigateChart = (type, direction) => {
            const currentIndex = displayIndices.value[type];
            const chartData =
                type === 'score' ? scoreChartData.value : type === 'interest' ? interestChartData.value : type === 'quest' ? questChartData.value : balanceChartData.value;
            const maxIndex = (chartData?.labels.length || 0) - displayCount;
            if (direction === 'next' && currentIndex < maxIndex) {
                displayIndices.value[type] = Math.min(currentIndex + displayCount, maxIndex);
            } else if (direction === 'prev' && currentIndex > 0) {
                displayIndices.value[type] = Math.max(currentIndex - displayCount, 0);
            }
        };
        // 데이터 포맷팅
        const formatChartData = (data) => {
            const formatLabel = (date) => {
                if (selectedPeriod.value === 'monthly') {
                    return format(new Date(date), 'M월');
                } else if (selectedPeriod.value === 'weekly') {
                    const weekDate = new Date(date);
                    const month = format(weekDate, 'M');
                    const weekOfMonth = Math.ceil(weekDate.getDate() / 7);
                    return `${month}월 ${weekOfMonth}주`;
                }
                return format(new Date(date), 'MM/dd');
            };
            return {
                labels: data.map((item) => formatLabel(item.date)),
                datasets: [
                    {
                        label: '',
                        data: data.map((item) => item.value),
                        borderColor: '#2196F3',
                        backgroundColor: 'rgba(33, 150, 243, 0.5)',
                        borderWidth: 2,
                        pointRadius: 4,
                        tension: 0.4,
                        fill: false,
                    },
                ],
            };
        };
        // API 호출
        const fetchReportData = async (page = 1) => {
            isLoading.value = true;
            if (currentRequest) {
                currentRequest.cancel('Operation canceled due to new request.');
            }
            const CancelToken = axios.CancelToken;
            const source = CancelToken.source();
            currentRequest = source;
            const dateRange =
                dateRangeCache.value ||
                (customStartDate.value && customEndDate.value
                    ? {
                          startDate: format(new Date(customStartDate.value), 'yyyy-MM-dd'),
                          endDate: format(new Date(customEndDate.value), 'yyyy-MM-dd'),
                      }
                    : getDateRange(selectedPeriod.value));
            try {
                console.log('accountNo/report', accountNo.value);
                const response = await api.post('/report', {
                    period: selectedPeriod.value,
                    page: page - 1,
                    accountNo: accountNo.value,
                    ...dateRange,
                });
                // 데이터 초기화
                scoreChartData.value = null;
                interestChartData.value = null;
                questChartData.value = null;
                balanceChartData.value = null;
                memberData.value = null;
                await nextTick();
                // 데이터 업데이트
                memberData.value = response.data.memberData;
                scoreChartData.value = formatChartData(response.data.scoreData);
                interestChartData.value = formatChartData(response.data.interestData);
                questChartData.value = formatChartData(response.data.questData);
                balanceChartData.value = formatChartData(response.data.balanceData);
                interestHistory.value = response.data.interestHistory;
                // 디스플레이 인덱스 초기화
                Object.keys(displayIndices.value).forEach((key) => {
                    displayIndices.value[key] = 0;
                });
                // 페이지네이션 업데이트
                currentPage.value = response.data.currentPage + 1;
                totalPages.value = response.data.totalPages;
            } catch (error) {
                console.error('Failed to fetch report data:', error);
            } finally {
                isLoading.value = false;
                currentRequest = null;
            }
        };
        // 퀘스트 유형 변환 함수 추가
        const getQuestType = (type) => {
            const types = {
                0: '일일 퀘스트',
                1: '주간 퀘스트',
                2: '월간 퀘스트',
            };
            return types[type] || '기타 퀘스트';
        };
        // 날짜 포맷 함수 수정
        // const formatDate = (date) => {
        //   try {
        //     return format(new Date(date), "yyyy년 MM월 dd일");
        //   } catch (error) {
        //     console.error("Date formatting error:", error);
        //     return date;
        //   }
        // };
        // 날짜 포맷팅 함수 수정
        // setup() 내부에 날짜 포맷팅 함수 추가
        const formatQuestDateTime = (dateString) => {
            if (!dateString) return '-';
            try {
                const date = new Date(dateString);
                return format(date, 'yyyy-MM-dd HH:mm:ss');
            } catch (error) {
                console.error('Date formatting error:', error);
                return dateString || '-';
            }
        };
        const fetchQuestDetails = async (date) => {
            try {
                const formattedDate = format(new Date(date), 'yyyy-MM-dd');
                console.log('Fetching quest details for date:', formattedDate);
                const response = await api.post('/report/details', {
                    accountNo: accountNo.value,
                    date: formattedDate,
                });
                // response.data가 바로 questDetails 배열인 경우를 처리
                if (response.data && Array.isArray(response.data)) {
                    questDetails.value = response.data;
                    selectedDate.value = date;
                    showQuestDetails.value = true;
                    console.log('Received quest details:', questDetails.value);
                    return;
                }
                // questDetails 필드 안에 있는 경우를 처리
                if (response.data?.questDetails && Array.isArray(response.data.questDetails)) {
                    questDetails.value = response.data.questDetails;
                    selectedDate.value = date;
                    showQuestDetails.value = true;
                    console.log('Received quest details from field:', questDetails.value);
                    return;
                }
                // 단일 객체인 경우를 배열로 변환
                if (response.data && !Array.isArray(response.data)) {
                    questDetails.value = [response.data];
                    selectedDate.value = date;
                    showQuestDetails.value = true;
                    console.log('Converted single object to array:', questDetails.value);
                    return;
                }
                // 데이터가 없는 경우
                questDetails.value = [];
                console.warn('No valid quest details data received');
            } catch (error) {
                console.error('Failed to fetch quest details:', error);
                questDetails.value = [];
                alert('퀘스트 정보를 불러오는데 실패했습니다.');
            }
        };
        const closeQuestDetails = () => {
            showQuestDetails.value = false;
            questDetails.value = [];
            selectedDate.value = null;
        };
        // 날짜 범위 계산
        const getDateRange = (period) => {
            const end = new Date();
            let start = new Date();
            switch (period) {
                case 'daily':
                    start.setDate(end.getDate() - 7);
                    break;
                case 'weekly':
                    start.setDate(end.getDate() - 30);
                    break;
                case 'monthly':
                    start.setDate(end.getDate() - 180);
                    break;
            }
            return {
                startDate: format(start, 'yyyy-MM-dd'),
                endDate: format(end, 'yyyy-MM-dd'),
            };
        };
        // 기간 변경
        const changePeriod = (period) => {
            selectedPeriod.value = period;
            if (!dateRangeCache.value) {
                customStartDate.value = null;
                customEndDate.value = null;
            }
            fetchReportData(1);
        };
        // 커스텀 날짜 범위 적용
        const applyCustomDateRange = () => {
            if (customStartDate.value && customEndDate.value) {
                dateRangeCache.value = {
                    startDate: customStartDate.value,
                    endDate: customEndDate.value,
                };
                fetchReportData(1);
            }
        };
        // 페이지 이동
        const loadNextPage = () => {
            if (currentPage.value < totalPages.value) {
                fetchReportData(currentPage.value + 1);
            }
        };
        const loadPreviousPage = () => {
            if (currentPage.value > 1) {
                fetchReportData(currentPage.value - 1);
            }
        };
        // 환경 영향 계산
        const environmentalImpact = (score, type) => {
            const impactPer5Points = {
                co2: 0.01,
                wildlife: 0.002,
                forest: 0.01,
                nest: 0.1,
                water: 0.5,
                plastic: 1,
                recycling: 1,
            };
            const baseImpact = score / 5;
            switch (type) {
                case 'wildlife':
                    return (impactPer5Points.wildlife * baseImpact).toFixed(2);
                case 'forest':
                    return (impactPer5Points.forest * baseImpact).toFixed(2);
                case 'nest':
                    return (impactPer5Points.nest * baseImpact).toFixed(2);
                case 'water':
                    return (impactPer5Points.water * baseImpact).toFixed(2);
                case 'plastic':
                    return (impactPer5Points.plastic * baseImpact).toFixed(2);
                case 'recycling':
                    return (impactPer5Points.recycling * baseImpact).toFixed(2);
                default:
                    return (impactPer5Points.co2 * baseImpact).toFixed(2);
            }
        };
        // 컴포넌트 마운트 시 데이터 로드
        onMounted(async () => {
            try {
                const response = await api.post('/member/report');
                console.log('response:', response);
                if (typeof response.data === 'number') {
                    accountNo.value = response.data;
                    console.log('accountNo:', accountNo.value);
                    // 기존의 초기 데이터 로드 함수들을 여기서 호출
                    await fetchReportData();
                }
            } catch (error) {
                console.error('Error', error);
            }
        });
        // 템플릿에서 사용할 메서드 및 데이터 반환
        return {
            memberData,
            selectedPeriod,
            periods,
            visibleScoreChartData,
            visibleInterestChartData,
            visibleQuestChartData,
            visibleBalanceChartData,
            scoreChartOptions,
            interestChartOptions,
            questChartOptions,
            balanceChartOptions,
            interestHistory,
            questDetails,
            selectedDate,
            showQuestDetails,
            isLoading,
            currentPage,
            totalPages,
            customStartDate,
            customEndDate,
            getQuestType,
            formatQuestDateTime,
            closeQuestDetails,
            loadNextPage,
            loadPreviousPage,
            changePeriod,
            applyCustomDateRange,
            navigateChart,
            hasMorePrev,
            hasMoreNext,
            fetchQuestDetails,
            environmentalImpact,
            getGradeText: (grade) => {
                const gradeMap = {
                    1: '새싹',
                    2: '어린 나무',
                    3: '작은 숲',
                    4: '울창한 숲',
                    5: '열대우림',
                };
                return gradeMap[grade] || '알 수 없음';
            },
            formatDate: (date) => format(new Date(date), 'yyyy-MM-dd'),
            formatDateTime: (date) => format(new Date(date), 'yyyy-MM-dd HH:mm:ss'),
            formatCurrency: (value) => new Intl.NumberFormat('ko-KR').format(value),
            formatRate: (value) => value.toFixed(2),
        };
    },
};
</script>

<style scoped>
.report-container {
    min-height: 100vh;
    background: #f8fafc;
    padding: 2rem;
    color: #334155;
}

/* 멤버 정보 섹션 */
.member-info {
    background: white;
    border-radius: 1rem;
    padding: 2rem;
    box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
    margin-bottom: 2rem;
}

.member-header {
    margin-bottom: 2rem;
}

.member-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 1rem;
}

.member-title h1 {
    font-size: 1.875rem;
    font-weight: 700;
    color: #0f172a;
    margin: 0;
}

.member-grade {
    display: flex;
    gap: 0.75rem;
}

.grade-badge,
.score-badge {
    padding: 0.5rem 1rem;
    border-radius: 9999px;
    font-weight: 600;
    font-size: 0.875rem;
}

.grade-badge {
    background: #10b981;
    color: white;
}

.score-badge {
    background: #f0fdf4;
    color: #10b981;
    border: 1px solid #10b981;
}

.environmental-stats {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 1.5rem;
}

.stat-card {
    background: #f8fafc;
    border-radius: 0.75rem;
    padding: 1.5rem;
    text-align: center;
    transition: transform 0.2s;
}

.stat-card:hover {
    transform: translateY(-4px);
}

.stat-icon {
    font-size: 2rem;
    margin-bottom: 0.75rem;
}

.stat-card p {
    margin: 0;
    font-size: 0.9375rem;
    line-height: 1.5;
    color: #475569;
}

/* 필터 섹션 */
.filter-section {
    background: white;
    border-radius: 1rem;
    padding: 1.5rem;
    margin-bottom: 2rem;
    box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 1rem;
}

.period-buttons {
    display: flex;
    gap: 0.75rem;
}

.period-btn {
    padding: 0.625rem 1.25rem;
    border-radius: 0.5rem;
    font-weight: 600;
    font-size: 0.875rem;
    border: 1px solid #e2e8f0;
    background: transparent;
    color: #64748b;
    cursor: pointer;
    transition: all 0.2s;
}

.period-btn:hover:not(:disabled) {
    background: #f1f5f9;
    color: #0f172a;
}

.period-btn.active {
    background: #10b981;
    color: white;
    border-color: #10b981;
}

.date-range {
    display: flex;
    gap: 1rem;
    align-items: center;
}

.date-input {
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.custom-date {
    padding: 0.5rem;
    border: 1px solid #e2e8f0;
    border-radius: 0.375rem;
    font-size: 0.875rem;
    color: #475569;
}

.date-separator {
    color: #94a3b8;
}

.apply-date-btn {
    padding: 0.625rem 1.25rem;
    background: #10b981;
    color: white;
    border: none;
    border-radius: 0.5rem;
    font-weight: 600;
    font-size: 0.875rem;
    cursor: pointer;
    transition: background-color 0.2s;
}

.apply-date-btn:hover:not(:disabled) {
    background: #10b981;
}

/* 차트 섹션 */
.charts-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
    gap: 1.5rem;
    margin-bottom: 2rem;
}

.chart-card {
    background: white;
    border-radius: 1rem;
    padding: 1.5rem;
    box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
}

.chart-card h3 {
    margin: 0 0 1.5rem 0;
    font-size: 1.125rem;
    font-weight: 600;
    color: #0f172a;
}

/* 차트 컨테이너 스타일 */
.chart-wrapper {
    position: relative;
    display: flex;
    align-items: center;
    gap: 1rem;
    height: 300px;
}

.nav-btn {
    padding: 0.5rem;
    border: none;
    background: #f8fafc;
    color: #64748b;
    border-radius: 9999px;
    width: 2rem;
    height: 2rem;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.2s;
    z-index: 10;
}

.nav-btn:hover:not(:disabled) {
    background: #e2e8f0;
    color: #0f172a;
}

.nav-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

/* 이자 내역 섹션 */
.history-section {
    background: white;
    border-radius: 1rem;
    padding: 1.5rem;
    box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
}

.history-section h3 {
    margin: 0 0 1.5rem 0;
    font-size: 1.125rem;
    font-weight: 600;
    color: #0f172a;
}

.table-wrapper {
    overflow-x: auto;
    margin-bottom: 1.5rem;
}

table {
    width: 100%;
    border-collapse: separate;
    border-spacing: 0;
}

th {
    background: #f8fafc;
    padding: 1rem;
    text-align: left;
    font-weight: 600;
    color: #475569;
    border-bottom: 2px solid #e2e8f0;
    white-space: nowrap;
}

td {
    padding: 1rem;
    border-bottom: 1px solid #e2e8f0;
    color: #334155;
}

.table-row {
    cursor: pointer;
    transition: background-color 0.2s;
}

.table-row:hover {
    background: #f8fafc;
}

/* 페이지네이션 */
.pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 1rem;
    margin-top: 1.5rem;
}

.page-btn {
    padding: 0.5rem 1rem;
    border: 1px solid #e2e8f0;
    border-radius: 0.5rem;
    background: white;
    color: #64748b;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
    background: #f1f5f9;
    color: #0f172a;
}

.page-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.page-info {
    color: #64748b;
    font-weight: 500;
}

/* 모달 스타일 */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 50;
}

.modal-content {
    background: white;
    border-radius: 1rem;
    width: 90%;
    max-width: 800px;
    max-height: 90vh;
    overflow-y: auto;
}

.modal-header {
    padding: 1.5rem;
    border-bottom: 1px solid #e2e8f0;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.modal-header h4 {
    margin: 0;
    font-size: 1.25rem;
    font-weight: 600;
    color: #0f172a;
}

.close-btn {
    background: none;
    border: none;
    font-size: 1.5rem;
    color: #64748b;
    cursor: pointer;
    padding: 0.5rem;
    transition: color 0.2s;
}

.close-btn:hover {
    color: #0f172a;
}

.modal-body {
    padding: 1.5rem;
}

.quest-type {
    display: inline-block;
    padding: 0.25rem 0.75rem;
    border-radius: 9999px;
    background: #f0fdf4;
    color: #10b981;
    font-size: 0.875rem;
    font-weight: 500;
}

.no-data {
    text-align: center;
    padding: 3rem 0;
    color: #64748b;
}

/* 반응형 스타일 */
@media (max-width: 768px) {
    .report-container {
        padding: 1rem;
    }

    .member-title {
        flex-direction: column;
        align-items: flex-start;
    }

    .filter-section {
        flex-direction: column;
        align-items: stretch;
    }

    .period-buttons {
        justify-content: center;
    }

    .date-range {
        flex-direction: column;
    }

    .date-input {
        width: 100%;
    }

    .custom-date {
        flex: 1;
    }

    .charts-grid {
        grid-template-columns: 1fr;
    }

    .modal-content {
        width: 95%;
        margin: 1rem;
    }
}

/* 애니메이션 */
@keyframes slideIn {
    from {
        opacity: 0;
        transform: translateY(1rem);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.chart-card,
.member-info,
.history-section {
    animation: slideIn 0.3s ease-out;
}

/* 로딩 상태 */
[disabled] {
    opacity: 0.6;
    cursor: not-allowed;
}
</style>
