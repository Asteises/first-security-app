package ru.asteises.firstsecurityapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.asteises.firstsecurityapp.models.Person;
import ru.asteises.firstsecurityapp.repositories.PeopleStorage;
import ru.asteises.firstsecurityapp.security.PersonDetails;

/**
 * С помощью implements UserDetailsService даем понять, что загружаем пользователя именно для Spring Security.
 */
@Service
@RequiredArgsConstructor
public class PersonDetailsService implements UserDetailsService {

    private final PeopleStorage peopleStorage;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Person person = peopleStorage.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new PersonDetails(person);
    }
}
