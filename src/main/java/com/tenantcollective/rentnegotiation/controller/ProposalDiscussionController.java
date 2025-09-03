package com.tenantcollective.rentnegotiation.controller;

import com.tenantcollective.rentnegotiation.model.ApiResponse;
import com.tenantcollective.rentnegotiation.model.ProposalDiscussion;
import com.tenantcollective.rentnegotiation.service.ProposalDiscussionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "https://houselent.vercel.app", "https://houselent-3srqcm2ee-woohyeok-kangs-projects.vercel.app"})
public class ProposalDiscussionController {
    
    private final ProposalDiscussionService discussionService;
    
    @Autowired
    public ProposalDiscussionController(ProposalDiscussionService discussionService) {
        this.discussionService = discussionService;
    }
    
    @PostMapping("/discussions")
    public ResponseEntity<ApiResponse<String>> createDiscussion(@Valid @RequestBody ProposalDiscussion discussion) {
        try {
            ProposalDiscussion savedDiscussion = discussionService.saveDiscussion(discussion);
            return ResponseEntity.ok(new ApiResponse<>(true, savedDiscussion.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to save discussion: " + e.getMessage()));
        }
    }
    
    @PostMapping("/discussions/{parentId}/reply")
    public ResponseEntity<ApiResponse<String>> createReply(
            @PathVariable String parentId,
            @RequestBody ReplyRequest request) {
        try {
            ProposalDiscussion reply = discussionService.createReply(
                    parentId, 
                    request.getAuthorId(), 
                    request.getAuthorRole(), 
                    request.getContent()
            );
            return ResponseEntity.ok(new ApiResponse<>(true, reply.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to create reply: " + e.getMessage()));
        }
    }
    
    @GetMapping("/discussions/proposal/{proposalId}")
    public ResponseEntity<ApiResponse<List<ProposalDiscussion>>> getDiscussionsByProposal(@PathVariable String proposalId) {
        try {
            List<ProposalDiscussion> discussions = discussionService.getDiscussionsByProposal(proposalId);
            return ResponseEntity.ok(new ApiResponse<>(true, discussions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to retrieve discussions: " + e.getMessage()));
        }
    }
    
    @GetMapping("/discussions/{id}")
    public ResponseEntity<ApiResponse<ProposalDiscussion>> getDiscussionById(@PathVariable String id) {
        try {
            return discussionService.findDiscussionById(id)
                    .map(discussion -> ResponseEntity.ok(new ApiResponse<>(true, discussion)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to retrieve discussion: " + e.getMessage()));
        }
    }
    
    @GetMapping("/discussions/{id}/replies")
    public ResponseEntity<ApiResponse<List<ProposalDiscussion>>> getReplies(@PathVariable String id) {
        try {
            List<ProposalDiscussion> replies = discussionService.getRepliesToDiscussion(id);
            return ResponseEntity.ok(new ApiResponse<>(true, replies));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to retrieve replies: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/discussions/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDiscussion(@PathVariable String id) {
        try {
            discussionService.deleteDiscussion(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Discussion deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to delete discussion: " + e.getMessage()));
        }
    }
    
    // Inner class for reply request
    public static class ReplyRequest {
        private String authorId;
        private String authorRole;
        private String content;
        
        // Getters and Setters
        public String getAuthorId() {
            return authorId;
        }
        
        public void setAuthorId(String authorId) {
            this.authorId = authorId;
        }
        
        public String getAuthorRole() {
            return authorRole;
        }
        
        public void setAuthorRole(String authorRole) {
            this.authorRole = authorRole;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
}
