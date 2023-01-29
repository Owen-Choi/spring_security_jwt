package study.security.domain.member.exception;

public class UserNotFoundByUsernameAndPhoneException extends IllegalArgumentException{

    public UserNotFoundByUsernameAndPhoneException() {
        super("사용자명과 휴대폰 번호로 존재하는 사용자가 없습니다.");
    }

    public UserNotFoundByUsernameAndPhoneException(String s) {
        super(s);
    }
}
