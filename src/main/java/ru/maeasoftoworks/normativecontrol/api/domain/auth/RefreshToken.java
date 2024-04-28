package ru.maeasoftoworks.normativecontrol.api.domain.auth;

import jakarta.persistence.*;
import lombok.*;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;

import java.util.Date;

@Entity(name = "refresh_tokens")
@NoArgsConstructor
@Getter
@ToString
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Builder
    public RefreshToken(User user, String token, Date createdAt, Date expiresAt) {
        this.user = user;
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "token")
    @Setter
    private String token;

    @Column(name = "created_at")
    @Setter
    private Date createdAt;

    @Column(name = "expires_at")
    @Setter
    private Date expiresAt;
}
