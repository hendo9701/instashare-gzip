package com.instashare.instasharegzip.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(S3ClientConfigurationProperties.class)
public class S3ClientConfig {

  @Bean
  public AmazonS3 amazonS3(S3ClientConfigurationProperties s3Config, AWSCredentials credentials) {
    return AmazonS3ClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(credentials))
        .withRegion(s3Config.getRegion())
        .build();
  }

  @Bean
  public AWSCredentials awsCredentials(S3ClientConfigurationProperties s3Properties) {
    return new BasicAWSCredentials(
        s3Properties.getAccessKeyId(), s3Properties.getSecretAccessKey());
  }
}
