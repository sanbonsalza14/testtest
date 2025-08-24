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

    public static UserEntity toDto(UserDto dto) {
        UserEntity entity = new UserEntity();
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setNickname(dto.getNickname());
        return entity;
    }
}