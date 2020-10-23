package com.gustavo.multiplication.service;

public interface RandomGeneratorService {

    /**
     * @return a randomly-generated factor. It's always between 11 and 99.
     */
    int generateRandomFactor();
}
