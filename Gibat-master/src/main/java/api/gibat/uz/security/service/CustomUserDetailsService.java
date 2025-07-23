package api.gibat.uz.security.service;


import api.gibat.uz.app.service.ResourceBoundleService;
import api.gibat.uz.security.config.CustomUserDetails;
import api.gibat.uz.profile.entity.ProfileEntity;
import api.gibat.uz.profile.enums.ProfileRole;
import api.gibat.uz.profile.repository.ProfileRepository;
import api.gibat.uz.profile.service.ProfileRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ProfileRoleService profileRoleService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername: " + username);
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(username);
        if (optional.isEmpty()) {
            throw new UsernameNotFoundException("Username not found");
        }
        ProfileEntity profile = optional.get();
        List<ProfileRole> roleList = profileRoleService.getByProfileId(profile.getId(), profile.getLangulage());
        return new CustomUserDetails(profile, roleList);
    }
}