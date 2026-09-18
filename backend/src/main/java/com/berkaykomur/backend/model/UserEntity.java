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

@Entity
@Table(name = "user_entity")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SoftDelete(columnName = "is_deleted")
@Getter
@Setter
public class UserEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @Size(min = 8)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;
}