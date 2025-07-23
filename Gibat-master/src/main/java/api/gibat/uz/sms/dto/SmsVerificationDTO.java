package api.gibat.uz.sms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsVerificationDTO {
    @NotBlank(message = "phone required")
    private String phone;
    @NotBlank(message = "code required")
    private String code;
}
