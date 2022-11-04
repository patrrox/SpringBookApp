package ostasp.bookapp.uploads.db;

import org.springframework.data.jpa.repository.JpaRepository;
import ostasp.bookapp.uploads.domain.Upload;

public interface UploadJpaRepository extends JpaRepository<Upload,Long> {
}
