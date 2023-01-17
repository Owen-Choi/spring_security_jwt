package study.security.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import study.security.domain.token.dto.TokenDTO;
import study.security.global.common.constants.JwtConstants;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

import static java.lang.System.getenv;
import static study.security.domain.token.dto.TokenDTO.*;
import static study.security.global.common.constants.JwtConstants.*;

@Component
@Slf4j
public class JwtTokenProvider {
    private final Key key;

    // 생성자에서 key값을 환경변수로 받아옴
    public JwtTokenProvider() {
        byte[] keyBytes = Decoders.BASE64.decode(getenv("JWT_SECRET"));
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenInfoDTO generateTokenDto(Authentication authentication) {
        // 권한들 가져오기
        // TODO 정확히 이해가 안간다.
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())       // payload "sub" : "name:
                .claim(AUTHORITIES_KEY, authorities)        // payload "auth" : "ROLE_USER"
                .setExpiration(accessTokenExpiresIn)        // payload "exp" : 1516239022 (예시)
                .signWith(key, SignatureAlgorithm.HS512)    // header "alg" : "HS512"
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenInfoDTO.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    // 토큰을 넘기면 확인해서 권한을 부여하는 코드
//    public Authentication getAuthentication(String accessToken) {
//        // 토근 복호화
//    }
}
