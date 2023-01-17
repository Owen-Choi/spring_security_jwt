package study.security.global.common.constants;

public class JwtConstants {

    public static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;   // 7일 이라고 한다.
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;             // 30분 이라고 한다.


    public static final String AUTHORITIES_KEY = "auth";
    public static final String BEARER_TYPE = "Bearer";
}
