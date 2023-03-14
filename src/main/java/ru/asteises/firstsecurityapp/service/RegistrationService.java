package ru.asteises.firstsecurityapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.asteises.firstsecurityapp.models.Person;
import ru.asteises.firstsecurityapp.repositories.PeopleStorage;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final PeopleStorage peopleStorage;

    private final PasswordEncoder passwordEncoder;

    @Transactional // Все изменения в БД должны помечаться как Transactional.
    public void register(Person person) {

        person.setPassword(passwordEncoder.encode(person.getPassword())); // шифруем пароль перед сохранением в БД
        peopleStorage.save(person);
    }
}
