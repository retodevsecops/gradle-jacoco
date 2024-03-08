package com.consubanco.logger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogApiErrorDTO<T> implements Serializable {
    transient T headers;
    transient T queryParams;
    transient T pathParams;
    transient T bodyParams;
    transient T error;
    transient T method;
}
