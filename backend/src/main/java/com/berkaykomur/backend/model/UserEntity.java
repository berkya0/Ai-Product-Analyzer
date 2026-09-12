package com.berkaykomur.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SoftDelete;
<<<<<<< HEAD
=======
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
>>>>>>> 6ee5fa01333b208303ad7c19c60004c8731e00d1

@Entity
@Table(name = "user_entity")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SoftDelete(columnName = "is_deleted")
@Getter
@Setter
<<<<<<< HEAD
public class UserEntity extends BaseEntity {
=======
public class UserEntity extends BaseEntity implements UserDetails {
>>>>>>> 6ee5fa01333b208303ad7c19c60004c8731e00d1

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    @Size(min=8)
    private String password;
    @Column(nullable = false,unique = true)
    private String email;

<<<<<<< HEAD
=======
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("USER"));
    }


>>>>>>> 6ee5fa01333b208303ad7c19c60004c8731e00d1
}
