package com.readhub.backend.user.entity;

import com.readhub.backend.global.audit.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Setter
@Getter
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

    @BatchSize(size = 10)
    @Fetch(value = FetchMode.SUBSELECT)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

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
