package com.juaracoding.pcmspringboot31.service;

import com.juaracoding.pcmspringboot31.core.IFile;
import com.juaracoding.pcmspringboot31.core.IServiceDML;
import com.juaracoding.pcmspringboot31.core.IServiceQuery;
import com.juaracoding.pcmspringboot31.dto.query.SearchMenuDTO;
import com.juaracoding.pcmspringboot31.dto.report.ReportMenuDTO;
import com.juaracoding.pcmspringboot31.dto.validation.ValMenuDTO;
import com.juaracoding.pcmspringboot31.handler.ResponseHandler;
import com.juaracoding.pcmspringboot31.model.Akses;
import com.juaracoding.pcmspringboot31.model.Menu;
import com.juaracoding.pcmspringboot31.repo.MenuRepo;
import com.juaracoding.pcmspringboot31.specification.MenuSpecification;
import com.juaracoding.pcmspringboot31.util.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.annotations.DynamicUpdate;
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
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Modul Code : 02
 */
@Service
@Transactional
public class MenuService implements IServiceDML<Menu>, IServiceQuery<SearchMenuDTO,Menu>, IFile<SearchMenuDTO> {

    @Autowired
    private MenuRepo menuRepo;

    @Autowired
    private ModelMapper map;

    @Autowired
    private TransformPagination tp;
    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    @Autowired
    private PdfGenerator pdfGenerator;

    private StringBuilder sBuild = new StringBuilder();

