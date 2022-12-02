package ru.job4j.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.domain.Person;
import ru.job4j.operation.Operation;
import ru.job4j.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/person")
@AllArgsConstructor
public class PersonController {

    static final int MIN_LENGTH = 3;

    private final PersonService personService;
    private BCryptPasswordEncoder encoder;
    private final ObjectMapper objectMapper;

    @GetMapping("/all")
    public List<Person> findAll() {
        return this.personService.findAll();
    }

    @PostMapping("/sign-up")
    @Validated(Operation.OnCreate.class)
    public void signUp(@Valid @RequestBody Person person) {
        if (person.getPassword().length() < MIN_LENGTH
                || person.getLogin().length() < MIN_LENGTH) {
            throw new IllegalArgumentException(
                    "Login and password must contain more than 2 characters"
            );
        }
        if (person.getPassword() == null
                || person.getLogin() == null) {
            throw new NullPointerException(
                    "Login or/and password is not specified"
            );
        }
        person.setPassword(encoder.encode(person.getPassword()));
        personService.save(person);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = this.personService.findById(id);
        if (person.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("Not found person id = %s", id)
            );
        }
        return new ResponseEntity<>(person.get(), HttpStatus.OK);
    }

    @PostMapping("/")
    @Validated(Operation.OnCreate.class)
    public ResponseEntity<Person> create(@Valid @RequestBody Person person) {
        if (person.getPassword() == null
                || person.getLogin() == null) {
            throw new NullPointerException(
                    "Login or/and password is not specified"
            );
        }
        return new ResponseEntity<>(
                this.personService.save(person), HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@Valid @RequestBody Person person) {
        this.personService
                .findById(person.getId()).orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                String.format("Not found person %s for update", person)
                        )
                );
        this.personService.save(person);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        this.personService
                .findById(id).orElseThrow(
                        () -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                String.format("Not found person id = %s", id)
                        )
                );
        Person person = new Person();
        person.setId(id);
        this.personService.delete(person);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public Map<String, String> patch(
            @PathVariable int id,
            @RequestBody Map<String, String> body) {
        Person person = personService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                String.format("Not found person id =%s", body.get("id"))
                        )
                );
        if (body.containsKey("login")) {
            person.setLogin(body.get("login"));
        }
        if (body.containsKey("password")) {
            person.setPassword(body.get("password"));
        }
        personService.save(person);
        return body;
    }

    @ExceptionHandler(value = { IllegalArgumentException.class })
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
    }
}
