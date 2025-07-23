package api.gibat.uz.profile.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfilePasswordUpdate {
    private String oldPassword;
    private String newPassword;
}
