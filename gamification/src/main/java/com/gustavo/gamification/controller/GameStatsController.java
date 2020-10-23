package com.gustavo.gamification.controller;

import com.gustavo.gamification.domain.GameStats;
import com.gustavo.gamification.service.GameService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
public class GameStatsController {
    private final GameService gameService;

    public GameStatsController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public GameStats getStatsForUser(@RequestParam Long userId) {
        return gameService.retrieveStatsForUser(userId);
    }
}
