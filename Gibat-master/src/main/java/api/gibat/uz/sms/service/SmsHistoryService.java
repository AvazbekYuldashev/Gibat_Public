package api.gibat.uz.sms.service;

import api.gibat.uz.app.enums.AppLangulage;
import api.gibat.uz.app.service.ResourceBoundleService;
import api.gibat.uz.exception.exps.AttemptLimitException;
import api.gibat.uz.exception.exps.ResourceConflictException;
import api.gibat.uz.exception.exps.ResourceNotFoundException;
import api.gibat.uz.sms.entity.SmsHistoryEntity;
import api.gibat.uz.sms.enums.SmsType;
import api.gibat.uz.sms.repository.SmsHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SmsHistoryService {
    @Autowired
    private SmsHistoryRepository smsHistoryRepository;
    @Autowired
    private ResourceBoundleService boundleService;


    public SmsHistoryEntity save(String phone, String message, String code, SmsType smsType) {
        SmsHistoryEntity entity = new SmsHistoryEntity();
        entity.setPhone(phone);
        entity.setMessage(message);
        entity.setCode(code);
        entity.setSmsType(smsType);
        return smsHistoryRepository.save(entity);
    }

    public Long getSmsCount(String phone){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusHours(1);
        return smsHistoryRepository.countByPhoneAndCreatedDateBetween(phone, from, now);
    }

    public Boolean check(String phone, String code, AppLangulage lang){
        // first last sms
        Optional<SmsHistoryEntity> optional = smsHistoryRepository.findTop1ByPhoneOrderByCreatedDateDesc(phone);
        if (optional.isEmpty()){
            throw new ResourceNotFoundException(boundleService.getMessage("confirm.code.not.found", lang)); // TODO DONE
        }

        SmsHistoryEntity entity = optional.get();
        // check code
        if (!entity.getCode().equals(code)){
            smsHistoryRepository.updateAttemptCount(entity.getId());
            throw new ResourceConflictException(boundleService.getMessage("confirm.code.conflict", lang));   // TODO DONE
        }

        if (entity.getAttemptCount() >= 3){
            throw new AttemptLimitException(boundleService.getMessage("the.number.of.attempts.exceeded", lang));   // TODO DONE
        }
        // check time
        if (LocalDateTime.now().isAfter(entity.getCreatedDate().plusMinutes(15))){
            throw new ResourceConflictException(boundleService.getMessage("confirm.code.expired", lang));   // TODO DONE
        }

        return true;

    }
}
