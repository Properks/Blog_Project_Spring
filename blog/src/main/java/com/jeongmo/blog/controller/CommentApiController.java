package com.jeongmo.blog.controller;

import com.jeongmo.blog.dto.comment.CommentResponse;
import com.jeongmo.blog.dto.comment.CreateCommentRequest;
import com.jeongmo.blog.dto.comment.UpdateCommentRequest;
import com.jeongmo.blog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentApiController {
    private final CommentService commentService;

    @PostMapping("/api/comment")
    public ResponseEntity<CommentResponse> createComment(@RequestBody CreateCommentRequest request) {
        try {
            CommentResponse response = new CommentResponse(commentService.createComment(request));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api/comment/{id}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable Long id) {
        try {
            CommentResponse response = new CommentResponse(commentService.getComment(id));
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api/comment")
    public ResponseEntity<List<CommentResponse>> getComments() {
        try {
            List<CommentResponse> responses = commentService.getComments()
                    .stream()
                    .map(CommentResponse::new)
                    .toList();
            return ResponseEntity.ok().body(responses);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/api/comment/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        try{
            commentService.deleteComment(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/api/comment")
    public ResponseEntity<CommentResponse> updateComment(@RequestBody UpdateCommentRequest request) {
        try{
            CommentResponse response = new CommentResponse(commentService.updateComment(request));
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

}
