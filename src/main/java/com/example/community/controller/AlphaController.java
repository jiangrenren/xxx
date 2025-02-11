package com.example.community.controller;

import com.example.community.service.AlphaService;
import com.example.community.util.CommunityUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

public class AlphaController {

    //@Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "Hello Spring Boot.";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return alphaService.find();
    }

    // cookie事例
    @GetMapping("/cookie/set")
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        // 创建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        // 设置cookie生效的范围
        cookie.setPath("/community/alpha");
        // 设置cookie的生成时间
        cookie.setMaxAge(60 * 10);
        // 发送cookie
        response.addCookie(cookie);

        return "set cookie";
    }

    @GetMapping("/cookie/get")
    @ResponseBody
    public String setCookie(@CookieValue("code") String code) {
        System.out.println(code);
        return "this is cookie.";
    }

    @GetMapping("/session/set")
    @ResponseBody
    public String setSession(HttpSession session) {
        session.setAttribute("id", 1);
        session.setAttribute("name", "Test");
        return "set session";
    }

    @GetMapping("/session/get")
    @ResponseBody
    public String getSession(HttpSession session) {
        System.out.println("session.getAttribute(\"id\") = " + session.getAttribute("id"));
        System.out.println("session.getAttribute(\"name\") = " + session.getAttribute("name"));
        return "get session";
    }

    //ajax示例
    @PostMapping("/ajax")
    @ResponseBody
    public String testAjax(@RequestParam String name, @RequestParam int age) {
        System.out.println("name = " + name);
        System.out.println("age = " + age);
        return CommunityUtil.getJSONString(0, "操作成功");
    }
}
