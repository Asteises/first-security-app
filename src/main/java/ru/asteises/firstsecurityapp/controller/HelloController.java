package ru.asteises.firstsecurityapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.asteises.firstsecurityapp.service.AdminService;

@Controller
@RequiredArgsConstructor
public class HelloController {

    private final AdminService adminService;

    @GetMapping("/hello")
    public String sayHello() {
        return "hello";
    }

    @GetMapping("/admin")
    public String adminPage() {
        adminService.doAdminStuff();
        return "admin";
    }
}
