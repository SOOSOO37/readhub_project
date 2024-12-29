package com.readhub.backend.book;

import com.readhub.backend.global.response.MultiResponseDto;
import com.readhub.backend.global.utils.UriCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/books")
@RestController
public class BookController {

    private final static String BOOK_DEFAULT_URL = "/books";

    private final BookService bookService;

    private final BookMapper mapper;


    @PostMapping
    public ResponseEntity createBook(@RequestBody BookCreateDto bookCreateDto) {
        Book book = bookService.createBook(mapper.bookCreateDtoToBook(bookCreateDto));

        URI location = UriCreator.createUri(BOOK_DEFAULT_URL, book.getId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity updateBook(@PathVariable("id")long id,
                                     @RequestBody BookUpdateDto bookUpdateDto){
        bookUpdateDto.setId(id);
        Book updatedBook = bookService.updateBook(mapper.bookUpdateDtoToBook(bookUpdateDto));

        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getNewBooks(@RequestParam int page,
                                       @RequestParam int size){
        Page<Book> books = bookService.getNewBooks(page -1, size);
        List<Book> bookList = books.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.booksToBookResponseDtos(bookList),books),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity getBook(@PathVariable("id")long id){
        Book book = bookService.findBooK(id);
        return new ResponseEntity<>(mapper.bookToBookDetailResponseDto(book),HttpStatus.OK);
    }
}
