package org.hackathon.economy.qrsession.controller;


import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.hackathon.economy.member.domain.Member;
import org.hackathon.economy.member.service.AuthenticationService;
import org.hackathon.economy.qrsession.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/session")
@AllArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final AuthenticationService authenticationService;

    // 세션 생성 (로그인된 사용자 기반)
    @PostMapping("/create")
    public ResponseEntity<String> createSession(HttpSession httpSession) {
        try {
            // 로그인된 사용자 확인
            Member authenticatedMember = authenticationService.getAuthenticatedMember(httpSession);

            // QR 세션 생성
            String sessionKey = sessionService.createSession(authenticatedMember.getMemberNo());
            return ResponseEntity.ok(sessionKey);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // 세션 검증
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateSession(@RequestParam String sessionKey) {
        boolean isValid = sessionService.isSessionValid(sessionKey);
        return ResponseEntity.ok(isValid);
    }

    // 세션 갱신
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshSession(@RequestParam String sessionKey) {
        try {
            String newSessionKey = sessionService.refreshSession(sessionKey);
            return ResponseEntity.ok(newSessionKey);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    // 세션 타임아웃 확인
    @GetMapping("/timeout")
    public ResponseEntity<Long> getSessionTimeout(@RequestParam String sessionKey) {
        try {
            Long remainingTime = sessionService.getRemainingTime(sessionKey);
            return ResponseEntity.ok(remainingTime);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(-1L);
        }
    }
}