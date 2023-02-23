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
import study.security.global.util.SecurityUtil;

import java.io.IOException;
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

        /*
            Spring Security에서 인증 처리가 일어나는 과정을 코드로 나타내면
            1. username 과 password를 조합해서 UsernamePasswordAuthenticationToken 인스턴스를 만든다.
            2. 검증을 위해 AuthenticationManager의 인스턴스에 1번에서 만든 인스턴스를 넘겨준다.
            3. AuthenticationManager는 인증에 성공하면 Authentication의 인스턴스를 반환한다.
            4. 3번에서 반환받은 인스턴스를 SecurityContextHolder.getContext().setAuthentication() 해준다.
            아래 코드에서는 4번 과정이 명시적으로 존재하진 않는데, 어디서 context에 등록을 해주는거지?
         */

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

        /*
            목표는 jwt를 생성하는 것.
            jwt를 생성하려면 authentication 객체가 필요함.
            => authentication 객체를 생성하기 위해서는 이메일, 비밀번호가 필요함(우리의 경우)
         */
    }


    // refresh가 만료되지 않고 존재한다면 Access를 재발급해서 DB(Redis)에 저장해주는 코드
    @Transactional
    public TokenIssueDTO reissue(AccessTokenDTO accessTokenDTO) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String refreshByAccess = valueOperations.get(accessTokenDTO.getAccessToken());
        // refreshToken이 없으면 만료됐다는 뜻이므로 만료 예외 터뜨려줌.
        if (refreshByAccess == null) {
            throw new ExpireRefreshTokenException();
        }

        // refresh token이 존재한다면 검증을 실시한다.
        if(!jwtTokenProvider.validateToken(refreshByAccess)) {
            throw new InvalidRefreshTokenException();
        }

        // Access Token 에서 멤버 아이디 가져오기
        // jwtTokenProvider.getAuthentication 내부에서 Claim 정보 (토큰 자체에 유저 정보가 있음)를 조회하여 authentication을 발급해준다.
        // 따라서 얻어온 authentication으로 TokenInfoDto를 발급받아서 redis에 저장하면 해당 사용자의 토큰으로서 사용할 수 있는 것이다.
        Authentication authentication = jwtTokenProvider.getAuthentication(accessTokenDTO.getAccessToken());

        // refresh Token이 만료되지 않았으므로 새로운 Access Token을 발급한다.
        TokenInfoDTO tokenInfoDTO = jwtTokenProvider.generateTokenDto(authentication);

        // 새로 발급한 토큰을 redis에 저장하고,
        valueOperations.set(tokenInfoDTO.getAccessToken(), tokenInfoDTO.getRefreshToken());

        // redis상에서 만료 시간을 설정해줌.
        redisTemplate.expire(tokenInfoDTO.getAccessToken(), REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        // 위의 과정을 모두 거친 뒤 토큰을 발급함
        // 토큰 발급
        return tokenInfoDTO.toTokenIssueDTO();
    }

    @Transactional
    public String logout(String token) throws IOException {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.getAndDelete(Long.toString(SecurityUtil.getCurrentMemberId()));
        valueOperations.getAndDelete(token);

        return "SUCCESS";
    }
}
