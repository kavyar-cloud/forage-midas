package com.jpmc.midascore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private double balance;

    public void setId(Long id) {
        this.id = id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    protected User() {
        // JPA requires a no-arg constructor
    }

    public User(String username, float balance) {
        this.username = username;
        this.balance = balance;
    }

    public Long getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }



    @Override
    public String toString() {
        return String.format("User[id=%d, username='%s', balance=%.2f]", id, username, balance);
    }
}
