package com.tenantcollective.rentnegotiation.repo;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.tenantcollective.rentnegotiation.model.InfoCard;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InfoCardRepositoryFile {

    private final String filePath;
    private final List<InfoCard> infoCards = new ArrayList<>();

    public InfoCardRepositoryFile(@Value("${infocard.storage.file}") String filePath) {
        this.filePath = filePath;
        loadData();
    }

    private void loadData() {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] nextLine;
            reader.readNext(); // Skip header
            while ((nextLine = reader.readNext()) != null) {
                InfoCard card = new InfoCard();
                card.setId(nextLine[0]);
                card.setCategory(nextLine[1]);
                card.setTitle(nextLine[2]);
                card.setSummary(nextLine[3]);
                card.setLinkUrl(nextLine[4]);
                card.setCreatedAt(LocalDateTime.parse(nextLine[5]));
                infoCards.add(card);
            }
        } catch (Exception e) { /* File might not exist */ }
    }

    private void saveData() {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(new String[]{"id", "category", "title", "summary", "linkUrl", "createdAt"});
            for (InfoCard card : infoCards) {
                writer.writeNext(new String[]{
                        card.getId(), card.getCategory(), card.getTitle(),
                        card.getSummary(), card.getLinkUrl(), card.getCreatedAt().toString()
                });
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving info cards", e);
        }
    }

    public List<InfoCard> findAll() {
        return new ArrayList<>(infoCards);
    }

    public Optional<InfoCard> findById(String id) {
        return infoCards.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    public InfoCard save(InfoCard infoCard) {
        if (infoCard.getId() == null || infoCard.getId().isEmpty()) {
            infoCard.setId(UUID.randomUUID().toString());
            infoCard.setCreatedAt(LocalDateTime.now());
            infoCards.add(infoCard);
        } else {
            int index = infoCards.indexOf(findById(infoCard.getId()).orElse(null));
            if (index != -1) {
                infoCards.set(index, infoCard);
            }
        }
        saveData();
        return infoCard;
    }

    public void deleteById(String id) {
        infoCards.removeIf(c -> c.getId().equals(id));
        saveData();
    }
}
