package study.security.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import study.security.domain.member.model.Member;
import study.security.domain.token.dto.TokenDTO;

import static study.security.domain.token.dto.TokenDTO.*;

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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;

        public void encrypt(PasswordEncoder passwordEncoder) {
            this.password = passwordEncoder.encode(password);
        }

        public UsernamePasswordAuthenticationToken toAuthentication() {
            return new UsernamePasswordAuthenticationToken(email, password);
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLoginDTO {
        // 로그인에 성공하면 유저 정보와 토큰 정보 DTO를 반환해준다.
        private UserInfo userInfo;
        private TokenIssueDTO tokenInfo;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private Long scrapId;
        private String userName;
        private String nickname;
        private String description;
        private String phoneNumber;
        private String email;
        private String birthDate;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedunCheckDto {
        private boolean exists;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDetail {
        private Long id;

        private String userName;

        private String nickname;

        private String description;

        private String phoneNumber;

        private String email;

        private String birthDate;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateUserInfo {
        private Long userId;
        private String nickname;
        private String phoneNumber;
        // 프로필사진 등도 원래는 포함되어야 함. 하지만 여기서는 넣지 않겠음;
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMemberNicknameDto {
        private Long userId;
        private String newNickname;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMemberPhoneNumberDto {
        private Long userId;
        private String newPhoneNumber;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewNicknameDto {
        private String newNickname;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewPhoneNumberDto {
        private String newPhoneNumber;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDescriptionDto {
        private Long userId;
        private String description;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewDescriptionDto {
        private String newDescription;
    }
}
