package ostasp.bookapp.uploads.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ostasp.bookapp.uploads.application.port.UploadUseCase;
import ostasp.bookapp.uploads.db.UploadJpaRepository;
import ostasp.bookapp.uploads.domain.Upload;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UploadService implements UploadUseCase {

    private final UploadJpaRepository repository;

    @Override
    public Upload save(SaveUploadCommand command) {
        Upload upload = Upload.builder()
                .file(command.getFile())
                .contentType(command.getContentType())
                .filename(command.getFilename())
                .build();

        Upload savedUpload = repository.save(upload);

        log.info("Upload saved: " + upload.getFilename() + " with id: " + savedUpload.getId());
        return upload;
    }

    @Override
    public Optional<Upload> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void removeById(Long id) {
        repository.deleteById(id);
    }
}
