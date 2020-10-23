package com.gustavo.gamification.repository;

import com.gustavo.gamification.domain.LeaderBoardRow;
import com.gustavo.gamification.domain.ScoreCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreCardRepository extends JpaRepository<ScoreCard, Long> {
    /**
     * Gets the total score for a given user, being the sum of
     * the scores of all his ScoreCards.
     *
     * @param userId the id of the user for which the total
     *               score should be retrieved
     * @return the total score for the given user
     */
    @Query("Select sum(s.score) " +
            "from ScoreCard s " +
            "where s.userId = :userId " +
            "group by s.userId")
    int getTotalScoreForUser(@Param("userId") Long userId);

    /**
     * Retrieves all the ScoreCards for a given user,
     * identified by his user id.
     *
     * @param userId the id of the user
     * @return a list containing all the ScoreCards for the
     * given user, sorted by most recent.
     */
    List<ScoreCard> findByUserIdOrderByScoreTimestampDesc(Long userId);

    /**
     * Retrieves a list of {@link LeaderBoardRow}s representing
     * the Leader Board of users and their total score.
     *
     * @return the leader board, sorted by highest score first.
     */
    @Query("Select new com.gustavo.gamification.domain.LeaderBoardRow(s.userId, sum(s.score)) " +
            "from ScoreCard  s " +
            "group by s.userId " +
            "order by (sum(s.score)) desc")
    List<LeaderBoardRow> findFirst10();

}
