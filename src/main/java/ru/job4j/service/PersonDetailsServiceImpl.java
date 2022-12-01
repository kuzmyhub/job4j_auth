package ru.job4j.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.job4j.domain.Person;

import static java.util.Collections.emptyList;

@Service
@AllArgsConstructor
public class PersonDetailsServiceImpl implements UserDetailsService {

    private PersonService personService;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Person person = personService.findByLogin(login);
        if (person == null) {
            throw new UsernameNotFoundException(login);
        }
        return new User(person.getLogin(), person.getPassword(), emptyList());
    }
}
