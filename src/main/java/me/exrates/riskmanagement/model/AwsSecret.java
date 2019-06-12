package me.exrates.riskmanagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AwsSecret {

    @JsonProperty("public_key")
    private String keyId;

    @JsonProperty("private_key")
    private String keySecret;
}
