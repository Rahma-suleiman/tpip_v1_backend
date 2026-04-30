package com.znz.tpip_backend.controller;

import com.znz.tpip_backend.dto.DocumentDto;
import com.znz.tpip_backend.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public DocumentDto create(@RequestBody DocumentDto dto) {
        return documentService.create(dto);
    }

    @GetMapping("/application/{applicationId}")
    public List<DocumentDto> getByApplication(@PathVariable Long applicationId) {
        return documentService.getByApplication(applicationId);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        documentService.delete(id);
        return "Deleted successfully";
    }
}