package com.example.OC.network.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest {

    @NotEmpty(message = "이메일을 입력해주세요")
    @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$",message = "올바른 이메일 형식이 아닙니다")
    private String email;

    /*
    * 비밀번호 유효성 검증
    * 최소 하나의 숫자 포함(0~9)
    * 최소 하나의 알파벳 문자 포함(a-z,A-z)
    * 최소 하나의 특수문자 포함
    * 공백 제외
    * 길이 8~32자
    */

    @NotEmpty(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,32}",message = "비밀번호는 숫자, 영문, 특수문자를 포함 8~32자로 입력해주세요")

    private String password;
    @NotEmpty(message = "이름을 입력해주세요")
    private String name;
    private String profileImage;
}
