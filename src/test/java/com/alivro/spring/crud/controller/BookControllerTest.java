package com.alivro.spring.crud.controller;

import com.alivro.spring.crud.exception.DataAlreadyExistsException;
import com.alivro.spring.crud.exception.DataNotFoundException;
import com.alivro.spring.crud.model.book.request.AuthorOfBookRequestDto;
import com.alivro.spring.crud.model.book.request.BookSaveRequestDto;
import com.alivro.spring.crud.model.book.response.AuthorOfBookResponseDto;
import com.alivro.spring.crud.model.book.response.BookResponseDto;
import com.alivro.spring.crud.service.IBookService;
import com.alivro.spring.crud.util.CustomData;
import com.alivro.spring.crud.util.CustomPageMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
public class BookControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private IBookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private static BookSaveRequestDto bookSaveRequestAustereAcademy;
    private static BookSaveRequestDto bookUpdateRequestAustereAcademy;
    private static BookResponseDto bookResponseBadBeginning;
    private static BookResponseDto bookResponseReptileRoom;
    private static BookResponseDto bookResponseWideWindow;
    private static BookResponseDto bookResponseMiserableMill;
    private static BookResponseDto bookSavedResponseAustereAcademy;
    private static BookResponseDto bookUpdatedResponseAustereAcademy;

    @BeforeAll
    public static void setup() {
        AuthorOfBookResponseDto booksAuthorResponseSnicket = AuthorOfBookResponseDto.builder()
                .id(1L)
                .pseudonym("Lemony Snicket")
                .build();

        bookResponseBadBeginning = BookResponseDto.builder()
                .id(1L)
                .title("A Series of Unfortunate Events")
                .subtitle("The Bad Beginning")
                .authors(Collections.singletonList(booksAuthorResponseSnicket))
                .totalPages(176)
                .publisher("HarperCollins")
                .publishedDate(LocalDate.parse("1999-08-25"))
                .isbn13("9780064407663")
                .build();

        bookResponseReptileRoom = BookResponseDto.builder()
                .id(2L)
                .title("A Series of Unfortunate Events")
                .subtitle("The Reptile Room")
                .authors(Collections.singletonList(booksAuthorResponseSnicket))
                .totalPages(208)
                .publisher("HarperCollins")
                .publishedDate(LocalDate.parse("1999-08-25"))
                .isbn13("9780064407670")
                .build();

        bookResponseWideWindow = BookResponseDto.builder()
                .id(3L)
                .title("A Series of Unfortunate Events")
                .subtitle("The Wide Window")
                .authors(Collections.singletonList(booksAuthorResponseSnicket))
                .totalPages(224)
                .publisher("HarperCollins")
                .publishedDate(LocalDate.parse("2000-02-02"))
                .isbn13("9780064407687")
                .build();

        bookResponseMiserableMill = BookResponseDto.builder()
                .id(4L)
                .title("A Series of Unfortunate Events")
                .subtitle("The Miserable Mill")
                .authors(Collections.singletonList(booksAuthorResponseSnicket))
                .totalPages(208)
                .publisher("HarperCollins")
                .publishedDate(LocalDate.parse("2000-04-05"))
                .isbn13("9780064407694")
                .build();

        AuthorOfBookRequestDto booksAuthorRequestSnicket = AuthorOfBookRequestDto.builder()
                .id(1L)
                .pseudonym("Lemony Snicket")
                .build();

        bookSaveRequestAustereAcademy = BookSaveRequestDto.builder()
                .title("A Series of Fortunate Events")
                .subtitle("The Ostentatious Academy")
                .authors(Collections.singletonList(booksAuthorRequestSnicket))
                .totalPages(231)
                .publisher("HarperCollins")
                .publishedDate(LocalDate.parse("2009-10-13"))
                .isbn13("9780064408639")
                .build();

        bookSavedResponseAustereAcademy = mapRequestDtoToResponseDto(5L, bookSaveRequestAustereAcademy);

        bookUpdateRequestAustereAcademy = BookSaveRequestDto.builder()
                .title("A Series of Unfortunate Events")
                .subtitle("The Austere Academy")
                .authors(Collections.singletonList(booksAuthorRequestSnicket))
                .totalPages(231)
                .publisher("HarperCollins")
                .publishedDate(LocalDate.parse("2009-10-13"))
                .isbn13("9780064408639")
                .build();

        bookUpdatedResponseAustereAcademy = mapRequestDtoToResponseDto(5L, bookUpdateRequestAustereAcademy);
    }

    @Test
    public void findAllBySubtitleAsc_Books_ExistingBooks_Return_Ok() throws Exception {
        //Given
        int pageNumber = 0;
        int pageSize = 5;
        String sortBy = "subtitle";
        Pageable pageable = PageRequest.ofSize(pageSize)
                .withPage(pageNumber)
                .withSort(Sort.by(sortBy).ascending());

        List<BookResponseDto> foundBooks = new ArrayList<>();
        foundBooks.add(bookResponseBadBeginning);
        foundBooks.add(bookResponseMiserableMill);
        foundBooks.add(bookResponseReptileRoom);
        foundBooks.add(bookResponseWideWindow);

        CustomPageMetadata metadata = CustomPageMetadata.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .numberOfElements(foundBooks.size())
                .totalPages((int) Math.ceil((double) foundBooks.size() / pageSize))
                .totalElements(foundBooks.size())
                .build();

        given(bookService.findAll(pageable)).willReturn(
                CustomData.<BookResponseDto, CustomPageMetadata>builder()
                        .data(foundBooks)
                        .metadata(metadata)
                        .build()
        );

        // When
        ResultActions response = mockMvc.perform(get("/api/v1/book/findAll")
                .param("page", "0")
                .param("size", "5")
                .param("sort", "subtitle,asc")
        );

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        CoreMatchers.is("Found books!")));

        response.andExpect(MockMvcResultMatchers.jsonPath("$.data", hasSize(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].subtitle",
                        CoreMatchers.is(bookResponseBadBeginning.getSubtitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].subtitle",
                        CoreMatchers.is(bookResponseMiserableMill.getSubtitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].subtitle",
                        CoreMatchers.is(bookResponseReptileRoom.getSubtitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[3].subtitle",
                        CoreMatchers.is(bookResponseWideWindow.getSubtitle())));

        response.andExpect(MockMvcResultMatchers.jsonPath("$.metadata.pageNumber",
                        CoreMatchers.is(pageNumber)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.metadata.pageSize",
                        CoreMatchers.is(pageSize)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.metadata.numberOfElements",
                        CoreMatchers.is(foundBooks.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.metadata.totalPages",
                        CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.metadata.totalElements",
                        CoreMatchers.is(foundBooks.size())));
    }

    @Test
    public void findAllBySubtitleDesc_Books_ExistingBooks_Return_Ok() throws Exception {
        //Given
        int pageNumber = 0;
        int pageSize = 5;
        String sortBy = "subtitle";
        Pageable pageable = PageRequest.ofSize(pageSize)
                .withPage(pageNumber)
                .withSort(Sort.by(sortBy).descending());

        List<BookResponseDto> foundBooks = new ArrayList<>();
        foundBooks.add(bookResponseWideWindow);
        foundBooks.add(bookResponseReptileRoom);
        foundBooks.add(bookResponseMiserableMill);
        foundBooks.add(bookResponseBadBeginning);

        CustomPageMetadata metadata = CustomPageMetadata.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .numberOfElements(foundBooks.size())
                .totalPages((int) Math.ceil((double) foundBooks.size() / pageSize))
                .totalElements(foundBooks.size())
                .build();

        given(bookService.findAll(pageable)).willReturn(
                CustomData.<BookResponseDto, CustomPageMetadata>builder()
                        .data(foundBooks)
                        .metadata(metadata)
                        .build()
        );

        // When
        ResultActions response = mockMvc.perform(get("/api/v1/book/findAll")
                .param("page", "0")
                .param("size", "5")
                .param("sort", "subtitle,desc")
        );

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        CoreMatchers.is("Found books!")));

        response.andExpect(MockMvcResultMatchers.jsonPath("$.data", hasSize(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].subtitle",
                        CoreMatchers.is(bookResponseWideWindow.getSubtitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].subtitle",
                        CoreMatchers.is(bookResponseReptileRoom.getSubtitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].subtitle",
                        CoreMatchers.is(bookResponseMiserableMill.getSubtitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[3].subtitle",
                        CoreMatchers.is(bookResponseBadBeginning.getSubtitle())));

        response.andExpect(MockMvcResultMatchers.jsonPath("$.metadata.pageNumber",
                        CoreMatchers.is(pageNumber)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.metadata.pageSize",
                        CoreMatchers.is(pageSize)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.metadata.numberOfElements",
                        CoreMatchers.is(foundBooks.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.metadata.totalPages",
                        CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.metadata.totalElements",
                        CoreMatchers.is(foundBooks.size())));
    }

    @Test
    public void findAll_Books_NonExistingBooks_Return_Ok() throws Exception {
        //Given
        int pageNumber = 0;
        int pageSize = 5;
        String sortBy = "id";
        Pageable pageable = PageRequest.ofSize(pageSize)
                .withPage(pageNumber)
                .withSort(Sort.by(sortBy).ascending());

        List<BookResponseDto> foundBooks = new ArrayList<>();

        CustomPageMetadata metadata = CustomPageMetadata.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .numberOfElements(foundBooks.size())
                .totalPages((int) Math.ceil((double) foundBooks.size() / pageSize))
                .totalElements(foundBooks.size())
                .build();

        given(bookService.findAll(pageable)).willReturn(
                CustomData.<BookResponseDto, CustomPageMetadata>builder()
                        .data(foundBooks)
                        .metadata(metadata)
                        .build()
        );

        // When
        ResultActions response = mockMvc.perform(get("/api/v1/book/findAll"));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        CoreMatchers.is("Found books!")));

        response.andExpect(MockMvcResultMatchers.jsonPath("$.data", hasSize(0)));

        response.andExpect(MockMvcResultMatchers.jsonPath("$.metadata.pageNumber",
                        CoreMatchers.is(pageNumber)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.metadata.pageSize",
                        CoreMatchers.is(pageSize)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.metadata.numberOfElements",
                        CoreMatchers.is(foundBooks.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.metadata.totalPages",
                        CoreMatchers.is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.metadata.totalElements",
                        CoreMatchers.is(foundBooks.size())));
    }

    @Test
    public void findById_Book_ExistingBook_Return_Ok() throws Exception {
        //Given
        long bookId = 1L;

        given(bookService.findById(bookId)).willReturn(bookResponseBadBeginning);

        // When
        ResultActions response = mockMvc.perform(get("/api/v1/book/find/{id}", bookId));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        CoreMatchers.is("Found book!")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].title",
                        CoreMatchers.is(bookResponseBadBeginning.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].subtitle",
                        CoreMatchers.is(bookResponseBadBeginning.getSubtitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].totalPages",
                        CoreMatchers.is(bookResponseBadBeginning.getTotalPages())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].isbn13",
                        CoreMatchers.is(bookResponseBadBeginning.getIsbn13())));
    }

    @Test
    public void findById_Book_NonExistingBook_Return_NotFound() throws Exception {
        //Given
        long bookId = 10L;

        given(bookService.findById(anyLong())).
                willThrow(new DataNotFoundException("Book not found!"));

        // When
        ResultActions response = mockMvc.perform(get("/api/v1/book/find/{id}", bookId));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error",
                        CoreMatchers.is("Book not found!")));
    }

    @Test
    public void findById_Book_StringId_Return_InternalServerError() throws Exception {
        //Given
        String bookId = "one";

        // When
        ResultActions response = mockMvc.perform(get("/api/v1/book/find/{id}", bookId));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void save_Book_NonExistingBook_Return_Created() throws Exception {
        // Given
        given(bookService.save(bookSaveRequestAustereAcademy))
                .willReturn(bookSavedResponseAustereAcademy);

        // When
        ResultActions response = mockMvc.perform(post("/api/v1/book/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookSaveRequestAustereAcademy)));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        CoreMatchers.is("Saved book!")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].title",
                        CoreMatchers.is(bookSavedResponseAustereAcademy.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].subtitle",
                        CoreMatchers.is(bookSavedResponseAustereAcademy.getSubtitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].totalPages",
                        CoreMatchers.is(bookSavedResponseAustereAcademy.getTotalPages())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].isbn13",
                        CoreMatchers.is(bookSavedResponseAustereAcademy.getIsbn13())));
    }

    @Test
    public void save_Book_ExistingBook_Return_Conflict() throws Exception {
        // Given
        given(bookService.save(any(BookSaveRequestDto.class))).
                willThrow(new DataAlreadyExistsException("Book already exists!"));

        // When
        ResultActions response = mockMvc.perform(post("/api/v1/book/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookSaveRequestAustereAcademy)));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error",
                        CoreMatchers.is("Book already exists!")));
    }

    @Test
    public void update_Book_ExistingBook_Return_Ok() throws Exception {
        // Given
        long bookId = 5L;

        given(bookService.update(bookId, bookUpdateRequestAustereAcademy))
                .willReturn(bookUpdatedResponseAustereAcademy);

        // When
        ResultActions response = mockMvc.perform(put("/api/v1/book/update/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookUpdateRequestAustereAcademy)));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        CoreMatchers.is("Updated book!")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].title",
                        CoreMatchers.is(bookUpdatedResponseAustereAcademy.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].subtitle",
                        CoreMatchers.is(bookUpdatedResponseAustereAcademy.getSubtitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].totalPages",
                        CoreMatchers.is(bookUpdatedResponseAustereAcademy.getTotalPages())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].isbn13",
                        CoreMatchers.is(bookUpdatedResponseAustereAcademy.getIsbn13())));
    }

    @Test
    public void update_Book_NonExistingBook_Return_NotFound() throws Exception {
        // Given
        long bookId = 10L;

        given(bookService.update(anyLong(), any(BookSaveRequestDto.class)))
                .willThrow(new DataNotFoundException("Book does not exist!"));
        // When
        ResultActions response = mockMvc.perform(put("/api/v1/book/update/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookUpdateRequestAustereAcademy)));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error",
                        CoreMatchers.is("Book does not exist!")));
    }

    @Test
    public void deleteById_Book_Return_Ok() throws Exception {
        // Given
        long bookId = 1L;

        willDoNothing().given(bookService).deleteById(anyLong());

        // When
        ResultActions response = mockMvc.perform(delete("/api/v1/book/delete/{id}", bookId));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    private static BookResponseDto mapRequestDtoToResponseDto(long id, BookSaveRequestDto request) {
        List<AuthorOfBookResponseDto> authorsOfBook = request.getAuthors().stream()
                .map(a -> AuthorOfBookResponseDto.builder()
                        .id(a.getId())
                        .pseudonym(a.getPseudonym())
                        .build())
                .collect(Collectors.toList());

        return BookResponseDto.builder()
                .id(id)
                .title(request.getTitle())
                .authors(authorsOfBook)
                .totalPages(request.getTotalPages())
                .publisher(request.getPublisher())
                .publishedDate(request.getPublishedDate())
                .isbn13(request.getIsbn13())
                .build();
    }
}
