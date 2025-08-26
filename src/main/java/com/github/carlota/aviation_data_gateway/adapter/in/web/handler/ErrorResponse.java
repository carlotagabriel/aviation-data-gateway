package com.github.carlota.aviation_data_gateway.adapter.in.web.handler;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class ErrorResponse {
    Instant timestamp;
    int status;
    String error;
    String message;
    String path;
}
