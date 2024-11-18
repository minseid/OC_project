package com.example.OC.service;

import com.example.OC.dto.UserDto;
import com.example.OC.entity.Token;
import com.example.OC.entity.User;
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

    public User add_user(UserDto userdto){
        User target = userdto.toEntity();
//        if(target.getId()!=null || (target)){
//            return null;
//        }
//        userRepository
        return null;
    }

    public User find_user_naver(String naver_key){
        List<Token> tokenEntities = tokenRepository.findByToken_naver(naver_key);
        if(tokenEntities.size()!=1){
            return null;
        }
        List<User> userEntities = userRepository.findByUser_token(tokenEntities.get(0).getToken_key());
        if(userEntities.size()!=1){
            return null;
        }
        return userEntities.get(0);
    }
    public User find_user_kakao(String kakao_key){
        List<Token> tokenEntities = tokenRepository.findByToken_kakao(kakao_key);
        if(tokenEntities.size()!=1){
            return null;
        }
        List<User> userEntities = userRepository.findByUser_token(tokenEntities.get(0).getToken_key());
        if(userEntities.size()!=1){
            return null;
        }
        return userEntities.get(0);
    }

}
