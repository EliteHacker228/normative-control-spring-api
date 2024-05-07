package ru.maeasoftoworks.normativecontrol.api.domain.documents;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
        this.verificationStatus = verificationStatus.name();
    }

    @OneToOne
    @PrimaryKeyJoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Setter
    @JsonIgnore
    private Document document;

    @Column(name = "status")
    private String verificationStatus;

    @Column(name = "description")
    private String description = "";

    @Column(name = "mistake_count")
    private int mistakeCount;

    public VerificationStatus getVerificationStatus(){
        return VerificationStatus.valueOf(this.verificationStatus);
    }
}
