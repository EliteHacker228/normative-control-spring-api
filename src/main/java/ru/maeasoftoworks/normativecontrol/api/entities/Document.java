package ru.maeasoftoworks.normativecontrol.api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "documents")
@ToString
@NoArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Getter
    private Long id;

    public Document(User user, Long timestamp) {
        this.user = user;
        this.timestamp = timestamp;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @Getter
    @Setter
    private User user;

    @Column(name = "correlation_id", unique = true, nullable = false)
    @Getter
    @Setter
    private String correlationId;

    @Column(name = "fingerprint")
    @Getter
    @Setter
    private String fingerprint;

    @Column(name = "timestamp")
    @Getter
    private Long timestamp;
}
