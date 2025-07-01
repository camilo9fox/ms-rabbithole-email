package com.rabbithole.email.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class DisenoEventDTO {
    private String type;
    private String userEmail;
    private String userName;
    private String designName;
    private String newStatus;
    private String previewUrl;
}
