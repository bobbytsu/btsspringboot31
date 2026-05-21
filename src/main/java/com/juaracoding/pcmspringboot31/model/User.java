package com.juaracoding.pcmspringboot31.model;

import com.juaracoding.pcmspringboot31.util.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "MstUser",schema = "projectz")
@Comment("Tabel ini merupakan tabel informasi user dari aplikasi ini")
@DynamicUpdate
public class User extends BaseEntity implements UserDetails {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdAkses",nullable = false,foreignKey = @ForeignKey(name = "FK_User_Akses"))
    private Akses akses;
    @Column(name = "Password",length = 64,nullable = false)
    private String password;
    @Column(name = "Username",length = 16,nullable = false,unique = true)
    private String username;
    @Column(name = "NoHp",length = 20,nullable = false,unique = true)
    private String noHp;
    @Comment("Informasi email user")
    @Column(name = "Email",nullable = false, unique = true)
    private String email;
    @Comment("Informasi Nama Lengkap user")
    @Column(name = "NamaLengkap",length = 75,nullable = false)
    private String namaLengkap;
    @Column(name = "TanggalLahir",nullable = false)
    private LocalDate tanggalLahir;
    @Column(name = "Otp",length = 64)
    private String otp;
    @Transient
    private String umur;
    @OneToMany(mappedBy = "userx")
    private List<MultiData> multiData;
    @Column(name = "IsRegistered",nullable = false)
    private Boolean isRegistered=false;

    public String getUmur() {
//        return Period.between(tanggalLahir,LocalDate.now()).getYears() + " Tahun";
        return tanggalLahir==null?"":Period.between(tanggalLahir,LocalDate.now()).getYears() + " Tahun";
    }

    /** RWX READ WRITE EXECUTION */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<AksesMenu> aksesMap = this.akses.getAksesMenuList();
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
//        grantedAuthorities.add(new SimpleGrantedAuthority(akses.getNama()));
        for (AksesMenu aksesMenu : aksesMap) {
            if(Boolean.TRUE.equals(aksesMenu.getCanInsert())){
                grantedAuthorities.add(new SimpleGrantedAuthority(aksesMenu.getMenu().getKodeMenu()+"i"));
            }
            if(Boolean.TRUE.equals(aksesMenu.getCanUpdate())){
                grantedAuthorities.add(new SimpleGrantedAuthority(aksesMenu.getMenu().getKodeMenu()+"u"));
            }
            if(Boolean.TRUE.equals(aksesMenu.getCanDelete())){
                grantedAuthorities.add(new SimpleGrantedAuthority(aksesMenu.getMenu().getKodeMenu()+"d"));
            }
            if(Boolean.TRUE.equals(aksesMenu.getCanView())){
                grantedAuthorities.add(new SimpleGrantedAuthority(aksesMenu.getMenu().getKodeMenu()+"v"));
            }
            if(Boolean.TRUE.equals(aksesMenu.getCanPrint())){
                grantedAuthorities.add(new SimpleGrantedAuthority(aksesMenu.getMenu().getKodeMenu()+"p"));
            }
        }
        System.out.println("otorisasi : "+grantedAuthorities);
        return grantedAuthorities;
    }
}