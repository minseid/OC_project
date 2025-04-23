package com.where.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DeepLinkController {

    @GetMapping("/.well-known/apple/apple-app-site-association")
    @ResponseBody
    public ResponseEntity<Resource> getAppleAppSiteAssociation() {
        Resource resource = new ClassPathResource("static/wellknown/apple-app-site-association");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resource);
    }

    @GetMapping("/.well-known/assetlinks.json")
    @ResponseBody
    public ResponseEntity<Resource> getAssetlinks() {
        Resource resource = new ClassPathResource("static/wellknown/assetlinks.json");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resource);
    }

    @GetMapping("/invite/{id}")
    public String deepLinkWebpage(@PathVariable("id") String inviteLink, @RequestParam String name, Model model) {
        String uuid = UUID.randomUUID().toString();
        String nameForLink = URLEncoder.encode(name, StandardCharsets.UTF_8);
        model.addAttribute("inviteLink", "audiwhere://invite/" + inviteLink +"?name=" + nameForLink );
        model.addAttribute("uuid", uuid);
        return "invite copy";
    }

}
