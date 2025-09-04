package com.tenantcollective.rentnegotiation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class RealEstateTransaction {
    @JacksonXmlProperty(localName = "dealYear")
    private String dealYear;
    
    @JacksonXmlProperty(localName = "dealMonth")
    private String dealMonth;
    
    @JacksonXmlProperty(localName = "dealDay")
    private String dealDay;
    
    @JacksonXmlProperty(localName = "deposit")
    private String deposit; // 보증금 (만원)
    
    @JacksonXmlProperty(localName = "monthlyRent")
    private String monthlyRent; // 월세 (만원)
    
    @JacksonXmlProperty(localName = "houseType")
    private String houseType; // 주택유형 (단독/다가구)
    
    @JacksonXmlProperty(localName = "umdNm")
    private String umdNm; // 법정동 이름
    
    @JacksonXmlProperty(localName = "totalFloorAr")
    private String totalFloorAr; // 연면적
    
    @JacksonXmlProperty(localName = "contractTerm")
    private String contractTerm; // 계약기간
    
    @JacksonXmlProperty(localName = "contractType")
    private String contractType; // 계약구분
    
    @JacksonXmlProperty(localName = "useRRRight")
    private String useRRRight; // 갱신요구권 사용 여부
    
    @JacksonXmlProperty(localName = "preDeposit")
    private String preDeposit; // 종전 계약 보증금
    
    @JacksonXmlProperty(localName = "preMonthlyRent")
    private String preMonthlyRent; // 종전 계약 월세
    
    @JacksonXmlProperty(localName = "buildYear")
    private String buildYear; // 건축년도
    
    @JacksonXmlProperty(localName = "sggCd")
    private String sggCd; // 시군구코드

    // Constructors
    public RealEstateTransaction() {}

    // Getters and Setters
    public String getDealYear() {
        return dealYear;
    }

    public void setDealYear(String dealYear) {
        this.dealYear = dealYear;
    }

    public String getDealMonth() {
        return dealMonth;
    }

    public void setDealMonth(String dealMonth) {
        this.dealMonth = dealMonth;
    }

    public String getDealDay() {
        return dealDay;
    }

    public void setDealDay(String dealDay) {
        this.dealDay = dealDay;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(String monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public String getHouseType() {
        return houseType;
    }

    public void setHouseType(String houseType) {
        this.houseType = houseType;
    }

    public String getUmdNm() {
        return umdNm;
    }

    public void setUmdNm(String umdNm) {
        this.umdNm = umdNm;
    }

    public String getTotalFloorAr() {
        return totalFloorAr;
    }

    public void setTotalFloorAr(String totalFloorAr) {
        this.totalFloorAr = totalFloorAr;
    }

    public String getContractTerm() {
        return contractTerm;
    }

    public void setContractTerm(String contractTerm) {
        this.contractTerm = contractTerm;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getUseRRRight() {
        return useRRRight;
    }

    public void setUseRRRight(String useRRRight) {
        this.useRRRight = useRRRight;
    }

    public String getPreDeposit() {
        return preDeposit;
    }

    public void setPreDeposit(String preDeposit) {
        this.preDeposit = preDeposit;
    }

    public String getPreMonthlyRent() {
        return preMonthlyRent;
    }

    public void setPreMonthlyRent(String preMonthlyRent) {
        this.preMonthlyRent = preMonthlyRent;
    }

    public String getBuildYear() {
        return buildYear;
    }

    public void setBuildYear(String buildYear) {
        this.buildYear = buildYear;
    }

    public String getSggCd() {
        return sggCd;
    }

    public void setSggCd(String sggCd) {
        this.sggCd = sggCd;
    }
}