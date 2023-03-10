package study.security.domain.member.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.security.domain.member.dto.MemberDTO;
import study.security.domain.post.model.Post;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

import static study.security.domain.member.dto.MemberDTO.*;
import static study.security.domain.member.model.Authority.*;

@Entity
@Getter
@NoArgsConstructor
public class Member extends MemberBase{

    private String birthDate;

    private String phoneNumber;

    private String userName;

    private String nickname;

    private String description;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Post> posts;

    public void updateUserInfo(UpdateUserInfo updateUserInfo) {
        this.nickname = updateUserInfo.getNickname();
        this.phoneNumber = updateUserInfo.getPhoneNumber();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateUserPassword(UpdateUserPassword updateUserPassword) {
        super.updatePassword(updateUserPassword.getNewPassword());
    }

    public EncryptEmailDto encryptEmail() {
        String[] subEmail = this.getEmail().split("@");
        int asteriskNum = subEmail[0].length() - 3;
        String asterisks = "*".repeat(asteriskNum);
        subEmail[0] = subEmail[0].substring(0,3) + asterisks;
        String returnEmail = subEmail[0] + "@" + subEmail[1];
        return new EncryptEmailDto(returnEmail);
    }

    @Builder
    public Member(Long id, String email, String password, String username, String birthDate, String phoneNumber, String nickname, String description) {
        super(id, email, password, ROLE_USER);
        this.userName = username;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.description = description;
    }

    public UserInfo toUserInfo() {
        return UserInfo.builder()
                .userName(this.userName)
                .email(getEmail())
                .phoneNumber(this.phoneNumber)
                .nickname(this.nickname)
                .id(this.getId())
                .birthDate(this.birthDate)
                .description(this.description)
                .build();
    }
}
