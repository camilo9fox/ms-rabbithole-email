package com.rabbithole.email.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderEventDTO {
    /** Tipo de evento: "CREATED" | "STATUS_CHANGED" */
    private String type;
    private String userEmail;
    private String userName;
    private java.math.BigDecimal total;
    private String currency;
    private java.util.List<java.util.Map<String,Object>> items;
    private String newStatus;
    @com.fasterxml.jackson.annotation.JsonProperty("ordenId")
    private String orderId;
    
}
