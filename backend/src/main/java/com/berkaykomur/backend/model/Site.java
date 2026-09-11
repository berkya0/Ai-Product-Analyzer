package com.berkaykomur.backend.model;

import com.berkaykomur.backend.util.PasswordEncryptionConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SoftDelete;

@Entity
@Table(name = "site")
@AllArgsConstructor
@NoArgsConstructor
@SoftDelete(columnName = "is_deleted")
@SuperBuilder
@Getter
@Setter
public class Site extends BaseEntity{

    @Column(nullable = false)
    private String siteName;

    @Column(nullable = false)
    private String siteUrl;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    @Convert(converter = PasswordEncryptionConverter.class)
    private String appPassword;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
