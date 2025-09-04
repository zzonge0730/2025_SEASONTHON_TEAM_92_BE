package com.tenantcollective.rentnegotiation.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GroupIdGenerator {
    
    /**
     * 주소를 기반으로 그룹 ID를 생성
     */
    public static String generateGroupId(String address) {
        if (address == null || address.trim().isEmpty()) {
            return "default-group-" + System.currentTimeMillis();
        }
        
        try {
            // 주소를 해시화하여 고유한 ID 생성
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(address.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            
            // 앞의 8자리만 사용하여 짧은 ID 생성
            return "group-" + sb.toString().substring(0, 8);
        } catch (NoSuchAlgorithmException e) {
            // MD5가 지원되지 않는 경우 타임스탬프 사용
            return "group-" + System.currentTimeMillis();
        }
    }
    
    /**
     * 건물명과 주소를 조합하여 그룹 ID 생성
     */
    public static String generateGroupId(String buildingName, String address) {
        String combined = (buildingName != null ? buildingName : "") + "|" + (address != null ? address : "");
        return generateGroupId(combined);
    }
}
