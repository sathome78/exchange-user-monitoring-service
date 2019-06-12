package me.exrates.riskmanagement.service;

import me.exrates.riskmanagement.model.AwsSecret;

public interface AWSSecretLookup {
    AwsSecret getSecretKeys(String region, String name);
}
