package com.alivro.spring.crud.controller;

import com.alivro.spring.crud.model.response.BookResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Optional;

import com.alivro.spring.crud.services.IBookService;
import com.alivro.spring.crud.model.request.BookRequestDTO;
import com.alivro.spring.crud.model.Book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/book")
@CrossOrigin(origins = "http://localhost:8080")
public class BookController {
    private final IBookService bookService;
    private final Logger logger = LoggerFactory.getLogger(BookController.class);

    /**
     * Constructor
     *
     * @param bookService
     */
    @Autowired
    public BookController(IBookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Endpoint para buscar un libro por su ID
     *
     * @param id Identificador único del libro
     * @return Información del libro buscado
     */
    @GetMapping("/find/{id}")
    public ResponseEntity<BookResponseDTO> findBook(@PathVariable("id") long id) {
        BookResponseDTO foundBook = bookService.findById(id);

        if (foundBook == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(foundBook, HttpStatus.OK);
    }

    /**
     * Endpoint para guardar un nuevo libro
     *
     * @param book Datos del libro
     * @return Información del libro guardado
     */
    @PostMapping("/save")
    public ResponseEntity<BookResponseDTO> saveBook(@Valid @RequestBody BookRequestDTO book) {
        BookResponseDTO savedBook = bookService.save(book);

        if (savedBook == null)
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    /**
     * Endpoint para actualizar la información de un libro
     *
     * @param id   Identificador único del libro
     * @param book Datos del libro
     * @return Información del libro actualizado
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable("id") long id, @Valid @RequestBody BookRequestDTO book) {
        BookResponseDTO updatedBook = bookService.updateById(id, book);

        if (updatedBook == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    /**
     * Endpoint para eliminar un libro por su ID
     *
     * @param id El identificador único del libro
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Optional<Book>> deleteBook(@PathVariable("id") long id) {
        bookService.deleteById(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
