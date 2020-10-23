package com.gustavo.multiplication.controller;

import com.gustavo.multiplication.domain.Multiplication;
import com.gustavo.multiplication.service.MultiplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/multiplications")
public class MultiplicationController {
    private final MultiplicationService multiplicationService;

    private final int serverPort;

    @Autowired
    public MultiplicationController(MultiplicationService multiplicationService,
                                    @Value("${server.port}") int serverPort) {

        this.multiplicationService = multiplicationService;
        this.serverPort = serverPort;
    }

    @GetMapping("/random")
    public ResponseEntity<Multiplication> getRandomMultiplication() {
        log.info("Generating a random multiplication from server @ {}", serverPort);
        return ResponseEntity.ok(multiplicationService.createRandomMultiplication());
    }
}
