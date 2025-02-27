package ecoders.polareco.member.controller;

import ecoders.polareco.member.dto.*;
import ecoders.polareco.member.entity.Member;
import ecoders.polareco.member.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@AllArgsConstructor
@RestController
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup/code/issue")
    public ResponseEntity<?> issueEmailVerificationCode(@RequestBody @Valid EmailDto emailDto) {
        memberService.issueEmailVerificationCode(emailDto.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup/code/verification")
    public ResponseEntity<?> verifyEmail(@RequestBody @Valid EmailVerificationCodeDto emailVerificationCodeDto) {
        String email = emailVerificationCodeDto.getEmail();
        String verificationCode = emailVerificationCodeDto.getVerificationCode();
        memberService.verifyEmail(email, verificationCode);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupDto signupDto) {
        memberService.signup(signupDto.toMember());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/member/my-info")
    public ResponseEntity<MemberInfoDto> getMyInfo(@AuthenticationPrincipal String email) {
        Member member = memberService.findMemberByEmail(email);
        return ResponseEntity.ok(new MemberInfoDto(member));
    }

    @PostMapping("/password/forgot/issue")
    public ResponseEntity<?> sendPasswordResetMail(@RequestBody @Valid EmailDto emailDto) {
        memberService.sendPasswordResetMail(emailDto.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/forgot/verification")
    public ResponseEntity<?> verifyPasswordResetToken(@RequestBody PasswordResetVerificationDto dto) {
        memberService.verifyPasswordResetToken(dto.getEmail(), dto.getToken());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/password/forgot/reset")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid PasswordResetDto dto) {
        memberService.resetPassword(dto.getEmail(), dto.getToken(), dto.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/update/profile-image")
    public ResponseEntity<ProfileImageDto> updateProfileImage(
        @AuthenticationPrincipal String email,
        @RequestPart MultipartFile imageFile
    ) {
        String profileImage = memberService.updateProfileImage(email, imageFile);
        return ResponseEntity.ok(new ProfileImageDto(profileImage));
    }

    @PatchMapping("/update/password")
    public ResponseEntity<?> updatePassword(
        @AuthenticationPrincipal String email,
        @RequestBody @Valid PasswordUpdateDto passwordUpdateDto
    ) {
        memberService.updatePassword(email, passwordUpdateDto.getCurrentPassword(), passwordUpdateDto.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check/google")
    public ResponseEntity<?> checkIsGoogleMember(@RequestParam("email") String email) {
        boolean isGoogleMember = memberService.checkIsGoogleMember(email);
        GoogleMemberCheckResponse response = new GoogleMemberCheckResponse(isGoogleMember);
        return ResponseEntity.ok(response);
    }
}
