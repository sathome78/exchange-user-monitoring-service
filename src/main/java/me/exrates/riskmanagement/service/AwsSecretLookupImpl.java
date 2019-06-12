package me.exrates.riskmanagement.service;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.riskmanagement.model.AwsSecret;

import java.io.IOException;

public class AwsSecretLookupImpl implements AWSSecretLookup {

    private ObjectMapper objectMapper = new ObjectMapper();

    private String getSecret(String region, String secretName) {

        AWSSecretsManagerClientBuilder clientBuilder = AWSSecretsManagerClientBuilder
                .standard()
                .withRegion(region);
        AWSSecretsManager client = clientBuilder.build();

        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretName);
        GetSecretValueResult getSecretValueResult;
        try {
            getSecretValueResult = client.getSecretValue(getSecretValueRequest);
        } catch (DecryptionFailureException e) {
            // Secrets Manager can't decrypt the protected secret text using the provided KMS key.
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        } catch (InternalServiceErrorException e) {
            // An error occurred on the server side.
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        } catch (InvalidParameterException e) {
            // You provided an invalid value for a parameter.
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        } catch (InvalidRequestException e) {
            // You provided a parameter value that is not valid for the current state of the resource.
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        } catch (ResourceNotFoundException e) {
            // We can't find the resource that you asked for.
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        }
        return getSecretValueResult.getSecretString();
    }

    @Override
    public AwsSecret getSecretKeys(String region, String name) {
        AwsSecret secret;
        try {
            return objectMapper.readValue(getSecret(region, name), AwsSecret.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
