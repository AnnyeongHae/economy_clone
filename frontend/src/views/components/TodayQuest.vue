<script setup>
import { ref, onUnmounted, onMounted, computed } from 'vue';
// import { useRoute, useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import jsQR from 'jsqr';
import todayQuestApi from '@/api/todayQuestApi';
import memberApi from '@/api/memberApi';

const quests = ref([]);
const selectedQuestType = ref(null);

const videoRef = ref(null);
const canvasRef = ref(null);
const streaming = ref(false);
const currentQuestNo = ref(null);

// 샘플 퀘스트 데이터
// const quests = ref([
//     { questNo: 1, questType: 0, questContent: '일간 퀘스트 1', questPoint: 10 },
//     { questNo: 2, questType: 1, questContent: '주간 퀘스트 1', questPoint: 20 },
//     { questNo: 3, questType: 2, questContent: '월간 퀘스트 1', questPoint: 30 },
//     { questNo: 4, questType: 0, questContent: '일간 퀘스트 2', questPoint: 15 },
//     { questNo: 5, questType: 1, questContent: '주간 퀘스트 2', questPoint: 25 },
// ]);

//퀘스트 타입 이미지 맵
const questTypeImages = {
    1: require('@/assets/img/weeklyQuest.png'), // 주간 퀘스트 이미지
    2: require('@/assets/img/monthlyQuest.png'), // 월간 퀘스트 이미지
    default: require('@/assets/img/grade0.png'), // 기본 이미지
};

// 퀘스트 조회
const fetchQuests = async (type) => {
    try {
        let data;
        if (type === null) {
            // 전체 퀘스트
            data = await todayQuestApi.getTotalToday();
        } else if (type === 1) {
            // 주간 퀘스트
            data = await todayQuestApi.getWeeklyToday();
        } else if (type === 2) {
            // 월간 퀘스트
            data = await todayQuestApi.getMonthlyToday();
        }
        quests.value = data; // 데이터를 quests에 저장
    } catch (error) {
        console.error('퀘스트 데이터를 가져오는 중 오류 발생:', error);
        Swal.fire('에러', '오늘의 퀘스트 데이터를 불러오는 데 실패했습니다.', 'error');
    }
};

// 선택된 타입에 따른 퀘스트 필터링
const filteredQuests = computed(() => quests.value);

// 퀘스트 타입 변경 및 데이터 로드
const toggleQuestType = async (type) => {
    selectedQuestType.value = type; // 선택된 타입 업데이트
    await fetchQuests(type); // API 호출
};

// 거래내역으로 퀘스트 인증
// const processQuestAchieve = async(memberNo) => {
//     try {
//         const response = await todayQuestApi.processQuestAchievements(memberNo);

//         if(response === 'success'){
//             Swal.fire({
//                 title : 퀘스트 인증 완료!
//             })
//         }
//     }
// }

const startQrScanner = async (questNo) => {
    try {
        streaming.value = true;
        currentQuestNo.value = questNo;

        // 모바일과 데스크톱 모두 지원하는 설정
        const constraints = {
            video: {
                facingMode: 'environment',
                width: { min: 640, ideal: 1280, max: 1920 },
                height: { min: 480, ideal: 720, max: 1080 },
                aspectRatio: { ideal: 1.7777777778 },
            },
        };

        const stream = await navigator.mediaDevices.getUserMedia(constraints);
        if (videoRef.value) {
            videoRef.value.srcObject = stream;
            await videoRef.value.play();
            requestAnimationFrame(scanQRCode); // 즉시 스캔 시작
        }
    } catch (error) {
        console.error('Camera error:', error);
        Swal.fire('에러', '카메라 접근에 실패했습니다.', 'error');
        streaming.value = false;
    }
};

const scanQRCode = () => {
    if (!streaming.value || !videoRef.value || !canvasRef.value) return;

    const video = videoRef.value;
    const canvas = canvasRef.value;
    const context = canvas.getContext('2d', { willReadFrequently: true });

    // 비디오가 준비되지 않았으면 다시 시도
    if (video.readyState !== video.HAVE_ENOUGH_DATA) {
        requestAnimationFrame(scanQRCode);
        return;
    }

    // 캔버스 크기를 비디오 크기에 맞춤
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;

    try {
        // 비디오 프레임을 캔버스에 그림
        context.drawImage(video, 0, 0, canvas.width, canvas.height);
        const imageData = context.getImageData(0, 0, canvas.width, canvas.height);

        // QR 코드 스캔 시도
        const code = jsQR(imageData.data, imageData.width, imageData.height, {
            inversionAttempts: 'dontInvert',
        });

        if (code) {
            // QR 코드 발견
            stopQrScanner();
            verifyQrCode(code.data, currentQuestNo.value);
        } else {
            // 계속 스캔
            requestAnimationFrame(scanQRCode);
        }
    } catch (error) {
        console.error('Scanning error:', error);
        requestAnimationFrame(scanQRCode);
    }
};

