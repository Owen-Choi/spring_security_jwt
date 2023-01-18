package study.security.domain.token.exception;

public class InvalidRefreshTokenException extends IllegalArgumentException{
    public InvalidRefreshTokenException() {
        super("EXPIRED_REFRESH_TOKEN");
    }

    public InvalidRefreshTokenException(String s) {
        super(s);
    }
}
