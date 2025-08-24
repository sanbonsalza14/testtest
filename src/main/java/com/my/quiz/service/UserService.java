package com.my.quiz.service;

import com.my.quiz.dto.UserDto;
import com.my.quiz.entity.Role;
import com.my.quiz.entity.UserEntity;
import com.my.quiz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository; //회원 저장(관리자 1명 규칙 + 승인여부세팅)
    public void saveUser(UserDto dto) {
        UserEntity entity = UserDto.toDto(dto);

        // 관리자 조건: id == "root" && pw == "admin"
        if ("root".equals(dto.getEmail()) && "admin".equals(dto.getPassword())) {
            if (userRepository.existsByRole(Role.ADMIN))
                throw new IllegalStateException("관리자는 이미 존재합니다.");
            entity.setRole(Role.ADMIN);
            entity.setApproved(true); // 관리자는 자동 승인
        } else {
            entity.setRole(Role.USER);
            entity.setApproved(false); // 일반 유저는 승인 대기
        }
        userRepository.save(entity);
    }

    // 비밀번호 수정(관리자 전용)
    public void updatePasswordByAdmin(String email, String newPw) {
        UserEntity u = userRepository.findById(email).orElseThrow();
        u.setPassword(newPw);
        userRepository.save(u);
    }

    // 승인 처리(관리자 전용)
    public void approveUser(String email) {
        UserEntity u = userRepository.findById(email).orElseThrow();
        u.setApproved(true);
        userRepository.save(u);
    }
    public List<UserDto> findAllUser() {
        return userRepository.findAll().stream().map(UserDto::fromEntity).toList();
    }
    public void deleteUser(String email) {
        userRepository.deleteById(email);
    }

    public UserDto findOneUser(String email) {
        UserEntity entity = userRepository.findById(email).orElse(null);
        return ObjectUtils.isEmpty(entity) ? null : UserDto.fromEntity(entity);
    }

    //로그인용 엔티티 조회 하나 추가
    public UserEntity findEntity(String email) {
        return userRepository.findById(email).orElse(null);
    }




}