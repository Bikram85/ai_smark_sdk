package com.market.alphavantage.controller;



import com.market.alphavantage.dto.IpoCalendarDTO;
import com.market.alphavantage.service.IpoCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ipo-calendar")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class IpoCalendarController {

    private final IpoCalendarService service;

    @PostMapping("/load")
    public ResponseEntity<String> load() {
        service.loadIpoCalendar();
        return ResponseEntity.ok("IPO Calendar loaded");
    }

    @GetMapping("/{id}")
    public ResponseEntity<IpoCalendarDTO> get(@PathVariable String id) {
        IpoCalendarDTO dto = service.getIpoCalendar(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
}

