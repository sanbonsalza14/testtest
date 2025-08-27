package com.my.quiz.repository;

import com.my.quiz.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query(value = "SELECT * FROM member ORDER BY id", nativeQuery = true)
    List<Member> searchQuery();

    @Query(value = "SELECT * FROM member WHERE name LIKE %:keyword% ORDER BY id", nativeQuery = true)
    List<Member> searchname(@Param("keyword") String keyword);

    // ✅ address 컬럼으로 수정
    @Query(value = "SELECT * FROM member WHERE address LIKE %:keyword% ORDER BY id", nativeQuery = true)
    List<Member> searchAddress(@Param("keyword") String keyword);
}
