package com.juaracoding.pcmspringboot31.specification;

import com.juaracoding.pcmspringboot31.model.Akses;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.time.LocalTime;

public class AksesSpecification {

    public static Specification<Akses> containsNamaAkses(String namaAkses) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nama")), "%" + namaAkses + "%");
    }
    public static Specification<Akses> containsDeskripsi(String deskripsiAkses) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get("deskripsi")), "%" + deskripsiAkses + "%");
    }
    public static Specification<Akses> dateBetween(LocalDate start,LocalDate end) {
        // 1. Konversi 'start' ke awal hari (00:00:00)
        // 2. Konversi 'end' ke penghujung hari (23:59:59.999999999)
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("createdAt"), start.atStartOfDay(), end.atTime(LocalTime.MAX));
    }
}
