package api.gibat.uz.email.repository;

import api.gibat.uz.email.entity.EmailHistoryEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailHistoryRepository extends CrudRepository<EmailHistoryEntity, String> {
    // select count(*) from email_history where = ? and created_date between ? and ?
    @Query("SELECT count(*) FROM EmailHistoryEntity WHERE email = :email AND createdDate BETWEEN :from AND :to")
    Long countByEmailAndCreatedDateBetween(@Param("email") String email, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // select * from sms_history where email = ? and order by created_date desc limit 1
    Optional<EmailHistoryEntity> findTop1ByEmailOrderByCreatedDateDesc(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("UPDATE EmailHistoryEntity SET attemptCount = attemptCount + 1 WHERE id = :id")
    void updateAttemptCount(String id);
}
