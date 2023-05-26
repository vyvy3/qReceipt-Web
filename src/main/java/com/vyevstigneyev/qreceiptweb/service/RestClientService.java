package com.vyevstigneyev.qreceiptweb.service;

import com.vyevstigneyev.qreceiptweb.dto.AuthResponseDto;
import com.vyevstigneyev.qreceiptweb.dto.LoginDto;
import com.vyevstigneyev.qreceiptweb.dto.ReceiptCreateDto;
import com.vyevstigneyev.qreceiptweb.dto.ReceiptRegistryDto;
import com.vyevstigneyev.qreceiptweb.ui.CreateReceiptForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@SuppressWarnings("serial")
public class RestClientService {
    private static final String CREATE_RECEIPT_API = "/api/receipt/create";
    private static final String GET_ALL_MY_RECEIPTS_API = "/api/receipt/getAllMy";
    private static final String GET_RECEIPT_QR_API = "/api/receipt/qr/";
    private static final String LOGIN_API = "/public/auth/login";

    private String authToken = null;

    public boolean authenticate(LoginDto dto) {
        try {
            final String token = Objects.requireNonNull(Objects.requireNonNull(WebClientProvider.getWebClient()
                            .post().uri(LOGIN_API)
                            .bodyValue(dto)
                            .retrieve()
                            .toEntity(AuthResponseDto.class)
                            .block())
                    .getBody()).getAccessToken();
            this.authToken = "bearer " + token;
            return true;
        } catch(Exception e) {
            log.warn("Incorrect login attempt for user " + dto.getEmail());
            return false;
        }
    }

    public UUID saveReceipt(ReceiptCreateDto dto) {
        final UUID id = WebClientProvider.getWebClient()
                .post().uri(CREATE_RECEIPT_API)
                .header("Authorization", authToken)
                .bodyValue(dto)
                .retrieve()
                .toEntity(UUID.class)
                .block()
                .getBody();
        return id;
    }

    public List<ReceiptRegistryDto> getAllReceipts() {
        final List<ReceiptRegistryDto> receipts = WebClientProvider.getWebClient()
                        .get().uri(GET_ALL_MY_RECEIPTS_API)
                                .header("Authorization", authToken)
                        .retrieve()
                        .toEntityList(ReceiptRegistryDto.class)
                        .block()
                .getBody();
        return receipts;
    }

    public byte[] getQr(UUID id) {
        final byte[] qr = WebClientProvider.getWebClient()
                .get().uri(GET_RECEIPT_QR_API + id.toString())
                .header("Authorization", authToken)
                .retrieve()
                .toEntity(byte[].class)
                .block()
                .getBody();
        return qr;
    }

    public boolean isAuthenticated() {
        return nonNull(authToken);
    }

}
