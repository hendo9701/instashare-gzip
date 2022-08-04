package com.instashare.instasharegzip.files;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class FileServiceTest {

  @MockBean FileRepository fileRepository;

  FileService fileService;

  private final String EXISTING_ID = "1";

  private final String NON_EXISTING_ID = "2";

  private final File someFile = new File(EXISTING_ID, "", "", FileStatus.RAW, 1L, "");

  @BeforeEach
  void setup() {
    when(fileRepository.findById(EXISTING_ID)).thenReturn(of(someFile));
    when(fileRepository.findById(NON_EXISTING_ID)).thenReturn(empty());
    when(fileRepository.save(someFile)).thenReturn(someFile);
    fileService = new FileService(fileRepository);
  }

  @Test
  @DisplayName("When a non-existing id is requested then an empty optional must be retrieved")
  void getByIdNonExisting() {
    // When
    val result = fileService.getById(NON_EXISTING_ID);
    // Then
    assertThat("Result must be empty optional", result, is(empty()));
  }

  @Test
  @DisplayName("When a non-existing id is requested then an empty optional must be retrieved")
  void getByIdExisting() {
    // When
    val result = fileService.getById(EXISTING_ID);
    // Then
    assertThat("Result must be non-empty optional", result, not(is(empty())));
  }

  @Test
  @DisplayName("When a file is saved then the same file must be obtained")
  void save() {
    // When
    val savedFile = fileService.save(someFile);
    assertThat("Files must match", savedFile, is(someFile));
  }
}
