package com.gustavo.multiplication.exceptions;

public class ResultAttemptNotFoundException extends RuntimeException {

    public ResultAttemptNotFoundException() {
        super("Multiplication Attempt not found with the given id");
    }
}
