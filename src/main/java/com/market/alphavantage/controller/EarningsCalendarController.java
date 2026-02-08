package com.market.alphavantage.controller;


import com.market.alphavantage.dto.EarningsCalendarDTO;
import com.market.alphavantage.service.EarningsCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/earnings-calendar")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EarningsCalendarController {

    private final EarningsCalendarService service;

    @GetMapping("/load")
    public ResponseEntity<String> load() {
        service.loadEarningsCalendar("3month");
        return ResponseEntity.ok("Earnings calendar loaded for horizon=" );
    }

    @GetMapping("/Data")
    public ResponseEntity<EarningsCalendarDTO> get() {
        EarningsCalendarDTO dto = service.getEarningsCalendar();
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
}

