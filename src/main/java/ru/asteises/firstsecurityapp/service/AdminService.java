package ru.asteises.firstsecurityapp.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SOME_OTHER')") // Вариант проверки авторизации на уровне метода
    public void doAdminStuff() {
        System.out.println("Only admin here");
    }
}
