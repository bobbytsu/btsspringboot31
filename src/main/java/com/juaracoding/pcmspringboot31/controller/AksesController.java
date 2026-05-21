package com.juaracoding.pcmspringboot31.controller;

import com.juaracoding.pcmspringboot31.config.OtherConfig;
import com.juaracoding.pcmspringboot31.dto.query.SearchAksesDTO;
import com.juaracoding.pcmspringboot31.dto.validation.ValAksesDTO;
import com.juaracoding.pcmspringboot31.service.AksesService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/akses")
public class AksesController {

    @Autowired
    private AksesService aksesService;

    @PostMapping("/v1")
    @PreAuthorize("hasAuthority('002i')")
    public ResponseEntity<Object> save(@Valid @RequestBody ValAksesDTO valAksesDTO, HttpServletRequest request) {
        return aksesService.save(aksesService.mapToEntity(valAksesDTO),request);
    }

    @PutMapping("/v1/{id}")
    @PreAuthorize("hasAuthority('002u')")
    public ResponseEntity<Object> update(
            @PathVariable Long id,
            @Valid @RequestBody ValAksesDTO valAksesDTO, HttpServletRequest request) {
        return aksesService.update(id,aksesService.mapToEntity(valAksesDTO),request);
    }

    @DeleteMapping("/v1/{id}")
    @PreAuthorize("hasAuthority('002d')")
    public ResponseEntity<Object> delete(
            @PathVariable Long id,
            HttpServletRequest request) {
        return aksesService.deleteById(id,request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('002v')")
    public ResponseEntity<Object> findAll(
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(0, OtherConfig.getDefaultPaginationSize(), Sort.by("id"));

        return aksesService.findAll(pageable,request);
    }


    @PostMapping("/v1/{sort}/{sort_by}/{page}")
    @PreAuthorize("hasAuthority('002v')")
    public ResponseEntity<Object> search(
            @PathVariable String sort,
            @PathVariable(value = "sort_by") String sortBy,
            @PathVariable Integer page,
            @RequestParam Integer size,
            @RequestBody SearchAksesDTO searchAksesDTO, HttpServletRequest request) {
        Pageable pageable = null;
        sortBy = checkSortBy(sortBy);

        switch(sort){
            case "asc"://alamat
                pageable = PageRequest.of(page, size, Sort.by(sortBy));break;
            default:
                pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());break;
        }
        return aksesService.findByParam(pageable,searchAksesDTO,request);
    }

    private String checkSortBy(String sortBy){
        switch (sortBy){
            case "nama": sortBy="nama";break;
            case "deskripsi": sortBy="deskripsi";break;
            default: sortBy="id";break;
        }
        return sortBy;
    }

}
