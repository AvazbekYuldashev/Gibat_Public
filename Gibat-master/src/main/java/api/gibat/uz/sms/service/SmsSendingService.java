package api.gibat.uz.sms.service;

import api.gibat.uz.app.enums.AppLangulage;
import api.gibat.uz.app.service.ResourceBoundleService;
import api.gibat.uz.exception.exps.AttemptLimitException;
import api.gibat.uz.sms.dto.SmsAuthDTO;
import api.gibat.uz.sms.dto.SmsAuthResponseDTO;
import api.gibat.uz.sms.dto.SmsRequestDTO;
import api.gibat.uz.sms.dto.SmsSendResponseDTO;
import api.gibat.uz.sms.entity.SmsProviderTokenHolderEntity;
import api.gibat.uz.sms.enums.SmsType;
import api.gibat.uz.sms.repository.SmsProviderTokenHolderRepository;
import api.gibat.uz.app.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SmsSendingService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private SmsHistoryService smsHistoryService;
    @Autowired
    private ResourceBoundleService boundleService;
    @Autowired
    private SmsProviderTokenHolderRepository smsProviderTokenHolderRepository;

    @Value("${eskiz.url}")
    private String smsUrl;
    @Value("${eskiz.email}")
    private String smsEmail;
    @Value("${eskiz.password}")
    private String smsPassword;


    public String sendRegistrationSms(String phone, AppLangulage lang) {
        String code = RandomUtil.getRandomSmsCode();
        String message = "Bu Eskiz dan test";

        System.out.println(code);
        //message = String.format(message, code);
        saveSms(phone, message, code, SmsType.REGISTRATION, lang);
        return boundleService.getMessage("registration.sms.confirm.send", lang);    // TODO DONE
    }

    public void sendUsernameChangeConfirmSms(String phone, AppLangulage lang) {
        String code = RandomUtil.getRandomSmsCode();
        String message = "Bu Eskiz dan test";
        message = String.format(message, code);
        saveSms(phone, message, code, SmsType.CHANGE_USERNAME_CONFIRM, lang);
    }

    public String sendResetPasswordSms(String phone, AppLangulage lang) {
        String code = RandomUtil.getRandomSmsCode();
        String message = "Bu Eskiz dan test";
        System.out.println(code);
        //message = String.format(message, code);
        saveSms(phone, message, code, SmsType.PASSWORD_RESET, lang);
        return boundleService.getMessage("resend.sms.confirm.send", lang);  // TODO DONE
    }

    private SmsSendResponseDTO saveSms(String phone, String message, String code, SmsType smsType, AppLangulage lang) {
        // check -> Sms limit
        // ..
        Long count = smsHistoryService.getSmsCount(phone);
        if (count >= 3){
            throw new AttemptLimitException(boundleService.getMessage("sms.limit.many.times", lang));
        }
        SmsSendResponseDTO result = sendSms(phone, message);
        smsHistoryService.save(phone, message, code, smsType);
        return result;
    }
        // String phone, String message
    private SmsSendResponseDTO sendSms(String phone, String message){

        // 1. check -> token
        String token = getToken();
        // 2. token -> login

        // Http Headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);

        // Http Body
        SmsRequestDTO body = new SmsRequestDTO();
        body.setMobile_phone(phone);
        body.setMessage(message);
        body.setFrom("4546");

        // Http request
        HttpEntity<SmsRequestDTO> request = new HttpEntity<>(body, headers);
        String url = smsUrl + "/message/sms/send";
        // 3. send sms
        try {
            ResponseEntity<SmsSendResponseDTO> response  = restTemplate.exchange(url, HttpMethod.POST, request, SmsSendResponseDTO.class);
            return response.getBody();
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }


    private String getToken() {
        Optional<SmsProviderTokenHolderEntity> optional = smsProviderTokenHolderRepository.findTop1By();
        if (optional.isEmpty()){
            SmsProviderTokenHolderEntity entity = new SmsProviderTokenHolderEntity();
            entity.setToken(getTokenFromProvider());
            return smsProviderTokenHolderRepository.save(entity).getToken();
        }

        // expired date
        SmsProviderTokenHolderEntity entity = optional.get();
        if (LocalDateTime.now().isBefore(entity.getExpDate())){
            return entity.getToken();
        }
        smsProviderTokenHolderRepository.delete(entity);
        entity.setToken(getTokenFromProvider());
        return smsProviderTokenHolderRepository.save(entity).getToken();
    }

    private String getTokenFromProvider() {
        // accaunt
        SmsAuthDTO dto = new SmsAuthDTO();
        dto.setEmail(smsEmail);
        dto.setPassword(smsPassword);

//        String response = restTemplate.postForObject(smsUrl + "/auth/login", dto, String.class);
//        JsonNode parrent = new ObjectMapper().readTree(response);
//        JsonNode data = parrent.get("data");
//        String token = data.get("token").asText();
        try {
            SmsAuthResponseDTO response = restTemplate.postForObject(smsUrl + "/auth/login", dto, SmsAuthResponseDTO.class);
            return response.getData().getToken();
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
