package com.gustavo.multiplication.domain;

import com.sun.org.apache.xpath.internal.operations.Mult;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@Entity
public final class Multiplication {
    @Id
    @GeneratedValue
    @Column(name = "MULTIPLICATION_ID")
    private Long id;
    private final int factorA;
    private final int factorB;

    //Empty constructor for JSON (de)serialization
    public Multiplication() {
        this(0, 0);
    }

}
