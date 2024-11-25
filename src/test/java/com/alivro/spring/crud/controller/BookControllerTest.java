package com.alivro.spring.crud.controller;

import com.alivro.spring.crud.model.request.BookRequestDTO;
import com.alivro.spring.crud.model.response.BookResponseDTO;
import com.alivro.spring.crud.services.IBookService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(BookControllerTest.class)
@ExtendWith(MockitoExtension.class)
public class BookControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private IBookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    static BookRequestDTO bookRequestTwenty;
    static BookRequestDTO bookRequestUpdateTwenty;
    static BookResponseDTO bookResponseJourney;
    static BookResponseDTO bookResponseTwenty;
    static BookResponseDTO bookResponseUpdateTwenty;

    @BeforeAll
    public static void setup() {
        bookResponseJourney = BookResponseDTO.builder()
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

        bookResponseTwenty = requestToResponse(2L, bookRequestTwenty);

        bookRequestUpdateTwenty = BookRequestDTO.builder()
                .title("Twenty Thousand Leagues Under the Sea")
                .author("Jules Verne")
                .totalPages(336)
                .publisher("Puffin Classics")
                .publishedDate(LocalDate.parse("2018-03-01"))
                .isbn("9780141377568")
                .build();

        bookResponseUpdateTwenty = requestToResponse(2L, bookRequestUpdateTwenty);
    }

    @Test
    public void findExistingBookReturnOK() throws Exception {
        //Given
        long bookID = 1L;

        given(bookService.findById(bookID)).willReturn(bookResponseJourney);

        // When
        ResultActions response = mockMvc.perform(get("/api/book/find/{id}", bookID));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(bookResponseJourney.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages", CoreMatchers.is(bookResponseJourney.getTotalPages())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isbn", CoreMatchers.is(bookResponseJourney.getIsbn())));
    }

    @Test
    public void findNonExistingBookReturnNotFound() throws Exception {
        //Given
        long bookID = 1L;

        given(bookService.findById(anyLong())).willReturn(null);

        // When
        ResultActions response = mockMvc.perform(get("/api/book/find/{id}", bookID));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void saveNonExistingBookReturnIsCreated() throws Exception {
        // Given
        given(bookService.save(bookRequestTwenty)).willReturn(bookResponseTwenty);

        // When
        ResultActions response = mockMvc.perform(post("/api/book/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequestTwenty)));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(bookResponseTwenty.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages", CoreMatchers.is(bookResponseTwenty.getTotalPages())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isbn", CoreMatchers.is(bookResponseTwenty.getIsbn())));
    }

    @Test
    public void saveExistingBookReturnIsConflict() throws Exception {
        // Given
        given(bookService.save(any(BookRequestDTO.class))).willReturn(null);

        // When
        ResultActions response = mockMvc.perform(post("/api/book/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequestTwenty)));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void updateExistingBookReturnIsOk() throws Exception {
        // Given
        long bookId = 2L;

        given(bookService.updateById(bookId, bookRequestUpdateTwenty)).willReturn(bookResponseUpdateTwenty);

        // When
        ResultActions response = mockMvc.perform(put("/api/book/update/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequestUpdateTwenty)));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(bookResponseUpdateTwenty.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages", CoreMatchers.is(bookResponseUpdateTwenty.getTotalPages())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isbn", CoreMatchers.is(bookResponseUpdateTwenty.getIsbn())));
    }

    @Test
    public void updateNonExistingBookReturnIsNotFound() throws Exception {
        // Given
        long bookId = 2L;

        given(bookService.updateById(anyLong(), any(BookRequestDTO.class))).willReturn(null);

        // When
        ResultActions response = mockMvc.perform(put("/api/book/update/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequestUpdateTwenty)));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteBookReturnIsOk() throws Exception {
        // Given
        long bookId = 1L;

        willDoNothing().given(bookService).deleteById(anyLong());

        // When
        ResultActions response = mockMvc.perform(delete("/api/book/delete/{id}", bookId));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    private static BookResponseDTO requestToResponse(long id, BookRequestDTO request) {
        return BookResponseDTO.builder()
                .id(id)
                .title(request.getTitle())
                .author(request.getAuthor())
                .totalPages(request.getTotalPages())
                .publisher(request.getPublisher())
                .publishedDate(request.getPublishedDate())
                .isbn(request.getIsbn())
                .build();
    }
}
