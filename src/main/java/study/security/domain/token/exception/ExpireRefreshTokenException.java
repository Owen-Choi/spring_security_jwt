package study.security.domain.token.exception;

public class ExpireRefreshTokenException extends IllegalArgumentException{
    public ExpireRefreshTokenException() {
        super("EXPIRED_REFRESH_TOKEN");
    }

    public ExpireRefreshTokenException(String s) {
        super(s);
    }
}
