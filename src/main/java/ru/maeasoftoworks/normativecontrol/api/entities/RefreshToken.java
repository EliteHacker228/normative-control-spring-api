package ru.maeasoftoworks.normativecontrol.api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cascade;

import java.util.Date;

@Entity(name = "refresh_tokens")
@ToString
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Getter
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @Getter
    @Setter
    private User user;

    public RefreshToken(User user, String token, Date createdAt, Date expiresAt) {
        this.user = user;
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    @Column(name = "token", length = 512)
    @Getter
    @Setter
    private String token;

    @Column(name = "created_at")
    @Getter
    @Setter
    private Date createdAt;

    @Column(name = "expires_at")
    @Getter
    @Setter
    private Date expiresAt;
}
