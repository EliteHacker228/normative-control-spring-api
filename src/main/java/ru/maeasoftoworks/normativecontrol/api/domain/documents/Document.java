package ru.maeasoftoworks.normativecontrol.api.domain.documents;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.maeasoftoworks.normativecontrol.api.domain.universities.AcademicGroup;
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
    public Document(User user, String studentName, AcademicGroup academicGroup, String fileName, boolean isReported, String comment) {
        this.user = user;
        this.studentName = studentName;
        this.academicGroup = academicGroup;
        this.fileName = fileName;
        this.isReported = isReported;
        this.comment = comment;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "student_name")
    private String studentName;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "academic_group_id")
    @Setter
    private AcademicGroup academicGroup;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "is_reported")
    private boolean isReported;

    @Column(name = "comment")
    private String comment;
}
