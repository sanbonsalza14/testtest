package com.my.quiz.service;

import com.my.quiz.dto.UserDto;
import com.my.quiz.entity.Role;
import com.my.quiz.entity.UserEntity;
import com.my.quiz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /** 회원가입 (닉네임 고유 보장) */
    public void saveUser(UserDto dto) {
        if (dto.getNickname() == null || dto.getNickname().isBlank())
            throw new IllegalArgumentException("닉네임은 필수입니다.");
        if (userRepository.existsByNickname(dto.getNickname()))
            throw new IllegalStateException("이미 사용중인 닉네임입니다.");

        UserEntity entity = UserDto.toEntity(dto);

        if ("root".equals(dto.getEmail()) && "admin".equals(dto.getPassword())) {
            if (userRepository.existsByRole(Role.ADMIN))
                throw new IllegalStateException("관리자는 이미 존재합니다.");
            entity.setRole(Role.ADMIN);
            entity.setApproved(true);
        } else {
            entity.setRole(Role.USER);
            entity.setApproved(false);
        }
        userRepository.save(entity);
    }

    /** 관리자: 특정 사용자의 닉네임 변경 (중복 검사) */
    public void updateNicknameByAdmin(String email, String newNickname) {
        if (newNickname == null || newNickname.isBlank())
            throw new IllegalArgumentException("닉네임은 비워둘 수 없습니다.");
        if (userRepository.existsByNickname(newNickname))
            throw new IllegalStateException("이미 사용중인 닉네임입니다.");
        UserEntity u = userRepository.findById(email).orElseThrow();
        u.setNickname(newNickname);
        userRepository.save(u);
    }

    public void updatePasswordByAdmin(String email, String newPw) {
        UserEntity u = userRepository.findById(email).orElseThrow();
        u.setPassword(newPw);
        userRepository.save(u);
    }

    public void approveUser(String email) {
        UserEntity u = userRepository.findById(email).orElseThrow();
        u.setApproved(true);
        userRepository.save(u);
    }

    /** 사용자 정보 수정(패스워드/닉네임 수정 시에도 닉네임 중복 체크) */
    public void updateUserPreserveMeta(UserDto dto) {
        UserEntity entity = userRepository.findById(dto.getEmail()).orElseThrow();

        if (dto.getNickname() != null && !dto.getNickname().isBlank()
                && !dto.getNickname().equals(entity.getNickname())) {
            if (userRepository.existsByNickname(dto.getNickname()))
                throw new IllegalStateException("이미 사용중인 닉네임입니다.");
            entity.setNickname(dto.getNickname());
        }
        entity.setPassword(dto.getPassword());
        userRepository.save(entity);
    }

    public List<UserDto> findAllUser() {
        return userRepository.findAll().stream().map(UserDto::fromEntity).toList();
    }
    public void deleteUser(String email) { userRepository.deleteById(email); }

    public UserDto findOneUser(String email) {
        UserEntity entity = userRepository.findById(email).orElse(null);
        return ObjectUtils.isEmpty(entity) ? null : UserDto.fromEntity(entity);
    }

    public UserEntity findEntity(String email) {
        return userRepository.findById(email).orElse(null);
    }
}
