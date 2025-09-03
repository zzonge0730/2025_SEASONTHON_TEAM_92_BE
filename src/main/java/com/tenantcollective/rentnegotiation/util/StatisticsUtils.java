package com.tenantcollective.rentnegotiation.util;

import java.util.List;
import java.util.stream.Collectors;

public class StatisticsUtils {
    
    public static double calculateAverage(List<Integer> values) {
        if (values.isEmpty()) {
            return 0.0;
        }
        return values.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }
    
    public static double calculateMedian(List<Integer> values) {
        if (values.isEmpty()) {
            return 0.0;
        }
        
        List<Integer> sortedValues = values.stream()
                .sorted()
                .collect(Collectors.toList());
        
        int size = sortedValues.size();
        if (size % 2 == 0) {
            return (sortedValues.get(size / 2 - 1) + sortedValues.get(size / 2)) / 2.0;
        } else {
            return sortedValues.get(size / 2).doubleValue();
        }
    }
    
    public static double calculateAverageNoticePct(List<Integer> noticePcts) {
        List<Integer> validNoticePcts = noticePcts.stream()
                .filter(pct -> pct != null && pct > 0)
                .collect(Collectors.toList());
        
        if (validNoticePcts.isEmpty()) {
            return 0.0;
        }
        
        return validNoticePcts.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }
}