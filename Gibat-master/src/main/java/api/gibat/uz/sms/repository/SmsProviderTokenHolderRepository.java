package api.gibat.uz.sms.repository;

import api.gibat.uz.sms.entity.SmsProviderTokenHolderEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SmsProviderTokenHolderRepository extends CrudRepository<SmsProviderTokenHolderEntity, Integer> {
    Optional<SmsProviderTokenHolderEntity> findTop1By();
}
