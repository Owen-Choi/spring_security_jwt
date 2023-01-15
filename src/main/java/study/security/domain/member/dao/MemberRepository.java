package study.security.domain.member.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import study.security.domain.member.model.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
