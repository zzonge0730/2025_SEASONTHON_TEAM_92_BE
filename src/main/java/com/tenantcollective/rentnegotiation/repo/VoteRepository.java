package com.tenantcollective.rentnegotiation.repo;

import com.tenantcollective.rentnegotiation.model.Vote;
import java.util.List;
import java.util.Optional;

public interface VoteRepository {
    Vote save(Vote vote);
    Optional<Vote> findById(String id);
    List<Vote> findAll();
    List<Vote> findByProposalId(String proposalId);
    List<Vote> findByUserId(String userId);
    Optional<Vote> findByProposalIdAndUserId(String proposalId, String userId);
    void deleteById(String id);
}
