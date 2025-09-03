package com.tenantcollective.rentnegotiation.repo;

import com.tenantcollective.rentnegotiation.model.ProposalDiscussion;
import java.util.List;
import java.util.Optional;

public interface ProposalDiscussionRepository {
    ProposalDiscussion save(ProposalDiscussion discussion);
    Optional<ProposalDiscussion> findById(String id);
    List<ProposalDiscussion> findAll();
    List<ProposalDiscussion> findByProposalId(String proposalId);
    List<ProposalDiscussion> findByAuthorId(String authorId);
    List<ProposalDiscussion> findByParentId(String parentId);
    void deleteById(String id);
}
