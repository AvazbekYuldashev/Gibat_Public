package api.gibat.uz.security.config;

import api.gibat.uz.profile.entity.ProfileEntity;
import api.gibat.uz.security.enums.GeneralStatus;
import api.gibat.uz.profile.enums.ProfileRole;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Getter
@Setter
public class CustomUserDetails implements UserDetails {

    private String id;
    private String name;
    private String surname;
    private String username;
    private String password;
    private GeneralStatus status;
    private Collection<? extends GrantedAuthority> authorities;
    private Boolean visible;
    private Integer employeeId;
    private String attachId;


    public CustomUserDetails(ProfileEntity profile, List<ProfileRole> roleList) {
        this.id = profile.getId();
        this.name = profile.getName();
        this.surname = profile.getSurname();
        this.username = profile.getUsername();
        this.password = profile.getPassword();
        this.status = profile.getStatus();
        List<SimpleGrantedAuthority> roles = new ArrayList<>();
        for (ProfileRole role : roleList) {roles.add(new SimpleGrantedAuthority(role.name()));}
        roleList.stream().map(item -> new SimpleGrantedAuthority(item.name())).forEach(roles::add);
        this.authorities = roles;
        this.visible = profile.getVisible();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status.equals(GeneralStatus.ACTIVE);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    @Override
    public boolean isEnabled() {
        return true;
    }
}