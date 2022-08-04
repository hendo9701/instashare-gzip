package com.instashare.instasharegzip.files;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileService {

  private final FileRepository fileRepository;

  public Optional<File> getById(@NonNull String id) {
    return fileRepository.findById(id);
  }

  public File save(@NonNull File file) {
    return fileRepository.save(file);
  }
}
