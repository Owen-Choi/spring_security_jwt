package study.security.domain.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.security.domain.member.dao.MemberRepository;
import study.security.domain.member.dto.MemberDTO;
import study.security.domain.member.exception.*;
import study.security.domain.member.model.Member;
import study.security.global.error.exception.NotFoundByIdException;

import java.util.Objects;
import java.util.Random;

import static study.security.domain.member.dto.MemberDTO.*;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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

    @Transactional
    public NewNicknameDto updateUserNickname(Long memberId, UpdateMemberNicknameDto updateMemberNicknameDto) {
        if (!Objects.equals(memberId, updateMemberNicknameDto.getUserId())) {
            throw new NotAuthorizedException();
        }
        Member member = memberRepository.findById(memberId).orElseThrow(NotFoundByIdException::new);
        member.updateNickname(updateMemberNicknameDto.getNewNickname());
        return NewNicknameDto.builder().newNickname(updateMemberNicknameDto.getNewNickname()).build();
    }

    @Transactional
    public NewPhoneNumberDto updateUserPhoneNumber(Long memberId, UpdateMemberPhoneNumberDto updateMemberPhoneNumberDto) {
        if (!Objects.equals(memberId, updateMemberPhoneNumberDto.getUserId())) {
            throw new NotAuthorizedException();
        }
        Member member = memberRepository.findById(memberId).orElseThrow(NotFoundByIdException::new);
        member.updatePhoneNumber(updateMemberPhoneNumberDto.getNewPhoneNumber());
        return NewPhoneNumberDto.builder().newPhoneNumber(updateMemberPhoneNumberDto.getNewPhoneNumber()).build();
    }

    @Transactional
    public NewDescriptionDto updateDescription(Long memberId, UpdateDescriptionDto updateDescriptionDto) {
        if (!Objects.equals(memberId, updateDescriptionDto.getUserId())) {
            throw new NotAuthorizedException();
        }
        Member member = memberRepository.findById(memberId).orElseThrow(NotFoundByIdException::new);
        member.updateDescription(updateDescriptionDto.getDescription());
        return NewDescriptionDto.builder().newDescription(updateDescriptionDto.getDescription()).build();
    }

    @Transactional
    public String updateUserPassword(UpdateUserPassword updateUserPassword, Long currentMemberId) {
        Member member = memberRepository.findById(currentMemberId).orElseThrow(NotFoundByIdException::new);
        if(!passwordEncoder.matches(updateUserPassword.getOldPassword(), member.getPassword())) {
            throw new InvalidPasswordException();
        }
        updateUserPassword.encrypt(passwordEncoder);
        member.updateUserPassword(updateUserPassword);
        return "UPDATE";
    }

    @Transactional
    public String deleteUser(Long currentMemberId) {
        Member member = memberRepository.findById(currentMemberId).orElseThrow(NotFoundByIdException::new);
        memberRepository.delete(member);
        return "DELETE";
    }

    @Transactional(readOnly = true)
    public EncryptEmailDto findUserEmail(FindEmailDto findEmailDto) {
        String userName = findEmailDto.getUserName();
        String phoneNumber = findEmailDto.getPhoneNumber();
        Member member = memberRepository.findByUserNameAndPhoneNumber(userName, phoneNumber).orElseThrow(UserNotFoundByUsernameAndPhoneException::new);
        return member.encryptEmail();
    }

    // 비밀번호 찾기 메소드
    @Transactional
    public ReturnPasswordDto findUserPassword(FindPasswordDto findPasswordDto) {
        String userName = findPasswordDto.getUserName();
        String phoneNumber = findPasswordDto.getPhoneNumber();
        String email = findPasswordDto.getEmail();
        Member member = memberRepository.findByUserNameAndPhoneNumberAndEmail(userName, phoneNumber, email).orElseThrow(UserNotFoundExpcetion::new);
        String newPassword =
    }

    private String createRandomPassword() {
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(targetStringLength);
        for(int i=0; i<targetStringLength; i++) {

        }
    }
}
