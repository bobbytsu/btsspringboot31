package com.juaracoding.pcmspringboot31.controller;


import com.juaracoding.pcmspringboot31.config.OtherConfig;
import com.juaracoding.pcmspringboot31.dto.validation.ValAksesMenuDTO;
import com.juaracoding.pcmspringboot31.model.Akses;
import com.juaracoding.pcmspringboot31.repo.AksesRepo;
import com.juaracoding.pcmspringboot31.util.ConstantMessage;
import com.juaracoding.pcmspringboot31.util.GenerateData;
import com.juaracoding.pcmspringboot31.util.TokenGenerator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

//DB_PASSWORD=034456e6b2b6e4a86a38ffc10fbe7445;DB_URL=e84b3efac0261a03fff0c5182c725421fca147c61a2530b148ed6cbcabf727df4c99eb129046ef3ec6d7d3eafdfb47413abcc8cd85318145d99908aafe7f4b0db594c3a51aa6675b6af17f83ba3b3d59bace672d05bd6d91a2ac30bf56bab411;DB_USERNAME=6d4bbf96fec4cbd55fc9be6954a06d17
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestAksesController extends AbstractTestNGSpringContextTests {

    public JSONObject requestBody;

    @Autowired
    private AksesRepo aksesRepo;

    private Akses akses;

    Long count = 0L;
    @BeforeClass
    public void initClass(){
        requestBody = new  JSONObject();
        count = aksesRepo.count();
        if(TestAuthController.jwt.equals("")){
            TestAuthController.jwt = new TokenGenerator(null).getToken();
        }
        if(TestAuthController.generateData==null){
            TestAuthController.generateData = new GenerateData();
        }
    }

    @Test(priority = 0)
    public void save(){
        List<ValAksesMenuDTO> listAksesMenu = new ArrayList<>();
        ValAksesMenuDTO aksesDTO = new ValAksesMenuDTO();
        aksesDTO.setMenu(1L);
        aksesDTO.setCanInsert(false);
        aksesDTO.setCanUpdate(false);
        aksesDTO.setCanDelete(false);
        aksesDTO.setCanView(false);
        aksesDTO.setCanPrint(false);
        listAksesMenu.add(aksesDTO);

        try{
            requestBody.clear();
            String nama = TestAuthController.generateData.genDataAlfaNumeric(5,20);
            requestBody.put("nama",nama);
            requestBody.put("deskripsi",TestAuthController.generateData.genDataAlfaNumeric(20,255));
            requestBody.put("access_menu",listAksesMenu);

            Response response = given()
                    .header("Content-Type","application/json")
                    .header(TestAuthController.AUTH_HEADER,TestAuthController.jwt)
                    .body(requestBody)
                    .post("/akses/v1");

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
            initObject(akses);
            requestBody.clear();
            List<ValAksesMenuDTO> listAksesMenu = new ArrayList<>();
            ValAksesMenuDTO aksesDTO = new ValAksesMenuDTO();
            aksesDTO.setMenu(2L);
            aksesDTO.setCanInsert(false);
            aksesDTO.setCanUpdate(false);
            aksesDTO.setCanDelete(false);
            aksesDTO.setCanView(false);
            aksesDTO.setCanPrint(false);
            listAksesMenu.add(aksesDTO);
            String nama = TestAuthController.generateData.genDataAlfaNumeric(5,20);
            String deskripsi = TestAuthController.generateData.genDataAlfaNumeric(20,255);
            requestBody.put("nama",nama);
            requestBody.put("deskripsi",deskripsi);
            requestBody.put("access_menu",listAksesMenu);
            akses.setNama(nama);
            akses.setDeskripsi(deskripsi);
            Response response = given()
                    .header("Content-Type","application/json")
                    .header("accept","application/json")
                    .header(TestAuthController.AUTH_HEADER,TestAuthController.jwt)
                    .body(requestBody)
                    .put("/akses/v1/"+akses.getId());

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
            initObject(akses);
            Response response = given()
                    .header("Content-Type","application/json")
                    .header("accept","application/json")
                    .header(TestAuthController.AUTH_HEADER,TestAuthController.jwt)
                    .body(requestBody)
                    .get("/akses/v1/"+akses.getId());

            int responseCode = response.getStatusCode();
            JsonPath jsonPath = response.getBody().jsonPath();
            jsonPath.prettyPrint();
            Assert.assertEquals(responseCode,200,"Error Response code ");
            Assert.assertEquals(jsonPath.getInt("status"),200,"Error Response status ");
            Assert.assertEquals(jsonPath.getString("message"), ConstantMessage.OK,"Error Response message ");
            Assert.assertEquals(jsonPath.getString("data.nama"),akses.getNama(),"Error Response data ");
            Assert.assertEquals(jsonPath.getString("data.deskripsi"),akses.getDeskripsi(),"Error Response deskripsi ");
            Assert.assertTrue(jsonPath.getBoolean("success"),"Error Response success ");
            Assert.assertNotNull(jsonPath.getString("timestamp"),"Error Response timestamp ");
        }catch (Exception e){
            Assert.assertNotNull(null,"Exception "+e.getCause()+" Message "+e.getMessage());
        }
    }

    @Test(priority = 30)
    public void findAll(){
        try{
            initObject(akses);
            Response response = given()
                    .header("Content-Type","application/json")
                    .header("accept","application/json")
                    .header(TestAuthController.AUTH_HEADER,TestAuthController.jwt)
                    .body(requestBody)
                    .get("/akses");

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
            Assert.assertEquals(Long.parseLong(jsonPath.getString("data.total_data")),aksesRepo.count());
        }catch (Exception e){
            Assert.assertNotNull(null,"Exception "+e.getCause()+" Message "+e.getMessage());
        }
    }

    @Test(priority = 40)
    public void findByParam(){
        initObject(akses);
        String sort = "asc";
        String sortBy = "id";
        Integer page = 0;
        Integer pageSize = 10;
        requestBody.clear();
        requestBody.put("nama",akses.getNama());
        try{
            Response response = given()
                    .header("Content-Type","application/json")
                    .header("accept","application/json")
                    .header(TestAuthController.AUTH_HEADER,TestAuthController.jwt)
                    .queryParam("size",pageSize)
                    .body(requestBody)
                    .post("/akses/v1/"+sort+"/"+sortBy+"/"+page);

            int responseCode = response.getStatusCode();
            JsonPath jsonPath = response.getBody().jsonPath();
            jsonPath.prettyPrint();
            List<Map<String,Object>> ltData = jsonPath.getList("data.content");
            Map<String,Object> map = ltData.get(0);
            Assert.assertEquals(responseCode,200,"Error Response code ");
            Assert.assertEquals(jsonPath.getInt("status"),200,"Error Response status ");
            Assert.assertEquals(jsonPath.getString("message"), ConstantMessage.OK,"Error Response message ");
            Assert.assertEquals(map.get("nama"),akses.getNama(),"Error Response data nama ");
            Assert.assertEquals(map.get("deskripsi"),akses.getDeskripsi(),"Error Response data deskripsi ");
            Assert.assertTrue(jsonPath.getBoolean("success"),"Error Response success ");
            Assert.assertNotNull(jsonPath.getString("timestamp"),"Error Response timestamp ");
            //=====================================DATA PAGING=============================================
            Assert.assertEquals(jsonPath.getString("data.sort_by"),"id");
            Assert.assertEquals(jsonPath.getString("data.filter.nama"),akses.getNama());
            Assert.assertEquals(Integer.parseInt(jsonPath.getString("data.current_page")),0);
            Assert.assertNotNull(jsonPath.getString("data.total_pages"));
            Assert.assertEquals(jsonPath.getString("data.sort"),"asc");
            Assert.assertEquals(Integer.parseInt(jsonPath.getString("data.size_per_page")), pageSize);
            Assert.assertEquals(Long.parseLong(jsonPath.getString("data.total_data")),1L);
        }catch (Exception e){
            Assert.assertNotNull(null,"Exception "+e.getCause()+" Message "+e.getMessage());
        }
    }

    @Test(priority = 99)
    public void delete(){
        try{
            initObject(akses);
            requestBody.clear();
            Response response = given()
                    .header("Content-Type","application/json")
                    .header("accept","application/json")
                    .header(TestAuthController.AUTH_HEADER,TestAuthController.jwt)
                    .body(requestBody)
                    .delete("/akses/v1/"+akses.getId());

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

    private void initObject(Akses akses){
        if(akses==null){
            akses = aksesRepo.findTop1ByOrderByIdDesc().orElse(null);
        }
        this.akses=akses;
    }

}