package com.tenantcollective.rentnegotiation;

import com.tenantcollective.rentnegotiation.model.Tenant;
import com.tenantcollective.rentnegotiation.repo.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

// @Component
public class DemoDataLoader implements CommandLineRunner {

    @Autowired
    private TenantRepository tenantRepository;

    @Override
    public void run(String... args) throws Exception {
        // Demo data loading disabled - using real user data only
        // if (tenantRepository.findAll().isEmpty()) {
        //     loadDemoData();
        // }
    }

    private void loadDemoData() {
        List<Tenant> demoTenants = Arrays.asList(
            new Tenant("Happy Apt", "Seoul Jongno-gu Jongno 12", "Jongno-dong", "Seoul", 
                      750000, 5000000, "2025-12", 5, "owner1@example.com", 
                      "Elevator issues, short notice", true),
            
            new Tenant("Happy Apt", "Seoul Jongno-gu Jongno 12", "Jongno-dong", "Seoul", 
                      720000, 4800000, "2025-11", 4, "owner1@example.com", 
                      "Heating problems", true),
            
            new Tenant("Happy Apt", "Seoul Jongno-gu Jongno 12", "Jongno-dong", "Seoul", 
                      780000, 5200000, "2025-10", 6, "owner1@example.com", 
                      "Noise from construction", true),
            
            new Tenant("Green Villa", "Seoul Gangnam-gu Teheran-ro 123", "Gangnam-dong", "Seoul", 
                      1200000, 10000000, "2025-09", 8, "owner2@example.com", 
                      "High maintenance fees", true),
            
            new Tenant("Green Villa", "Seoul Gangnam-gu Teheran-ro 123", "Gangnam-dong", "Seoul", 
                      1150000, 9500000, "2025-08", 7, "owner2@example.com", 
                      "Parking issues", true),
            
            new Tenant("Blue House", "Seoul Mapo-gu Hongik-ro 45", "Hongik-dong", "Seoul", 
                      650000, 3000000, "2025-07", 3, "owner3@example.com", 
                      "Old building, needs renovation", true),
            
            new Tenant("Blue House", "Seoul Mapo-gu Hongik-ro 45", "Hongik-dong", "Seoul", 
                      680000, 3200000, "2025-06", 4, "owner3@example.com", 
                      "Water pressure issues", true),
            
            new Tenant("Sunshine Tower", "Seoul Seongdong-gu Wangsimni-ro 78", "Wangsimni-dong", "Seoul", 
                      950000, 8000000, "2025-05", 5, "owner4@example.com", 
                      "Elevator maintenance", true)
        );

        for (Tenant tenant : demoTenants) {
            tenantRepository.save(tenant);
        }

        System.out.println("Demo data loaded successfully!");
    }
}