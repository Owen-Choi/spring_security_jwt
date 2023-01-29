package study.security.domain.member.exception;

import study.security.domain.member.application.EmailCertificationService;

public class EmailCertificationExpireException extends IllegalArgumentException{
    public EmailCertificationExpireException() {
        super("코드가 만료되었습니다.");
    }

    public EmailCertificationExpireException(String s) {
        super(s);
    }
}
