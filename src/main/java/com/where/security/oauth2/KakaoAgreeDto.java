package com.where.security.oauth2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoAgreeDto {

    List<KakaoAgreeDetailDto> scopes;
}
