package com.gustavo.multiplication.service;

import com.gustavo.multiplication.domain.Multiplication;
import com.gustavo.multiplication.domain.MultiplicationResultAttempt;
import com.gustavo.multiplication.domain.User;
import com.gustavo.multiplication.event.EventDispatcher;
import com.gustavo.multiplication.event.MultiplicationSolvedEvent;
import com.gustavo.multiplication.repository.MultiplicationRepository;
import com.gustavo.multiplication.repository.MultiplicationResultAttemptRepository;
import com.gustavo.multiplication.repository.UserRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

class MultiplicationServiceImplTest {
    @Mock
    private RandomGeneratorService randomGeneratorService;

    @Mock
    private MultiplicationResultAttemptRepository attemptRepository;

    @Mock
    private MultiplicationRepository multiplicationRepository;

    @Mock
    private EventDispatcher eventDispatcher;

    @Mock
    private UserRepository userRepository;

    private MultiplicationServiceImpl multiplicationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        multiplicationService = new MultiplicationServiceImpl(randomGeneratorService, attemptRepository, userRepository, multiplicationRepository, eventDispatcher);
    }

    @Test
    public void createRandomMultiplicationTest() {
        given(randomGeneratorService.generateRandomFactor()).willReturn(50, 30);

        Multiplication multiplication = multiplicationService.createRandomMultiplication();

        then(assertThat(multiplication.getFactorA()).isEqualTo(50));
        then(assertThat(multiplication.getFactorB()).isEqualTo(30));
    }

    @Test
    public void checkCorrectAttemptTest() {
        //given
        Multiplication multiplication = new Multiplication(60, 50);
        User user = new User("john_doe");
        MultiplicationResultAttempt attempt =
                new MultiplicationResultAttempt(user, multiplication, 3000, false);
        MultiplicationResultAttempt verifiedAttempt =
                new MultiplicationResultAttempt(user, multiplication, 3000, true);

        MultiplicationSolvedEvent event = new MultiplicationSolvedEvent(attempt.getId(), attempt.getUser().getId(), true);

        given(userRepository.findByAlias("john_doe"))
                .willReturn(Optional.empty());
        given(multiplicationRepository.findByFactorAAndFactorB(60, 50))
                .willReturn(Optional.empty());

        //when
        boolean attemptResult = multiplicationService.checkAttempt(attempt);

        //then
        assertThat(attemptResult).isTrue();
        verify(attemptRepository).save(verifiedAttempt);
        verify(eventDispatcher).send(event);

    }

    @Test
    public void checkWrongAttemptTest() {
        Multiplication multiplication = new Multiplication(60, 50);
        User user = new User("john_doe");
        MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3010, false);
        MultiplicationSolvedEvent event = new MultiplicationSolvedEvent(attempt.getId(), attempt.getUser().getId(), false);

        given(userRepository.findByAlias("john_doe"))
                .willReturn(Optional.empty());
        given(multiplicationRepository.findByFactorAAndFactorB(60, 50))
                .willReturn(Optional.empty());
        boolean attemptResult = multiplicationService.checkAttempt(attempt);

        assertThat(attemptResult).isFalse();
        verify(attemptRepository).save(attempt);
        verify(eventDispatcher).send(event);
    }

    @Test
    public void retrieveStatsTest() {
        Multiplication multiplication = new Multiplication
                (50, 60);
        User user = new User("john_doe");
        MultiplicationResultAttempt attempt1 = new
                MultiplicationResultAttempt(
                user, multiplication, 3010, false);
        MultiplicationResultAttempt attempt2 = new
                MultiplicationResultAttempt(
                user, multiplication, 3051, false);

        List<MultiplicationResultAttempt> latestAttempts =
                Lists.newArrayList(attempt1, attempt2);
        given(userRepository.findByAlias("john_doe")).
                willReturn(Optional.empty());
        given(attemptRepository.findTop5ByUserAliasOrderByIdDesc("john_doe"))
                .willReturn(latestAttempts);

        List<MultiplicationResultAttempt> latestAttemptsResult =
                multiplicationService.getStatsForUser("john_doe");

        assertThat(latestAttemptsResult).isEqualTo
                (latestAttempts);
    }

}