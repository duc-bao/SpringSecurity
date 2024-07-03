package com.example.springsecurity.entity;

import java.util.List;

import jakarta.persistence.*;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "roles")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

//    public Role(String name) {
//        this.name = name;
//    }

    @ManyToMany(
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "id_role", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "id_user", referencedColumnName = "id"))
    private List<User> users;

    @Override
    public String getAuthority() {
        return name;
    }
}
