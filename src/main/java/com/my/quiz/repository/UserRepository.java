package com.my.quiz.repository;

import com.my.quiz.entity.Role;
import com.my.quiz.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    boolean existsByRole(Role role);

//    JpaRepository<UserEntity, String> 상속
//
//    UserEntity : 이 레포지토리가 다룰 엔티티 클래스
//
//    String : 엔티티의 기본 키 타입 (여기서는 email이 @Id로 되어 있어서 타입이 String)
//
//    기본 제공 기능
//
//    save(), findById(), findAll(), deleteById() 등 CRUD 메서드를 바로 사용 가능.
}