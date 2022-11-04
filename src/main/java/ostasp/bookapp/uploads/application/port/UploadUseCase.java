package ostasp.bookapp.uploads.application.port;

import lombok.Value;
import ostasp.bookapp.uploads.domain.Upload;

import java.util.Optional;

public interface UploadUseCase {

    Upload save(SaveUploadCommand command);
    Optional <Upload> getById (Long id);

    void removeById(Long id);

    @Value
    class SaveUploadCommand{
        String filename;
        byte[] file;
        String contentType;
    }
}
