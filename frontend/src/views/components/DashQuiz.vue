<template>
    <div class="quiz-status border-radius-lg h-100">
        <!-- 퀴즈를 풀지 않은 경우 -->
        <div v-if="!hasCompletedQuiz">
            <div
                class="quiz-content"
                :style="{
                    backGroundIm: 'url(' + require('@/assets/img/quizbg.png') + ')',
                    backgroundSize: 'cover',
                    cursor: 'pointer',
                }"
            >
                <p class="quiz-prompt">아직 퀴즈를 풀지 않으셨군요!</p>
                <p class="quiz-info">우리는 친환경 행동에 대해 얼마나 잘 알고 있을까요?</p>
                <p class="quiz-info">매일 퀴즈를 풀고 이율과 상식을 높여보세요.</p>
            </div>
            <button @click="openQuizModal" class="btn btn-primary">퀴즈 풀기</button>
        </div>

        <!-- 퀴즈를 완료한 경우 -->
        <div v-else>
            <p>오늘의 일일 퀘스트를 완료했습니다!</p>
        </div>

        <!-- 퀴즈 모달 -->
        <teleport to="body">
            <div v-if="showQuizModal" class="quiz-modal">
                <quiz @close="closeQuizModal" />
            </div>
        </teleport>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
// import Swal from 'sweetalert2';
import quizApi from '@/api/quizApi';
import memberApi from '@/api/memberApi';
import Quiz from '../Quiz.vue';

const hasCompletedQuiz = ref(false); // 퀴즈 완료 상태
const showQuizModal = ref(false); // 퀴즈 모달 상태
const memberNo = ref(null); // 동적으로 가져올 memberNo

// 회원 정보 조회 및 퀴즈 완료 여부 확인
const fetchQuizStatus = async () => {
    try {
        // 회원 정보 조회
        const memberData = await memberApi.getMember();
        memberNo.value = memberData?.memberNo;

        if (!memberNo.value) {
            throw new Error('회원 번호를 가져오지 못했습니다.');
        }

        // 퀴즈 완료 여부 확인
        const response = await quizApi.checkQuizCompletion(memberNo.value);
        hasCompletedQuiz.value = response?.data?.completed || false;
    } catch (error) {
        console.error('Error fetching quiz status:', error);
        // Swal.fire('에러', '퀴즈 상태를 확인하지 못했습니다.', 'error');
    }
};

// 모달 열기
const openQuizModal = () => {
    showQuizModal.value = true;
};

// 모달 닫기
const closeQuizModal = () => {
    showQuizModal.value = false;
};

// DashQuiz 초기화
onMounted(() => {
    fetchQuizStatus();
});
</script>

<style scoped>
.quiz-status {
    text-align: center;
    padding: 20px;
    display: flex;
    flex-direction: column;
    justify-content: space-between; /* 버튼을 아래로 내리기 위해 추가 */
    height: 100%; /* 부모 높이에 맞게 조정 */
    background-image: url('@/assets/img/quizbg.png'); /* 배경 이미지 설정 */
    background-size: cover; /* 컴포넌트 크기에 꽉 차게 설정 */
    background-position: center; /* 배경 이미지를 중앙 정렬 */
    background-repeat: no-repeat; /* 이미지 반복 방지 */
    color: #fff; /* 텍스트 색상을 흰색으로 변경 */
}

.quiz-content {
    margin-bottom: 20px; /* 버튼과 콘텐츠 사이 간격 추가 */
}

.quiz-prompt {
    font-size: 23px;
    font-weight: bold;
    margin-top: 20px;
    margin-bottom: 25px;
    color: #4caf50;
}

.quiz-info {
    font-size: 15px;
    font-weight: bold;
    margin-bottom: 5px;
    color: white;
}

button {
    padding: 10px 20px;
    color: white;
    background-color: #4caf50;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    margin-top: 30px; /* 추가 간격 */
    margin-bottom: 60px;
}

button:disabled {
    background-color: #ccc;
    cursor: not-allowed;
}

.quiz-modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}
</style>
