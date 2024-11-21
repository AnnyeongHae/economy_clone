package org.hackathon.economy.auth;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.hackathon.economy.member.domain.Member;
import org.hackathon.economy.member.service.AuthenticationService;
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

    private final QrAuthService qrAuthService;
    private final AuthenticationService authenticationService;
    private final QuestService questService; // 아직 안 만들어짐

    @PostMapping("/generate/{questNo}")
    public ResponseEntity<?> generateQrCode(@PathVariable final Long questNo, HttpSession session) {
        try {
            Member member = authenticationService.getAuthenticatedMember(session);
            if (member.getMemberGrade() != 1) {
                return ResponseEntity.status((HttpStatus.FORBIDDEN).body(
                        "관리자만 QR코드를 생성할 수 있습니다.");
                )
            }
            Quest quest = questService.findOneQu()
        }
    }
}
