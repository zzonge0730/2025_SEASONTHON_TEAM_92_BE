package com.tenantcollective.rentnegotiation.service;

import com.tenantcollective.rentnegotiation.model.Vote;
import com.tenantcollective.rentnegotiation.model.VoteResult;
import com.tenantcollective.rentnegotiation.repo.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoteService {
    
    private final VoteRepository voteRepository;
    
    @Autowired
    public VoteService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }
    
    public Vote saveVote(Vote vote) {
        // Check if user has already voted on this proposal
        Optional<Vote> existingVote = voteRepository.findByProposalIdAndUserId(
                vote.getProposalId(), vote.getUserId());
        
        if (existingVote.isPresent()) {
            // Update existing vote
            Vote existing = existingVote.get();
            existing.setVote(vote.getVote());
            return voteRepository.save(existing);
        } else {
            // Create new vote
            return voteRepository.save(vote);
        }
    }
    
    public Optional<Vote> findVoteById(String id) {
        return voteRepository.findById(id);
    }
    
    public List<Vote> getAllVotes() {
        return voteRepository.findAll();
    }
    
    public List<Vote> getVotesByProposal(String proposalId) {
        return voteRepository.findByProposalId(proposalId);
    }
    
    public List<Vote> getVotesByUser(String userId) {
        return voteRepository.findByUserId(userId);
    }
    
    public Optional<Vote> getUserVoteForProposal(String proposalId, String userId) {
        return voteRepository.findByProposalIdAndUserId(proposalId, userId);
    }
    
    public VoteResult getVoteResult(String proposalId) {
        List<Vote> votes = voteRepository.findByProposalId(proposalId);
        
        int totalVotes = votes.size();
        int agreeVotes = (int) votes.stream().filter(v -> "agree".equals(v.getVote())).count();
        int disagreeVotes = totalVotes - agreeVotes;
        
        return new VoteResult(proposalId, totalVotes, agreeVotes, disagreeVotes);
    }
    
    public void deleteVote(String id) {
        voteRepository.deleteById(id);
    }
}
