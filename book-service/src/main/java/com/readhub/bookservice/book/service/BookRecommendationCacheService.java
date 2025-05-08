package com.readhub.bookservice.book.service;

import com.readhub.bookservice.book.dto.BookRecommendDto;
import com.readhub.bookservice.book.entity.Book;
import com.readhub.bookservice.book.mapper.BookMapper;
import com.readhub.bookservice.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BookRecommendationCacheService {

    private final BookService bookService;
    private final BookMapper bookMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String RECOMMEND_PREFIX = "RECOMMEND:";

    public Page<BookRecommendDto> getCachedRecommendations(Long userId, Pageable pageable) {
        String redisKey = RECOMMEND_PREFIX + userId;
        List<BookRecommendDto> cached = (List<BookRecommendDto>) redisTemplate.opsForValue().get(redisKey);

        if (cached != null) {
            return new PageImpl<>(cached, pageable, cached.size());
        }

        Page<Book> books = bookService.findRecommendedBooks(userId, pageable);
        List<BookRecommendDto> dtos = bookMapper.booksToRecommendationDtos(books.getContent());
        redisTemplate.opsForValue().set(redisKey, dtos, Duration.ofHours(3));

        return new PageImpl<>(dtos, pageable, books.getTotalElements());
    }
}
