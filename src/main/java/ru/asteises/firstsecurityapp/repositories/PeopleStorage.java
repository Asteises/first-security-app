package ru.asteises.firstsecurityapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.asteises.firstsecurityapp.models.Person;

import java.util.Optional;

@Repository
public interface PeopleStorage extends JpaRepository<Person, Integer> {

    Optional<Person> findByUsername(String username);
}
