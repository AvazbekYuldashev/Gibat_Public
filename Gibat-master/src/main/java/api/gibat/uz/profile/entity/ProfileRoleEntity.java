package api.gibat.uz.profile.entity;

import api.gibat.uz.profile.enums.ProfileRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "profile_role")
public class ProfileRoleEntity {
    @Id
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;


    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private ProfileRole role;

    @Column(name = "profile_id")
    private String profileId;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "uptated_date")
    private LocalDateTime updatedDate;

    @Column(name = "visible")
    private Boolean visible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", updatable = false, insertable = false)
    private ProfileEntity profile;

    @PrePersist
    protected void onCreate() {
        this.visible = Boolean.TRUE;
        this.createdDate = LocalDateTime.now();
        this.updatedDate  = LocalDateTime.now();
    }

}
