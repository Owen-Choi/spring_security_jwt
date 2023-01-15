package study.security.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import study.security.domain.member.model.Member;

public class MemberDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUpRequest {
        private String userName;
        private String nickname;
        private String phoneNumber;
        private String email;
        private String password;
        private String birthDate;

        public void encrypt(PasswordEncoder passwordEncoder) {
            this.password = passwordEncoder.encode(password);
        }

        public Member toEntity() {
            return Member.builder()
                    .username(userName)
                    .nickname(nickname)
                    .phoneNumber(phoneNumber)
                    .description("")
                    .birthDate(birthDate)
                    .email(email)
                    .password(password)
                    .build();
        }
    }
}
