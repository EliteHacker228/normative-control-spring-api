package ru.maeasoftoworks.normativecontrol.api.domain.documents;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;

@Entity(name = "documents")
@NoArgsConstructor
@ToString
@Getter
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Builder
    public Document(User user, String fileName, boolean isReported, String comment) {
        this.user = user;
        this.fileName = fileName;
        this.isReported = isReported;
        this.comment = comment;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "is_reported")
    private boolean isReported;

    @Column(name = "comment")
    private String comment;
}
