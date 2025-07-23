package api.gibat.uz.sms.repository;

import api.gibat.uz.sms.entity.SmsHistoryEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SmsHistoryRepository extends CrudRepository<SmsHistoryEntity, String> {

    // select count(*) from sms_history where = ? and created_date between ? and ?
    @Query("SELECT count(*) FROM SmsHistoryEntity WHERE phone = :phone AND createdDate BETWEEN :from AND :to")
    Long countByPhoneAndCreatedDateBetween(@Param("phone") String phone, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // select * from sms_history where phone = ? and order by created_date desc limit 1
    Optional<SmsHistoryEntity> findTop1ByPhoneOrderByCreatedDateDesc(@Param("phone") String phone);

    @Modifying
    @Transactional
    @Query("UPDATE SmsHistoryEntity SET attemptCount = attemptCount + 1 WHERE id = :id")
    void updateAttemptCount(String id);
}
