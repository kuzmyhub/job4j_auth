package ru.job4j.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.job4j.domain.Person;
import ru.job4j.repository.UserStore;

import static java.util.Collections.emptyList;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserStore users;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Person user = users.findByLogin(login);
        if (user == null) {
            throw new UsernameNotFoundException(login);
        }
        return new User(user.getLogin(), user.getPassword(), emptyList());
    }
}
