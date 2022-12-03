package ru.job4j.domain;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.job4j.operation.Operation;

import javax.persistence.*;
import javax.validation.constraints.*;

@Data
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Min(value = 1, groups = { Operation.OnUpdate.class })
    private int id;

    @Pattern(regexp = "(?=.*[A-Z]).+",
            message = "Login must contain at least one large letter",
            groups = { Operation.OnCreate.class })
    @NotNull
    @Length(min = 3)
    private String login;

    @Pattern(regexp = "(?=.*[A-Z]).+",
            message = "Password must contain at least one large letter",
            groups = { Operation.OnCreate.class })
    @NotNull
    @Length(min = 3)
    private String password;
}
