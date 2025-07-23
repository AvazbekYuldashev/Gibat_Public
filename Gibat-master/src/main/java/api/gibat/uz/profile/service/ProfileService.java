package api.gibat.uz.profile.service;

import api.gibat.uz.app.dto.AppResponse;
import api.gibat.uz.app.service.ResourceBoundleService;
import api.gibat.uz.email.service.EmailHistoryService;
import api.gibat.uz.email.service.EmailSendingService;
import api.gibat.uz.exception.exps.AppBadException;
import api.gibat.uz.exception.exps.ResourceConflictException;
import api.gibat.uz.exception.exps.ResourceNotFoundException;
import api.gibat.uz.security.dto.CodeConfirmDTO;
import api.gibat.uz.profile.dto.ProfileDetailUpdateDTO;
import api.gibat.uz.profile.dto.ProfilePasswordUpdate;
import api.gibat.uz.profile.dto.ProfileUsernameUpdateDTO;
import api.gibat.uz.profile.entity.ProfileEntity;
import api.gibat.uz.profile.entity.ProfileRoleEntity;
import api.gibat.uz.app.enums.AppLangulage;
import api.gibat.uz.security.enums.GeneralStatus;
import api.gibat.uz.profile.enums.ProfileRole;
import api.gibat.uz.profile.repository.ProfileRepository;
import api.gibat.uz.profile.repository.ProfileRoleRepository;
import api.gibat.uz.sms.service.SmsHistoryService;
import api.gibat.uz.sms.service.SmsSendingService;
import api.gibat.uz.email.util.EmailUtil;
import api.gibat.uz.jwt.util.JwtUtil;
import api.gibat.uz.sms.util.SmsUtil;
import api.gibat.uz.security.util.SpringSecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ResourceBoundleService boundleService;
    @Autowired
    private BCryptPasswordEncoder bc;
    @Autowired
    private EmailSendingService emailSendingService;
    @Autowired
    private SmsSendingService smsSendingService;
    @Autowired
    private SmsHistoryService smsHistoryService;
    @Autowired
    private EmailHistoryService emailHistoryService;
    @Autowired
    private ProfileRoleRepository profileRoleRepository;


    public AppResponse<String> updateDetail(ProfileDetailUpdateDTO dto, AppLangulage lang) {
        String id = SpringSecurityUtil.getCurrentUserId();
        profileRepository.updateDetail(id, dto.getName(), dto.getSurname());
        return new AppResponse<>("DONE");
    }


    public AppResponse<String> updatePassword(@Valid ProfilePasswordUpdate dto, AppLangulage lang) {
        String id = SpringSecurityUtil.getCurrentUserId();
        ProfileEntity profile = getById(id, lang);
        if (!bc.matches(dto.getOldPassword(), profile.getPassword())){
            throw new AppBadException(boundleService.getMessage("wrong.password", lang));
        }
        profileRepository.updatePassword(id, bc.encode(dto.getNewPassword()));
        return new AppResponse<>("DONE");
    }

    public AppResponse<String> updateUsername(@Valid ProfileUsernameUpdateDTO dto, AppLangulage lang) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isPresent()){
            if (!optional.get().getUsername().equals(SpringSecurityUtil.getCurrentProfile().getUsername())){
                throw new ResourceConflictException(boundleService.getMessage("username.conflict", lang));
            }
        }
        if (SmsUtil.isPhone(dto.getUsername())){
            smsSendingService.sendUsernameChangeConfirmSms(dto.getUsername(), lang);
        }
        if (EmailUtil.isEmail(dto.getUsername())){
            emailSendingService.sendUsernameChangeConfirmEmail(dto.getUsername(), lang);
        }
        profileRepository.updateTempUsername(SpringSecurityUtil.getCurrentUserId(), dto.getUsername());
        return new AppResponse<>("SEND CHANGE USERNAME CONFIRM CODE");

    }

    public ProfileEntity getById(String id, AppLangulage lang) {
        Optional<ProfileEntity> optional = profileRepository.findByIdAndVisibleTrue(id);
        if (optional.isEmpty()){
            throw new ResourceNotFoundException(boundleService.getMessage("profile.not.found", lang) + ": " + id);
        }
        return optional.get();
    }

    public void delete(ProfileEntity entity) {
        profileRepository.delete(entity);
    }

    public void updateStatus(String id, GeneralStatus status){
        profileRepository.changeStatus(id, status);
    }

    public AppResponse<String> updateUsernameConfirm(@Valid CodeConfirmDTO dto, AppLangulage lang) {

        ProfileEntity profile = getById(SpringSecurityUtil.getCurrentUserId(), lang);
        if (SmsUtil.isPhone(profile.getUsername())){
            smsHistoryService.check(profile.getTempUsername(),dto.getCode(), lang);
        }
        if (EmailUtil.isEmail(profile.getUsername())){
            emailHistoryService.check(profile.getTempUsername(), dto.getCode(), lang);
        }

        profileRepository.updateUsername(SpringSecurityUtil.getCurrentUserId(), profile.getTempUsername());

        List<ProfileRoleEntity> roles = profileRoleRepository.getAllByProfileIdAndVisibleTrue(SpringSecurityUtil.getCurrentUserId());
        List<ProfileRole> roleList = new java.util.ArrayList<>();
        for (ProfileRoleEntity entity : roles) {
            roleList.add(entity.getRole());
        }

        return new AppResponse<>(JwtUtil.encode(profile.getTempUsername(), profile.getId(), roleList));
    }

    public ProfileEntity findByUsername(String username, AppLangulage lang) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(username);
        if (optional.isEmpty()) {
            throw new ResourceNotFoundException(boundleService.getMessage("username.not.found", lang));
        }
        return optional.get();
    }
}
