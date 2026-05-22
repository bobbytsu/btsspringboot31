package com.juaracoding.pcmspringboot31.repo;


import com.juaracoding.pcmspringboot31.model.Akses;
import com.juaracoding.pcmspringboot31.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface AksesRepo extends JpaRepository<Akses, Long> , JpaSpecificationExecutor<Akses> {
    Optional<Akses> findTop1ByOrderByIdDesc();
}