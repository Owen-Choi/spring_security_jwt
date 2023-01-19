package study.security.global.util;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import study.security.domain.member.exception.NotAuthorizedException;

@Slf4j
@NoArgsConstructor
public class SecurityUtil {
    // SecurityContext에 유저 정보가 저장되는 시점
    // Request가 들어올때 JwtFilter의 doFilter에서 저장됨
    // TODO 세부 원리를 조금 더 딥하게 공부해봐야 할거같다. Spring security가 관여하는 부분으로 알고있음.
    public static Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication.getName() == null) {
            throw new NotAuthorizedException();
        }
        return Long.parseLong(authentication.getName());
    }
}
