package ru.practicum.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.dto.CompilationDto;
import ru.practicum.service.CompilationService;

import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/compilations")
public class CompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                                @RequestParam(defaultValue = "0") Integer from,
                                                                @RequestParam(defaultValue = "10") Integer size) {
        try {
            List<CompilationDto> compilations = compilationService.getAll(pinned, from, size);
            return ResponseEntity.ok(compilations);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrectly made request.", ex);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompilationDto> getCompilation(@PathVariable Long id) {
        try {
            CompilationDto compilation = compilationService.get(id);
            return ResponseEntity.ok(compilation);
        } catch (ChangeSetPersister.NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Compilation with id=" + id + " was not found", ex);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrectly made request.", ex);
        }
    }
}
