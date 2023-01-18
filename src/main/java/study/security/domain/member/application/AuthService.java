package study.security.domain.member.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.security.domain.member.dao.MemberRepository;
import study.security.domain.member.model.Member;
import study.security.domain.token.dto.TokenDTO;
import study.security.domain.token.exception.ExpireRefreshTokenException;
import study.security.domain.token.exception.InvalidRefreshTokenException;
import study.security.global.common.constants.JwtConstants;
import study.security.global.error.exception.NotFoundByIdException;
import study.security.global.jwt.JwtTokenProvider;

import java.util.concurrent.TimeUnit;

import static study.security.domain.member.dto.MemberDTO.*;
import static study.security.domain.token.dto.TokenDTO.*;
import static study.security.global.common.constants.JwtConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public String signUp(SignUpRequest signUpRequest) {
        signUpRequest.encrypt(passwordEncoder);
        Member member = signUpRequest.toEntity();
        memberRepository.save(member);
        return "CREATED";
    }

    @Transactional
    public UserLoginDTO login(LoginRequest loginRequest) {
        // 1. 이메일, 비밀번호 기반으로 토큰 생성
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = loginRequest.toAuthentication();

        // 2. 실제로 검증이 이루어지는 부분.
        // authenticate 메서드가 실행이 될 때 CustomUserDetailsService에서 만들었던 loadUserByUsername 메서드가 실행됨.
        // TODO 원리 공부해보기
        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(usernamePasswordAuthenticationToken);

        // 3. 인증 정보를 기반으로 jwt 토큰 생성
        TokenInfoDTO tokenInfoDTO = jwtTokenProvider.generateTokenDto(authenticate);

        // 4. refresh token 저장
        // opsForValue는 Strings를 쉽게 직렬화, 역직렬화 시켜주는 인터페이스
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(authenticate.getName(), tokenInfoDTO.getRefreshToken());
        valueOperations.set(tokenInfoDTO.getAccessToken(), tokenInfoDTO.getRefreshToken());
        redisTemplate.expire(authenticate.getName(), REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        // refresh가 살아있으면 access를 재발급할 수 있다.
        redisTemplate.expire(tokenInfoDTO.getAccessToken(), REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        Member member = memberRepository.findById(Long.parseLong(authenticate.getName())).orElseThrow(NotFoundByIdException::new);

        return UserLoginDTO.builder()
                .userInfo(member.toUserInfo())
                .tokenInfo(tokenInfoDTO.toTokenIssueDTO())
                .build();
    }

    @Transactional
    public TokenIssueDTO reissue(AccessTokenDTO accessTokenDTO) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String refreshByAccess = valueOperations.get(accessTokenDTO.getAccessToken());
        if (refreshByAccess == null) {
            throw new ExpireRefreshTokenException();
        }

        // refresh token이 존재한다면 검증을 실시한다.
        if(!jwtTokenProvider.validateToken(refreshByAccess)) {
            throw new InvalidRefreshTokenException();
        }

        // Access Token 에서 멤버 아이디 가져오기
        Authentication authentication = jwtTokenProvider.getAuthentication(accessTokenDTO.getAccessToken());
    }

}
