package com.where.config;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper(){

        ModelMapper modelMapper = new ModelMapper();

        // 사용자 정의 매핑 규칙 추가
        modelMapper.addConverter(new AbstractConverter<Object, String>() {
            @Override
            protected String convert(Object source) {
                // null일 경우 빈 문자열 반환
                return source == null ? "" : source.toString();
            }
        });

        return modelMapper;
    }
}