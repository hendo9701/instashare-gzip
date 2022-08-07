package com.instashare.instasharegzip.files;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "files")
@Data
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndex(name = "owner_fileName_idx", def = "{'owner' : 1, 'fileName' : 1}", unique = true)
public class File {
  private @Id String id;

  private String fileName;

  private String owner;
  private FileStatus fileStatus;
  private Long size;
  private String mimeType;
}
