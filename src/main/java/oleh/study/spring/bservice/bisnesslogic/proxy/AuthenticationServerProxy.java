package oleh.study.spring.bservice.bisnesslogic.proxy;

import oleh.study.spring.bservice.bisnesslogic.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Component
public class AuthenticationServerProxy {
    public final static String APP_KEY_HEADER = "application-key";

    private final RestTemplate restTemplate;
    private HttpHeaders appKeyHeader;

    @Value("${auth.server.base.url}")
    private String baseUrl;

    @Value("${application.key}")
    private String applicationKey;

    @PostConstruct
    public void initializeHeader() {
        appKeyHeader = new HttpHeaders();
        appKeyHeader.set(APP_KEY_HEADER, applicationKey);
    }
    public AuthenticationServerProxy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendAuth(String username, String password) {
        String url = baseUrl + "/user/auth";

        User user = new User(username, password, null);

        HttpEntity<User> request = new HttpEntity<>(user, appKeyHeader);

        restTemplate.postForEntity(url, request, Void.class);
    }

    public boolean sendOtp(String username, String code) {
        String url = baseUrl + "/otp/check";

        User user = new User(username, null, code);

        HttpEntity<User> request = new HttpEntity<>(user, appKeyHeader);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity(url, request, Void.class);

        return responseEntity.getStatusCode().equals(HttpStatus.OK);
    }
}
