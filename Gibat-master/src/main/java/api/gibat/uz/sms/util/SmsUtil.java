package api.gibat.uz.sms.util;

import java.util.regex.Pattern;

public class SmsUtil {
    public static boolean isPhone(String value){
        String phoneRegex = "^998\\d{9}$";
        return Pattern.matches(phoneRegex, value);
    }
}
