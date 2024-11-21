package org.hackathon.economy.qr.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
//@CrossOrigin(origins = "http://localhost:5173") // Vue 개발 서버 주소
public class QRCodeController {

    @GetMapping("/generate-qr")
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
}
