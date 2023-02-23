package study.security.domain.member.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import study.security.domain.member.model.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByNickname(String nickname);
    Optional<Member> findByUserNameAndPhoneNumber(String username, String phoneNumber);
    Optional<Member> findByUserNameAndPhoneNumberAndEmail(String username, String phoneNumber, String email);
}
