package api.gibat.uz.app.service;

import api.gibat.uz.app.enums.AppLangulage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ResourceBoundleService {
    @Autowired
    private MessageSource messageSource;

    public String getMessage(String code, AppLangulage language) {
        return messageSource.getMessage(code, null, new Locale(language.name()));
    }

}
