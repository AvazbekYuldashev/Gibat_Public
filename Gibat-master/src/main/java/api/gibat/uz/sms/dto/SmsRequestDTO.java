package api.gibat.uz.sms.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsRequestDTO {
    private String mobile_phone;
    private String message;
    private String from;
}
