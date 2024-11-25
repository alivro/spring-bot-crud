package com.alivro.spring.crud.services.impl;

import com.alivro.spring.crud.model.Book;
import com.alivro.spring.crud.model.request.BookRequestDTO;
import com.alivro.spring.crud.model.response.BookResponseDTO;
import com.alivro.spring.crud.repository.BookRepository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class IBookServiceImplTest {
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private IBookServiceImpl bookService;

    private static BookRequestDTO bookRequestTwenty;
    private static BookRequestDTO bookRequestUpdateTwenty;
    private static Book bookJourney;
    private static Book bookTwenty;
    private static Book bookUpdateTwenty;

    @BeforeAll
    public static void setup() {
        bookJourney = Book.builder()
                .id(1L)
                .title("Journey to the Center of the Earth")
                .author("Jules Verne")
                .totalPages(352)
                .publisher("Simon & Schuster")
                .publishedDate(LocalDate.parse("2008-05-06"))
                .isbn("9781416561460")
                .build();

        bookRequestTwenty = BookRequestDTO.builder()
                .title("Twenty Thousand Leages Under the Sea")
                .author("Jules Verne")
                .totalPages(363)
                .publisher("Puffin Classics")
                .publishedDate(LocalDate.parse("2018-03-01"))
                .isbn("9780141377586")
                .build();

        bookTwenty = BookRequestDTO.requestDTOtoEntity(bookRequestTwenty);

        bookRequestUpdateTwenty = BookRequestDTO.builder()
                .title("Twenty Thousand Leagues Under the Sea")
                .author("Jules Verne")
                .totalPages(336)
                .publisher("Puffin Classics")
                .publishedDate(LocalDate.parse("2018-03-01"))
                .isbn("9780141377568")
                .build();

        bookUpdateTwenty = BookRequestDTO.requestDTOtoEntity(2L, bookRequestUpdateTwenty);
    }

    @Test
    public void findExistingBookReturnBookResponseDTO() {
        // Given
        long bookID = 1L;
        given(bookRepository.findById(bookID)).willReturn(Optional.of(bookJourney));

        // When
        BookResponseDTO foundBook = bookService.findById(bookID);

        // Then
        assertThat(foundBook).isNotNull();
        assertThat(foundBook.getTitle()).isEqualTo("Journey to the Center of the Earth");
        assertThat(foundBook.getTotalPages()).isEqualTo(352);
        assertThat(foundBook.getIsbn()).isEqualTo("9781416561460");
    }

    @Test
    public void findNoExistingBookReturnNull() {
        // Given
        long bookID = 1L;
        given(bookRepository.findById(anyLong())).willReturn(Optional.empty());

        // When
        BookResponseDTO foundBook = bookService.findById(bookID);

        // Then
        assertThat(foundBook).isNull();
    }

    @Test
    public void saveNonExistingBookReturnBookResponseDTO() {
        // Given
        given(bookRepository.existsByIsbn(bookRequestTwenty.getIsbn())).willReturn(false);
        given(bookRepository.save(bookTwenty)).willReturn(bookTwenty);

        // When
        BookResponseDTO savedBook = bookService.save(bookRequestTwenty);

        // Then
        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Twenty Thousand Leages Under the Sea");
        assertThat(savedBook.getTotalPages()).isEqualTo(363);
        assertThat(savedBook.getIsbn()).isEqualTo("9780141377586");
    }

    @Test
    public void saveExistingBookReturnNull() {
        // Given
        given(bookRepository.existsByIsbn(anyString())).willReturn(true);

        // When
        BookResponseDTO savedBook = bookService.save(bookRequestTwenty);

        // Then
        assertThat(savedBook).isNull();
    }

    @Test
    public void updateExistingBookReturnBookResponseDTO() {
        // Given
        long bookID = 2L;
        given(bookRepository.existsById(bookID)).willReturn(true);
        given(bookRepository.save(bookUpdateTwenty)).willReturn(bookUpdateTwenty);

        // When
        BookResponseDTO updatedBook = bookService.updateById(bookID, bookRequestUpdateTwenty);

        // Then
        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getTitle()).isEqualTo("Twenty Thousand Leagues Under the Sea");
        assertThat(updatedBook.getTotalPages()).isEqualTo(336);
        assertThat(updatedBook.getIsbn()).isEqualTo("9780141377568");
    }

    @Test
    public void updateNonExistingBookReturnNull() {
        // Given
        long bookID = 2L;
        given(bookRepository.existsById(anyLong())).willReturn(false);

        // When
        BookResponseDTO updatedBook = bookService.updateById(bookID, bookRequestUpdateTwenty);

        // Then
        assertThat(updatedBook).isNull();
    }

    @Test
    public void deleteBookNoReturn() {
        // Given
        long bookID = 1L;
        willDoNothing().given(bookRepository).deleteById(anyLong());

        // When
        bookService.deleteById(bookID);

        // Then
        verify(bookRepository, times(1)).deleteById(bookID);
    }
}
