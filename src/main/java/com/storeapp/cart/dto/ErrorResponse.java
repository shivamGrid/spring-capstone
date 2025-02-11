package com.storeapp.cart.dto;

import lombok.*;

@Data
@Builder
public class ErrorResponse {
    private String errorCode;
    private String errorMsg;
}
