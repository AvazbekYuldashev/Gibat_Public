package api.gibat.uz.sms.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsSendResponseDTO {
    private String id;
    private String message;
    private String status;
}
