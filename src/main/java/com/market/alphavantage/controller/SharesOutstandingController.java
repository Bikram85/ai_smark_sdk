package com.market.alphavantage.controller;


import com.market.alphavantage.dto.SharesOutstandingDTO;
import com.market.alphavantage.service.SharesOutstandingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shares-outstanding")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SharesOutstandingController {

    private final SharesOutstandingService service;

    @GetMapping("/load")
    public ResponseEntity<String> load() {
        service.loadSharesOutstanding();
        return ResponseEntity.ok("Shares Outstanding loaded for ");
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<SharesOutstandingDTO> get(@PathVariable String symbol) {
        SharesOutstandingDTO dto = service.getSharesOutstanding(symbol.toUpperCase());
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/reload/{symbol}")
    public ResponseEntity<String> reload(@PathVariable String symbol) {
       // service.loadSharesOutstanding(symbol.toUpperCase());
        return ResponseEntity.ok("Shares Outstanding reloaded for " + symbol);
    }
}
