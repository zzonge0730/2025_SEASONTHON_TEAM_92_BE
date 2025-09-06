package com.tenantcollective.rentnegotiation.mission.data;

import com.tenantcollective.rentnegotiation.mission.domain.entity.MissionQuestion;
import com.tenantcollective.rentnegotiation.mission.domain.entity.WeeklyMission;
import com.tenantcollective.rentnegotiation.mission.domain.repository.WeeklyMissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MissionDataLoader implements CommandLineRunner {

    private final WeeklyMissionRepository missionRepository;

    @Override
    public void run(String... args) throws Exception {
        if (missionRepository.count() == 0) {
            log.info("미션 데이터를 초기화합니다...");
            createSampleMissions();
            log.info("미션 데이터 초기화 완료!");
        } else {
            log.info("미션 데이터가 이미 존재합니다. 건너뜁니다.");
        }
    }

    private void createSampleMissions() {
        // 1. 주거환경 만족도 미션
        WeeklyMission environmentMission = WeeklyMission.builder()
                .category("주거환경")
                .title("우리 집 주거환경은 어떤가요?")
                .description("거주하고 계신 건물의 주거환경에 대한 만족도를 평가해주세요.")
                .startDate(LocalDate.now().minusDays(7))
                .endDate(LocalDate.now().plusDays(7))
                .isActive(true)
                .build();

        MissionQuestion envQ1 = MissionQuestion.builder()
                .mission(environmentMission)
                .questionText("건물의 보안 상태는 어떤가요?")
                .questionType("multiple_choice")
                .options("[\"매우 좋음\", \"좋음\", \"보통\", \"나쁨\", \"매우 나쁨\"]")
                .orderNumber(1)
                .build();

        MissionQuestion envQ2 = MissionQuestion.builder()
                .mission(environmentMission)
                .questionText("건물의 청결도는 어떤가요?")
                .questionType("multiple_choice")
                .options("[\"매우 좋음\", \"좋음\", \"보통\", \"나쁨\", \"매우 나쁨\"]")
                .orderNumber(2)
                .build();

        environmentMission.setQuestions(Arrays.asList(envQ1, envQ2));
        missionRepository.save(environmentMission);

        // 2. 관리비 만족도 미션
        WeeklyMission maintenanceMission = WeeklyMission.builder()
                .category("관리비")
                .title("관리비는 적절한가요?")
                .description("현재 지불하고 계신 관리비에 대한 만족도를 평가해주세요.")
                .startDate(LocalDate.now().minusDays(3))
                .endDate(LocalDate.now().plusDays(11))
                .isActive(true)
                .build();

        MissionQuestion maintQ1 = MissionQuestion.builder()
                .mission(maintenanceMission)
                .questionText("관리비 금액은 적절하다고 생각하시나요?")
                .questionType("multiple_choice")
                .options("[\"매우 적절함\", \"적절함\", \"보통\", \"부적절함\", \"매우 부적절함\"]")
                .orderNumber(1)
                .build();

        MissionQuestion maintQ2 = MissionQuestion.builder()
                .mission(maintenanceMission)
                .questionText("관리비 대비 서비스 품질은 어떤가요?")
                .questionType("multiple_choice")
                .options("[\"매우 좋음\", \"좋음\", \"보통\", \"나쁨\", \"매우 나쁨\"]")
                .orderNumber(2)
                .build();

        maintenanceMission.setQuestions(Arrays.asList(maintQ1, maintQ2));
        missionRepository.save(maintenanceMission);

        // 3. 소음 수준 미션
        WeeklyMission noiseMission = WeeklyMission.builder()
                .category("소음")
                .title("소음 수준은 어떤가요?")
                .description("거주하고 계신 건물의 소음 수준에 대한 평가를 해주세요.")
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(13))
                .isActive(true)
                .build();

        MissionQuestion noiseQ1 = MissionQuestion.builder()
                .mission(noiseMission)
                .questionText("이웃 간 소음 수준은 어떤가요?")
                .questionType("multiple_choice")
                .options("[\"매우 조용함\", \"조용함\", \"보통\", \"시끄러움\", \"매우 시끄러움\"]")
                .orderNumber(1)
                .build();

        MissionQuestion noiseQ2 = MissionQuestion.builder()
                .mission(noiseMission)
                .questionText("외부 소음 수준은 어떤가요?")
                .questionType("multiple_choice")
                .options("[\"매우 조용함\", \"조용함\", \"보통\", \"시끄러움\", \"매우 시끄러움\"]")
                .orderNumber(2)
                .build();

        noiseMission.setQuestions(Arrays.asList(noiseQ1, noiseQ2));
        missionRepository.save(noiseMission);

        log.info("샘플 미션 3개가 생성되었습니다.");
    }
}