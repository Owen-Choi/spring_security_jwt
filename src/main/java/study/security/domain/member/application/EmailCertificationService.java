package study.security.domain.member.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import study.security.domain.member.dto.MemberDTO;
import study.security.domain.member.exception.EmailCertificationExpireException;
import study.security.global.common.constants.EmailConstants;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static study.security.domain.member.dto.MemberDTO.*;
import static study.security.global.common.constants.EmailConstants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailCertificationService {
    private final JavaMailSender emailSender;
    private final RedisTemplate<String, String> redisTemplate;

    // 이메일 인증번호
    private String ePw;

    public MimeMessage createMessage(String to) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(Message.RecipientType.TO, to);
        message.setSubject("security jwt 학습용 이메일 인증");  // 제목

        String msg = "";
        msg += "<div style = 'margin:100px;'>";
        msg += "<h1> 안녕하세요 </h1>";
        msg += "<h1> 시큐리티 jwt 실습용 이메일입니다.</h1>";
        msg += "<br>";
        msg += "<p>인증번호는 아래와 같습니다.</p>";
        msg += "<br>";
        msg += "<div align='center' style = 'border:1px solid black; font-family:verdana';>";
        msg += "<h3 style = 'color:blue;'>인증 코드</h3>";
        msg += "<div style='font-style:130%'>";
        msg += "CODE: <strong>";
        msg += ePw + "</strong><div><br/>";
        msg += "</div>";
        message.setText(msg, "utf-8", "html");
        message.setFrom(new InternetAddress("security_jwt@naver.com", "securityJwtAdmin"));
        return message;
    }

    public String createKey() {
        StringBuffer key = new StringBuffer();
        Random random = new Random();

        for(int i=0; i<8; i++) {
            int index = random.nextInt(3);

            switch(index) {
                case 0 :
                    key.append((char) random.nextInt(26) + 97);
                    break;
                case 1:
                    key.append((char) random.nextInt(26) + 65);
                    break;
                case 2:
                    key.append((random.nextInt(10)));
                    break;
            }
        }
        return key.toString();
    }

    // 메일 발송
    public void sendSimpleMessage(String to) throws Exception {
        ePw = createKey();
        MimeMessage message = createMessage(to);
        try {
            emailSender.send(message);
            log.info("secret code = " + ePw);
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(to, ePw);
            redisTemplate.expire(to, EMAIL_CERTIFICATION_TIME, TimeUnit.MILLISECONDS);
        } catch(MailException es) {
            log.info(es.getLocalizedMessage());
            throw new IllegalArgumentException(es.getMessage());
        }
    }

    // 코드 검증
    public CodeConfirmDto confirmCode(EmailConfirmCodeDto emailConfirmCodeDto) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String code = valueOperations.get(emailConfirmCodeDto.getEmail());
        // 코드가 없으면 만료된 것.
        if(code == null) {
            throw new EmailCertificationExpireException();
        }
        // 코드가 다르면 잘못 입력한 것.
        if(!code.equals(emailConfirmCodeDto.getCode())) {
            return CodeConfirmDto.builder().matches(false).build();
        }
        return CodeConfirmDto.builder().matches(true).build();
    }
}
