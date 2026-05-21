package com.juaracoding.pcmspringboot31.model;

import com.juaracoding.pcmspringboot31.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Setter
@Getter
@Entity
@Table(name = "MstMenu",schema = "projectz")
@DynamicUpdate
public class Menu extends BaseEntity {
    @Column(name = "NamaMenu",length = 20, nullable = false, unique = true)
    private String nama;
    @Column(name = "Path",length = 50, nullable = false, unique = true)
    private String path;
    @Column(name = "DeskripsiMenu", nullable = false)
    private String deskripsi;
    @Column(name = "KodeMenu",length = 3,unique = true,nullable = false)
    private String kodeMenu;
}