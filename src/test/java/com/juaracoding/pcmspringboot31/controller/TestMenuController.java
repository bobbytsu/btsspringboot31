package com.juaracoding.pcmspringboot31.controller;


import com.juaracoding.pcmspringboot31.config.OtherConfig;
import com.juaracoding.pcmspringboot31.dto.validation.ValMenuDTO;
import com.juaracoding.pcmspringboot31.model.Menu;
import com.juaracoding.pcmspringboot31.repo.MenuRepo;
import com.juaracoding.pcmspringboot31.util.ConstantMessage;
import com.juaracoding.pcmspringboot31.util.GenerateData;
import com.juaracoding.pcmspringboot31.util.GenerateExcelMenu;
import com.juaracoding.pcmspringboot31.util.TokenGenerator;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

//DB_PASSWORD=034456e6b2b6e4a86a38ffc10fbe7445;DB_URL=e84b3efac0261a03fff0c5182c725421fca147c61a2530b148ed6cbcabf727df4c99eb129046ef3ec6d7d3eafdfb47413abcc8cd85318145d99908aafe7f4b0db594c3a51aa6675b6af17f83ba3b3d59bace672d05bd6d91a2ac30bf56bab411;DB_USERNAME=6d4bbf96fec4cbd55fc9be6954a06d17
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestMenuController extends AbstractTestNGSpringContextTests {

    public JSONObject requestBody;

    @Autowired
    private MenuRepo menuRepo;

    private Menu menu;

    Long count = 0L;
    @BeforeClass
    public void initClass(){
        requestBody = new  JSONObject();
        count = menuRepo.count();
        if(TestAuthController.jwt.equals("")){
            TestAuthController.jwt = new TokenGenerator(null).getToken();
        }
        if(TestAuthController.generateData==null){
            TestAuthController.generateData = new GenerateData();
        }
    }

    @Test(priority = 0)
    public void save(){
        try{
            requestBody.clear();
            String nama = TestAuthController.generateData.genDataAlfaNumeric(3,20);
            requestBody.put("nama",TestAuthController.generateData.genDataAlfaNumeric(3,20));
            requestBody.put("path","/"+nama.replaceAll(" ","").trim().toLowerCase());
            requestBody.put("deskripsi",TestAuthController.generateData.genDataAlfaNumeric(20,255));
            requestBody.put("kode_menu",getKodeMenu(String.valueOf(count+1)));

            Response response = given()
                    .header("Content-Type","application/json")
                    .header(TestAuthController.AUTH_HEADER,TestAuthController.jwt)
                    .body(requestBody)
                    .post("/menu/v1");

            int responseCode = response.getStatusCode();
            JsonPath jsonPath = response.getBody().jsonPath();
            jsonPath.prettyPrint();
            Assert.assertEquals(responseCode,201,"Error Response code ");
            Assert.assertEquals(jsonPath.getInt("status"),201,"Error Response status ");
            Assert.assertEquals(jsonPath.getString("message"), ConstantMessage.SUCCESS_SAVE,"Error Response status ");
            Assert.assertEquals(jsonPath.getString("data"),"","Error Response data ");
            Assert.assertTrue(jsonPath.getBoolean("success"),"Error Response success ");
            Assert.assertNotNull(jsonPath.getString("timestamp"),"Error Response timestamp ");
        }catch (Exception e){
            Assert.assertNotNull(null,"Exception "+e.getCause()+" Message "+e.getMessage());
        }
    }

    @Test(priority = 10)
    public void update(){
        try{
            initObject(menu);
            requestBody.clear();
            String nama = TestAuthController.generateData.genDataAlfaNumeric(3,20);
            String path = "/"+nama.replaceAll(" ","").trim().toLowerCase();
            String deskripsi = TestAuthController.generateData.genDataAlfaNumeric(20,255);
            String kodeMenu = menu.getKodeMenu();
            requestBody.put("nama",nama);
            requestBody.put("path",path);
            requestBody.put("deskripsi",deskripsi);
            requestBody.put("kode_menu",kodeMenu);
            menu.setPath(path);
            menu.setNama(nama);
            menu.setKodeMenu(kodeMenu);
            menu.setDeskripsi(deskripsi);

            Response response = given()
                    .header("Content-Type","application/json")
                    .header("accept","application/json")
                    .header(TestAuthController.AUTH_HEADER,TestAuthController.jwt)
                    .body(requestBody)
                    .put("/menu/v1/"+menu.getId());

            int responseCode = response.getStatusCode();
            JsonPath jsonPath = response.getBody().jsonPath();
            jsonPath.prettyPrint();
            Assert.assertEquals(responseCode,200,"Error Response code ");
            Assert.assertEquals(jsonPath.getInt("status"),200,"Error Response status ");
            Assert.assertEquals(jsonPath.getString("message"), ConstantMessage.SUCCESS_UPDATE,"Error Response message ");
            Assert.assertEquals(jsonPath.getString("data"),"","Error Response data ");
            Assert.assertTrue(jsonPath.getBoolean("success"),"Error Response success ");
            Assert.assertNotNull(jsonPath.getString("timestamp"),"Error Response timestamp ");
        }catch (Exception e){
            Assert.assertNotNull(null,"Exception "+e.getCause()+" Message "+e.getMessage());
        }
    }

    @Test(priority = 20)
    public void findById(){
        try{
            initObject(menu);
            Response response = given()
                    .header("Content-Type","application/json")
                    .header("accept","application/json")
                    .header(TestAuthController.AUTH_HEADER,TestAuthController.jwt)
                    .body(requestBody)
                    .get("/menu/v1/"+menu.getId());

            int responseCode = response.getStatusCode();
            JsonPath jsonPath = response.getBody().jsonPath();
            jsonPath.prettyPrint();
            Assert.assertEquals(responseCode,200,"Error Response code ");
            Assert.assertEquals(jsonPath.getInt("status"),200,"Error Response status ");
            Assert.assertEquals(jsonPath.getString("message"), ConstantMessage.OK,"Error Response message ");
            Assert.assertEquals(jsonPath.getString("data.nama"),menu.getNama(),"Error Response data ");
            Assert.assertEquals(jsonPath.getString("data.path"),menu.getPath(),"Error Response data ");
            Assert.assertEquals(jsonPath.getString("data.kode_menu"),menu.getKodeMenu(),"Error Response data ");
            Assert.assertTrue(jsonPath.getBoolean("success"),"Error Response success ");
            Assert.assertNotNull(jsonPath.getString("timestamp"),"Error Response timestamp ");
        }catch (Exception e){
            Assert.assertNotNull(null,"Exception "+e.getCause()+" Message "+e.getMessage());
        }
    }

    @Test(priority = 30)
    public void findAll(){
        try{
            initObject(menu);
            Response response = given()
                    .header("Content-Type","application/json")
                    .header("accept","application/json")
                    .header(TestAuthController.AUTH_HEADER,TestAuthController.jwt)
                    .body(requestBody)
                    .get("/menu");

            int responseCode = response.getStatusCode();
            JsonPath jsonPath = response.getBody().jsonPath();
            List<Map<String,Object>> ltData = jsonPath.getList("data.content");
            Map<String,Object> map = ltData.get(0);
            jsonPath.prettyPrint();
            Assert.assertEquals(responseCode,200,"Error Response code ");
            Assert.assertEquals(jsonPath.getInt("status"),200,"Error Response status ");
            Assert.assertEquals(jsonPath.getString("message"), ConstantMessage.OK,"Error Response message ");
            Assert.assertTrue(jsonPath.getBoolean("success"),"Error Response success ");
            Assert.assertNotNull(jsonPath.getString("timestamp"),"Error Response timestamp ");
            //=====================================DATA SORT=============================================
            Assert.assertEquals(jsonPath.getString("data.sort_by"),"id");
            Assert.assertEquals(Integer.parseInt(jsonPath.getString("data.current_page")),0);
            Assert.assertNotNull(jsonPath.getString("data.total_pages"));
            Assert.assertEquals(jsonPath.getString("data.sort"),"asc");
            Assert.assertEquals(Integer.parseInt(jsonPath.getString("data.size_per_page")), OtherConfig.getDefaultPaginationSize());
            Assert.assertEquals(Long.parseLong(jsonPath.getString("data.total_data")),menuRepo.count());
        }catch (Exception e){
            Assert.assertNotNull(null,"Exception "+e.getCause()+" Message "+e.getMessage());
        }
    }

    @Test(priority = 40)
    public void findByParam(){
        initObject(menu);
        String sort = "asc";
        String sortBy = "id";
        Integer page = 0;
        Integer pageSize = 10;
        requestBody.clear();
        requestBody.put("nama",menu.getNama());
//        requestBody.put("path",null);
//        requestBody.put("deskripsi",null);
//        requestBody.put("start",null);
//        requestBody.put("end",null);
        try{
            Response response = given()
                    .header("Content-Type","application/json")
                    .header("accept","application/json")
                    .header(TestAuthController.AUTH_HEADER,TestAuthController.jwt)
                    .queryParam("size",pageSize)
                    .body(requestBody)
                    .post("/menu/v1/"+sort+"/"+sortBy+"/"+page);

            int responseCode = response.getStatusCode();
            JsonPath jsonPath = response.getBody().jsonPath();
            jsonPath.prettyPrint();
            List<Map<String,Object>> ltData = jsonPath.getList("data.content");
            Map<String,Object> map = ltData.get(0);
            Assert.assertEquals(responseCode,200,"Error Response code ");
            Assert.assertEquals(jsonPath.getInt("status"),200,"Error Response status ");
            Assert.assertEquals(jsonPath.getString("message"), ConstantMessage.OK,"Error Response message ");
            Assert.assertEquals(map.get("nama"),menu.getNama(),"Error Response message ");
            Assert.assertEquals(map.get("path"),menu.getPath(),"Error Response message ");
            Assert.assertEquals(map.get("kode_menu"),menu.getKodeMenu(),"Error Response message ");
            Assert.assertTrue(jsonPath.getBoolean("success"),"Error Response success ");
            Assert.assertNotNull(jsonPath.getString("timestamp"),"Error Response timestamp ");
            //=====================================DATA PAGING=============================================
            Assert.assertEquals(jsonPath.getString("data.sort_by"),"id");
            Assert.assertEquals(jsonPath.getString("data.filter.nama"),menu.getNama());
            Assert.assertEquals(Integer.parseInt(jsonPath.getString("data.current_page")),0);
            Assert.assertNotNull(jsonPath.getString("data.total_pages"));
            Assert.assertEquals(jsonPath.getString("data.sort"),"asc");
            Assert.assertEquals(Integer.parseInt(jsonPath.getString("data.size_per_page")), pageSize);
            Assert.assertEquals(Long.parseLong(jsonPath.getString("data.total_data")),1L);
        }catch (Exception e){
            Assert.assertNotNull(null,"Exception "+e.getCause()+" Message "+e.getMessage());
        }
    }

    @Test(priority = 50)
    void uploadExcel(){
        Response response ;
        try{
            Long longCount = menuRepo.count();
            List<ValMenuDTO> list=new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                longCount++;
                String nama = TestAuthController.generateData.genDataAlfaNumeric(3,20);
                String path = "/"+nama.replaceAll(" ","").trim().toLowerCase();
                String deskripsi = TestAuthController.generateData.genDataAlfaNumeric(20,255);
                String kodeMenu = getKodeMenu(String.valueOf(longCount));
                ValMenuDTO menu = new ValMenuDTO();
                menu.setNama(nama);
                menu.setPath(path);
                menu.setDeskripsi(deskripsi);
                menu.setKodeMenu(kodeMenu);
                list.add(menu);
            }
            String [] kolomMenu = {"NAMA","PATH","DESKRIPSI","KODE_MENU"};
            GenerateExcelMenu.generateDataExcel(list,kolomMenu);
            response = given().
                    header("Content-Type","multipart/form-data").
                    header("accept","application/json").
                    header(TestAuthController.AUTH_HEADER,TestAuthController.jwt).
                    multiPart("file",new File(System.getProperty("user.dir")+"/src/test/resources/data-test/menu.xlsx"),
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").
                    request(Method.POST,"/menu/v1/upload");

            int intResponse = response.getStatusCode();
            JsonPath jsonPath = response.jsonPath();
            Assert.assertEquals(intResponse,201,"Error response code");
            Assert.assertEquals(jsonPath.getString("message"),ConstantMessage.SUCCESS_UPLOAD,"Error Response message ");
            Assert.assertNotNull(jsonPath.getString("data"),"Errorr Content");
            Assert.assertTrue(Boolean.parseBoolean(jsonPath.getString("success")),"Error Response success ");
            Assert.assertNotNull(jsonPath.getString("timestamp"),"Error Response timestamp ");
        }catch (Exception e){
            Assert.assertNotNull(null,"Exception "+e.getCause()+" Message "+e.getMessage());
        }
    }

    @Test(priority = 60)
    void downloadExcel(){
        initObject(menu);
        requestBody.clear();
        requestBody.put("nama",menu.getNama());

        try{
            Response response = given().
                    header("Content-Type","application/json").
                    header("accept","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").
                    header(TestAuthController.AUTH_HEADER,TestAuthController.jwt).
                    body(requestBody).
                    request(Method.POST,"/menu/v1/excel");

            int intResponse = response.getStatusCode();
            Assert.assertEquals(intResponse,200);
            /** khusus untuk download file harus di cek header nya */
            Assert.assertTrue(response.getHeader("Content-Disposition").contains(".xlsx"));// file nya memiliki extension .xlsx
            Assert.assertEquals(response.getHeader("Content-Type"),"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");// content type wajib ini untuk excel
        }catch (Exception e){
            Assert.assertNotNull(null,"Exception "+e.getCause()+" Message "+e.getMessage());
        }
    }

    @Test(priority = 70)
    void downloadPdf(){
        initObject(menu);
        requestBody.clear();
        requestBody.put("nama",menu.getNama());
        try{
            Response response = given().
                    header("Content-Type","application/json").
                    header("accept","application/pdf").//mime type
                    header(TestAuthController.AUTH_HEADER,TestAuthController.jwt).
                    body(requestBody).
                    request(Method.POST,"/menu/v1/pdf");

            int intResponse = response.getStatusCode();
            Assert.assertEquals(intResponse,200);
            /** khusus untuk download file harus di cek header nya */
            Assert.assertTrue(response.getHeader("Content-Disposition").contains(".pdf"));// file nya memiliki extension .pdf
            Assert.assertEquals(response.getHeader("Content-Type"),"application/pdf");// content type wajib ini untuk pdf
        }catch (Exception e){
            Assert.assertNotNull(null,"Exception "+e.getCause()+" Message "+e.getMessage());
        }
    }

    @Test(priority = 99)
    public void delete(){
        try{
            initObject(menu);
            requestBody.clear();
            Response response = given()
                    .header("Content-Type","application/json")
                    .header("accept","application/json")
                    .header(TestAuthController.AUTH_HEADER,TestAuthController.jwt)
                    .body(requestBody)
                    .delete("/menu/v1/"+menu.getId());

            int responseCode = response.getStatusCode();
            JsonPath jsonPath = response.getBody().jsonPath();
            jsonPath.prettyPrint();
            Assert.assertEquals(responseCode,200,"Error Response code ");
            Assert.assertEquals(jsonPath.getInt("status"),200,"Error Response status ");
            Assert.assertEquals(jsonPath.getString("message"), ConstantMessage.SUCCESS_DELETE,"Error Response message ");
            Assert.assertEquals(jsonPath.getString("data"),"","Error Response data ");
            Assert.assertTrue(jsonPath.getBoolean("success"),"Error Response success ");
            Assert.assertNotNull(jsonPath.getString("timestamp"),"Error Response timestamp ");
        }catch (Exception e){
            Assert.assertNotNull(null,"Exception "+e.getCause()+" Message "+e.getMessage());
        }
    }
    private String getKodeMenu(String count){
//        menu = menuRepo.findTop1ByOrderByIdDesc().orElse(null);
        String kode = "";
        if(count.length()<3){
            int paddingLength = 3 - count.length();
            String padding = "0".repeat(paddingLength);
            kode = padding + count;
        }else{
            kode = count;
        }
        return kode;
    }

    private void initObject(Menu menu){
        if(menu==null){
            menu = menuRepo.findTop1ByOrderByIdDesc().orElse(null);
        }
        this.menu=menu;
    }

}