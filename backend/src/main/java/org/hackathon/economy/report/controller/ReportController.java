package org.hackathon.economy.report.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackathon.economy.report.domain.dto.ReportResponse;
import org.hackathon.economy.report.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping()
    public ResponseEntity<ReportResponse> getReport(@RequestBody HashMap<String, Object> payload) {

        for (Object value : payload.values()) {
            System.out.println("value = " + value);
        }

        String period = (String) payload.get("period");
        LocalDate startDate = LocalDate.parse(payload.get("startDate").toString());
        LocalDate endDate = LocalDate.parse(payload.get("endDate").toString());
        Long accountNo = Long.parseLong(payload.get("accountNo").toString());
        Integer page = Integer.parseInt(payload.get("page").toString());

        ReportResponse response = reportService.getReport(accountNo, period, startDate, endDate, page);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/details")
    public ResponseEntity<ReportResponse> getQuest(@RequestBody HashMap<String,Object> payload){

        Long accountNo = Long.parseLong(payload.get("accountNo").toString());
        String questDate = payload.get("date").toString();
        ReportResponse response = reportService.getQuest(accountNo,questDate);
        return ResponseEntity.ok(response);

    }
}