package com.instashare.instasharegzip.services;

import com.amazonaws.services.s3.AmazonS3;
import com.instashare.instasharegzip.config.S3ClientConfigurationProperties;
import com.instashare.instasharegzip.files.File;
import com.instashare.instasharegzip.files.FileService;
import com.instashare.instasharegzip.files.FileStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumeMessageService {

  private final AmazonS3 amazonS3;

  private final S3ClientConfigurationProperties s3Properties;

  private final FileService fileService;

  public void consumeMessage(String fileKey) throws IOException {
    log.info("Processing: {}", fileKey);
    val optionalOldFile = fileService.getById(fileKey);
    if (optionalOldFile.isEmpty()) {
      log.error("File not found: {}", fileKey);
      return;
    }
    val oldFile = optionalOldFile.get();
    // 1- Compress old file
    val compressedFile = compressFile(oldFile);
    // 2- Delete old file from s3
    deleteFileFromS3(oldFile.getId());
    // 3- Upload compressed file to s3
    uploadCompressedFile(oldFile, compressedFile);
    val updatedFile =
        new File(
            oldFile.getId(),
            oldFile.getFileName() + ".gz",
            oldFile.getOwner(),
            FileStatus.COMPRESSED,
            Files.size(compressedFile),
            "application/x-gzip-compressed");
    // 4- Update file metadata
    fileService.save(updatedFile);
    deleteTmpFile(compressedFile);
    log.info("File: {} has been processed.", fileKey);
  }

  private void deleteFileFromS3(String id) {
    amazonS3.deleteObject(s3Properties.getBucket(), id);
  }

  /**
   * Uploads a local file to AWS S3
   *
   * @param file The old file metadata
   * @param compressedFile The compressed file path
   * @throws IOException
   */
  private void uploadCompressedFile(File file, Path compressedFile) throws IOException {
    amazonS3.putObject(s3Properties.getBucket(), file.getId(), compressedFile.toFile());
  }

  /**
   * Deletes a file from the disk
   *
   * @param compressedFile The path to the file to be deleted
   */
  private void deleteTmpFile(Path compressedFile) {
    val deleted = compressedFile.toFile().delete();
    if (!deleted) {
      log.error("Unable to delete tmp file: {}", compressedFile.getFileName());
    }
  }

  /**
   * Compresses a file from s3 storage and saves into disk
   *
   * @param file The file metadata
   * @return The path to the compressed file
   * @throws IOException
   */
  private Path compressFile(File file) throws IOException {
    val s3Object = amazonS3.getObject(s3Properties.getBucket(), file.getId());
    val compressedPath = Files.createTempFile("compressed-", ".gz");
    try (val from = s3Object.getObjectContent();
        val to = new GZIPOutputStream(new FileOutputStream(compressedPath.toFile()))) {
      var bytes = new byte[1024];
      int length;
      while ((length = from.read(bytes)) > 0) {
        to.write(bytes, 0, length);
      }
    }
    return compressedPath;
  }
}
