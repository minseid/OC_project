package com.example.OC.service;

import com.example.OC.dto.UserDto;
import com.example.OC.entity.TokenEntity;
import com.example.OC.entity.UserEntity;
import com.example.OC.repository.TokenRepository;
import com.example.OC.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenRepository tokenRepository;

    public UserEntity add_user(UserDto userdto){
        UserEntity target = userdto.toEntity();
//        if(target.getId()!=null || (target)){
//            return null;
//        }
//        userRepository
        return null;
    }

    public UserEntity find_user_naver(String naver_key){
        List<TokenEntity> tokenEntities = tokenRepository.findByToken_naver(naver_key);
        if(tokenEntities.size()!=1){
            return null;
        }
        List<UserEntity> userEntities = userRepository.findByUser_token(tokenEntities.get(0).getToken_key());
        if(userEntities.size()!=1){
            return null;
        }
        return userEntities.get(0);
    }
    public UserEntity find_user_kakao(String kakao_key){
        List<TokenEntity> tokenEntities = tokenRepository.findByToken_kakao(kakao_key);
        if(tokenEntities.size()!=1){
            return null;
        }
        List<UserEntity> userEntities = userRepository.findByUser_token(tokenEntities.get(0).getToken_key());
        if(userEntities.size()!=1){
            return null;
        }
        return userEntities.get(0);
    }

}
