package com.tenantcollective.rentnegotiation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.List;

public class RealEstateApiResponse {
    @JacksonXmlProperty(localName = "header")
    private Header header;
    
    @JacksonXmlProperty(localName = "body")
    private Body body;
    
    @JacksonXmlProperty(localName = "cmmMsgHeader")
    private CmmMsgHeader cmmMsgHeader;

    public static class Header {
        @JacksonXmlProperty(localName = "resultCode")
        private String resultCode;
        
        @JacksonXmlProperty(localName = "resultMsg")
        private String resultMsg;

        public String getResultCode() {
            return resultCode;
        }

        public void setResultCode(String resultCode) {
            this.resultCode = resultCode;
        }

        public String getResultMsg() {
            return resultMsg;
        }

        public void setResultMsg(String resultMsg) {
            this.resultMsg = resultMsg;
        }
    }

    public static class Body {
        @JacksonXmlProperty(localName = "items")
        private Items items;
        
        @JacksonXmlProperty(localName = "numOfRows")
        private String numOfRows;
        
        @JacksonXmlProperty(localName = "pageNo")
        private String pageNo;
        
        @JacksonXmlProperty(localName = "totalCount")
        private String totalCount;

        public static class Items {
            @JacksonXmlElementWrapper(useWrapping = false)
            @JacksonXmlProperty(localName = "item")
            private List<RealEstateTransaction> item;

            public List<RealEstateTransaction> getItem() {
                return item;
            }

            public void setItem(List<RealEstateTransaction> item) {
                this.item = item;
            }
        }

        public Items getItems() {
            return items;
        }

        public void setItems(Items items) {
            this.items = items;
        }

        public String getNumOfRows() {
            return numOfRows;
        }

        public void setNumOfRows(String numOfRows) {
            this.numOfRows = numOfRows;
        }

        public String getPageNo() {
            return pageNo;
        }

        public void setPageNo(String pageNo) {
            this.pageNo = pageNo;
        }

        public String getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(String totalCount) {
            this.totalCount = totalCount;
        }
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public CmmMsgHeader getCmmMsgHeader() {
        return cmmMsgHeader;
    }

    public void setCmmMsgHeader(CmmMsgHeader cmmMsgHeader) {
        this.cmmMsgHeader = cmmMsgHeader;
    }

    public static class CmmMsgHeader {
        @JacksonXmlProperty(localName = "errMsg")
        private String errMsg;
        
        @JacksonXmlProperty(localName = "returnAuthMsg")
        private String returnAuthMsg;
        
        @JacksonXmlProperty(localName = "returnReasonCode")
        private String returnReasonCode;

        public String getErrMsg() {
            return errMsg;
        }

        public void setErrMsg(String errMsg) {
            this.errMsg = errMsg;
        }

        public String getReturnAuthMsg() {
            return returnAuthMsg;
        }

        public void setReturnAuthMsg(String returnAuthMsg) {
            this.returnAuthMsg = returnAuthMsg;
        }

        public String getReturnReasonCode() {
            return returnReasonCode;
        }

        public void setReturnReasonCode(String returnReasonCode) {
            this.returnReasonCode = returnReasonCode;
        }
    }
}