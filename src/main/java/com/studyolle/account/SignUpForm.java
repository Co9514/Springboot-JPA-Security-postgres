package com.studyolle.account;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignUpForm {

    @NotBlank
    @Length(min = 3,max = 20)
    // 정규표현식을 통한 input 검증
    // 프론트에서도 확인을 해주는 것이 좋음. 프론트에서만 해주는 것은 자바스크립트를 통해 뚫릴 수 있으므로 서버에서
    // 한번 더 체크를 해줘야함. 프론트에서 하는 것은 fast-fail 빠른 피드백을 위해.
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣A-Za-z0-9_-]{3,20}$")
    private String nickname;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Length(min = 8,max = 50)
    private String password;
}
