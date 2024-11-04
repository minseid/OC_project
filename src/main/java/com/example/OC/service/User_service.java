package com.example.OC.service;

import com.example.OC.dto.User_dto;
import com.example.OC.entity.Token_Entity;
import com.example.OC.entity.User_Entity;
import com.example.OC.repository.Token_Repository;
import com.example.OC.repository.User_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class User_service {
    @Autowired
    User_Repository userRepository;
    @Autowired
    Token_Repository tokenRepository;

    public User_Entity add_user(User_dto userdto){
        User_Entity target = userdto.toEntity();
//        if(target.getId()!=null || (target)){
//            return null;
//        }
//        userRepository
        return null;
    }

    public User_Entity find_user_naver(String naver_key){
        List<Token_Entity> tokenEntities = tokenRepository.findByToken_naver(naver_key);
        if(tokenEntities.size()!=1){
            return null;
        }
        List<User_Entity> userEntities = userRepository.findByUser_token(tokenEntities.get(0).getToken_key());
        if(userEntities.size()!=1){
            return null;
        }
        return userEntities.get(0);
    }
    public User_Entity find_user_kakao(String kakao_key){
        List<Token_Entity> tokenEntities = tokenRepository.findByToken_kakao(kakao_key);
        if(tokenEntities.size()!=1){
            return null;
        }
        List<User_Entity> userEntities = userRepository.findByUser_token(tokenEntities.get(0).getToken_key());
        if(userEntities.size()!=1){
            return null;
        }
        return userEntities.get(0);
    }

}
