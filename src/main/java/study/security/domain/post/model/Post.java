package study.security.domain.post.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import study.security.domain.member.model.Member;
import study.security.domain.model.BaseTimeEntity;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member author;

    private String title;

    private String content;

}
