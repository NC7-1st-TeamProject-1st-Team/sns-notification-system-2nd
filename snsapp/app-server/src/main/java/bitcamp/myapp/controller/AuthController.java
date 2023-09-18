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
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("login")
    public String login(
            String phone_Number,
            String password,
            String savephone_Number,
            HttpSession session,
            Model model,
            HttpServletResponse response) throws Exception {

        if (savephone_Number != null) {
            Cookie cookie = new Cookie("phone_Number", phone_Number);
            response.addCookie(cookie);
        } else {
            Cookie cookie = new Cookie("phone_Number", "phone_Number");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }

        return "redirect:/";
    }

    @GetMapping("logout")
    public String logout(HttpSession session) throws Exception {
        session.invalidate();
        return "redirect:/";
    }


    @PostMapping("add")
    public String add(Member member, MultipartFile photofile) throws Exception {
        System.out.println(member);
        if (photofile.getSize() > 0) {
            String uploadFileUrl = ncpObjectStorageService.uploadFile(
                    "bitcamp-nc7-bucket-14", "member/", photofile);
            member.setPhoto(uploadFileUrl);
        }
        memberService.add(member);
        return "redirect:list";
    }




}
