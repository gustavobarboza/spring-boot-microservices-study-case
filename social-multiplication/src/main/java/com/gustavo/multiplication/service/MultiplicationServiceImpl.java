package com.gustavo.multiplication.service;

import com.gustavo.multiplication.domain.Multiplication;
import com.gustavo.multiplication.domain.MultiplicationResultAttempt;
import com.gustavo.multiplication.domain.User;
import com.gustavo.multiplication.event.EventDispatcher;
import com.gustavo.multiplication.event.MultiplicationSolvedEvent;
import com.gustavo.multiplication.exceptions.ResultAttemptNotFoundException;
import com.gustavo.multiplication.repository.MultiplicationRepository;
import com.gustavo.multiplication.repository.MultiplicationResultAttemptRepository;
import com.gustavo.multiplication.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class MultiplicationServiceImpl implements MultiplicationService {
    private RandomGeneratorService randomGeneratorService;
    private MultiplicationResultAttemptRepository attemptRepository;
    private UserRepository userRepository;
    private MultiplicationRepository multiplicationRepository;
    private EventDispatcher eventDispatcher;

    public MultiplicationServiceImpl(RandomGeneratorService randomGeneratorService,
                                     MultiplicationResultAttemptRepository attemptRepository,
                                     UserRepository userRepository, MultiplicationRepository multiplicationRepository, EventDispatcher eventDispatcher) {
        this.randomGeneratorService = randomGeneratorService;
        this.attemptRepository = attemptRepository;
        this.userRepository = userRepository;
        this.multiplicationRepository = multiplicationRepository;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public Multiplication createRandomMultiplication() {
        int factorA = randomGeneratorService.generateRandomFactor();
        int factorB = randomGeneratorService.generateRandomFactor();

        return new Multiplication(factorA, factorB);
    }

    @Transactional
    @Override
    public boolean checkAttempt(MultiplicationResultAttempt resultAttempt) {
        Optional<User> user = userRepository.findByAlias(resultAttempt.getUser().getAlias());
        Optional<Multiplication> multiplication =
                multiplicationRepository
                        .findByFactorAAndFactorB(resultAttempt.getMultiplication().getFactorA(),
                                resultAttempt.getMultiplication().getFactorB());

        boolean correct = resultAttempt.getResultAttempt() ==
                resultAttempt.getMultiplication().getFactorA() *
                        resultAttempt.getMultiplication().getFactorB();

        Assert.isTrue(!resultAttempt.isCorrect(), "You can't send an attempt marked as correct!!");

        MultiplicationResultAttempt checkedAttempt =
                new MultiplicationResultAttempt(
                        user.orElseGet(resultAttempt::getUser),
                        multiplication.orElseGet(resultAttempt::getMultiplication),
                        resultAttempt.getResultAttempt(),
                        correct);

        attemptRepository.save(checkedAttempt);


        //Communicates the result via an Event
        eventDispatcher.send(new MultiplicationSolvedEvent(checkedAttempt.getId(),
                checkedAttempt.getUser().getId(),
                checkedAttempt.isCorrect()));

        return correct;
    }

    public List<MultiplicationResultAttempt> getStatsForUser(String userAlias) {
        return attemptRepository.findTop5ByUserAliasOrderByIdDesc(userAlias);
    }

    @Override
    public MultiplicationResultAttempt getResultById(Long resultId) {
        Optional<MultiplicationResultAttempt> attempt = attemptRepository.findById(resultId);
        return attempt.orElseThrow(ResultAttemptNotFoundException::new);

    }

}
