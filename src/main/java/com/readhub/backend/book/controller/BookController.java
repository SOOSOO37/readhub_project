package com.readhub.backend.book.controller;

import com.readhub.backend.book.dto.BookCreateDto;
import com.readhub.backend.book.dto.BookStatusUpdateDto;
import com.readhub.backend.book.mapper.BookMapper;
import com.readhub.backend.book.service.BookService;
import com.readhub.backend.book.dto.BookUpdateDto;
import com.readhub.backend.book.entity.Book;
import com.readhub.backend.global.response.MultiResponseDto;
import com.readhub.backend.global.utils.UriCreator;
import com.readhub.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/newest")
    public ResponseEntity getNewBooks(@RequestParam int page,
                                       @RequestParam int size){
        Page<Book> books = bookService.getNewBooks(page -1, size);
        List<Book> bookList = books.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.booksToBookResponseDtos(bookList),books),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity getBook(@PathVariable("id")long id){
        Book book = bookService.findBook(id);
        return new ResponseEntity<>(mapper.bookToBookDetailResponseDto(book),HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity searchBooks( @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(defaultValue = "1") int sorting,
                                              @RequestParam(required = false, defaultValue = "") String category,
                                              @RequestParam(required = false, defaultValue = "") String keyword){

        Page<Book> bookPage = bookService.searchBooks(page-1,size,sorting,category,keyword);
        List<Book> books = bookPage.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(mapper.booksToBookResponseDtos(books),bookPage),
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteProduct(@PathVariable("id")long id){

        bookService.deleteBook(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PatchMapping("/{id}/status")
    public ResponseEntity updateStatus(@PathVariable("id")long id,
                                       @RequestBody BookStatusUpdateDto bookStatusUpdateDto){

        bookStatusUpdateDto.setId(id);
        Book updateBook = bookService.updateBookStatus(id,mapper.bookStatusPatchDtoToBook(bookStatusUpdateDto));

        return new ResponseEntity<>(mapper.bookToBookDetailResponseDto(updateBook), HttpStatus.OK);

    }

    @GetMapping("/ranking")
    public ResponseEntity findRanking(@RequestParam int page,
                                      @RequestParam int size){

        Page<Book> books = bookService.findBookRanks(page -1, size);
        List<Book> bookList = books.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.booksToBookResponseDtos(bookList),books),HttpStatus.OK);
    }

    @GetMapping("/recommend")
    public ResponseEntity findRecommendedBooks(@RequestParam int page,
                                               @RequestParam int size,
                                               @AuthenticationPrincipal User user) {
        Page<Book> books = bookService.findRecommendedBooks(user, page -1, size);
        List<Book> bookList = books.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.booksToRecommendationDtos(bookList), books), HttpStatus.OK);
    }

    @GetMapping("/redis")
    public ResponseEntity getRecommendedBooks(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @AuthenticationPrincipal User user){

        Page<Book> recommendedBooks = bookService.findRedisRecommendedBooks(user, page, size);
        List<Book> response = recommendedBooks.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.booksToRecommendationDtos(response), recommendedBooks), HttpStatus.OK);
    }
}
