package api.gibat.uz.sms.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsAuthResponseDTO {
    private String message;
    private SmsAuthDataDTO data;
    private String token_type;
}
