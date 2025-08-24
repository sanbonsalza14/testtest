package com.my.quiz.service;

import com.my.quiz.dto.MemberDto;
import com.my.quiz.entity.Member;
import com.my.quiz.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class MemberService {
    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }
    public List<MemberDto> getAllList(){
        return repository.findAll().stream().map(MemberDto::fromMemberEntity).toList();
    }
    public void insertMember(MemberDto dto){
        repository.save(MemberDto.toDto(dto));
    }

    public void deleteMember(Long id) {
        repository.deleteById(id);
    }

    public MemberDto findMember(Long updateId) {
        Member member = repository.findById(updateId).orElse(null);
        return ObjectUtils.isEmpty(member) ? null : MemberDto.fromMemberEntity(member);
    }

    public void updateMember(MemberDto dto) {
        repository.save(MemberDto.toDto(dto));
    }

    public List<MemberDto> searchMember(String type, String keyword) {
        return switch (type) {
            case "name" -> repository.searchname(keyword).stream().map(MemberDto::fromMemberEntity).toList();
            case "address"-> repository.searchAddress(keyword).stream().map(MemberDto::fromMemberEntity).toList();
            default -> repository.searchQuery().stream().map(MemberDto::fromMemberEntity).toList();

        };
    }

}