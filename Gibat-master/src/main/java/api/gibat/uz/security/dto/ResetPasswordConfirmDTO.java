package api.gibat.uz.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordConfirmDTO {
    private String username;
    private String confirmCode;
    private String password;
}
