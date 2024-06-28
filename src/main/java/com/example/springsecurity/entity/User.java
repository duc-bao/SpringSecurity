package com.example.springsecurity.entity;

import java.util.Set;

import jakarta.persistence.*;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    // Kiểm tra đó là duy nhất và kiểm tra chữ viết hoa
    @Column(name = "username", unique = true, columnDefinition = "varchar(250) COLLATE utf8mb4_unicode_ci")
    private String username;

    private String email;
    private String password;
    private boolean enable;
    private boolean accountlock;

    @ManyToMany(
            cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
            fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "id_user"),
            inverseJoinColumns = @JoinColumn(name = "id_role"))
    private Set<Role> roles;
}
