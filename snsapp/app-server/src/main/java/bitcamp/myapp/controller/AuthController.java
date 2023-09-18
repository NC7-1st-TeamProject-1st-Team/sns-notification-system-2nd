package bitcamp.myapp.controller;

import bitcamp.myapp.service.MemberService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bitcamp.myapp.service.NcpObjectStorageService;
import bitcamp.myapp.vo.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {
    {
        System.out.println("AuthController 생성됨!");
    }

    @Autowired
    MemberService memberService;

    @Autowired
    NcpObjectStorageService ncpObjectStorageService;

    @GetMapping("form")
    public void form(@CookieValue(required = false) String phoneNumber, Model model) {
        model.addAttribute("phoneNumber", phoneNumber);
    }

    @GetMapping("add")
    public String add() {
        return"auth/membership";
    }

    @PostMapping("login")
    public String login(
            String phoneNumber,
            String password,
            String savePhoneNumber,
            HttpSession session,
            Model model,
            HttpServletResponse response) throws Exception {

        if (savePhoneNumber != null) {
            Cookie cookie = new Cookie("phoneNumber", phoneNumber);
            response.addCookie(cookie);
        } else {
            Cookie cookie = new Cookie("phoneNumber", "no");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }

//        Member loginUser = memberService.get(phoneNumber, password);
//        if (loginUser == null) {
//            model.addAttribute("refresh", "2;url=form");
//            throw new Exception("회원 정보가 일치하지 않습니다.");
//        }
//
//        session.setAttribute("loginUser", loginUser);
        return "redirect:/";
    }

    @PostMapping("add")
    public String add(
            Member member,
            Model model) throws Exception {

        try {
            System.out.println(member);
            memberService.add(member);
            return "auth/membership";

        } catch (Exception e) {
            model.addAttribute("message", "회원 등록 오류!");
            model.addAttribute("refresh", "2;url=list");
            throw e;
        }
    }

    @GetMapping("logout")
    public String logout(HttpSession session) throws Exception {
        session.invalidate();
        return "redirect:/";
    }
}