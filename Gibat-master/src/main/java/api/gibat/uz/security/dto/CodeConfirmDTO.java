package api.gibat.uz.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeConfirmDTO {
    @NotBlank(message = "code required")
    private String code;
}
