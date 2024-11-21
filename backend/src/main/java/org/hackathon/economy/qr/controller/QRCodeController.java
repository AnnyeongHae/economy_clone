package org.hackathon.economy.qr.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.hackathon.economy.qr.service.QrAchieveService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qr")
public class QRCodeController {

    private final QrAchieveService qrAchieveService;

    @GetMapping("/generate")
    public ResponseEntity<byte[]> generateQRCode(@RequestParam("sessionId") String sessionId) {
        try {
            // QR 코드 생성
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(sessionId, BarcodeFormat.QR_CODE, 300, 300);

            // ByteArray로 변환
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            // HTTP 응답으로 반환
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return ResponseEntity.ok().headers(headers).body(pngData);
        } catch (WriterException | IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/verify/{sessionKey}/{questNo}")
    public ResponseEntity<String> verifyQrCode(@PathVariable String sessionKey,
                                               @PathVariable Long questNo,
                                               HttpSession session) {
        try {
            qrAchieveService.verifyAndSaveQrAchieve(sessionKey, questNo, session);
            return ResponseEntity.ok("QR 코드 인증에 성공했습니다.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
