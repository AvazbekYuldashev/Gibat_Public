package api.gibat.uz.profile.controller;

import api.gibat.uz.app.dto.AppResponse;
import api.gibat.uz.security.dto.CodeConfirmDTO;
import api.gibat.uz.profile.dto.ProfileDetailUpdateDTO;
import api.gibat.uz.profile.dto.ProfilePasswordUpdate;
import api.gibat.uz.profile.dto.ProfileUsernameUpdateDTO;
import api.gibat.uz.app.enums.AppLangulage;
import api.gibat.uz.profile.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    @PutMapping("/detail")
    public ResponseEntity<AppResponse<String>> update(@Valid @RequestBody ProfileDetailUpdateDTO dto,
                                                      @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLangulage lang){
        return ResponseEntity.ok().body(profileService.updateDetail(dto, lang));
    }

    @PutMapping("/password")
    public ResponseEntity<AppResponse<String>> updatePassword(@Valid @RequestBody ProfilePasswordUpdate dto,
                                                      @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLangulage lang){
        return ResponseEntity.ok().body(profileService.updatePassword(dto, lang));
    }

    @PutMapping("/username")
    public ResponseEntity<AppResponse<String>> updateUsername(@Valid @RequestBody ProfileUsernameUpdateDTO dto,
                                                              @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLangulage lang){
        return ResponseEntity.ok().body(profileService.updateUsername(dto, lang));
    }
    @PutMapping("/username/confirm")
    public ResponseEntity<AppResponse<String>> updateUsernameConfirm(@Valid @RequestBody CodeConfirmDTO dto,
                                                                     @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLangulage lang){
        return ResponseEntity.ok().body(profileService.updateUsernameConfirm(dto, lang));
    }

}
