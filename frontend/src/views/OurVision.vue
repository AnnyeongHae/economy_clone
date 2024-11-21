<script setup></script>

<template>
  <div class="background-container">
    <!-- 페이지 헤더 -->
    <header class="header-section">
      <h1>2030 세대를 위한 탄소 발자국 관리 서비스</h1>
      <p>환경 보호와 경제적 이득을 함께 추구하는 새로운 방식</p>
    </header>

    <!-- 프로젝트 배경 섹션 -->
    <section class="content-section">
      <h2>프로젝트 배경</h2>
      <p>
        현재 많은 기업들이 탄소 배출을 관리하기 위한 시스템을 도입하고 있지만,
        개인 차원에서의 탄소 발자국 관리는 상대적으로 덜 주목받고 있습니다.
        특히, 2030 청년층은 환경 문제에 관심이 있음에도 불구하고 시간 부족,
        명확한 보상 시스템의 부재, 실천의 직접적 결과가 눈에 보이지 않기 때문에
        실천으로 이어지지 않는 경우가 많습니다.
      </p>
      <p>
        따라서 청년들이 경제적 혜택을 쉽게 얻으면서, 자신이 실천한 친환경 행동의
        효과를 눈으로 확인할 수 있는 시스템의 필요성을 체감하였습니다. 저희는
        이런 청년층을 대상으로 그린카드와 파킹 통장을 결합한 새로운 솔루션을
        통해 지속 가능한 소비를 실천하면서 동시에 경제적 보상을 받을 수 있도록
        서비스를 기획하였습니다.
      </p>
    </section>

    <!-- 프로젝트 목표 섹션 -->
    <section class="content-section">
      <h2>프로젝트 목표</h2>
      <ul>
        <li>
          <strong>탄소 발자국 관리 시스템 제공:</strong> 개인 맞춤형 리포트를
          통해 탄소 절감 행동을 촉진합니다.
        </li>
        <li>
          <strong>친환경 챌린지를 통한 경제적 인센티브 제공:</strong> 그린카드와
          파킹 통장을 연계하여 경제적 보상을 제공합니다.
        </li>
        <li>
          <strong>커뮤니티 기반 실천 독려:</strong> 친구들과 함께 실천할 수 있는
          친환경 미션을 제공합니다.
        </li>
      </ul>
    </section>

    <!-- 서비스 제안 섹션 -->
    <section class="content-section">
      <h2>KB의 새로운 솔루션</h2>
      <p>
        KB는 기업을 위한 탄소관리시스템만 운영하고 있지만, 우리는 개인을
        대상으로 한 금융 상품을 통해 탄소 발자국 관리에 참여할 수 있는 기회를
        제공합니다. 저축과 함께 환경 보호 미션을 수행하면 경제적 혜택과 환경
        개선의 성과를 함께 누릴 수 있습니다.
      </p>
    </section>

    <!-- 서비스 특징 섹션 -->
    <section class="features-section">
      <h2>서비스의 주요 특징</h2>
      <ul>
        <li>
          <strong>경제적 혜택:</strong> 파킹통장처럼 저축액이 늘어나는 것을
          실시간으로 확인 가능
        </li>
        <li>
          <strong>환경 보호 미션:</strong> 일상 속 작은 실천으로 탄소 배출량
          감소
        </li>
        <li>
          <strong>커뮤니티:</strong> 다른 사용자들과 함께 목표를 달성하며 상호
          격려
        </li>
        <li>
          <strong>결과의 시각화:</strong> 실천 결과를 직관적으로 확인 가능
        </li>
      </ul>
    </section>

    <!-- 기대 효과 섹션 -->
    <section class="content-section">
      <h2>기대 효과</h2>
      <ul>
        <li>탄소 절감 실천 촉진 및 경제적 보상 제공</li>
        <li>KB 금융 브랜드 이미지 제고</li>
        <li>친환경 실천의 사회적 확산</li>
        <li>
          소비 패턴과 탄소 배출량 데이터 분석을 통한 맞춤형 금융 서비스 제공
        </li>
      </ul>
    </section>

    <!-- Call to Action -->
    <footer class="footer-section">
      <h2>탄소 발자국 관리, 지금 시작하세요!</h2>
      <router-link to="/signup" class="cta-button">지금 가입하기</router-link>
    </footer>
  </div>
</template>

<style scoped>
/* 공통 스타일 */
.background-container {
  font-family: 'Roboto', Arial, sans-serif;
  line-height: 1.6;
  color: #333;
  margin: 0;
  padding: 0;
}

/* 헤더 스타일 */
.header-section {
  text-align: center;
  padding: 4rem 1rem;
}

.header-section h1 {
  font-size: 2.8rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

.header-section p {
  font-size: 1.3rem;
  margin: 0;
}

/* 콘텐츠 섹션 */
.content-section {
  padding: 3rem 10%;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.content-section h2 {
  font-size: 2.2rem;
  color: #4caf50;
  margin-bottom: 1.5rem;
  font-weight: bold;
}

.content-section p {
  font-size: 1.1rem;
  margin-bottom: 1.5rem;
}

.content-section ul {
  list-style-type: disc;
  padding-left: 1.5rem;
}

.content-section li {
  margin-bottom: 1rem;
}

/* 특징 섹션 */
.features-section {
  padding: 3rem 10%;
  /* background-color: #e8f5e9; */
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.features-section h2 {
  text-align: left;
  font-size: 2.2rem;
  color: #4caf50;
  margin-bottom: 1.5rem;
  font-weight: bold;
}

.features-section ul {
  list-style: none;
  padding-left: 0;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.features-section li {
  flex: 0 1 45%;
  margin-bottom: 1.5rem;
  padding-left: 2rem;
  position: relative;
  font-size: 1.1rem;
}

.features-section li::before {
  content: '✔';
  position: absolute;
  left: 0;
  color: #4caf50;
  font-size: 1.5rem;
}

/* Call to Action */
.footer-section {
  background-color: #f9f9f9;
  text-align: center;
  padding: 4rem 1rem;
  /* border-top: 5px solid #4caf50; */
}

.footer-section h2 {
  font-size: 2.2rem;
  margin-bottom: 1rem;
}

.cta-button {
  display: inline-block;
  padding: 1rem 2.5rem;
  background: linear-gradient(135deg, #4caf50, #81c784);
  color: white;
  text-decoration: none;
  border-radius: 50px;
  font-size: 1.3rem;
  transition:
    transform 0.3s ease,
    background-color 0.3s ease;
}

.cta-button:hover {
  background-color: #388e3c;
  transform: scale(1.05);
}
</style>
