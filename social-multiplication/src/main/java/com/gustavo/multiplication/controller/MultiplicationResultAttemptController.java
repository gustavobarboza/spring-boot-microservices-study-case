package com.gustavo.multiplication.controller;

import com.gustavo.multiplication.domain.MultiplicationResultAttempt;
import com.gustavo.multiplication.service.MultiplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/results")
public class MultiplicationResultAttemptController {

    private final MultiplicationService multiplicationService;
    private final int serverPort;

    @Autowired
    public MultiplicationResultAttemptController(MultiplicationService multiplicationService,
                                                 @Value("${server.port}") int serverPort) {

        this.multiplicationService = multiplicationService;
        this.serverPort = serverPort;
    }

    @PostMapping
    ResponseEntity<MultiplicationResultAttempt> postResult(@RequestBody MultiplicationResultAttempt resultAttempt) {
        boolean isCorrect = multiplicationService.checkAttempt(resultAttempt);
        MultiplicationResultAttempt resultAttemptCopy = new MultiplicationResultAttempt(
                resultAttempt.getUser(),
                resultAttempt.getMultiplication(),
                resultAttempt.getResultAttempt(),
                isCorrect
        );

        log.info("Retrieving result {} from server @ {}", resultAttempt.getId(), serverPort);
        return ResponseEntity.ok(resultAttemptCopy);
    }

    @GetMapping
    public ResponseEntity<List<MultiplicationResultAttempt>> getStatistics(@RequestParam String alias) {
        return ResponseEntity.ok(multiplicationService.getStatsForUser(alias));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MultiplicationResultAttempt> getMultiplicationById(@PathVariable("id") Long resultId) {
        return ResponseEntity.ok(multiplicationService.getResultById(resultId));
    }
}
