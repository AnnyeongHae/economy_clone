package org.hackathon.economy.member.controller;

import jakarta.persistence.*;
import jakarta.servlet.http.HttpSession;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hackathon.economy.account.service.AccountService;
import org.hackathon.economy.member.domain.LoginDTO;
import org.hackathon.economy.member.domain.Member;
import org.hackathon.economy.member.service.AuthenticationService;
import org.hackathon.economy.member.service.MemberService;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;
    private final AccountService accountService;
    private final AuthenticationService authenticationService;

    //Email로 멤버 검색
//    @GetMapping("/email/{email}")
//    public ResponseEntity<Member> getMemberByEmail(@PathVariable("email") String email) {
//        return ResponseEntity.ok(memberService.findByEmail(email));
//    }

    //no로 멤버 검색
//    @GetMapping("/no/{no}")
//    public ResponseEntity<Member> getMember(@PathVariable("no") Long no) {
//        return ResponseEntity.ok(memberService.findOne(no));
//    }

    //회원가입
    @PostMapping("/join")
    public ResponseEntity<Long> join(@RequestBody Member member) {
        Long memberNo = memberService.join(member);
        accountService.create(member);
        return ResponseEntity.ok(memberNo);
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<Long> login(@RequestBody LoginDTO loginDTO, HttpSession session) {
        System.out.println("로그인 요청 수신: " + loginDTO);
        Member loginResult = memberService.login(loginDTO);
        if (loginResult != null) {
            //로그인 성공
            session.setAttribute("memberEmail", loginResult.getMemberEmail());
            return ResponseEntity.ok(loginResult.getMemberNo());
        }
        //로그인 실패
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    //로그인 확인
    @GetMapping("/check-login")
    public ResponseEntity<String> checkLogin(HttpSession session) {
        try {
            String memberEmail = authenticationService.checkLogin(session);
            return ResponseEntity.ok("Logged in as: " + memberEmail);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    //회원정보 조회
    @GetMapping("/")
    public ResponseEntity<MemberUtil> getMember(HttpSession session) {
        String memberEmail = (String) session.getAttribute("memberEmail");
        if(memberEmail != null) {
            Member member = memberService.findByEmail(memberEmail);
            MemberUtil memberUtil = MemberUtil.builder()
                    .memberName(member.getMemberName())
                    .memberGrade(member.getMemberGrade())
                    .memberPoint(member.getMemberPoint())
                    .memberEmail(member.getMemberEmail())
                    .memberPassword(member.getMemberPassword())
                    .build();
            return ResponseEntity.ok(memberUtil);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        // 세션을 무효화하여 모든 정보를 삭제
        session.invalidate();
        return ResponseEntity.ok("Logged out successfully");
    }

    //회원탈퇴
    @DeleteMapping("/{no}")
    public ResponseEntity<Void> delete(@PathVariable Long no) {
        memberService.delete(no);
        return ResponseEntity.ok().build();
    }
}

@Data
@Builder
class MemberUtil {
    private String memberName;
    private Integer memberGrade;
    private Long memberPoint;
    private String memberEmail;
    private String memberPassword;
}