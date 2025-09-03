package com.tenantcollective.rentnegotiation.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GroupIdGenerator {
    
    public static String generateGroupId(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            
            // Convert to hex and take first 8 characters
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < Math.min(4, hash.length); i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return "g_" + hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback to simple hash
            return "g_" + Integer.toHexString(input.hashCode());
        }
    }
}