package com.juaracoding.pcmspringboot31.core;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 301-450
 * @param <T>
 */
public interface IFile<T> {
    public ResponseEntity<Object> upload(MultipartFile file, HttpServletRequest request) throws IOException;//301-310
    public void downloadExcel(T param,HttpServletRequest request, HttpServletResponse response);//311-320
    public void downloadPdf(T param,HttpServletRequest request, HttpServletResponse response);//321-330
}