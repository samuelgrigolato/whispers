package io.whispers.sns;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.AuthorizationErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
class WhispersSnsConfiguration {

    @Bean
    AmazonSNS amazonSns(@Value("${sns.endpoint}") String maybeEndpoint,
                        @Value("${whisper-created.topic_name}") String whisperCreatedTopicName,
                        @Value("${aws.credentials.access_key_id}") String accessKeyId,
                        @Value("${aws.credentials.secret_access_key}") String secretAccessKey) {
        var builder = AmazonSNSClientBuilder.standard();
        if (maybeEndpoint.isBlank()) {
            builder.withRegion("us-east-1");
        } else {
            builder.withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration(
                            maybeEndpoint,
                            "us-east-1"
                    )
            );
        }
        if (!accessKeyId.isBlank()) {
            var credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
            builder.withCredentials(new AWSStaticCredentialsProvider(credentials));
        }
        AmazonSNS amazonSNS = builder.build();
        try {
            amazonSNS.createTopic(whisperCreatedTopicName);
        } catch (AuthorizationErrorException e) {
            // this is expected when running in the cloud
        }
        return amazonSNS;
    }

}
