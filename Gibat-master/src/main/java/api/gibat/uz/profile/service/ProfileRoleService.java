package api.gibat.uz.profile.service;

import api.gibat.uz.app.enums.AppLangulage;
import api.gibat.uz.app.service.ResourceBoundleService;
import api.gibat.uz.exception.exps.ResourceNotFoundException;
import api.gibat.uz.profile.entity.ProfileRoleEntity;
import api.gibat.uz.profile.enums.ProfileRole;
import api.gibat.uz.profile.repository.ProfileRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProfileRoleService {

    @Autowired
    private ProfileRoleRepository profileRoleRepository;
    @Autowired
    private ResourceBoundleService boundleService;

    public List<ProfileRole> getByProfileId(String id, AppLangulage lang) {
        List<ProfileRoleEntity> optional = profileRoleRepository.getAllByProfileIdAndVisibleTrue(id);
        if (optional.isEmpty()) {
            throw new ResourceNotFoundException(boundleService.getMessage("profile.role.not.found", lang)+ ": " + id);
        }
        List<ProfileRole> profileRoles = new ArrayList<>();
        for (ProfileRoleEntity entity : optional) {
            profileRoles.add(entity.getRole());
        }
        return profileRoles;
    }

    public ProfileRoleEntity createUser(String id, AppLangulage lang) {
        getByProfileId(id, lang);
        ProfileRoleEntity entity = new ProfileRoleEntity();
        entity.setProfileId(id);
        entity.setRole(ProfileRole.ROLE_USER);
        return profileRoleRepository.save(entity);
    }

    public String createAdmin(String id) {
        ProfileRoleEntity entity = new ProfileRoleEntity();
        entity.setProfileId(id);
        entity.setRole(ProfileRole.ROLE_ADMIN);
        return profileRoleRepository.save(entity).getId();
    }


    public void deleteRoles(String profileId) {
        profileRoleRepository.deleteByProfileId(profileId);
    }


}
