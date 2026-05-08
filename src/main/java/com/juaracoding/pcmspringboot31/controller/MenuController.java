package com.juaracoding.pcmspringboot31.controller;

import com.juaracoding.pcmspringboot31.config.OtherConfig;
import com.juaracoding.pcmspringboot31.dto.query.SearchMenuDTO;
import com.juaracoding.pcmspringboot31.dto.validation.ValAksesDTO;
import com.juaracoding.pcmspringboot31.dto.validation.ValMenuDTO;
import com.juaracoding.pcmspringboot31.service.MenuService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @PostMapping("/v1")
    public ResponseEntity<Object> save(@Valid @RequestBody ValMenuDTO valMenuDTO, HttpServletRequest request) {
        return menuService.save(menuService.mapperToEntity(valMenuDTO),request);
    }

    @PutMapping("/v1/{id}")
    public ResponseEntity<Object> update(
            @PathVariable Long id,
            @Valid @RequestBody ValMenuDTO valMenuDTO, HttpServletRequest request) {
        return menuService.update(id,menuService.mapperToEntity(valMenuDTO),request);
    }

    @DeleteMapping("/v1/{id}")
    public ResponseEntity<Object> delete(
            @PathVariable Long id,
            HttpServletRequest request) {
        return menuService.deleteById(id,request);
    }

    @GetMapping
    public ResponseEntity<Object> delete(
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(0, OtherConfig.getDefaultPaginationSize(), Sort.by("id"));

        return menuService.findAll(pageable,request);
    }


    @PostMapping("/v1/{sort}/{sort_by}/{page}")
    public ResponseEntity<Object> search(
            @PathVariable String sort,
            @PathVariable(value = "sort_by") String sortBy,
            @PathVariable Integer page,
            @RequestParam Integer size,
            @RequestBody SearchMenuDTO searchMenuDTO, HttpServletRequest request) {
        Pageable pageable = null;
        sortBy = checkSortBy(sortBy);

        switch(sort){
            case "asc"://alamat
                pageable = PageRequest.of(page, size, Sort.by(sortBy));break;
            default:
                pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());break;
        }
        return menuService.findByParam(pageable,searchMenuDTO,request);
    }

    @PostMapping("/v1/upload")
    public ResponseEntity<Object> uploadExcel(
            @RequestParam MultipartFile file,
            HttpServletRequest request) throws IOException {

        return menuService.upload(file,request);
    }

    @PostMapping("/v1/excel")
    public void downloadExcel(
            @RequestBody SearchMenuDTO searchMenuDTO,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        menuService.downloadExcel(searchMenuDTO,request,response);
    }

    @PostMapping("/v1/pdf")
    public void downloadPdf(
            @RequestBody SearchMenuDTO searchMenuDTO,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        menuService.downloadPdf(searchMenuDTO,request,response);
    }

    private String checkSortBy(String sortBy){
        switch (sortBy){
            case "nama": sortBy="nama";break;
//            case "path": sortBy="path";break;
//            case "deskripsi": sortBy="deskripsi";break;
            default: sortBy="id";break;
        }
        return sortBy;
    }

}
