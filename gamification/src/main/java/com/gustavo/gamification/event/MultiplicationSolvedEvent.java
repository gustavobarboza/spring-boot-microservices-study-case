package com.gustavo.gamification.event;

import lombok.*;

import java.io.Serializable;

/**
 * Event that models the fact that a Multiplication has been solved in the system.
 * Provides some context information about the multiplication.
 */
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class MultiplicationSolvedEvent implements Serializable {
    private final Long multiplicationResultAttemptId;
    private final Long userId;
    private final boolean correct;

    public MultiplicationSolvedEvent() {
        multiplicationResultAttemptId = null;
        userId = null;
        correct = false;
    }
}