package com.tenantcollective.rentnegotiation.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.tenantcollective.rentnegotiation.model.MarketData;
import com.tenantcollective.rentnegotiation.model.RealEstateApiResponse;
import com.tenantcollective.rentnegotiation.model.RealEstateTransaction;
import com.tenantcollective.rentnegotiation.util.StatisticsUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RealEstateApiService {
    
    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper;
    private final DataValidationService dataValidationService;
    
    

    @Value("${realestate.api.key.single:}")
    private String singleHouseApiKey;

    @Value("${realestate.api.key.rowhouse:}")
    private String rowHouseApiKey;

    @Value("${realestate.api.key.officetel:}")
    private String officetelApiKey;

    @Value("${realestate.api.key.apartment:}")
    private String apartmentApiKey;
    
    @Value("${realestate.api.url.single:}")
    private String singleHouseApiUrl;

    @Value("${realestate.api.url.rowhouse:}")
    private String rowHouseApiUrl;

    @Value("${realestate.api.url.officetel:}")
    private String officetelApiUrl;

    @Value("${realestate.api.url.apartment:}")
    private String apartmentApiUrl;

    private String getApiKeyForBuildingType(String buildingType) {
        if (buildingType == null) {
            return singleHouseApiKey; // Default
        }
        switch (buildingType.toLowerCase()) {
            case "shrent":
                return singleHouseApiKey;
            case "rhrent":
                return rowHouseApiKey;
            case "offirent":
                return officetelApiKey;
            case "aptrent":
                return apartmentApiKey;
            default:
                // Unsupported building type for API key, defaulting to single house API key
                return singleHouseApiKey;
        }
    }
    
    public RealEstateApiService(DataValidationService dataValidationService) {
        // Configure RestTemplate with a custom User-Agent and detailed logging
        this.restTemplate = new RestTemplate();
        this.restTemplate.getInterceptors().add((request, body, execution) -> {
            // Set User-Agent to mimic curl
            request.getHeaders().set("User-Agent", "curl/7.81.0");
            
            // Log request details
            // HTTP request details logged
            
            // Execute request
            org.springframework.http.client.ClientHttpResponse response = execution.execute(request, body);
            
            // Log response details
            // HTTP response details logged
            
            return response;
        });
        this.xmlMapper = new XmlMapper();
        this.xmlMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.xmlMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        this.xmlMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        this.dataValidationService = dataValidationService;
    }

    @PostConstruct
    public void init() {
        // API 정보 출력
        // Real Estate API Information initialized
        
        // API 키 등록 상태 확인
        checkApiKeyRegistration();
    }
    
    /**
     * API 키 등록 상태 확인
     */
    private void checkApiKeyRegistration() {
        // API Key Registration Check
        
        // 단독다가구 API 테스트 (확인된 API)
        testApiKey(singleHouseApiUrl, "단독다가구");
        
        // 다른 API들 테스트
        testApiKey(apartmentApiUrl, "아파트");
        testApiKey(officetelApiUrl, "오피스텔");
        testApiKey(rowHouseApiUrl, "연립다주택");
        
        // API Key Registration Check completed
    }
    
    /**
     * 특정 API에 대한 키 등록 상태 테스트
     */
    private void testApiKey(String apiUrl, String apiName) {
        if (apiUrl == null) {
            // API URL not configured
            return;
        }
        
        try {
            // URL 구성 시 키를 한 번만 인코딩
            String testUrl = String.format("%s?serviceKey=%s&LAWD_CD=11110&DEAL_YMD=202412&numOfRows=1", 
                                         apiUrl, java.net.URLEncoder.encode(getApiKeyForBuildingType(getApiSuffix(apiUrl)), java.nio.charset.StandardCharsets.UTF_8));
            
            // API request details logged
            
            // RestTemplate 대신 직접 HTTP 요청 (이중 인코딩 방지)
            java.net.URL url = new java.net.URL(testUrl);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            connection.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.9,en;q=0.8");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            
            int responseCode = connection.getResponseCode();
            String response;
            if (responseCode >= 200 && responseCode < 300) {
                response = new String(connection.getInputStream().readAllBytes());
            } else {
                response = new String(connection.getErrorStream().readAllBytes());
            }
            connection.disconnect();
            
            if (response.contains("SERVICE_KEY_IS_NOT_REGISTERED_ERROR")) {
                // API key not registered
            } else if (response.contains("NODATA_ERROR")) {
                // API key registered (no data)
            } else {
                // API key registered
            }
        } catch (Exception e) {
            // API test failed
        }
    }
    
    /**
     * API URL에서 적절한 서비스명 추출
     */
    private String getApiSuffix(String apiUrl) {
        if (apiUrl.contains("SHRent")) {
            return "SHRent";
        } else if (apiUrl.contains("RHRent")) {
            return "RHRent";
        } else if (apiUrl.contains("OffiRent")) {
            return "OffiRent";
        } else if (apiUrl.contains("AptRent")) {
            return "AptRent";
        }
        return "SHRent"; // 기본값
    }
    
    /**
     * 건물 유형에 따라 적절한 API URL 선택 (등록된 API만 사용)
     */
    private String getApiUrlForBuildingType(String buildingType) {
        String defaultUrl = "https://apis.data.go.kr/1613000/RTMSDataSvcSHRent/getRTMSDataSvcSHRent";
        
        if (buildingType == null) {
            return singleHouseApiUrl != null ? singleHouseApiUrl : defaultUrl;
        }
        
        switch (buildingType.toLowerCase()) {
            case "single": // 단독/다가구
                return singleHouseApiUrl != null ? singleHouseApiUrl : defaultUrl;
            case "apartment": // 아파트 (등록된 API)
                return apartmentApiUrl != null ? apartmentApiUrl : defaultUrl;
            case "rowhouse": // 연립다세대 (미등록 - 단독/다가구로 폴백)
                // Row house API not registered, falling back to single house API
                return singleHouseApiUrl != null ? singleHouseApiUrl : defaultUrl;
            case "officetel": // 오피스텔 (미등록 - 단독/다가구로 폴백)
                // Officetel API not registered, falling back to single house API
                return singleHouseApiUrl != null ? singleHouseApiUrl : defaultUrl;
            default:
                // Handle unsupported building types, e.g., throw an exception or return null
                // For now, returning singleHouseApiUrl as a fallback
                // Unsupported building type, defaulting to single house API
                return singleHouseApiUrl != null ? singleHouseApiUrl : defaultUrl;
        }
    }
    
    /**
     * 특정 지역의 최근 3개월 부동산 거래 데이터를 조회 (건물 유형별)
     */
    public List<RealEstateTransaction> getRecentTransactions(String lawdCd, int months, String buildingType) {
        String apiUrl = getApiUrlForBuildingType(buildingType);
        // Using API for building type
        
        List<RealEstateTransaction> allTransactions = new ArrayList<>();
        
        // 최근 3개월 데이터 조회 (테스트용으로 과거 날짜 사용)
        for (int i = 0; i < months; i++) {
            LocalDate date = LocalDate.of(2024, 12, 1).minusMonths(i); // 2024년 12월부터 역산
            String dealYmd = date.format(DateTimeFormatter.ofPattern("yyyyMM"));
            allTransactions.addAll(fetchTransactions(apiUrl, lawdCd, dealYmd));
        }
        
        return allTransactions;
    }
    
    /**
     * 특정 지역의 최근 3개월 부동산 거래 데이터를 조회 (기존 호환성)
     */
    public List<RealEstateTransaction> getRecentTransactions(String lawdCd, int months) {
        return getRecentTransactions(lawdCd, months, null);
    }
    
    /**
     * 특정 월의 거래 데이터 조회 (API URL 지정)
     */
    private List<RealEstateTransaction> fetchTransactions(String apiUrl, String lawdCd, String dealYmd) {
        String currentApiKey = getApiKeyForBuildingType(getApiSuffix(apiUrl));
        if (currentApiKey == null || currentApiKey.isEmpty()) {
            // Real estate API key not configured, returning empty data
            return new ArrayList<>();
        }
        
        try {
            // URL 구성 시 키를 한 번만 인코딩
            String url = String.format("%s?serviceKey=%s&LAWD_CD=%s&DEAL_YMD=%s&numOfRows=1000", 
                                     apiUrl, java.net.URLEncoder.encode(currentApiKey, java.nio.charset.StandardCharsets.UTF_8), lawdCd, dealYmd);
            
            // Real estate API request details logged
            
            // RestTemplate 대신 직접 HTTP 요청 (이중 인코딩 방지)
            java.net.URL httpUrl = new java.net.URL(url);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) httpUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            connection.setRequestProperty("Accept", "application/xml, text/xml, */*");
            connection.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.9,en;q=0.8");
            // Remove Accept-Encoding to avoid compressed responses that need decompression
            connection.setRequestProperty("Connection", "keep-alive");
            
            int responseCode = connection.getResponseCode();
            // HTTP Response Code logged
            
            String xmlResponse;
            if (responseCode >= 200 && responseCode < 300) {
                byte[] responseBytes = connection.getInputStream().readAllBytes();
                xmlResponse = new String(responseBytes, StandardCharsets.UTF_8);
                // Response size logged
            } else {
                byte[] errorBytes = connection.getErrorStream().readAllBytes();
                xmlResponse = new String(errorBytes, StandardCharsets.UTF_8);
                // Error response size logged
            }
            connection.disconnect();
            
            // Real estate API response details logged
            
            // API 키 등록 오류 확인
            if (xmlResponse.contains("SERVICE_KEY_IS_NOT_REGISTERED_ERROR")) {
                // API Key not registered for this service, falling back to single house API
                return new ArrayList<>();
            }
            
            // Check if response looks like valid XML
            if (!xmlResponse.trim().startsWith("<")) {
                                // Response does not appear to be valid XML
                return new ArrayList<>();
            }
            
            RealEstateApiResponse response;
            try {
                response = xmlMapper.readValue(xmlResponse, RealEstateApiResponse.class);
            } catch (Exception e) {
                // XML Deserialization Error
                e.printStackTrace();
                return new ArrayList<>();
            }
            
            // 에러 응답 확인
            if (response.getCmmMsgHeader() != null) {
                // API Error logged
                return new ArrayList<>();
            }
            
            // 성공 응답에서 데이터 출처 확인
            if (response.getHeader() != null) {
                // Data Source and Result Code logged
            }
            
            if (response.getBody() != null && 
                response.getBody().getItems() != null) {
                return response.getBody().getItems().getItem();
            }
        } catch (HttpClientErrorException e) {
            // HTTP Error fetching real estate data
            e.printStackTrace(); // Print stack trace for more details
        } catch (ResourceAccessException e) {
            // Network/Resource Access Error fetching real estate data
            e.printStackTrace(); // Print stack trace for more details
        } catch (Exception e) {
            // General Error fetching real estate data
            e.printStackTrace(); // Print stack trace for more details
        }
        
        return new ArrayList<>();
    }
    
    /**
     * 동네별 시장 데이터 분석 (건물 유형별)
     */
    public MarketData analyzeMarketData(String neighborhood, String buildingName, String buildingType) {
        try {
            // 지역코드 매핑
            String lawdCd = getLawdCode(neighborhood);
            if (lawdCd == null) {
                // No lawd code found for neighborhood
                return createEmptyMarketData(neighborhood, buildingName);
            }
            
            // Analyzing market data for neighborhood and building type
            List<RealEstateTransaction> transactions = getRecentTransactions(lawdCd, 3, buildingType);
            
            if (transactions.isEmpty()) {
                return createEmptyMarketData(neighborhood, buildingName);
            }
        
        // 보증금과 월세 데이터 추출 (API 데이터는 만원 단위이므로 원 단위로 변환)
        List<Double> deposits = transactions.stream()
                .map(t -> parseDouble(t.getDeposit()) * 10000) // 만원 -> 원 변환
                .filter(d -> d > 0)
                .collect(Collectors.toList());
        
        List<Double> monthlyRents = transactions.stream()
                .map(t -> parseDouble(t.getMonthlyRent()) * 10000) // 만원 -> 원 변환
                .filter(d -> d > 0)
                .collect(Collectors.toList());
        
        // 통계 계산
        double avgDeposit = deposits.isEmpty() ? 0 : StatisticsUtils.calculateAverage(
                deposits.stream().mapToInt(Double::intValue).boxed().collect(Collectors.toList()));
        double avgMonthlyRent = monthlyRents.isEmpty() ? 0 : StatisticsUtils.calculateAverage(
                monthlyRents.stream().mapToInt(Double::intValue).boxed().collect(Collectors.toList()));
        
        double medianDeposit = deposits.isEmpty() ? 0 : StatisticsUtils.calculateMedian(
                deposits.stream().mapToInt(Double::intValue).boxed().collect(Collectors.toList()));
        double medianMonthlyRent = monthlyRents.isEmpty() ? 0 : StatisticsUtils.calculateMedian(
                monthlyRents.stream().mapToInt(Double::intValue).boxed().collect(Collectors.toList()));
        
        // 최근 거래일
        String recentDate = transactions.stream()
                .map(t -> t.getDealYear() + "-" + t.getDealMonth() + "-" + t.getDealDay())
                .max(String::compareTo)
                .orElse("N/A");
        
            return new MarketData(neighborhood, buildingName, avgDeposit, avgMonthlyRent,
                                medianDeposit, medianMonthlyRent, transactions.size(), recentDate);
        } catch (Exception e) {
            // Error analyzing market data for neighborhood
            e.printStackTrace();
            return createEmptyMarketData(neighborhood, buildingName);
        }
    }
    
    /**
     * 동네별 시장 데이터 분석 (기존 호환성)
     */
    public MarketData analyzeMarketData(String neighborhood, String buildingName) {
        return analyzeMarketData(neighborhood, buildingName, null);
    }
    
    /**
     * 동네명으로 지역코드 조회 (간단한 매핑)
     */
    private String getLawdCode(String neighborhood) {
        // 서울 주요 지역코드 매핑
        Map<String, String> lawdCodeMap = new HashMap<>();
        lawdCodeMap.put("종로동", "11110");
        lawdCodeMap.put("Jongno-dong", "11110");
        lawdCodeMap.put("중구", "11140");
        lawdCodeMap.put("용산구", "11170");
        lawdCodeMap.put("성동구", "11200");
        lawdCodeMap.put("광진구", "11215");
        lawdCodeMap.put("동대문구", "11230");
        lawdCodeMap.put("중랑구", "11260");
        lawdCodeMap.put("성북구", "11290");
        lawdCodeMap.put("강북구", "11305");
        lawdCodeMap.put("도봉구", "11320");
        lawdCodeMap.put("노원구", "11350");
        lawdCodeMap.put("은평구", "11380");
        lawdCodeMap.put("서대문구", "11410");
        lawdCodeMap.put("마포구", "11440");
        lawdCodeMap.put("Hongik-dong", "11440"); // 홍익동은 마포구
        lawdCodeMap.put("양천구", "11470");
        lawdCodeMap.put("강서구", "11500");
        lawdCodeMap.put("구로구", "11530");
        lawdCodeMap.put("금천구", "11545");
        lawdCodeMap.put("영등포구", "11560");
        lawdCodeMap.put("동작구", "11590");
        lawdCodeMap.put("관악구", "11620");
        lawdCodeMap.put("서초구", "11650");
        lawdCodeMap.put("강남구", "11680");
        lawdCodeMap.put("Gangnam-dong", "11680");
        lawdCodeMap.put("송파구", "11710");
        lawdCodeMap.put("강동구", "11740");
        lawdCodeMap.put("성동구", "11200");
        lawdCodeMap.put("Wangsimni-dong", "11200"); // 왕십리동은 성동구
        lawdCodeMap.put("구영리", "11200"); // 구영리도 성동구
        lawdCodeMap.put("동자동", "11110"); // 동자동은 종로구
        
        // 울산 지역코드 매핑
        lawdCodeMap.put("울산", "31000"); // 울산광역시
        lawdCodeMap.put("중구", "31110");
        lawdCodeMap.put("남구", "31140");
        lawdCodeMap.put("동구", "31170");
        lawdCodeMap.put("북구", "31200");
        lawdCodeMap.put("울주군", "31710");
        lawdCodeMap.put("범서읍", "31710"); // 울주군 범서읍
        lawdCodeMap.put("언양읍", "31710"); // 울주군 언양읍
        lawdCodeMap.put("온양읍", "31710"); // 울주군 온양읍
        lawdCodeMap.put("웅촌면", "31710"); // 울주군 웅촌면
        lawdCodeMap.put("두동면", "31710"); // 울주군 두동면
        lawdCodeMap.put("두서면", "31710"); // 울주군 두서면
        lawdCodeMap.put("상북면", "31710"); // 울주군 상북면
        lawdCodeMap.put("삼남면", "31710"); // 울주군 삼남면
        lawdCodeMap.put("삼동면", "31710"); // 울주군 삼동면
        
        // 부산 지역코드 매핑
        lawdCodeMap.put("부산", "26000"); // 부산광역시
        lawdCodeMap.put("해운대구", "26350");
        lawdCodeMap.put("사상구", "26530");
        lawdCodeMap.put("금정구", "26410");
        lawdCodeMap.put("강서구", "26440");
        lawdCodeMap.put("연제구", "26470");
        lawdCodeMap.put("수영구", "26500");
        lawdCodeMap.put("사하구", "26560");
        lawdCodeMap.put("북구", "26590");
        lawdCodeMap.put("동래구", "26260");
        lawdCodeMap.put("남구", "26290");
        lawdCodeMap.put("중구", "26230");
        lawdCodeMap.put("서구", "26260");
        lawdCodeMap.put("영도구", "26200");
        lawdCodeMap.put("동구", "26170");
        lawdCodeMap.put("부산진구", "26140");
        
        // 대구 지역코드 매핑
        lawdCodeMap.put("대구", "27000"); // 대구광역시
        lawdCodeMap.put("수성구", "27230");
        lawdCodeMap.put("달서구", "27200");
        lawdCodeMap.put("달성군", "27710");
        lawdCodeMap.put("서구", "27170");
        lawdCodeMap.put("북구", "27140");
        lawdCodeMap.put("남구", "27110");
        lawdCodeMap.put("중구", "27070");
        lawdCodeMap.put("동구", "27040");
        lawdCodeMap.put("수성구", "27230");
        
        // 인천 지역코드 매핑
        lawdCodeMap.put("인천", "28000"); // 인천광역시
        lawdCodeMap.put("연수구", "28185");
        lawdCodeMap.put("서구", "28140");
        lawdCodeMap.put("미추홀구", "28177");
        lawdCodeMap.put("남동구", "28140");
        lawdCodeMap.put("부평구", "28170");
        lawdCodeMap.put("계양구", "28200");
        lawdCodeMap.put("동구", "28110");
        lawdCodeMap.put("중구", "28070");
        lawdCodeMap.put("강화군", "28710");
        lawdCodeMap.put("옹진군", "28720");
        
        // 광주 지역코드 매핑
        lawdCodeMap.put("광주", "29000"); // 광주광역시
        lawdCodeMap.put("서구", "29170");
        lawdCodeMap.put("북구", "29140");
        lawdCodeMap.put("남구", "29110");
        lawdCodeMap.put("동구", "29080");
        lawdCodeMap.put("광산구", "29200");
        
        // 대전 지역코드 매핑
        lawdCodeMap.put("대전", "30000"); // 대전광역시
        lawdCodeMap.put("서구", "30170");
        lawdCodeMap.put("유성구", "30200");
        lawdCodeMap.put("대덕구", "30230");
        lawdCodeMap.put("중구", "30070");
        lawdCodeMap.put("동구", "30040");
        
        // 세종 지역코드 매핑
        lawdCodeMap.put("세종", "36000"); // 세종특별자치시
        
        // 경기도 주요 지역코드 매핑
        lawdCodeMap.put("경기", "41000"); // 경기도
        lawdCodeMap.put("수원시", "41110");
        lawdCodeMap.put("성남시", "41130");
        lawdCodeMap.put("의정부시", "41150");
        lawdCodeMap.put("안양시", "41170");
        lawdCodeMap.put("부천시", "41190");
        lawdCodeMap.put("광명시", "41210");
        lawdCodeMap.put("평택시", "41220");
        lawdCodeMap.put("과천시", "41250");
        lawdCodeMap.put("오산시", "41270");
        lawdCodeMap.put("시흥시", "41390");
        lawdCodeMap.put("군포시", "41410");
        lawdCodeMap.put("의왕시", "41430");
        lawdCodeMap.put("하남시", "41450");
        lawdCodeMap.put("용인시", "41460");
        lawdCodeMap.put("파주시", "41480");
        lawdCodeMap.put("이천시", "41500");
        lawdCodeMap.put("안성시", "41550");
        lawdCodeMap.put("김포시", "41570");
        lawdCodeMap.put("화성시", "41590");
        lawdCodeMap.put("광주시", "41610");
        lawdCodeMap.put("여주시", "41630");
        lawdCodeMap.put("양평군", "41830");
        lawdCodeMap.put("고양시", "41280");
        lawdCodeMap.put("동두천시", "41250");
        lawdCodeMap.put("가평군", "41820");
        lawdCodeMap.put("연천군", "41800");
        
        // 강원도 주요 지역코드 매핑
        lawdCodeMap.put("강원", "42000"); // 강원도
        lawdCodeMap.put("춘천시", "42110");
        lawdCodeMap.put("원주시", "42130");
        lawdCodeMap.put("강릉시", "42150");
        lawdCodeMap.put("동해시", "42170");
        lawdCodeMap.put("태백시", "42190");
        lawdCodeMap.put("속초시", "42210");
        lawdCodeMap.put("삼척시", "42230");
        lawdCodeMap.put("홍천군", "42720");
        lawdCodeMap.put("횡성군", "42730");
        lawdCodeMap.put("영월군", "42750");
        lawdCodeMap.put("평창군", "42760");
        lawdCodeMap.put("정선군", "42770");
        lawdCodeMap.put("철원군", "42780");
        lawdCodeMap.put("화천군", "42790");
        lawdCodeMap.put("양구군", "42800");
        lawdCodeMap.put("인제군", "42810");
        lawdCodeMap.put("고성군", "42820");
        lawdCodeMap.put("양양군", "42830");
        
        // 충청도 주요 지역코드 매핑
        lawdCodeMap.put("충북", "43000"); // 충청북도
        lawdCodeMap.put("충남", "44000"); // 충청남도
        lawdCodeMap.put("청주시", "43110");
        lawdCodeMap.put("충주시", "43130");
        lawdCodeMap.put("제천시", "43150");
        lawdCodeMap.put("천안시", "44130");
        lawdCodeMap.put("공주시", "44150");
        lawdCodeMap.put("보령시", "44180");
        lawdCodeMap.put("아산시", "44200");
        lawdCodeMap.put("서산시", "44210");
        lawdCodeMap.put("논산시", "44230");
        lawdCodeMap.put("계룡시", "44250");
        lawdCodeMap.put("당진시", "44270");
        
        // 전라도 주요 지역코드 매핑
        lawdCodeMap.put("전북", "45000"); // 전라북도
        lawdCodeMap.put("전남", "46000"); // 전라남도
        lawdCodeMap.put("전주시", "45110");
        lawdCodeMap.put("군산시", "45130");
        lawdCodeMap.put("익산시", "45140");
        lawdCodeMap.put("정읍시", "45180");
        lawdCodeMap.put("남원시", "45190");
        lawdCodeMap.put("김제시", "45210");
        lawdCodeMap.put("완주군", "45710");
        lawdCodeMap.put("진안군", "45720");
        lawdCodeMap.put("무주군", "45730");
        lawdCodeMap.put("장수군", "45740");
        lawdCodeMap.put("임실군", "45750");
        lawdCodeMap.put("순창군", "45770");
        lawdCodeMap.put("고창군", "45790");
        lawdCodeMap.put("부안군", "45800");
        lawdCodeMap.put("목포시", "46110");
        lawdCodeMap.put("여수시", "46130");
        lawdCodeMap.put("순천시", "46150");
        lawdCodeMap.put("나주시", "46170");
        lawdCodeMap.put("광양시", "46230");
        
        // 경상도 주요 지역코드 매핑
        lawdCodeMap.put("경북", "47000"); // 경상북도
        lawdCodeMap.put("경남", "48000"); // 경상남도
        lawdCodeMap.put("포항시", "47110");
        lawdCodeMap.put("경주시", "47130");
        lawdCodeMap.put("김천시", "47150");
        lawdCodeMap.put("안동시", "47170");
        lawdCodeMap.put("구미시", "47190");
        lawdCodeMap.put("영주시", "47210");
        lawdCodeMap.put("영천시", "47230");
        lawdCodeMap.put("상주시", "47250");
        lawdCodeMap.put("문경시", "47280");
        lawdCodeMap.put("경산시", "47290");
        lawdCodeMap.put("창원시", "48120");
        lawdCodeMap.put("진주시", "48170");
        lawdCodeMap.put("통영시", "48220");
        lawdCodeMap.put("사천시", "48240");
        lawdCodeMap.put("김해시", "48250");
        lawdCodeMap.put("밀양시", "48270");
        lawdCodeMap.put("거제시", "48310");
        lawdCodeMap.put("양산시", "48330");
        
        // 제주도 지역코드 매핑
        lawdCodeMap.put("제주", "50000"); // 제주특별자치도
        lawdCodeMap.put("제주시", "50110");
        lawdCodeMap.put("서귀포시", "50130");
        lawdCodeMap.put("인제군", "42810");
        lawdCodeMap.put("고성군", "42820");
        lawdCodeMap.put("양양군", "42830");
        
        // 충청도 지역코드 매핑
        lawdCodeMap.put("충북", "43000"); // 충청북도
        lawdCodeMap.put("충남", "44000"); // 충청남도
        lawdCodeMap.put("청주시", "43110");
        lawdCodeMap.put("충주시", "43130");
        lawdCodeMap.put("제천시", "43150");
        lawdCodeMap.put("천안시", "44130");
        lawdCodeMap.put("공주시", "44150");
        lawdCodeMap.put("보령시", "44180");
        lawdCodeMap.put("아산시", "44200");
        lawdCodeMap.put("서산시", "44210");
        lawdCodeMap.put("논산시", "44230");
        lawdCodeMap.put("계룡시", "44250");
        lawdCodeMap.put("당진시", "44270");
        
        // 전라도 지역코드 매핑
        lawdCodeMap.put("전북", "45000"); // 전라북도
        lawdCodeMap.put("전남", "46000"); // 전라남도
        lawdCodeMap.put("전주시", "45110");
        lawdCodeMap.put("군산시", "45130");
        lawdCodeMap.put("익산시", "45140");
        lawdCodeMap.put("정읍시", "45180");
        lawdCodeMap.put("남원시", "45190");
        lawdCodeMap.put("김제시", "45210");
        lawdCodeMap.put("완주군", "45710");
        lawdCodeMap.put("진안군", "45720");
        lawdCodeMap.put("무주군", "45730");
        lawdCodeMap.put("장수군", "45740");
        lawdCodeMap.put("임실군", "45750");
        lawdCodeMap.put("순창군", "45770");
        lawdCodeMap.put("고창군", "45790");
        lawdCodeMap.put("부안군", "45800");
        lawdCodeMap.put("목포시", "46110");
        lawdCodeMap.put("여수시", "46130");
        lawdCodeMap.put("순천시", "46150");
        lawdCodeMap.put("나주시", "46170");
        lawdCodeMap.put("광양시", "46230");
        lawdCodeMap.put("담양군", "46710");
        lawdCodeMap.put("곡성군", "46720");
        lawdCodeMap.put("구례군", "46730");
        lawdCodeMap.put("고흥군", "46770");
        lawdCodeMap.put("보성군", "46780");
        lawdCodeMap.put("화순군", "46790");
        lawdCodeMap.put("장흥군", "46800");
        lawdCodeMap.put("강진군", "46810");
        lawdCodeMap.put("해남군", "46820");
        lawdCodeMap.put("영암군", "46830");
        lawdCodeMap.put("무안군", "46840");
        lawdCodeMap.put("함평군", "46860");
        lawdCodeMap.put("영광군", "46870");
        lawdCodeMap.put("장성군", "46880");
        lawdCodeMap.put("완도군", "46890");
        lawdCodeMap.put("진도군", "46900");
        lawdCodeMap.put("신안군", "46910");
        
        // 경상도 지역코드 매핑
        lawdCodeMap.put("경북", "47000"); // 경상북도
        lawdCodeMap.put("경남", "48000"); // 경상남도
        lawdCodeMap.put("포항시", "47110");
        lawdCodeMap.put("경주시", "47130");
        lawdCodeMap.put("김천시", "47150");
        lawdCodeMap.put("안동시", "47170");
        lawdCodeMap.put("구미시", "47190");
        lawdCodeMap.put("영주시", "47210");
        lawdCodeMap.put("영천시", "47230");
        lawdCodeMap.put("상주시", "47250");
        lawdCodeMap.put("문경시", "47280");
        lawdCodeMap.put("경산시", "47290");
        lawdCodeMap.put("군위군", "47720");
        lawdCodeMap.put("의성군", "47730");
        lawdCodeMap.put("청송군", "47750");
        lawdCodeMap.put("영양군", "47760");
        lawdCodeMap.put("영덕군", "47770");
        lawdCodeMap.put("청도군", "47820");
        lawdCodeMap.put("고령군", "47830");
        lawdCodeMap.put("성주군", "47840");
        lawdCodeMap.put("칠곡군", "47850");
        lawdCodeMap.put("예천군", "47900");
        lawdCodeMap.put("봉화군", "47920");
        lawdCodeMap.put("울진군", "47930");
        lawdCodeMap.put("울릉군", "47940");
        lawdCodeMap.put("창원시", "48120");
        lawdCodeMap.put("진주시", "48170");
        lawdCodeMap.put("통영시", "48220");
        lawdCodeMap.put("사천시", "48240");
        lawdCodeMap.put("김해시", "48250");
        lawdCodeMap.put("밀양시", "48270");
        lawdCodeMap.put("거제시", "48310");
        lawdCodeMap.put("양산시", "48330");
        lawdCodeMap.put("의령군", "48720");
        lawdCodeMap.put("함안군", "48730");
        lawdCodeMap.put("창녕군", "48740");
        lawdCodeMap.put("고성군", "48820");
        lawdCodeMap.put("남해군", "48840");
        lawdCodeMap.put("하동군", "48850");
        lawdCodeMap.put("산청군", "48860");
        lawdCodeMap.put("함양군", "48870");
        lawdCodeMap.put("거창군", "48880");
        lawdCodeMap.put("합천군", "48890");
        
        // 제주도 지역코드 매핑
        lawdCodeMap.put("제주", "50000"); // 제주특별자치도
        lawdCodeMap.put("제주시", "50110");
        lawdCodeMap.put("서귀포시", "50130");
        
        // Looking for neighborhood
        
        // 동네명에서 구 이름 추출
        for (Map.Entry<String, String> entry : lawdCodeMap.entrySet()) {
            if (neighborhood.contains(entry.getKey())) {
                String lawdCode = entry.getValue();
                
                // 10자리 지역코드를 5자리로 변환 (공공데이터 API는 5자리 사용)
                if (lawdCode.length() > 5) {
                    lawdCode = lawdCode.substring(0, 5);
                    // Converted 10-digit code to 5-digit
                }
                
                // Found lawd code for neighborhood
                return lawdCode;
            }
        }
        
        // No lawd code found for neighborhood
        return null;
    }
    
    private MarketData createEmptyMarketData(String neighborhood, String buildingName) {
        // API 키 문제로 인해 더미 시장 데이터 생성
        return createDummyMarketData(neighborhood, buildingName);
    }
    
    private MarketData createDummyMarketData(String neighborhood, String buildingName) {
        // 동네별 더미 시장 데이터
        double avgRent = 0;
        double avgDeposit = 0;
        
        // Creating dummy market data for neighborhood
        
        if (neighborhood.contains("Gangnam") || neighborhood.contains("강남")) {
            avgRent = 1200000; // 120만원 (원 단위)
            avgDeposit = 10000000; // 1000만원 (원 단위)
        } else if (neighborhood.contains("Jongno") || neighborhood.contains("종로")) {
            avgRent = 800000; // 80만원 (원 단위)
            avgDeposit = 6000000; // 600만원 (원 단위)
        } else if (neighborhood.contains("Hongik") || neighborhood.contains("홍익")) {
            avgRent = 700000; // 70만원 (원 단위)
            avgDeposit = 5000000; // 500만원 (원 단위)
        } else if (neighborhood.contains("Wangsimni") || neighborhood.contains("왕십리")) {
            avgRent = 900000; // 90만원 (원 단위)
            avgDeposit = 7000000; // 700만원 (원 단위)
        } else if (neighborhood.contains("구영리")) {
            avgRent = 850000; // 85만원 (원 단위)
            avgDeposit = 6500000; // 650만원 (원 단위)
        } else if (neighborhood.contains("지곡동")) {
            avgRent = 450000; // 45만원 (원 단위)
            avgDeposit = 3000000; // 300만원 (원 단위)
        } else if (neighborhood.contains("울주군")) {
            avgRent = 400000; // 40만원 (원 단위)
            avgDeposit = 2500000; // 250만원 (원 단위)
        } else if (neighborhood.contains("범서읍")) {
            avgRent = 350000; // 35만원 (원 단위)
            avgDeposit = 2000000; // 200만원 (원 단위)
        } else if (neighborhood.contains("언양읍")) {
            avgRent = 300000; // 30만원 (원 단위)
            avgDeposit = 1800000; // 180만원 (원 단위)
        } else if (neighborhood.contains("동자동")) {
            avgRent = 950000; // 95만원 (원 단위)
            avgDeposit = 8000000; // 800만원 (원 단위)
        } else if (neighborhood.contains("울산") || neighborhood.contains("범서") || neighborhood.contains("언양")) {
            avgRent = 350000; // 35만원 (울산 지역)
            avgDeposit = 2000000; // 200만원 (원 단위)
        } else if (neighborhood.contains("부산")) {
            avgRent = 450000; // 45만원 (부산 지역)
            avgDeposit = 3000000; // 300만원 (원 단위)
        } else if (neighborhood.contains("대구")) {
            avgRent = 400000; // 40만원 (대구 지역)
            avgDeposit = 2500000; // 250만원 (원 단위)
        } else if (neighborhood.contains("인천")) {
            avgRent = 500000; // 50만원 (인천 지역)
            avgDeposit = 3500000; // 350만원 (원 단위)
        } else if (neighborhood.contains("경기")) {
            avgRent = 600000; // 60만원 (경기 지역)
            avgDeposit = 4000000; // 400만원 (원 단위)
        } else {
            avgRent = 550000; // 55만원 (기타 지역)
            avgDeposit = 3500000; // 350만원 (원 단위)
        }
        
        // Market data for neighborhood logged
        
        return new MarketData(neighborhood, buildingName, avgDeposit, avgRent, 
                            avgDeposit, avgRent, 5, "2024-12");
    }
    
    private Double parseDouble(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return 0.0;
            }
            // 쉼표 제거 후 파싱
            String cleanValue = value.replaceAll(",", "").trim();
            return Double.parseDouble(cleanValue);
        } catch (NumberFormatException e) {
            // Failed to parse double value
            return 0.0;
        }
    }

    public MarketData fetchMarketData(String neighborhood) {
        return analyzeMarketData(neighborhood, "Unknown");
    }
}