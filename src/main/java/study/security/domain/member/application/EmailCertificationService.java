package study.security.domain.member.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;

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

        }
    }
}
