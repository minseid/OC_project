package com.example.OC.API;

import com.example.OC.entity.Token_Entity;
import com.example.OC.entity.User_Entity;
import com.example.OC.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class ApiController {

    @Autowired
    private User_Repository user_repository;

    @Autowired
    private Comment_Repository comment_repository;

    @Autowired
    private Link_Repository link_repository;

    @Autowired
    private Mapping_Repository mapping_repository;

    @Autowired
    private Place_Repository place_repository;

    @Autowired
    private Social_Repository social_repository;

    @Autowired
    private Token_Repository token_repository;

    @GetMapping("/api/user/{navertoken}")
    public User_Entity getuserbynaver(@PathVariable String navertoken ) {

        List<Token_Entity> tokenEntities = token_repository.findByToken_naver(navertoken);
        if (tokenEntities.size() == 1) {
            List<User_Entity> userEntities = user_repository.findByUser_token(tokenEntities.get(0).getToken_key());
            if(userEntities.size() == 1) {
                return userEntities.get(0);
            }
            else {
                return null;
            }
        }
        else {

            return null;
        }
    }

    @GetMapping("/api/user/{kakaotoken}")
    public User_Entity getuserbykakao(@PathVariable String kakaotoken ) {

        List<Token_Entity> tokenEntities = token_repository.findByToken_kakao(kakaotoken);
        if (tokenEntities.size() == 1) {
            List<User_Entity> userEntities = user_repository.findByUser_token(tokenEntities.get(0).getToken_key());
            if(userEntities.size() == 1) {
                return userEntities.get(0);
            }
            else {
                return null;
            }
        }
        else {

            return null;
        }
    }
}
