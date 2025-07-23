package api.gibat.uz.email.entity;

import api.gibat.uz.email.enums.EmailType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "email_history")
public class EmailHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "email")
    private String email;

    @Column(name = "code")
    private String code;
    @Column(name = "created_date")
    private java.time.LocalDateTime createdDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "email_type")
    private EmailType emailType;

    @Column(name = "attempt_count")
    private Integer attemptCount;

    @PrePersist
    protected void onCreate() {
        this.attemptCount = 0;
        this.createdDate = LocalDateTime.now();
    }
}
