package ru.asteises.firstsecurityapp.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.asteises.firstsecurityapp.models.Person;
import ru.asteises.firstsecurityapp.service.PersonDetailsService;

@Component
@RequiredArgsConstructor
public class PersonValidator implements Validator {

    private final PersonDetailsService personDetailsService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;

        try {
            personDetailsService.loadUserByUsername(person.getUsername());
        } catch (UsernameNotFoundException ignored) {
            return; // все ок, пользователь не найден // TODO переделать
        }
        errors.rejectValue("username", "", "Username already exist");
    }
}
