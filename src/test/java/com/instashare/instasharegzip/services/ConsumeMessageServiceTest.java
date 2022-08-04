package com.instashare.instasharegzip.services;

import com.amazonaws.services.kinesis.model.InvalidArgumentException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.instashare.instasharegzip.config.S3ClientConfigurationProperties;
import com.instashare.instasharegzip.files.FileService;
import com.instashare.instasharegzip.files.FileStatus;
import lombok.val;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ConsumeMessageServiceTest {

  ConsumeMessageService consumeMessageService;

  @MockBean AmazonS3 amazonS3;

  @MockBean S3ClientConfigurationProperties clientConfigurationProperties;

  @MockBean FileService fileService;

  private static final String NON_EXISTING_ID = "1";

  @BeforeEach
  void prepare() {
    val someObjectResult = mock(PutObjectResult.class);
    when(amazonS3.putObject(any(), any(), any(File.class))).thenReturn(someObjectResult);
    doNothing().when(amazonS3).deleteObject(any(), any());
    when(fileService.getById(NON_EXISTING_ID))
        .thenThrow(new InvalidArgumentException(NON_EXISTING_ID));
    consumeMessageService =
        new ConsumeMessageService(amazonS3, clientConfigurationProperties, fileService);
  }

  @Test
  @DisplayName("Consuming a non-existing file id must result in a thrown exception")
  void consumeMessageModifiesFileStatus() {
    // Then
    Assertions.assertThrows(
        InvalidArgumentException.class,
        () -> consumeMessageService.consumeMessage(NON_EXISTING_ID));
  }

  @Test
  @DisplayName("Deleting a non-existing file must not succeed")
  void deleteTmpFileNonExistingFile() {
    // Given
    val noExistingFile = Path.of("Does not exist");
    // When
    val deleteResult = consumeMessageService.deleteTmpFile(noExistingFile);
    // Then
    assertThat("Deleting a non-existing file must return false", deleteResult, Matchers.is(false));
  }

  @Test
  @DisplayName("Deleting an existing file must succeed")
  void deleteTmpFileExisting() throws IOException {
    // Given
    val existingFile = Files.createTempFile("foo", "bar");
    // When
    val deleteResult = consumeMessageService.deleteTmpFile(existingFile);
    // Then
    assertThat("Deleting an existing file must return true", deleteResult, Matchers.is(true));
  }

  @Test
  @DisplayName("Compressing a given file must succeed")
  void compressFile() throws IOException {
    // Given
    val givenString = new ByteArrayInputStream("Foo".getBytes(StandardCharsets.UTF_8));
    // When
    val compressResult = consumeMessageService.compressFile(givenString);
    // Then
    assertThat("Compressed file must exist", compressResult.toFile().exists(), Matchers.is(true));

    compressResult.toFile().delete();
  }

  @Test
  @DisplayName("Uploading a compressed file must succeed")
  void uploadCompressedFile() {
    // Given
    val somePath = Path.of("Foo");
    val someFile =
        new com.instashare.instasharegzip.files.File("1", "", "", FileStatus.RAW, 1L, "");
    // When
    consumeMessageService.uploadCompressedFile(someFile, somePath);
  }

  @Test
  @DisplayName("Delete file from S3 must be executed")
  void deleteFileFromS3() {
    // Given
    val someId = "123";
    // When
    consumeMessageService.deleteFileFromS3(someId);
  }
}
