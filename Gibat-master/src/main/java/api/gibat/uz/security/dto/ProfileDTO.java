package api.gibat.uz.security.dto;

import api.gibat.uz.profile.enums.ProfileRole;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProfileDTO {
    private String name;
    private String surname;
    private String username;

    private List<ProfileRole> roleList;
    private String jwt;
}
