package com.my.quiz.dto;

import com.my.quiz.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String email;
    private String password;
    private String nickname;

    public static UserDto fromEntity(UserEntity entity) {
        return new UserDto(
                entity.getEmail(),
                entity.getPassword(),
                entity.getNickname()
        );
    }

    // ✅ DTO -> Entity (명확한 이름)
    public static UserEntity toEntity(UserDto dto) {
        UserEntity entity = new UserEntity();
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setNickname(dto.getNickname());
        return entity;
    }

    // (호환 유지) 기존 이름도 남겨두되 내부 위임
    public static UserEntity toDto(UserDto dto) {
        return toEntity(dto);
    }
}
