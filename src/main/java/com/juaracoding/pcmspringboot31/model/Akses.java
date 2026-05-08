package com.juaracoding.pcmspringboot31.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.juaracoding.pcmspringboot31.util.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "MstAkses",schema = "projectz")
@DynamicUpdate
public class Akses extends BaseEntity {
    @Column(name = "NamaAkses",length = 20, nullable = false, unique = true)
    private String nama;
    @Column(name = "Deskripsi", nullable = false)
    private String deskripsi;
    @ManyToMany
    @JoinTable(name = "AksesMenu",uniqueConstraints = @UniqueConstraint(name = "Unq_Akses_Menu", columnNames = {"IdAkses","IdMenu"}),
    joinColumns = @JoinColumn(name = "IdAkses",nullable = false,foreignKey = @ForeignKey(name = "FK_AksesMenu_Akses")),
    inverseJoinColumns = @JoinColumn(name = "IdMenu",nullable = false,foreignKey = @ForeignKey(name = "FK_AksesMenu_Menu"))
    )
    private List<Menu> menus;
}