package vn.edu.uit.socialjob.platform.modules.auth.provider;

import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;

@Component
public class JwtProvider {
    // Lưu ý: Key này phải dài ít nhất 32 ký tự. Trong thực tế nên để ở application.yml
    private final String SECRET_KEY = "Bdpldka0TPHYTRpaCNANqTZMd5mlYweYnWSnRuWPhB6";
    private final long JWT_EXPIRATION = 86400000L; 
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(user.getId().toString()) // Lưu ID user vào Subject
                .claim("email", user.getEmail())     // Thêm thông tin bổ sung (Payload)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getSubject(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    // Sau này ông sẽ cần hàm này để verify token khi User gọi API khác
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}