package com.tenantcollective.rentnegotiation.llm;

import com.tenantcollective.rentnegotiation.model.Group;
import com.tenantcollective.rentnegotiation.model.LetterRequest;

public interface LlmClient {
    String generateLetter(Group group, LetterRequest request, String samplePainPoints);
}