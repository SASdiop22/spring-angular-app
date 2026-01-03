package edu.miage.springboot.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.miage.springboot.services.interfaces.AiStatisticsService;
import edu.miage.springboot.web.dtos.AiStatisticsDTO;

@RestController
@RequestMapping("/api/stats")
public class StatisticsResource {

    private final AiStatisticsService service;

    public StatisticsResource(AiStatisticsService service) {
        this.service = service;
    }

    @GetMapping("/ai")
    public ResponseEntity<AiStatisticsDTO> globalStats() {
        return ResponseEntity.ok(service.globalStats());
    }

    @GetMapping("/ai/job/{jobOfferId}")
    public ResponseEntity<AiStatisticsDTO> statsByOffer(
            @PathVariable Long jobOfferId) {
        return ResponseEntity.ok(service.statsByJobOffer(jobOfferId));
    }
}

