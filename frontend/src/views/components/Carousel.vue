<template>
    <div class="card card-carousel overflow-hidden h-100 p-0">
        <div id="carouselExampleCaptions" class="carousel slide h-100" data-bs-ride="carousel">
            <div class="carousel-inner border-radius-lg h-100">
                <div class="carousel-item h-100 active">
                    <router-link
                        to="/about"
                        class="h-100 w-100 d-block"
                        :style="{
                            backgroundImage: 'url(' + require('@/assets/img/그린카드1.png') + ')',
                            backgroundPosition: 'center',
                            backgroundSize: '95% auto',
                            backgroundRepeat: 'no-repeat',
                            cursor: 'pointer',
                        }"
                    ></router-link>
                </div>
                <div class="carousel-item h-100">
                    <router-link
                        to="/about"
                        class="h-100 w-100 d-block"
                        :style="{
                            backgroundImage: 'url(' + require('@/assets/img/그린카드2.png') + ')',
                            backgroundPosition: 'center',
                            backgroundSize: '95% auto',
                            backgroundRepeat: 'no-repeat',
                            cursor: 'pointer',
                        }"
                    ></router-link>
                </div>
                <div class="carousel-item h-100">
                    <router-link
                        to="/about"
                        class="h-100 w-100 d-block"
                        :style="{
                            backgroundImage: 'url(' + require('@/assets/img/그린카드3.png') + ')',
                            backgroundPosition: 'center',
                            backgroundSize: '95% auto',
                            backgroundRepeat: 'no-repeat',
                            cursor: 'pointer',
                        }"
                    ></router-link>
                </div>
            </div>
            <button class="carousel-control-prev w-5 me-3" type="button" data-bs-target="#carouselExampleCaptions" data-bs-slide="prev">
                <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                <span class="visually-hidden">Previous</span>
            </button>
            <button class="carousel-control-next w-5 me-3" type="button" data-bs-target="#carouselExampleCaptions" data-bs-slide="next">
                <span class="carousel-control-next-icon" aria-hidden="true"></span>
                <span class="visually-hidden">Next</span>
            </button>
        </div>
    </div>
</template>

<script setup>
import { onMounted } from 'vue';
import * as bootstrap from 'bootstrap';

onMounted(() => {
    new bootstrap.Carousel(document.getElementById('carouselExampleCaptions'), {
        interval: 3000, // 3초마다
        wrap: true     // 무한 반복
    });
});
</script>

<style scoped>
.card-carousel {
    background-color: #ffffff;
}

.carousel-image {
    display: flex;
    align-items: center;
    justify-content: center;
}

/* 버튼 위치 조정 */
:deep(.carousel-control-prev),
:deep(.carousel-control-next) {
    width: 5%;
    margin: 0;
}

@media (max-width: 768px) {
    .carousel-image {
        background-size: contain !important;
    }
}
</style>