const verifyQrCode = async (qrData, questNo) => {
    try {
        console.log('Scanned QR code:', qrData); // 디버깅용
        console.log('Quest No:', questNo);

        const response = await fetch(`/api/qr/verify/${qrData}/${questNo}`, {
            method: 'POST',
            credentials: 'include',
        });

        if (response.ok) {
            Swal.fire('성공', 'QR 코드 인증이 완료되었습니다.', 'success');
        } else {
            const error = await response.text();
            Swal.fire('실패', error || 'QR 코드 인증에 실패했습니다.', 'error');
        }
    } catch (error) {
        console.error('Verification error:', error);
        Swal.fire('에러', '서버 연결에 실패했습니다.', 'error');
    }
};

const stopQrScanner = () => {
    if (videoRef.value?.srcObject) {
        videoRef.value.srcObject.getTracks().forEach((track) => track.stop());
    }
    streaming.value = false;
};

const handleQuestAchieve = async (questContent, questNo, isQr) => {
    try {
        const result = await Swal.fire({
            title: `${questContent} 인증`,
            text: '인증하시겠습니까?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: '확인',
            cancelButtonText: '취소',
            customClass: {
                confirmButton: 'swal-confirm-btn', // 커스텀 클래스 이름 변경
                cancelButton: 'swal-cancel-btn', // 커스텀 클래스 이름 변경
            },
        });

        if (result.isConfirmed) {
            if (isQr) {
                await startQrScanner(questNo); // QR 인증 진행
            } else {
                console.log(`퀘스트 ${questNo} 거래내역 인증 진행`);

                // 회원 정보 조회
                const memberData = await memberApi.getMember();
                const memberNo = memberData?.memberNo; // 회원 ID 추출

                if (!memberNo) {
                    Swal.fire('에러', '로그인 정보를 확인할 수 없습니다.', 'error');
                    return;
                }

                // 거래내역 인증 요청
                const response = await todayQuestApi.processQuestAchievements(memberNo);

                // 서버 응답 확인
                if (response === 'success') {
                    Swal.fire('성공', '퀘스트 인증이 성공적으로 완료되었습니다.', 'success');
                } else {
                    Swal.fire('알림', '완료된 퀘스트가 없습니다.', 'info');
                }
            }
        }
    } catch (error) {
        console.error('인증 오류:', error);
        Swal.fire('에러', '처리 중 오류가 발생했습니다.', 'error');
    }
};

// 카메라 테스트 시 코드
// const handleQuestAchieve = async (questContent) => {
//     try {
//     const result = await Swal.fire({
//       title: `${questContent} 인증`,
//       text: '인증하시겠습니까?',
//       icon: 'question',
//       showCancelButton: true,
//       confirmButtonText: '확인',
//       cancelButtonText: '취소',
//       customClass: {
//                 confirmButton: 'swal-confirm-btn', // 커스텀 클래스 이름 변경
//                 cancelButton: 'swal-cancel-btn', // 커스텀 클래스 이름 변경
//             }
//     });

//     if (result.isConfirmed) {
//       await startQrScanner();
//     }
//   } catch (error) {
//     console.error('Quest error:', error);
//     Swal.fire('에러', '처리 중 오류가 발생했습니다.', 'error');
//   }
// };

onMounted(() => {
    fetchQuests(null);
});

onUnmounted(() => {
    stopQrScanner();
});
</script>

