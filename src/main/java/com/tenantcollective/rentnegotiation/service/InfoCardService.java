package com.tenantcollective.rentnegotiation.service;

import com.tenantcollective.rentnegotiation.model.InfoCard;
import com.tenantcollective.rentnegotiation.repo.InfoCardRepositoryFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InfoCardService {

    private final InfoCardRepositoryFile infoCardRepository;

    @Autowired
    public InfoCardService(InfoCardRepositoryFile infoCardRepository) {
        this.infoCardRepository = infoCardRepository;
    }

    public List<InfoCard> getAllCards() {
        return infoCardRepository.findAll();
    }

    public InfoCard createCard(InfoCard infoCard) {
        return infoCardRepository.save(infoCard);
    }

    public InfoCard updateCard(String id, InfoCard infoCard) {
        infoCard.setId(id);
        return infoCardRepository.save(infoCard);
    }

    public void deleteCard(String id) {
        infoCardRepository.deleteById(id);
    }
}
