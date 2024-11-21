package org.hackathon.economy.account.controller;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.servlet.http.HttpSession;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackathon.economy.account.domain.Account;
import org.hackathon.economy.account.service.AccountService;
import org.hackathon.economy.member.domain.Member;
import org.hackathon.economy.member.service.AuthenticationService;
import org.hackathon.economy.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/account")
@Slf4j
public class AccountController {

    private final AccountService accountService;
    private final AuthenticationService authenticationService;

    // 계좌 생성
    @PostMapping("/create")
    public ResponseEntity<Long> create(HttpSession session) {
        try {
            Member member = authenticationService.getAuthenticatedMember(session);
            Account account = accountService.create(member);
            return ResponseEntity.ok(account.getAccountNo());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // 회원 계좌 정보 조회(없으면 생성)
    @GetMapping("/find")
    public ResponseEntity<AccountUtil> findAccount(HttpSession session) {
        try {
            Member member = authenticationService.getAuthenticatedMember(session);
            Account account = accountService.findByMember(member);
            AccountUtil accountUtil = AccountUtil.builder()
                    .accountBalance(account.getAccountBalance())
                    .accountRate(account.getAccountRate())
                    .build();

            return ResponseEntity.ok(accountUtil);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
//        Account account = Account.builder()
//                .accountBalance(100000L)
//                .accountRate(4.5)
//                .createDate(new Date())
//                .updateDate(new Date())
//                .accountStatus(true)
//                .build();
//
//        return ResponseEntity.ok(account);

    }

    // 계좌에 입금
    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(HttpSession session, @RequestBody Long amount) {
        try {
            log.info("intoooooooo");

            // 입금 금액이 null이거나 0보다 작거나 같으면 안됨
            if (amount == null || amount <= 0) {
                log.info("baaaaad");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Deposit amount must be greater than 0.");
            }

            Member member = authenticationService.getAuthenticatedMember(session);
            Account account = accountService.findByMember(member);
            Long newBalance = accountService.deposit(account, amount);
            log.info("newBalance : {}", newBalance);
            return ResponseEntity.ok("Deposit successful. New balance: " + newBalance);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access.");
        } catch (Exception e) {
            // 추가적인 예외 처리를 통해 에러 메시지를 클라이언트에 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(HttpSession session, @RequestBody Long amount) {
        try {
            // 금액 검증 (0 이하의 금액은 출금 불가)
            if (amount == null || amount <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Withdrawal amount must be greater than 0.");
            }

            Member member = authenticationService.getAuthenticatedMember(session);
            Account account = accountService.findByMember(member);

            // 출금 가능한 금액 확인 (잔액 체크)
            if (account.getAccountBalance() < amount) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Insufficient balance for withdrawal.");
            }

            Long newBalance = accountService.withdraw(account, amount);
            return ResponseEntity.ok("Withdrawal successful. New balance: " + newBalance);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }
}

@Data
@Builder
class AccountUtil {
    private Long accountBalance;
    private Double accountRate;
}