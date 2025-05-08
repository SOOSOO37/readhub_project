package com.readhub.userservice.admin.entity;

import com.readhub.global.audit.Auditable;
import com.readhub.userservice.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
public class Admin extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String email;
    @Column
    private String password;
    @OneToOne(mappedBy = "admin")
    private User user;

    @Builder
    public Admin(Long id, String email, String password, User user) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.user = user;
    }
}
