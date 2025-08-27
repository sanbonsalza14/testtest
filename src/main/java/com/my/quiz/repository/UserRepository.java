package com.my.quiz.repository;

import com.my.quiz.entity.Role;
import com.my.quiz.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    boolean existsByRole(Role role);
    boolean existsByNickname(String nickname);

    // 랭킹: 승인된 사용자만, 정답 desc → 오답 asc → 닉네임 asc
    List<UserEntity> findByApprovedTrueOrderByAnswerTrueDescAnswerFalseAscNicknameAsc();

    // 관리자 목록: 권한/승인/닉네임 정렬
    List<UserEntity> findAllByOrderByRoleAscApprovedDescNicknameAsc();
}
