package com.my.quiz.repository;

import com.my.quiz.entity.PlayResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayResultRepository extends JpaRepository<PlayResult,Long> {
    @Query("SELECT COUNT(p) FROM PlayResult p WHERE p.userEmail = :email AND p.correct = true ")
    long countCorrectByUser(@Param("email") String email);

    @Query("SELECT COUNT(p) FROM PlayResult p WHERE p.userEmail = :email AND p.correct = false")
    long countWrongByUser(@Param("email") String email);

}