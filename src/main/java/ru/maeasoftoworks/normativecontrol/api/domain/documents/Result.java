package ru.maeasoftoworks.normativecontrol.api.domain.documents;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity(name = "results")
@NoArgsConstructor
@ToString
@Getter
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    public Result(Document document, VerificationStatus verificationStatus) {
        this.document = document;
        this.verificationStatus = verificationStatus;
    }

    @OneToOne
    @JoinColumn(name = "document_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Document document;

    @Column(name = "verification_status")
    private VerificationStatus verificationStatus;
}
