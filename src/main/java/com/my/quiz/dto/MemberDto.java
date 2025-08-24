package com.my.quiz.dto;

import com.my.quiz.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    private Long id;
    @NotBlank(message= "이름은 반드시 입력하셔야 합니다.")
    private String name;
    @Range(min = 1, max= 100, message = "나이는 {min}~{max}사이여야 합니다.")
    private int age;
    @Size(min= 3, max= 20, message = "주소가 너무 잛거나 길어요.(3자~20자)")
    private String address;


    public static MemberDto fromMemberEntity(Member member) {
        return new MemberDto(
                member.getId(),
                member.getName(),
                member.getAge(),
                member.getAddress()
        );
    }


    public static Member toDto(MemberDto dto) {
        Member member = new Member();
        member.setId((dto.getId()));
        member.setName((dto.getName()));
        member.setAge((dto.getAge()));
        member.setAddress((dto.getAddress()));
        return member;
    }
}