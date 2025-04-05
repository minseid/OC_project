package com.where.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String deepLinkWebpage(@PathVariable("id") String inviteLink, Model model) {
        model.addAttribute("inviteLink", "audiwhere://invite/" + inviteLink);
        return "invite copy";
    }

}
