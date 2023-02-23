package study.security.domain.member.exception;

public class UserNotFoundExpcetion extends IllegalArgumentException{
    public UserNotFoundExpcetion() {
        super("해당 사용자가 존재하지 않습니다.");
    }

    public UserNotFoundExpcetion(String msg) {
        super(msg);
    }
}
