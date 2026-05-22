package com.juaracoding.pcmspringboot31.service;

import com.juaracoding.pcmspringboot31.core.IServiceDML;
import com.juaracoding.pcmspringboot31.core.IServiceQuery;
import com.juaracoding.pcmspringboot31.dto.RelDTO;
import com.juaracoding.pcmspringboot31.dto.query.SearchAksesDTO;
import com.juaracoding.pcmspringboot31.dto.report.ReportAksesDTO;
import com.juaracoding.pcmspringboot31.dto.validation.ValAksesDTO;
import com.juaracoding.pcmspringboot31.handler.ResponseHandler;
import com.juaracoding.pcmspringboot31.model.Akses;
import com.juaracoding.pcmspringboot31.model.AksesMenu;
import com.juaracoding.pcmspringboot31.model.Menu;
import com.juaracoding.pcmspringboot31.repo.AksesMenuRepo;
import com.juaracoding.pcmspringboot31.repo.AksesRepo;
import com.juaracoding.pcmspringboot31.specification.AksesSpecification;
import com.juaracoding.pcmspringboot31.util.*;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Modul Code : 01
 */
@Service
@Transactional
public class AksesService implements IServiceDML<Akses>, IServiceQuery<SearchAksesDTO,Akses> {

    @Autowired
    private AksesRepo aksesRepo;

    @Autowired
    private ModelMapper map;

    @Autowired
    private TransformPagination tp;

    @Override
    public ResponseEntity<Object> save(Akses akses, HttpServletRequest request) {
        if(akses==null){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.FAILED_SAVE, HttpStatus.BAD_REQUEST,null,"USM01001",request);
        }
        // 1. Definisikan informasi user (biasanya didapat dari Token/Session/SecurityContext)
        String currentUser = "{\"id\":\"1\",\"nama\":\"System\"}";
        // 2. Set CreatedBy untuk Parent (Akses)
        akses.setCreatedBy(currentUser);

