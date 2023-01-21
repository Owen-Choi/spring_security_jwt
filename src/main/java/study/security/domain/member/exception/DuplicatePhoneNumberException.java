package study.security.domain.member.exception;

public class DuplicatePhoneNumberException extends IllegalArgumentException{
    public DuplicatePhoneNumberException() {
        super("동일한 전화번호가 존재합니다.");
    }

    public DuplicatePhoneNumberException(String s) {
        super(s);
    }
}
