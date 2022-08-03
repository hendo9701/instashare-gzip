package com.instashare.instasharegzip.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.s3")
@Data
public class S3ClientConfigurationProperties {

  private String region;

  private String accessKeyId;

  private String secretAccessKey;

  private String bucket;
}
