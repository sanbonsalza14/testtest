package com.my.quiz.controller;

import com.my.quiz.dto.MemberDto;
import com.my.quiz.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MemberController {
    @Autowired
    MemberService service;

    @GetMapping("/list")
    public String showList(Model model) {
        model.addAttribute("title", "리스트보기");
        List<MemberDto> memberList = service.getAllList();
        model.addAttribute("list", memberList);
        return "member/showMember";
    }

    @GetMapping("/member/insertForm")
    public String insertFormView(Model model) {
        model.addAttribute("dto", new MemberDto());
        return "member/insertForm";
    }

    @PostMapping("/member/insert")
    public String insert(@Valid @ModelAttribute("dto")MemberDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "member/insertForm";
        service.insertMember(dto);
        return "redirect:/list";
    }
}