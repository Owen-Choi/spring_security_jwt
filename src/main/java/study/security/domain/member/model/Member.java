package study.security.domain.member.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.security.domain.post.model.Post;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

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

    @Builder
    public Member(Long id, String email, String password, String username, String birthDate, String phoneNumber, String nickname, String description) {
        super(id, email, password, ROLE_USER);
        this.userName = username;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.description = description;
    }
}
