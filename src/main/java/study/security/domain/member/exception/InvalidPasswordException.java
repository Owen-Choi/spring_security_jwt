package study.security.domain.member.exception;

public class InvalidPasswordException extends IllegalArgumentException {
    public InvalidPasswordException() {
        super("비밀번호가 일치하지 않습니다.");
    }

    public InvalidPasswordException(String s) {
        super(s);
    }
}
