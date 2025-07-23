package api.gibat.uz.security.controller;

import api.gibat.uz.security.dto.*;
import api.gibat.uz.sms.dto.SmsResendDTO;
import api.gibat.uz.sms.dto.SmsVerificationDTO;
import api.gibat.uz.app.enums.AppLangulage;
import api.gibat.uz.security.service.AuthService;
import api.gibat.uz.sms.service.SmsSendingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private SmsSendingService smsSendingService;

    @Operation(summary = "Create User", description = "Api used for creating new User")
    @PostMapping("/registration")
    public ResponseEntity<String> registration(@Valid @RequestBody RegistrationDTO dto,
                                               @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLangulage lang) {
        return ResponseEntity.ok().body(authService.registration(dto, lang));
    }

    @Operation(summary = "login by username and password", description = "API used for Login")
    @GetMapping("/login")
    public ResponseEntity<ProfileDTO> login(@Valid @RequestBody AuthDTO dto,
                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLangulage lang) {
        return ResponseEntity.ok().body(authService.login(dto, lang));
    }

    @Operation(summary = "verification by link", description = "API used for verification")
    @GetMapping("/registration/email-verification/{token}/{lang}")
    public ResponseEntity<String> emailRegVerification(@PathVariable("token") String token,
                                                  @PathVariable("lang") AppLangulage lang) {
        return ResponseEntity.ok().body(authService.registrationEmailVerification(token, lang));
    }

    @Operation(summary = "verification by link", description = "API used for verification")
    @GetMapping("/registration/sms-verification")
    public ResponseEntity<ProfileDTO> smsRegVerification(@RequestBody SmsVerificationDTO dto,
                                                        @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLangulage lang) {
        return ResponseEntity.ok().body(authService.registrationSmsVerification(dto, lang));
    }

    @Operation(summary = "verification by link", description = "API used for verification")
    @GetMapping("/registration/sms-verification-resend")
    public ResponseEntity<String> smsVerificationResend(@RequestBody SmsResendDTO dto,
                                                         @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLangulage lang) {
        return ResponseEntity.ok().body(authService.registrationSmsVerificationResend(dto, lang));
    }

    @Operation(summary = "verification by link", description = "API used for verification")
    @GetMapping("/registration/email-verification-resend")
    public ResponseEntity<String> emailVerificationResend(@RequestBody SmsResendDTO dto,
                                                        @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLangulage lang) {
        return ResponseEntity.ok().body(authService.registrationEmailVerificationResend(dto, lang));
    }

    @Operation(summary = "login by username and password", description = "API used for Login")
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordDTO dto,
                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLangulage lang) {
        return ResponseEntity.ok().body(authService.resetPassword(dto, lang));
    }


    @Operation(summary = "login by username and password", description = "API used for Login")
    @PostMapping("/reset-password-confrim")
    public ResponseEntity<String> resetPasswordConfrim(@Valid @RequestBody ResetPasswordConfirmDTO dto,
                                                @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLangulage lang) {
        return ResponseEntity.ok().body(authService.resetPasswordConfrim(dto, lang));
    }


}
