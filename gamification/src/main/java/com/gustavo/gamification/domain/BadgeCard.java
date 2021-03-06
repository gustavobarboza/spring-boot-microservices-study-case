package com.gustavo.gamification.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * This class links a Badge to a User. Contains also a
 * timestamp with the moment in which the user got it.
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Entity
public class BadgeCard {
    @Id
    @GeneratedValue
    private final Long id;
    private final Long userId;
    private final long badgeTimestamp;
    private final Badge badge;

    public BadgeCard() {
        this(null, null, 0, null);
    }

    public BadgeCard(final Long userId, final Badge badge) {
        this(null, userId, System.currentTimeMillis(), badge);
    }
}
