package com.juaracoding.pcmspringboot31.core;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;

/**
 * 151-300
 * @param <T>
 */
public interface IServiceQuery<T,V> {
    public ResponseEntity<Object> findAll(Pageable pageable, HttpServletRequest request);//151-160
    public ResponseEntity<Object> findByParam(Pageable pageable, T param,HttpServletRequest request);//161-170
    public Specification<V> getSpecification(T param);//171-180
    public ResponseEntity<Object> findById(Long id,HttpServletRequest request);//181-190
}