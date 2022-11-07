package ostasp.bookapp.catalog.application;

import lombok.AllArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.stereotype.Service;
import ostasp.bookapp.catalog.application.port.CatalogUseCase;
import ostasp.bookapp.catalog.db.AuthorJpaRepository;
import ostasp.bookapp.catalog.db.BookJpaRepository;
import ostasp.bookapp.catalog.domain.Author;
import ostasp.bookapp.catalog.domain.Book;
import ostasp.bookapp.uploads.application.port.UploadUseCase;
import ostasp.bookapp.uploads.application.port.UploadUseCase.SaveUploadCommand;
import ostasp.bookapp.uploads.domain.Upload;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class vice implements CatalogUseCase {
    //private final CatalogRepository repository;
    private final BookJpaRepository bookRepository;
    private final AuthorJpaRepository authorRepository;
    private final UploadUseCase upload;

    @Override
    public List<Book> findByTitle(String title) {
        return bookRepository.findByTitleStartsWithIgnoreCase(title);
    }

    @Override
    public Optional<Book> findOneByTitle(String title) {
        return bookRepository.findOneByTitleContainsIgnoreCase(title);
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public List<Book> findByTitleAndAuthor(String title, String author) {
        return bookRepository.findByTitleAndAuthor(title, author);
    }


    @Override
    public Book addBook(CreateBookCommand createBookCommand) {
        Book book = toBook(createBookCommand);
        return bookRepository.save(book);
    }

    private Book toBook(CreateBookCommand command) {
        Book book = new Book(command.getTitle(), command.getYear(), command.getPrice());
        Set<Author> authors = fetchAuthorsByIds(command.getAuthors());
        updateBooks(book, authors);
        return book;
    }

    private void updateBooks(Book book, Set<Author> authors) {
        book.removeAuthors();
        authors.forEach(book::addAuthor);
    }

    private Set<Author> fetchAuthorsByIds(Set<Long> authors) {
        return authors.stream()
                .map(authorId -> authorRepository
                        .findById(authorId)
                        .orElseThrow(() -> new IllegalArgumentException("Unable to find author with id: " + authorId)))
                .collect(Collectors.toSet());

    }

    @Override
    public UpdateBookResponse updateBook(UpdateBookCommand command) {
        return bookRepository.findById(command.getId())
                .map(book -> {
                    Book updatedBook = updateFields(command, book);
                    bookRepository.save(updatedBook);
                    return UpdateBookResponse.SUCCESS;
                })
                .orElseGet(() -> new UpdateBookResponse(false, List.of("Book not found with ID " + command.getId())));
    }

    private Book updateFields(UpdateBookCommand command, Book book) {
        if (command.getTitle() != null)
            book.setTitle(command.getTitle());
        if (command.getAuthors() != null && command.getAuthors().size() > 0)
            updateBooks(book, fetchAuthorsByIds(command.getAuthors()));
        if (command.getYear() != null)
            book.setYear(command.getYear());
        if (command.getPrice() != null)
            book.setPrice(command.getPrice());
        return book;
    }

    @Override
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public void updateBookCover(UpdateBookCoverCommand command) {
        bookRepository.findById(command.getId())
                .ifPresent(book -> {
                    Upload savedUpload = upload.save(new SaveUploadCommand(command.getFilename(), command.getFile(), command.getContentType()));
                    book.setCoverId(savedUpload.getId());
                    bookRepository.save(book);
                });
    }

    @Override
    public void removeBookCover(Long id) {
        bookRepository.findById(id)
                .ifPresent(book -> {
                    if (book.getCoverId() != null) {
                        upload.removeById(book.getCoverId());
                        book.setCoverId(null);
                        bookRepository.save(book);
                    }
                });


    }

    @Override
    public void removeById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Book not found with ID:" + id));
        book.removeAuthors();

        bookRepository.deleteById(id);
    }

}
