package study.security.domain.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.security.domain.member.dao.MemberRepository;
import study.security.domain.member.dto.MemberDTO;
import study.security.domain.member.exception.DuplicateNicknameException;
import study.security.domain.member.exception.DuplicatePhoneNumberException;
import study.security.domain.member.model.Member;
import study.security.global.error.exception.NotFoundByIdException;

import static study.security.domain.member.dto.MemberDTO.*;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    // 이메일 중복체크용 로직, 회원가입에서 이메일 입력 EditText에서 focus가 풀릴때마다 해당 로직이 실행될 예정
    @Transactional(readOnly = true)
    public RedunCheckDto existsByEmail(String email) {
        return new RedunCheckDto(memberRepository.existsByEmail(email));
    }

    // 닉네임 중복체크용 로직.
    @Transactional(readOnly = true)
    public RedunCheckDto existsByNickname(String nickname) {
        return new RedunCheckDto(memberRepository.existsByNickname(nickname));
    }

    // 전화번호 중복체크용 로직
    @Transactional(readOnly = true)
    public RedunCheckDto existsByPhoneNumber(String phoneNumber) {
        return new RedunCheckDto(memberRepository.existsByEmail(phoneNumber));
    }

    // 유저 조회
    @Transactional(readOnly = true)
    public UserDetail getUserInfo(Long userId) {
        // 쿼리 DSL 커스텀 레포지토리 호출
        // 일단은 null로 두겠음
        return null;
    }

    // 유저 업데이트
    @Transactional
    public UserInfo updateUserInfo(UpdateUserInfo updateUserInfo, Long currentMemberId) {
        Member member = memberRepository.findById(currentMemberId).orElseThrow(NotFoundByIdException::new);
        if(!member.getNickname().equals(updateUserInfo.getNickname())
                && memberRepository.existsByNickname(updateUserInfo.getNickname())) {
            throw new DuplicateNicknameException();
        }
        if(!member.getPhoneNumber().equals(updateUserInfo.getPhoneNumber())
                && memberRepository.existsByPhoneNumber(updateUserInfo.getPhoneNumber())) {
            throw new DuplicatePhoneNumberException();
        }

        member.updateUserInfo(updateUserInfo);
        return member.toUserInfo();
    }


}
