package com.example.OC.controller;

import com.example.OC.network.request.BookmarkFriendRequest;
import com.example.OC.network.request.DeleteFriendRequest;
import com.example.OC.network.response.BookmarkFriendResponse;
import com.example.OC.network.response.GetFriendResponse;
import com.example.OC.service.FriendService;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @GetMapping("/api/friend/{id}")
    public ResponseEntity<List<GetFriendResponse>> getfriend(@PathVariable Long id) {
        return ResponseEntity.ok(friendService.getFriend(id));
    }

    @DeleteMapping("/api/friend")
    public ResponseEntity<Void> deletefriend(@RequestBody DeleteFriendRequest request) {
        friendService.deleteFriend(request.getUserId(), request.getFriendId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/friend/bookmark")
    public ResponseEntity<BookmarkFriendResponse> bookmarkfriend(@RequestBody BookmarkFriendRequest request) {
        return ResponseEntity.ok(friendService.bookmarkFriend(request.getUserId(), request.getFriendId()));
    }
}
