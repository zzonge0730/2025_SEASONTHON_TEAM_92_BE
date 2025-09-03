package com.tenantcollective.rentnegotiation.service;

import com.tenantcollective.rentnegotiation.model.ProposalDiscussion;
import com.tenantcollective.rentnegotiation.repo.ProposalDiscussionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProposalDiscussionService {
    
    private final ProposalDiscussionRepository discussionRepository;
    
    @Autowired
    public ProposalDiscussionService(ProposalDiscussionRepository discussionRepository) {
        this.discussionRepository = discussionRepository;
    }
    
    public ProposalDiscussion saveDiscussion(ProposalDiscussion discussion) {
        return discussionRepository.save(discussion);
    }
    
    public Optional<ProposalDiscussion> findDiscussionById(String id) {
        return discussionRepository.findById(id);
    }
    
    public List<ProposalDiscussion> getAllDiscussions() {
        return discussionRepository.findAll();
    }
    
    public List<ProposalDiscussion> getDiscussionsByProposal(String proposalId) {
        return discussionRepository.findByProposalId(proposalId);
    }
    
    public List<ProposalDiscussion> getDiscussionsByAuthor(String authorId) {
        return discussionRepository.findByAuthorId(authorId);
    }
    
    public List<ProposalDiscussion> getRepliesToDiscussion(String parentId) {
        return discussionRepository.findByParentId(parentId);
    }
    
    public ProposalDiscussion createReply(String parentId, String authorId, String authorRole, String content) {
        ProposalDiscussion reply = new ProposalDiscussion();
        reply.setParentId(parentId);
        reply.setAuthorId(authorId);
        reply.setAuthorRole(authorRole);
        reply.setContent(content);
        reply.setIsReply(true);
        return discussionRepository.save(reply);
    }
    
    public void deleteDiscussion(String id) {
        discussionRepository.deleteById(id);
    }
}
