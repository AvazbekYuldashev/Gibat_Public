package api.gibat.uz.security.service;

import api.gibat.uz.app.service.ResourceBoundleService;
import api.gibat.uz.email.service.EmailHistoryService;
import api.gibat.uz.email.service.EmailSendingService;
import api.gibat.uz.exception.exps.AppBadException;
import api.gibat.uz.exception.exps.ResourceConflictException;
import api.gibat.uz.exception.exps.ResourceNotFoundException;
import api.gibat.uz.exception.exps.ProfileStatusException;
import api.gibat.uz.profile.service.ProfileRoleService;
import api.gibat.uz.profile.service.ProfileService;
import api.gibat.uz.security.dto.*;
import api.gibat.uz.sms.dto.SmsResendDTO;
import api.gibat.uz.sms.dto.SmsVerificationDTO;
import api.gibat.uz.profile.entity.ProfileEntity;
import api.gibat.uz.app.enums.AppLangulage;
import api.gibat.uz.security.enums.GeneralStatus;
import api.gibat.uz.profile.repository.ProfileRepository;
import api.gibat.uz.sms.service.SmsHistoryService;
import api.gibat.uz.sms.service.SmsSendingService;
import api.gibat.uz.email.util.EmailUtil;
import api.gibat.uz.jwt.util.JwtUtil;
import api.gibat.uz.sms.util.SmsUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class AuthService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private BCryptPasswordEncoder bc;
    @Autowired
    private ProfileRoleService profileRoleService;
    @Autowired
    private EmailSendingService emailSendingService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private ResourceBoundleService boundleService;
    @Autowired
    private SmsSendingService smsSendingService;
    @Autowired
    private SmsHistoryService smsHistoryService;
    @Autowired
    private EmailHistoryService emailHistoryService;

    public String registration(RegistrationDTO dto, AppLangulage lang) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());

        if (optional.isPresent()) {
            ProfileEntity profile = optional.get();
            if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                return emailSendingService.sendRegistrationEmail(dto.getUsername(), optional.get().getId(), lang);
            }
            throw new ProfileStatusException(boundleService.getMessage("you.are.not.fully.registered", lang));  // todo UserConflictExeption
        }
        ProfileEntity entity = new ProfileEntity();
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setUsername(dto.getUsername());
        entity.setPassword(bc.encode(dto.getPassword()));
        entity.setStatus(GeneralStatus.IN_REGISTRATION);
        entity.setVisible(true);
        entity.setLangulage(lang);
        ProfileEntity profile = profileRepository.save(entity);
        profileRoleService.createAdmin(entity.getId());

        if (SmsUtil.isPhone(profile.getUsername())) {
            return smsSendingService.sendRegistrationSms(profile.getUsername(), lang);
        }
        if (EmailUtil.isEmail(profile.getUsername())) {
            return emailSendingService.sendRegistrationEmail(profile.getUsername(), profile.getId(), lang);
        }
        return boundleService.getMessage("not.send", lang);
    }

    public String registrationEmailVerification(String token, AppLangulage lang) {
        try {
            String id = JwtUtil.decodeRegVerToken(token);
            ProfileEntity profile = profileService.getById(id, lang);
            if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                // ACTIVE
                profileRepository.changeStatus(profile.getId(), GeneralStatus.ACTIVE);
                return boundleService.getMessage("successfully.registered", lang);
            }
        } catch (JwtException e){}
        throw new AppBadException(boundleService.getMessage("verification.failed", lang));
    }

    public ProfileDTO registrationSmsVerification(SmsVerificationDTO dto, AppLangulage lang) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getPhone());
        if (optional.isEmpty()){
            throw new AppBadException(boundleService.getMessage("username.not.found", lang) + " " + dto.getPhone());
        }
        ProfileEntity profile = optional.get();
        if (!profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
            throw new AppBadException(boundleService.getMessage("you.have.previously.confirmed.your.registration", lang));
        }
        // code check
        smsHistoryService.check(dto.getPhone(), dto.getCode(), lang);
        // ACTIVE
        profileRepository.changeStatus(profile.getId(), GeneralStatus.ACTIVE);
        return getLoginResponse(profile, lang);
    }




    public ProfileDTO login(AuthDTO dto, AppLangulage lang) {
        ProfileEntity profile = profileService.findByUsername(dto.getUsername(), lang);

        if (!bc.matches(dto.getPassword(), profile.getPassword())) {
            throw new ResourceNotFoundException(boundleService.getMessage("username.not.found", lang));
        }
        if (!profile.getStatus().equals(GeneralStatus.ACTIVE)) {
            throw new ResourceNotFoundException(boundleService.getMessage("wrong.status", lang));
        }
        return getLoginResponse(profile, lang);
    }
    public ProfileDTO getLoginResponse(ProfileEntity profile, AppLangulage lang) {
        ProfileDTO response = new ProfileDTO();
        response.setName(profile.getName());
        response.setSurname(profile.getSurname());
        response.setUsername(profile.getUsername());
        response.setRoleList(profileRoleService.getByProfileId(profile.getId(), lang));
        response.setJwt(JwtUtil.encode(profile.getUsername(), profile.getId(), response.getRoleList())); // retnrn jwt
        return response;
    }

    public String registrationSmsVerificationResend(SmsResendDTO dto, AppLangulage lang) {
        ProfileEntity profile = profileService.findByUsername(dto.getPhone(), lang);
        if (!profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
            throw new ResourceConflictException(boundleService.getMessage("you.have.previously.confirmed.your.registration", lang));
        }
        return sendRegistrationResend(dto.getPhone(), dto.getPhone(), lang);
    }

    public String registrationEmailVerificationResend(SmsResendDTO dto, AppLangulage lang) {
        ProfileEntity profile = profileService.findByUsername(dto.getPhone(), lang);
        if (!profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
            throw new ResourceConflictException(boundleService.getMessage("you.have.previously.confirmed.your.registration", lang));
        }
        return sendRegistrationResend(dto.getPhone(), profile.getId(), lang);
    }



    public String resetPassword(ResetPasswordDTO dto, AppLangulage lang) {
        ProfileEntity profile = profileService.findByUsername(dto.getUsername(), lang);
        // check
        if (!profile.getStatus().equals(GeneralStatus.ACTIVE)) {throw new ProfileStatusException(boundleService.getMessage("wrong.status", lang));}
        // email or phoneNumber send confirm message
        return sendResetPassword(profile.getUsername(), lang);
    }

    public String resetPasswordConfrim(ResetPasswordConfirmDTO dto, AppLangulage lang) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isEmpty()){
            throw new ResourceNotFoundException(boundleService.getMessage("username.not.found", lang));
        }
        ProfileEntity profile = optional.get();
        if (!profile.getStatus().equals(GeneralStatus.ACTIVE)) {
            throw new ProfileStatusException(boundleService.getMessage("you.are.not.fully.registered", lang));
        }
        if (SmsUtil.isPhone(profile.getUsername())) {
            smsHistoryService.check(dto.getUsername(), dto.getConfirmCode(), lang);
        }
        if (EmailUtil.isEmail(profile.getUsername())) {
            emailHistoryService.check(dto.getUsername(), dto.getConfirmCode(), lang);

        }
        profileRepository.updatePassword(profile.getId(), bc.encode(dto.getPassword()));
        return boundleService.getMessage("password.changed.successfully", lang);
    }


    // TODO DONE
    private String sendResetPassword(String username, AppLangulage lang) {
        if (SmsUtil.isPhone(username)) {
            return smsSendingService.sendResetPasswordSms(username, lang);
        }
        if (EmailUtil.isEmail(username)) {
            return emailSendingService.sendResetPasswordEmail(username, lang);
        }
        return boundleService.getMessage("not.send", lang);
    }

    private String sendRegistrationResend(String id, String username, AppLangulage lang) {
        if (SmsUtil.isPhone(username)) {
            return smsSendingService.sendRegistrationSms(username, lang);
        }
        if (EmailUtil.isEmail(username)) {
            emailSendingService.sendRegistrationEmail(username, id, lang);
        }
        return boundleService.getMessage("not.send", lang);
    }
}