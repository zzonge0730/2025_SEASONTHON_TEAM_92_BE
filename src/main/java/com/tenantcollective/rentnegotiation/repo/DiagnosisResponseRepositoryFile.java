package com.tenantcollective.rentnegotiation.repo;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.tenantcollective.rentnegotiation.model.DiagnosisResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class DiagnosisResponseRepositoryFile implements DiagnosisResponseRepository {

    private final String filePath;
    private final List<DiagnosisResponse> responses = new ArrayList<>();

    public DiagnosisResponseRepositoryFile(@Value("${diagnosis.storage.file}") String filePath) {
        this.filePath = filePath;
        loadData();
    }

    private void loadData() {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] nextLine;
            reader.readNext(); // Skip header
            while ((nextLine = reader.readNext()) != null) {
                DiagnosisResponse response = new DiagnosisResponse();
                response.setId(nextLine[0]);
                response.setUserId(nextLine[1]);
                response.setBuildingName(nextLine[2]);
                response.setNeighborhood(nextLine[3]);
                response.setQuestionId(nextLine[4]);
                response.setAnswerValue(Integer.parseInt(nextLine[5]));
                response.setTimestamp(LocalDateTime.parse(nextLine[6]));
                responses.add(response);
            }
        } catch (Exception e) {
            // File might not exist on first run, which is fine.
        }
    }

    private void saveData() {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(new String[]{"id", "userId", "buildingName", "neighborhood", "questionId", "answerValue", "timestamp"});
            for (DiagnosisResponse response : responses) {
                writer.writeNext(new String[]{
                        response.getId(),
                        response.getUserId(),
                        response.getBuildingName(),
                        response.getNeighborhood(),
                        response.getQuestionId(),
                        String.valueOf(response.getAnswerValue()),
                        response.getTimestamp().toString()
                });
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving diagnosis responses to file", e);
        }
    }

    @Override
    public DiagnosisResponse save(DiagnosisResponse diagnosisResponse) {
        diagnosisResponse.setId(UUID.randomUUID().toString());
        diagnosisResponse.setTimestamp(LocalDateTime.now());
        responses.add(diagnosisResponse);
        saveData();
        return diagnosisResponse;
    }

    @Override
    public List<DiagnosisResponse> findAll() {
        return new ArrayList<>(responses);
    }

    @Override
    public List<DiagnosisResponse> findByUserId(String userId) {
        return responses.stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<DiagnosisResponse> findByBuildingName(String buildingName) {
        return responses.stream()
                .filter(r -> r.getBuildingName().equals(buildingName))
                .collect(Collectors.toList());
    }

    @Override
    public List<DiagnosisResponse> findByNeighborhood(String neighborhood) {
        return responses.stream()
                .filter(r -> r.getNeighborhood().equals(neighborhood))
                .collect(Collectors.toList());
    }
}
