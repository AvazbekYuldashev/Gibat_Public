package api.gibat.uz.sms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "sms_provider_token_holder")
public class SmsProviderTokenHolderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "token", columnDefinition = "TEXT")
    private String token;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "exp_date")
    private LocalDateTime expDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
        this.expDate = this.createdDate.plusMonths(1); // Masalan, 1 soatlik token muddati
    }
}
