package oleh.study.spring.bservice.bisnesslogic.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class InitialAuthenticationFilter extends OncePerRequestFilter {
    private AuthenticationManager authenticationManager;

    @Value("${jwt.signing.key}")
    private String signingKey;

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String username = httpServletRequest.getHeader("username");
        String password = httpServletRequest.getHeader("password");
        String code = httpServletRequest.getHeader("code");

        if (code == null) {
            Authentication authentication =
                    new UsernamePasswordAuthentication(username, password);
            authenticationManager.authenticate(authentication);
        } else {
            Authentication authentication =
                    new OtpAuthentication(username, code);
            authenticationManager.authenticate(authentication);

            SecretKey key = Keys.hmacShaKeyFor(
                    signingKey.getBytes(StandardCharsets.UTF_8)
            );

            Map<String, String> userData = new HashMap<>();
            userData.put("username", username);

            String jwt = Jwts.builder()
                    .setClaims(userData)
                    .signWith(key)
                    .compact();

            httpServletResponse.setHeader("Authorization", jwt);
        }

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().equals("/login");
    }
}
