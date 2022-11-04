package ostasp.bookapp.uploads.application;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import ostasp.bookapp.uploads.application.port.UploadUseCase;
import ostasp.bookapp.uploads.db.UploadJpaRepository;
import ostasp.bookapp.uploads.domain.Upload;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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

        System.out.println("Upload saved: " + upload.getFilename() + " with id: " + savedUpload.getId());
        return upload;
    }

    @Override
    public Optional<Upload> getById(Long id) {
        return Optional.of(repository.getReferenceById(id));
    }

    @Override
    public void removeById(Long id) {
        repository.deleteById(id);
    }
}
