package study.security.domain.token.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TokenDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenInfoDTO {
        private String grantType;
        private String accessToken;
        private Long accessTokenExpiresIn;
        private String refreshToken;
        public TokenIssueDTO toTokenIssueDTO() {
            return TokenIssueDTO.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiresIn(accessTokenExpiresIn)
                    .grantType(grantType)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenIssueDTO {
        private String accessToken;
        private String grantType;
        private Long accessTokenExpiresIn;
    }
}
