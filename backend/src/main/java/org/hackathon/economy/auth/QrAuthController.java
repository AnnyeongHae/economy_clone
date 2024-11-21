package org.hackathon.economy.auth.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.hackathon.economy.auth.service.QrAuthService;
import org.hackathon.economy.member.domain.Member;
import org.hackathon.economy.member.service.AuthenticationService;
import org.hackathon.economy.qrsession.service.SessionService;
import org.hackathon.economy.quest.domain.Quest;
import org.hackathon.economy.quest.repository.QuestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
public class QrAuthController {

    private final QrAuthService qrAuthService; // qr 인증 서비스
    private final AuthenticationService authenticationService; // 사용자 인증 서비스
    private final SessionService sessionService; // 세션 관리 서비스
    private final QuestRepository questRepository;

    @PostMapping("/verify/{sessionKey}/{questNo}")
    public ResponseEntity<?> verifyQrQuest(@PathVariable String sessionKey, @PathVariable Long questNo, HttpSession httpSession) {
        try {
            // 현재 로그인한 사용자 정보 가져오기
            Member member = authenticationService.getAuthenticatedMember(httpSession);

            // 저장소를 통해 quest 조회
            Quest quest = questRepository.findByNo(questNo)
                    .orElseThrow(() -> new IllegalStateException("존재하지 않는 퀘스트입니다."));

            // QR 퀘스트인 경우에만 QR 인증 진행
            if (quest.getIsQr()) {
                if (!sessionService.isSessionValid(sessionKey)) {
                    return ResponseEntity.badRequest().body("유효하지 않은 QR 코드입니다.");
                }
                qrAuthService.verifyQrQuest(quest, member);
                return ResponseEntity.ok("QR 퀘스트 인증이 완료되었습니다.");
            }

            // QR 퀘스트가 아닌 경우
            return ResponseEntity.ok("일반 퀘스트는 다른 방식으로 인증해주세요.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}