package com.readhub.backend.user;

import com.readhub.backend.global.audit.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Entity
public class User extends Auditable implements Principal{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Email
    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.ACTIVE;

    public enum UserStatus {
        ACTIVE(1, "활동중"),

        STOP(2,"정지된 계정"),

        QUIT(3,"탈퇴");


        @Getter
        private int number;

        @Getter
        private String description;

        UserStatus(int number, String description) {
            this.number = number;
            this.description = description;
        }
    }

    public User(String username, String s, List<GrantedAuthority> authorities) {
        super();
    }

    @Override
    public String getName() {
        return null;
    }

    public Map.Entry<Object, Object> getRole() {
        return null;
    }
}
