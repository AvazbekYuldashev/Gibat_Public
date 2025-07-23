package api.gibat.uz.email.service;

import api.gibat.uz.app.enums.AppLangulage;
import api.gibat.uz.app.service.ResourceBoundleService;
import api.gibat.uz.email.entity.EmailHistoryEntity;
import api.gibat.uz.email.enums.EmailType;
import api.gibat.uz.email.repository.EmailHistoryRepository;
import api.gibat.uz.exception.exps.AttemptLimitException;
import api.gibat.uz.exception.exps.ResourceConflictException;
import api.gibat.uz.exception.exps.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EmailHistoryService {
    @Autowired
    private EmailHistoryRepository emailHistoryRepository;
    @Autowired
    private ResourceBoundleService boundleService;

    public EmailHistoryEntity save(String email, String code, EmailType emailType) {
        EmailHistoryEntity entity = new EmailHistoryEntity();
        entity.setEmail(email);
        entity.setCode(code);
        entity.setEmailType(emailType);
        return emailHistoryRepository.save(entity);
    }

    public Long getEmailCount(String email){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusHours(1);
        return emailHistoryRepository.countByEmailAndCreatedDateBetween(email, from, now);
    }

    public Boolean check(String email, String code, AppLangulage lang){
        // first last sms
        Optional<EmailHistoryEntity> optional = emailHistoryRepository.findTop1ByEmailOrderByCreatedDateDesc(email);
        if (optional.isEmpty()){
            throw new ResourceNotFoundException(boundleService.getMessage("confirm.code.not.found", lang)); // TODO DONE
        }

        EmailHistoryEntity entity = optional.get();
        // check code
        if (!entity.getCode().equals(code)){
            emailHistoryRepository.updateAttemptCount(entity.getId());
            throw new ResourceConflictException(boundleService.getMessage("confirm.code.conflict", lang));  // TODO DONE
        }

        if (entity.getAttemptCount() >= 3){
            throw new AttemptLimitException(boundleService.getMessage("the.number.of.attempts.exceeded", lang));    // TODO DONE
        }
        // check time
        if (LocalDateTime.now().isAfter(entity.getCreatedDate().plusMinutes(15))){
            throw new ResourceConflictException(boundleService.getMessage("confirm.code.expired", lang));   // TODO DONE
        }
        return true;
    }


}
