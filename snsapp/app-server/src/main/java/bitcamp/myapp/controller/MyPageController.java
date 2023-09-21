package bitcamp.myapp.controller;

import bitcamp.myapp.service.BoardService;
import bitcamp.myapp.service.MemberService;
import bitcamp.myapp.service.MyPageService;
import bitcamp.myapp.service.NcpObjectStorageService;
import bitcamp.myapp.vo.LoginUser;
import bitcamp.myapp.vo.Member;
import bitcamp.myapp.vo.MyPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/myPage")
public class MyPageController {

  @Autowired
  MemberService memberService;

  @Autowired
  MyPageService myPageService;

  @Autowired
  BoardService boardService;

  @Autowired
  NcpObjectStorageService ncpObjectStorageService;

  {
    System.out.println("MyPageController 생성됨!");
  }

  @GetMapping("{no}")
  public String detail(
      @PathVariable int no,
      @RequestParam(defaultValue = "") String show,
      Model model,
      HttpSession session) throws Exception {
    LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
    if (loginUser == null) {
      return "redirect:/auth/form";
    }

    // 세션에 저장된 방문한 마이페이지 번호 목록을 가져오기
    HashSet<Integer> visitedMyPages = loginUser.getVisitedMyPages();

    // 만약 방문한 적 없는 마이페이지라면 조회수 증가
    if (!visitedMyPages.contains(no)) {
      myPageService.increaseVisitCount(no);

      // 방문한 마이페이지 번호를 세션에 추가
      visitedMyPages.add(no);
    }

    model.addAttribute("myPage", myPageService.get(no));
    model.addAttribute("show", show);

    switch (show) {
      case "followers":
        model.addAttribute("followList", myPageService.followerList(no));
        break;
      case "followings":
        model.addAttribute("followList", myPageService.followingList(no));
        break;
      default:
        model.addAttribute("followList", null);
        model.addAttribute("list", boardService.list(1));
        break;
    }
    // myPageService.increaseVisitCount(no);
    // model.addAttribute("loginUser", loginUser);
    session.setAttribute("loginUser", loginUser);
    return "myPage/detail";
  }

  @GetMapping("{no}/info")
  public String info(@PathVariable int no, Model model) throws Exception {
    model.addAttribute("myPage", myPageService.get(no));

    return "myPage/memberInfoUpdate";
  }

  @PostMapping("{no}/update")
  public String update(
      Member member,
      @RequestParam("birthday") String birthday,
      @RequestParam("gender") int gender,
      Model model,
      MultipartFile photofile) throws Exception {
    if (photofile.getSize() > 0) {
      String uploadFileUrl = ncpObjectStorageService.uploadFile(
          "bitcamp-nc7-bucket-14", "sns_member/", photofile);
      member.setPhoto(uploadFileUrl);
    }

    MyPage myPage = myPageService.get(member.getNo());

    if (birthday.isEmpty()) {
      birthday = null;
    } else {
      // 생일 값을 문자열에서 Timestamp로 변환
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      Date parsedDate = dateFormat.parse(birthday);
      Timestamp timestamp = new Timestamp(parsedDate.getTime());

      myPage.setBirthday(timestamp);
      myPage.setGender(gender);
    }

    if (memberService.update(member) == 0 || myPageService.update(myPage) == 0) {
      throw new Exception("회원이 없습니다.");
    } else {
      return "redirect:/myPage/" + myPage.getNo();
    }
  }

  @GetMapping("follow")
  public void follow(
      @RequestParam("followingNo") int followingNo,
      HttpSession session,
      HttpServletResponse response) throws Exception, IOException {
    LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");

    Map<String, Object> returnMap = new HashMap<>();
    try {
      myPageService.follow(loginUser, followingNo);
      loginUser.getFollowMemberSet().add(memberService.get(followingNo));
      session.setAttribute("loginUser", loginUser);
      returnMap.put("result", "success");

    } catch (Exception e) {
      returnMap.put("result", "fail");

    } finally {
      try {
        response.getWriter().print(new ObjectMapper().writeValueAsString(returnMap));
      } catch (IOException ioExceptione) {
        ioExceptione.printStackTrace();
      }
    }

  }

  @GetMapping("unfollow")
  public void unfollow(
      @RequestParam("followingNo") int followingNo,
      HttpSession session,
      HttpServletResponse response) throws Exception {
    LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");

    Map<String, Object> returnMap = new HashMap<>();
    try {
      myPageService.unfollow(loginUser, followingNo);
      loginUser.getFollowMemberSet().remove(memberService.get(followingNo));
      session.setAttribute("loginUser", loginUser);
      returnMap.put("result", "success");

    } catch (Exception e) {
      returnMap.put("result", "fail");

    } finally {
      try {
        response.getWriter().print(new ObjectMapper().writeValueAsString(returnMap));
      } catch (IOException ioExceptione) {
        ioExceptione.printStackTrace();
      }
    }
  }

}