    @Override
    public ResponseEntity<Object> save(Menu menu, HttpServletRequest request) {
        if(menu==null){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.FAILED_SAVE, HttpStatus.BAD_REQUEST,null,"USM02001",request);
        }
        menu.setCreatedBy("{\"id\":\"1\",\"nama\":\"System\"}");
        menuRepo.save(menu);
        return new ResponseHandler().
                handleResponse(ConstantMessage.SUCCESS_SAVE, HttpStatus.CREATED,null,null,request);
    }

    @Override//011-020
    public ResponseEntity<Object> update(Long id,Menu menu, HttpServletRequest request) {
        if(menu==null){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.FAILED_UPDATE, HttpStatus.BAD_REQUEST,null,"USM02011",request);
        }
        Optional<Menu> menuOptional = menuRepo.findById(id);
        if(menuOptional.isEmpty()){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.NOT_FOUND, HttpStatus.NOT_FOUND,null,"USM02012",request);
        }

        /** dirty checking
         * UPDATE MstMenu SET nama = 'menu.getNama()',
         * deskripsi = 'menu.getDeskripsi()',
         * path = 'menu.getPath()',
         * updated_by = '{"id":"1","nama":"System"}' WHERE id = 'id'
         * */
            Menu menuDB = menuOptional.get();
            if(menuDB.getDeletedBy()!=null){
                return new ResponseHandler().
                        handleResponse(ConstantMessage.NOT_FOUND, HttpStatus.NOT_FOUND,null,"USM02013",request);
            }
            menuDB.setNama(menu.getNama());//akses -> akses
            menuDB.setDeskripsi(menu.getDeskripsi());
            menuDB.setPath(menu.getPath());
            menuDB.setUpdatedBy("{\"id\":\"1\",\"nama\":\"System\"}");
        return new ResponseHandler().
                handleResponse(ConstantMessage.SUCCESS_UPDATE, HttpStatus.OK,null,null,request);
    }

    @Override
    public ResponseEntity<Object> deleteById(Long id, HttpServletRequest request) {
        if(id==null){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.FAILED_DELETE, HttpStatus.BAD_REQUEST,null,"USM02021",request);
        }
        Optional<Menu> menuOptional = menuRepo.findById(id);
        if(menuOptional.isEmpty()){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.NOT_FOUND, HttpStatus.NOT_FOUND,null,"USM02022",request);
        }
        Menu menuDB = menuOptional.get();
        if(menuDB.getDeletedBy()!=null){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.NOT_FOUND, HttpStatus.NOT_FOUND,null,"USM02023",request);
        }
        menuDB.setDeletedBy("{\"id\":\"1\",\"nama\":\"System\"}");
        menuDB.setDeletedAt(LocalDateTime.now());
        return new ResponseHandler().
                handleResponse(ConstantMessage.SUCCESS_DELETE, HttpStatus.OK,null,null,request);
    }

    @Override
    public ResponseEntity<Object> findAll(Pageable pageable, HttpServletRequest request) {
        Page<Menu> page = menuRepo.findAll(pageable);
        if(page.isEmpty()){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.NOT_FOUND, HttpStatus.NOT_FOUND,null,"USM02151",request);
        }

        return new ResponseHandler().
                handleResponse(ConstantMessage.OK, HttpStatus.OK,
                tp.transformPagination(mapperToDTO(page.getContent()),page,new SearchMenuDTO()),
                null,request);
    }

    @Override
    public ResponseEntity<Object> findByParam(Pageable pageable, SearchMenuDTO param, HttpServletRequest request) {
        Page<Menu> page = null;
        if(param==null){
            page = menuRepo.findAll(pageable);
            return new ResponseHandler().
                    handleResponse(ConstantMessage.OK, HttpStatus.OK,
                            tp.transformPagination(mapperToDTO(page.getContent()),page,param),"USM02161",request);
        }
        page = menuRepo.findAll(getSpecification(param), pageable);
        if(page.isEmpty()){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.NOT_FOUND, HttpStatus.NOT_FOUND,null,"USM02162",request);
        }
        return new ResponseHandler().
                handleResponse(ConstantMessage.OK, HttpStatus.OK,tp.transformPagination(mapperToDTO(page.getContent()),page,param),null,request);
    }

    @Override
    public ResponseEntity<Object> upload(MultipartFile file, HttpServletRequest request) throws IOException {

        if(!ExcelReader.hasWorkBookFormat(file)){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.MUST_EXCEL, HttpStatus.UNSUPPORTED_MEDIA_TYPE,null,"USM02301",request);
        }
        String strArr[][] = new ExcelReader(file.getInputStream(),"Sheet1").getDataWithoutHeader();
        List<Menu> list = new ArrayList<>();
        for(int i=0;i<strArr.length;i++){
            Menu menu = new Menu();
            menu.setNama(strArr[i][0]);
            menu.setPath(strArr[i][1]);
            menu.setDeskripsi(strArr[i][2]);
            menu.setKodeMenu(strArr[i][3]);
            menu.setCreatedBy("{\"id\":\"1\",\"nama\":\"System\"}");
            list.add(menu);
        }
        menuRepo.saveAll(list);
        return new ResponseHandler().
                handleResponse(ConstantMessage.SUCCESS_UPLOAD, HttpStatus.CREATED,null,null,request);
    }

    @Override
    public void downloadExcel(SearchMenuDTO param, HttpServletRequest request, HttpServletResponse response) {
        List<Menu> list = null;
        if(param==null){
            GlobalFunction.manualResponse(response,new ResponseHandler().
                    handleResponse(ConstantMessage.NOT_FOUND, HttpStatus.NOT_FOUND,null,"USM02311",request));
        }
        list = menuRepo.findAll(getSpecification(param));
        int intSize = list.size();
        String strColumnArr [] = {"Nama Menu","Path Menu","Deskripsi"};
        String strData [][] = new String[intSize][strColumnArr.length];
        for(int i=0;i<intSize;i++){
            strData[i][0] = list.get(i).getNama();
            strData[i][1] = list.get(i).getPath();
            strData[i][2] = list.get(i).getDeskripsi();
        }
        String headerKey = "Content-Disposition";
        sBuild.setLength(0);
        String headerValue = sBuild.append("attachment; filename=menu_").
                append(new SimpleDateFormat("ddMMyyHHmmss").format(new Date())).
                append(".xlsx").toString();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(headerKey, headerValue);
        new ExcelWriter(strData,strColumnArr,"Menu",response);
    }

    @Override
    public void downloadPdf(SearchMenuDTO param, HttpServletRequest request, HttpServletResponse response) {
        List<Menu> list = null;
        if(param==null){
            GlobalFunction.manualResponse(response,new ResponseHandler().
                    handleResponse(ConstantMessage.NOT_FOUND, HttpStatus.NOT_FOUND,null,"USM02311",request));
        }
        list = menuRepo.findAll(getSpecification(param));
        int intSize = list.size();
        Map<String,Object> mapResponse = new HashMap<>();
        String strHtml = null;
        Context context = new Context();
        mapResponse.put("title","REPORT MENU");
        mapResponse.put("listContent",list);
        mapResponse.put("totalData",intSize);
        mapResponse.put("username","Paul");
        mapResponse.put("timestamp", LocalDateTime.now());
        context.setVariables(mapResponse);
        strHtml = springTemplateEngine.process("menu",context);
//        System.out.println("Html Rendering \n"+strHtml);
        pdfGenerator.htmlToPdf(strHtml,"menu",response);
    }

    @Override
    public Specification<Menu> getSpecification(SearchMenuDTO param){
        // 1. Inisialisasi dasar: SELECT * FROM User WHERE 1=1
        // (Wadah kosong yang tidak memfilter apa-apa)
        Specification<Menu> spec = Specification.where(null);

        //SELECT * FROM User WHERE 1=1 OR lower(nama) like lower(concat('%',?,'%'))
        if(GlobalFunction.checkFilter(param.getNama())){
            spec = spec.or(MenuSpecification.containsNamaMenu(param.getNama()));
        }
        //SELECT * FROM User WHERE 1=1 OR lower(nama) like lower(concat('%',?,'%')) OR lower(path) like lower(concat('%',?,'%'))
        if(GlobalFunction.checkFilter(param.getPath())){
            spec = spec.or(MenuSpecification.containsPathMenu(param.getPath()));
        }
        //SELECT * FROM User WHERE 1=1 OR lower(nama) like lower(concat('%',?,'%')) OR lower(path) like lower(concat('%',?,'%')) OR lower(deskripsi) like lower(concat('%',?,'%'))
        if(GlobalFunction.checkFilter(param.getDeskripsi())){
            spec = spec.or(MenuSpecification.containsPathMenu(param.getDeskripsi()));
        }
        //SELECT * FROM User WHERE 1=1 OR lower(nama) like lower(concat('%',?,'%')) OR lower(path) like lower(concat('%',?,'%')) OR lower(deskripsi) like lower(concat('%',?,'%')) AND createdAt BETWEEN start and end
        if(GlobalFunction.checkFilter(param.getStart()) && GlobalFunction.checkFilter(param.getEnd())){
            spec = spec.and(MenuSpecification.dateBetween(param.getStart(), param.getEnd()));
        }
        return spec;
    }

    @Override
    public ResponseEntity<Object> findById(Long id,HttpServletRequest request) {
        if(id==null){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.NOT_FOUND, HttpStatus.BAD_REQUEST,null,"USM0281",request);
        }
        Optional<Menu> menuOptional = menuRepo.findById(id);
        if(menuOptional.isEmpty()){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.NOT_FOUND, HttpStatus.NOT_FOUND,null,"USM02182",request);
        }

        Menu menuDB = menuOptional.get();

        return new ResponseHandler().
                handleResponse(ConstantMessage.OK, HttpStatus.OK,mapperToDTO(menuDB),null,request);
    }
    public Menu mapToEntity(ValMenuDTO valMenuDTO){
        Menu menu = new Menu();
        menu.setNama(valMenuDTO.getNama());
        menu.setDeskripsi(valMenuDTO.getDeskripsi());
        menu.setPath(valMenuDTO.getPath());
        return menu;
    }
    public Menu mapperToEntity(ValMenuDTO valMenuDTO){
        Menu menu = map.map(valMenuDTO, Menu.class);
        return menu;
    }
    public ReportMenuDTO mapperToDTO(Menu menu){
        return map.map(menu, ReportMenuDTO.class);
    }
    public List<ReportMenuDTO> mapperToDTO(List<Menu> menus){
        List<ReportMenuDTO> menu = map.map(menus, new TypeToken<List<ReportMenuDTO>>() {}.getType());
        return menu;
    }
}