package study.security.domain.member.exception;

public class DuplicateNicknameException extends IllegalArgumentException{
    public DuplicateNicknameException() {
        super("동일한 닉네임이 존재합니다.");
    }

    public DuplicateNicknameException(String s) {
        super(s);
    }
}
