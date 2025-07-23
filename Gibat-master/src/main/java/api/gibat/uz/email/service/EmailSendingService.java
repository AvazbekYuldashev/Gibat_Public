package api.gibat.uz.email.service;

import api.gibat.uz.app.enums.AppLangulage;
import api.gibat.uz.email.enums.EmailType;
import api.gibat.uz.app.service.ResourceBoundleService;
import api.gibat.uz.exception.exps.AttemptLimitException;
import api.gibat.uz.jwt.util.JwtUtil;
import api.gibat.uz.app.util.RandomUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EmailSendingService {
    @Value("${spring.mail.username}")
    private String fromAccaunt;
    @Value("${server.domain}")
    private String serverDomain;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private ResourceBoundleService boundleService;
    @Autowired
    private EmailHistoryService emailHistoryService;

    public String sendRegistrationEmail(String email, String profileId, AppLangulage lang){
        String subject = "Complete Registration";
        String body = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "    <style>\n" +
                "        a {\n" +
                "            padding: 10px 30px;\n" +
                "            display: inline-block;\n" +
                "        }\n" +
                "        .buttin-link {\n" +
                "            text-decoration: none;\n" +
                "            color: white;\n" +
                "            background-color: indianred;\n" +
                "        }\n" +
                "        .buttin-link:hover {\n" +
                "            background-color: #dd4444;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<h1>Complete Registration</h1>\n" +
                "<p>\n" +
                "    %s\n" +
                "    <a class=\"buttin-link\"\n" +
                "\n" +
                "        href=\"%s/auth/registration/email-verification/%s/%s\" target=\"_blank\">Click there</a>\n" +
                "</p>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
        body = String.format(body, boundleService.getMessage("send.registration.email", lang), serverDomain, JwtUtil.encodeVer(profileId), lang.name());
        sendMimeEmail(email, subject, body);
        return boundleService.getMessage("registration.email.confirm.send", lang);  // TODO DONE
    }

    public String sendResetPasswordEmail(String email, AppLangulage lang){
        String subject = "Reset Password Confirmation";
        // generation code
        String code = RandomUtil.getRandomSmsCode();
        System.out.println(code);

        String body = "How are you mazgi. This is your confirm code for reset password: " + code;

        // check
        checkAndSendMimeEmail(email, subject, body, code, lang);
        return boundleService.getMessage("resend.email.confirm.send", lang);    // TODO DONE
    }


    public void sendUsernameChangeConfirmEmail(String email, AppLangulage lang) {
        String subject = "Change Email Confirmation";
        // generation code
        String code = RandomUtil.getRandomSmsCode();
        System.out.println(code);

        String body = "How are you mazgi. This is your confirm code for Change email: " + code;

        // check
        checkAndSendMimeEmail(email, subject, body, code, lang);

    }

    public void checkAndSendMimeEmail(String email, String subject, String body, String code, AppLangulage lang){
        // check count
        Long count = emailHistoryService.getEmailCount(email);
        if (count >= 3){
            throw new AttemptLimitException(boundleService.getMessage("the.number.of.attempts.exceeded", lang));    // TODO DONE
        }
        // send
        sendMimeEmail(email, subject, body);
        // save
        emailHistoryService.save(email, code, EmailType.PASSWORD_RESET);
    }

    public void sendMimeEmail(String email, String subject, String body){
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            mimeMessage.setFrom(fromAccaunt);
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true);

            CompletableFuture.runAsync(() -> {
                mailSender.send(mimeMessage);
            });

        } catch (MessagingException e){
            throw new RuntimeException(e);
        }
    }

    public void sendSimpleEmail(String email, String subject, String body){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAccaunt);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

}