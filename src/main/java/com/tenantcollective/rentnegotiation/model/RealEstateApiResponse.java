package com.tenantcollective.rentnegotiation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class RealEstateApiResponse {
    @JsonProperty("response")
    private Response response;
    
    @JsonProperty("cmmMsgHeader")
    private CmmMsgHeader cmmMsgHeader;

    public static class Response {
        @JsonProperty("header")
        private Header header;
        
        @JsonProperty("body")
        private Body body;

        public static class Header {
            @JsonProperty("resultCode")
            private String resultCode;
            
            @JsonProperty("resultMsg")
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
            @JsonProperty("items")
            private Items items;
            
            @JsonProperty("numOfRows")
            private String numOfRows;
            
            @JsonProperty("pageNo")
            private String pageNo;
            
            @JsonProperty("totalCount")
            private String totalCount;

            public static class Items {
                @JsonProperty("item")
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
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public static class CmmMsgHeader {
        @JsonProperty("errMsg")
        private String errMsg;
        
        @JsonProperty("returnAuthMsg")
        private String returnAuthMsg;
        
        @JsonProperty("returnReasonCode")
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

    public CmmMsgHeader getCmmMsgHeader() {
        return cmmMsgHeader;
    }

    public void setCmmMsgHeader(CmmMsgHeader cmmMsgHeader) {
        this.cmmMsgHeader = cmmMsgHeader;
    }
}