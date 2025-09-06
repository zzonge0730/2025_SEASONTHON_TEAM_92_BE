package com.tenantcollective.rentnegotiation.officetel.application;

import com.tenantcollective.rentnegotiation.officetel.dto.OfficetelMarketDataResponseDTO;
import com.tenantcollective.rentnegotiation.officetel.dto.OfficetelTransactionResponseDTO;

import java.util.List;
import java.util.Map;

public interface OfficetelService {
    Map<String, List<OfficetelTransactionResponseDTO>> getOfficetelRentData(String lawdCd);
    List<OfficetelMarketDataResponseDTO> getJeonseMarketData(String lawdCd);
    List<OfficetelMarketDataResponseDTO> getMonthlyRentMarketData(String lawdCd);
}