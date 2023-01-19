package study.security.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import study.security.domain.token.dto.TokenDTO;
import study.security.global.common.constants.JwtConstants;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
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
        // TODO 정확히 이해가 안간다. - 아래 코드의 결과로 ROLE_USER가 나오는건가??
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
    public Authentication getAuthentication(String accessToken) {
        // 토근 복호화
        Claims claims = parseClaims(accessToken);

        if(claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        /*
            클레임에서 권한 정보 가져오기
            Claim이란 사용자에 대한 프로퍼티나 속성을 이야기함
            JWT는 Claim 기반임. 토큰 자체가 Claim (사용자에 대한 프로퍼티나 속성)이 됨
            예시 :
                    {
                        "id":"terry"
                        ,"role":["admin","user"]
                        ,"company":"pepsi"
                    }
         */
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    private Claims parseClaims(String accessToken) {
        try{
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        }catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public boolean validateToken(String token) {
        try {
            // 이 코드는 뭐하는 코드지??
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원하지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}
