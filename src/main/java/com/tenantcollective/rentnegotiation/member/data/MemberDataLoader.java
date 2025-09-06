package com.tenantcollective.rentnegotiation.member.data;

import com.tenantcollective.rentnegotiation.member.domain.Member;
import com.tenantcollective.rentnegotiation.member.domain.Role;
import com.tenantcollective.rentnegotiation.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemberDataLoader implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (memberRepository.count() == 0) {
            log.info("회원 데이터를 초기화합니다...");
            createSampleMembers();
            log.info("회원 데이터 초기화 완료!");
        } else {
            log.info("회원 데이터가 이미 존재합니다. 건너뜁니다.");
        }
    }

    private void createSampleMembers() {
        // 테스트용 관리자 계정
        Member admin = Member.builder()
                .name("관리자")
                .email("admin@test.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.Admin)
                .building("테스트빌딩")
                .detailAddress("101호")
                .buildingType("오피스텔")
                .contractType("월세")
                .security(10000000L)
                .dong("테스트동")
                .build();

        memberRepository.save(admin);

        // 테스트용 일반 사용자 계정들
        Member user1 = Member.builder()
                .name("김세입자")
                .email("user1@test.com")
                .password(passwordEncoder.encode("user123"))
                .role(Role.User)
                .building("아파트A")
                .detailAddress("201호")
                .buildingType("아파트")
                .contractType("전세")
                .security(50000000L)
                .dong("강남동")
                .build();

        memberRepository.save(user1);

        Member user2 = Member.builder()
                .name("이세입자")
                .email("user2@test.com")
                .password(passwordEncoder.encode("user123"))
                .role(Role.User)
                .building("오피스텔B")
                .detailAddress("301호")
                .buildingType("오피스텔")
                .contractType("월세")
                .security(20000000L)
                .dong("서초동")
                .build();

        memberRepository.save(user2);

        Member user3 = Member.builder()
                .name("박세입자")
                .email("user3@test.com")
                .password(passwordEncoder.encode("user123"))
                .role(Role.User)
                .building("빌라C")
                .detailAddress("401호")
                .buildingType("빌라")
                .contractType("월세")
                .security(15000000L)
                .dong("송파동")
                .build();

        memberRepository.save(user3);

        log.info("샘플 회원 4명이 생성되었습니다.");
    }
}