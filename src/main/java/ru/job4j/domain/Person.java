package ru.job4j.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String login;
    private String password;
}
