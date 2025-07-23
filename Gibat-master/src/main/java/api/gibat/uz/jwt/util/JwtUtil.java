package api.gibat.uz.jwt.util;

import api.gibat.uz.jwt.dto.JwtDTO;
import api.gibat.uz.profile.enums.ProfileRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private static final int tokenLiveTimeOneHour = 1000 * 3600;    // 1-hour
    private static final int tokenLiveTimeOneDay = 1000 * 3600 * 24; // 1-day
    private static final String secretKey = "veryLongSecretmazgillattayevlasharaaxmojonjinnijonsurbetbekkiydirhonuxlatdibekloxovdangasabekochkozjonduxovmashaynikmaydagapchishularnioqiganbolsangizgapyoqaniqsizmazgi";


    public static String encode(String username, String id, List<ProfileRole> roleList) {
        String strRoles = roleList.stream().map(Enum::name)
                .collect(Collectors.joining(","));

        return Jwts
                .builder()
                .subject(username)
                .claim("role", strRoles)
                .claim("id", id)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenLiveTimeOneDay))
                .signWith(getSignInKey())
                .compact();
    }

    public static JwtDTO decode(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String username = claims.getSubject();
        String strRole = (String) claims.get("role");
        String id = (String) claims.get("id");
        List<ProfileRole> roleList = new ArrayList<>();

        Arrays.stream(strRole.split(","))
                .map(ProfileRole::valueOf)
                .toList();

        return new JwtDTO(id, username, roleList);
    }

    public static String encodeVer(String id) {

        return Jwts
                .builder()
                .subject(String.valueOf(id))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenLiveTimeOneHour))
                .signWith(getSignInKey())
                .compact();
    }
    public static String decodeRegVerToken(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    private static SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
