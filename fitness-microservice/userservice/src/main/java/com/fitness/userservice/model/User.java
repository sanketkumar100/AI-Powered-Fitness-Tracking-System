package com.fitness.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String email;

    //keycloak user id
    private String keyCloakId;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @CreationTimestamp   //hibernate will auto-populate this field once the record is generated.So no need to do it manually
    private LocalDateTime createdAt;

    @UpdateTimestamp        //hibernate will populate automatically whenever the row is changed.
    private LocalDateTime updatedAt;
}
