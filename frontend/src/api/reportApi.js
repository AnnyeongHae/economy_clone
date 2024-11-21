import api from "@/api";

export default {
  // 리포트용 회원 데이터 조회
  async getMemberReport() {
    const { data } = await api.post("/member/report");
    return data;
  },

  // 리포트 데이터 조회
  async getReport(requestData) {
    const { data } = await api.post(
      "http://localhost:8080/api/report",
      requestData
    );
    return data;
  },

  // 퀘스트 상세 조회
  async getQuestDetails(requestData) {
    const { data } = await api.post(
      "http://localhost:8080/api/report/details",
      requestData
    );
    return data;
  },
};
