package com.tenantcollective.rentnegotiation.controller;

import com.tenantcollective.rentnegotiation.model.ApiResponse;
import com.tenantcollective.rentnegotiation.model.Vote;
import com.tenantcollective.rentnegotiation.model.VoteResult;
import com.tenantcollective.rentnegotiation.service.VoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://172.20.196.193:3000", "https://houselent.vercel.app", "https://houselent-3srqcm2ee-woohyeok-kangs-projects.vercel.app"})
public class VoteController {
    
    private final VoteService voteService;
    
    @Autowired
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }
    
    @PostMapping("/votes")
    public ResponseEntity<ApiResponse<VoteResult>> createVote(@Valid @RequestBody Vote vote) {
        try {
            Vote savedVote = voteService.saveVote(vote);
            VoteResult result = voteService.getVoteResult(vote.getProposalId());
            return ResponseEntity.ok(new ApiResponse<>(true, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to save vote: " + e.getMessage()));
        }
    }
    
    @GetMapping("/votes/proposal/{proposalId}")
    public ResponseEntity<ApiResponse<VoteResult>> getVoteResult(@PathVariable String proposalId) {
        try {
            VoteResult result = voteService.getVoteResult(proposalId);
            return ResponseEntity.ok(new ApiResponse<>(true, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to retrieve vote result: " + e.getMessage()));
        }
    }
    
    @GetMapping("/votes/proposal/{proposalId}/user/{userId}")
    public ResponseEntity<ApiResponse<Vote>> getUserVote(@PathVariable String proposalId, @PathVariable String userId) {
        try {
            return voteService.getUserVoteForProposal(proposalId, userId)
                    .map(vote -> ResponseEntity.ok(new ApiResponse<>(true, vote)))
                    .orElse(ResponseEntity.ok(new ApiResponse<>(true, null)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to retrieve user vote: " + e.getMessage()));
        }
    }
    
    @GetMapping("/votes/proposal/{proposalId}/all")
    public ResponseEntity<ApiResponse<List<Vote>>> getAllVotesForProposal(@PathVariable String proposalId) {
        try {
            List<Vote> votes = voteService.getVotesByProposal(proposalId);
            return ResponseEntity.ok(new ApiResponse<>(true, votes));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to retrieve votes: " + e.getMessage()));
        }
    }
    
    @GetMapping("/votes/user/{userId}")
    public ResponseEntity<ApiResponse<List<Vote>>> getUserVotes(@PathVariable String userId) {
        try {
            List<Vote> votes = voteService.getVotesByUser(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, votes));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to retrieve user votes: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/votes/{id}")
    public ResponseEntity<ApiResponse<String>> deleteVote(@PathVariable String id) {
        try {
            voteService.deleteVote(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Vote deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to delete vote: " + e.getMessage()));
        }
    }
}