        // 3. Modifikasi: Inject info ke Child (AksesMenu)
        if (akses.getAksesMenuList() != null) {
            for (AksesMenu am : akses.getAksesMenuList()) {
                // Inject CreatedBy untuk child
                am.setCreatedBy(currentUser);

                // PENTING: Karena Anda pakai ModelMapper, mapping DTO ke Entity
                // seringkali membuat child tidak tahu siapa parent-nya.
                // Kita ikat kembali parent-nya di sini.
                am.setAkses(akses);
            }
        }
        aksesRepo.save(akses);
        return new ResponseHandler().
                handleResponse(ConstantMessage.SUCCESS_SAVE, HttpStatus.CREATED,null,null,request);
    }

    @Override//011-020
    public ResponseEntity<Object> update(Long id,Akses akses, HttpServletRequest request) {
        if(akses==null){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.FAILED_UPDATE, HttpStatus.BAD_REQUEST,null,"USM01011",request);
        }
        Optional<Akses> aksesOptional = aksesRepo.findById(id);
        if(aksesOptional.isEmpty()){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.NOT_FOUND, HttpStatus.NOT_FOUND,null,"USM01012",request);
        }

            Akses aksesDB = aksesOptional.get();
            if(aksesDB.getDeletedBy()!=null){
                return new ResponseHandler().
                        handleResponse(ConstantMessage.NOT_FOUND, HttpStatus.NOT_FOUND,null,"USM01013",request);
            }
            String currentUser = "{\"id\":\"1\",\"nama\":\"System\"}";
            aksesDB.setNama(akses.getNama());//akses -> akses
            aksesDB.setDeskripsi(akses.getDeskripsi());
            aksesDB.setUpdatedBy(currentUser);
            // Update List Menu (Orphan Removal akan menghapus data lama yang tidak ada di list baru)
            aksesDB.getAksesMenuList().clear(); // Kosongkan list lama
            /**
                defaultnya hibenate melakukan query insert terlebih dahulu baru delete
                sehingga menyebabkan error "Violation of UNIQUE KEY constraint" oleh karena itu kita perlu
                menambahkan perintah flush agar memaksa Hibernate untuk menembakkan query DELETE ke database SEKARANG JUGA
             */
            aksesRepo.flush();

            if (akses.getAksesMenuList() != null) {
                for(AksesMenu am : akses.getAksesMenuList()){
                    am.setAkses(aksesDB); // Pastikan parent ID terset ke ID yang sedang diupdate
                    am.setCreatedBy(currentUser);
                    aksesDB.getAksesMenuList().add(am);
                }
            }
        return new ResponseHandler().
                handleResponse(ConstantMessage.SUCCESS_UPDATE, HttpStatus.OK,null,null,request);
    }

    @Override
    public ResponseEntity<Object> deleteById(Long id, HttpServletRequest request) {
        if(id==null){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.FAILED_DELETE, HttpStatus.BAD_REQUEST,null,"USM01021",request);
        }
        Optional<Akses> aksesOptional = aksesRepo.findById(id);
        if(aksesOptional.isEmpty()){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.NOT_FOUND, HttpStatus.NOT_FOUND,null,"USM01022",request);
        }
        Akses aksesDB = aksesOptional.get();
        if(aksesDB.getDeletedBy()!=null){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.NOT_FOUND, HttpStatus.NOT_FOUND,null,"USM01023",request);
        }
        aksesDB.setDeletedBy("{\"id\":\"1\",\"nama\":\"System\"}");
        aksesDB.setDeletedAt(LocalDateTime.now());
        aksesDB.getAksesMenuList().clear(); // Kosongkan list lama
        aksesRepo.flush();
        return new ResponseHandler().
                handleResponse(ConstantMessage.SUCCESS_DELETE, HttpStatus.OK,null,null,request);
    }

    @Override
    public ResponseEntity<Object> findAll(Pageable pageable, HttpServletRequest request) {
        Page<Akses> page = aksesRepo.findAll(pageable);
        if(page.isEmpty()){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.NOT_FOUND, HttpStatus.NOT_FOUND,null,"USM01151",request);
        }

        return new ResponseHandler().
                handleResponse(ConstantMessage.OK, HttpStatus.OK,
                tp.transformPagination(mapperToDTO(page.getContent()),page,new SearchAksesDTO()),
                null,request);
    }

    @Override
    public ResponseEntity<Object> findByParam(Pageable pageable, SearchAksesDTO param, HttpServletRequest request) {
        Page<Akses> page = null;
        if(param==null){
            page = aksesRepo.findAll(pageable);
            return new ResponseHandler().
                    handleResponse(ConstantMessage.OK, HttpStatus.OK,
                            tp.transformPagination(mapperToDTO(page.getContent()),page,param),"USM01161",request);
        }
        page = aksesRepo.findAll(getSpecification(param), pageable);
        if(page.isEmpty()){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.NOT_FOUND, HttpStatus.NOT_FOUND,null,"USM01162",request);
        }
        return new ResponseHandler().
                handleResponse(ConstantMessage.OK, HttpStatus.OK,tp.transformPagination(mapperToDTO(page.getContent()),page,param),null,request);
    }


    @Override
    public Specification<Akses> getSpecification(SearchAksesDTO param){
        // 1. Inisialisasi dasar: SELECT * FROM User WHERE 1=1
        // (Wadah kosong yang tidak memfilter apa-apa)
        Specification<Akses> spec = Specification.where(null);

        //SELECT * FROM User WHERE 1=1 OR lower(nama) like lower(concat('%',?,'%'))
        if(GlobalFunction.checkFilter(param.getNama())){
            spec = spec.or(AksesSpecification.containsNamaAkses(param.getNama()));
        }
        //SELECT * FROM User WHERE 1=1 OR lower(nama) like lower(concat('%',?,'%')) OR lower(path) like lower(concat('%',?,'%')) OR lower(deskripsi) like lower(concat('%',?,'%'))
        if(GlobalFunction.checkFilter(param.getDeskripsi())){
            spec = spec.or(AksesSpecification.containsDeskripsi(param.getDeskripsi()));
        }
        //SELECT * FROM User WHERE 1=1 OR lower(nama) like lower(concat('%',?,'%')) OR lower(path) like lower(concat('%',?,'%')) OR lower(deskripsi) like lower(concat('%',?,'%')) AND createdAt BETWEEN start and end
        if(GlobalFunction.checkFilter(param.getStart()) && GlobalFunction.checkFilter(param.getEnd())){
            spec = spec.and(AksesSpecification.dateBetween(param.getStart(), param.getEnd()));
        }
        return spec;
    }

    @Override
    public ResponseEntity<Object> findById(Long id,HttpServletRequest request) {
        if(id==null){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.NOT_FOUND, HttpStatus.BAD_REQUEST,null,"USM0181",request);
        }
        Optional<Akses> aksesOptional = aksesRepo.findById(id);
        if(aksesOptional.isEmpty()){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.NOT_FOUND, HttpStatus.NOT_FOUND,null,"USM01182",request);
        }

        Akses aksesDB = aksesOptional.get();

        return new ResponseHandler().
                handleResponse(ConstantMessage.OK, HttpStatus.OK,mapperToDTO(aksesDB),null,request);
    }

    public Akses mapToEntity(ValAksesDTO valAksesDTO){
        Akses akses = new Akses();
        akses.setNama(valAksesDTO.getNama());
        akses.setDeskripsi(valAksesDTO.getDeskripsi());
        List<AksesMenu> listAksesMenu = new ArrayList<>(); // Buat list lokal
        Integer intSize = valAksesDTO.getAksesMenu().size();
        if (valAksesDTO.getAksesMenu() != null) {
            for(int i=0;i<intSize;i++){
                AksesMenu aksesMenu = new AksesMenu();
                Menu menu = new Menu();
                menu.setId(valAksesDTO.getAksesMenu().get(i).getMenu());
                aksesMenu.setMenu(menu);
                aksesMenu.setAkses(akses);
                aksesMenu.setCanInsert(valAksesDTO.getAksesMenu().get(i).getCanInsert());
                aksesMenu.setCanUpdate(valAksesDTO.getAksesMenu().get(i).getCanUpdate());
                aksesMenu.setCanDelete(valAksesDTO.getAksesMenu().get(i).getCanDelete());
                aksesMenu.setCanView(valAksesDTO.getAksesMenu().get(i).getCanView());
                aksesMenu.setCanPrint(valAksesDTO.getAksesMenu().get(i).getCanPrint());
                listAksesMenu.add(aksesMenu);
            }
        }
        akses.setAksesMenuList(listAksesMenu);
        return akses;
    }
    public Akses mapperToEntity(ValAksesDTO valAksesDTO){
        return map.map(valAksesDTO, Akses.class);
    }
    public ReportAksesDTO mapperToDTO(Akses akses){
        return map.map(akses, ReportAksesDTO.class);
    }
    public List<ReportAksesDTO> mapperToDTO(List<Akses> aksess){
        List<ReportAksesDTO> akses = map.map(aksess, new TypeToken<List<ReportAksesDTO>>() {}.getType());
        return akses;
    }
}