<template>
    <div class="card">
        <div class="card-header pb-0 d-flex justify-content-between align-items-center">
            <h6>오늘의 퀘스트</h6>
            <!-- 퀘스트 타입 필터링 버튼 -->
            <div class="btn-group">
                <button
                    class="btn btn-outline-secondary btn-xs py-1 px-3 custom-hover"
                    :class="{ 'btn-success text-white': selectedQuestType === null }"
                    @click="toggleQuestType(null)"
                >
                    전체
                </button>

                <button class="btn btn-outline-secondary btn-xs py-1 px-3 custom-hover" :class="{ 'btn-success text-white': selectedQuestType === 1 }" @click="toggleQuestType(1)">
                    주간
                </button>
                <button class="btn btn-outline-secondary btn-xs py-1 px-3 custom-hover" :class="{ 'btn-success text-white': selectedQuestType === 2 }" @click="toggleQuestType(2)">
                    월간
                </button>
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
                                <button class="badge bg-gradient-success border-0" @click="handleQuestAchieve(quest.questContent, quest.questNo, quest.isQr)">인증</button>
                            </td>
                        </tr>
                        <tr v-if="filteredQuests.length === 0">
                            <td colspan="4" class="text-center">해당 퀘스트가 없습니다.</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- QR 스캐너 오버레이 -->
    <div v-if="streaming" class="qr-scanner-overlay">
        <div class="qr-scanner-container">
            <video ref="videoRef" autoplay playsinline class="qr-video"></video>
            <canvas ref="canvasRef" class="qr-canvas"></canvas>
            <div class="scanner-guide"></div>
            <div class="scanner-controls">
                <button @click="stopQrScanner" class="scanner-btn">닫기</button>
            </div>
        </div>
    </div>
</template>

<style>
/* 기존 스타일 (테이블, 카드 등) */
.card {
    background-color: var(--bs-card-bg);
    border: 0 solid transparent;
    margin-bottom: 30px;
    box-shadow: 0 0 2rem 0 rgb(136 152 170 / 15%);
    position: relative;
    display: flex;
    flex-direction: column;
    min-width: 0;
    word-wrap: break-word;
    background-clip: border-box;
    border-radius: 0.75rem;
}

.card-header {
    padding: 1.5rem;
    margin-bottom: 0;
    background-color: transparent;
    border-bottom: 0 solid transparent;
}

.card-body {
    flex: 1 1 auto;
    padding: var(--bs-card-spacer-y) var(--bs-card-spacer-x);
}

.table-responsive {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
}

.table {
    margin-bottom: 0;
}

.avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
}

/* QR 스캐너 관련 새로운 스타일 */
.qr-scanner-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.9);
    z-index: 1000;
    display: flex;
    justify-content: center;
    align-items: center;
}

.qr-scanner-container {
    position: relative;
    width: 100%;
    max-width: 500px;
    aspect-ratio: 4/3;
    margin: 20px;
}

.qr-video {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 12px;
}

.qr-canvas {
    display: none;
}

.scanner-guide {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 200px;
    height: 200px;
    border: 2px solid #40a578;
    border-radius: 20px;
    box-shadow: 0 0 0 100vmax rgba(0, 0, 0, 0.5);
}

.scanner-controls {
    position: absolute;
    bottom: -60px;
    left: 0;
    right: 0;
    display: flex;
    justify-content: center;
}

.scanner-btn {
    background: #40a578;
    color: white;
    border: none;
    padding: 12px 24px;
    border-radius: 8px;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.scanner-btn:hover {
    background: #338a63;
}

.close-scanner {
    position: absolute;
    top: 10px;
    right: 10px;
    background: white;
    border: none;
    padding: 5px 10px;
    border-radius: 5px;
    cursor: pointer;
}

/* 기본 상태를 유지하며 호버 시 효과 적용 */
.custom-hover:hover {
    background-color: #ffffff !important; /* 흰 배경 */
    color: #40a578 !important; /* success 색상 */
    border-color: #40a578 !important; /* 테두리 색 변경 */
    transition: all 0.3s ease; /* 부드러운 애니메이션 */
}

/* 클릭된 상태 유지 */
.btn-success.text-white {
    background-color: #40a578 !important; /* 배경 초록색 */
    color: #ffffff !important; /* 흰 글자색 */
    border-color: #40a578 !important; /* 테두리 초록색 */
}

/* SweetAlert2 확인 버튼 */
.swal-confirm-btn {
    background: linear-gradient(90deg, #40a578, #3085d6); /* 그라디언트 색상 */
    color: white;
    font-size: 14px;
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.3s ease;
    margin-right: 10px; /* 버튼 간격 */
}

.swal-confirm-btn:hover {
    background: linear-gradient(90deg, #3085d6, #40a578); /* 호버 시 색상 반전 */
    opacity: 0.9;
}

/* SweetAlert2 취소 버튼 */
.swal-cancel-btn {
    background: linear-gradient(90deg, #d33, #b52c2c); /* 그라디언트 색상 */
    color: white;
    font-size: 14px;
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.3s ease;
}

.swal-cancel-btn:hover {
    background: linear-gradient(90deg, #b52c2c, #d33); /* 호버 시 색상 반전 */
    opacity: 0.9;
}
</style>
