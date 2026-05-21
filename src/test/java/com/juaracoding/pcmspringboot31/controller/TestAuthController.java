package com.juaracoding.pcmspringboot31.controller;


import com.juaracoding.pcmspringboot31.util.ConstantMessage;
import com.juaracoding.pcmspringboot31.util.GenerateData;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestAuthController extends AbstractTestNGSpringContextTests {

    public static GenerateData generateData;
    public static String jwt = "";
    private String email;
    private String username;
    private String password;
    private String otp;
    private JSONObject requestBody;
    public static final String ADMIN_USERNAME = "admin.123";
    public static final String ADMIN_PASSWORD = "Admin@123";
    public static final String TEST_HOST = "http://localhost:8081/api";

    //1x request regis under 500ms / 0.5s throughput
    @BeforeSuite
    public void initSuite(){
        RestAssured.baseURI = TEST_HOST;
        generateData = new GenerateData();
    }
    @BeforeClass
    public void initClass(){
        requestBody = new JSONObject();
    }

    @Test(priority = 2)
    public void regis(){
        try{
            email = generateData.generateEmail();
            username = generateData.genDataLowerCase(6,12)+"."+generateData.randomInt(999);
            password = generateData.genDataAlfabet(6,11)+generateData.getSymbol()+generateData.randomInt(999);
            requestBody.put("username",username);
            requestBody.put("password",password);
            requestBody.put("email", email);
            requestBody.put("no_hp", "08"+generateData.randomInt(100000000,999999999));
            requestBody.put("nama_lengkap",generateData.genDataLowerCase(4,75));
            requestBody.put("tanggal_lahir",generateData.generateTanggalLahir());

            Response response = given()
                    .header("Content-Type","application/json")
                    .body(requestBody)
                    .request(Method.POST,"/auth/regis");
            int responseCode = response.getStatusCode();
            JsonPath jsonPath = response.jsonPath();
//            jsonPath.prettyPrint();
            otp =  jsonPath.getString("data.otp");
            Assert.assertEquals(responseCode,201,"Error Response code ");
            Assert.assertEquals(jsonPath.getInt("status"),201,"Error Response status ");
            Assert.assertEquals(jsonPath.getString("message"), ConstantMessage.SUCCESS_SAVE,"Error Response status ");
            Assert.assertTrue(jsonPath.getBoolean("success"),"Error Response success ");
            Assert.assertNotNull(jsonPath.getString("timestamp"),"Error Response timestamp ");
        }catch (Exception e){
            Assert.assertNotNull(null,"Error exception TestAuthController - regis : "+e.getMessage());
        }
    }

    @Test(priority = 10)
    public void verifyRegis(){
        requestBody.clear();
        requestBody.put("email",email);
        requestBody.put("otp",otp);
        Response response = given()
                .header("Content-Type","application/json")
                .body(requestBody)
                .request(Method.POST,"/auth/verify_regis");
        int responseCode = response.getStatusCode();
        JsonPath jsonPath = response.jsonPath();
//        jsonPath.prettyPrint();
        Assert.assertEquals(responseCode,200,"Error Response code ");
        Assert.assertEquals(jsonPath.getInt("status"),200,"Error Response status ");
        Assert.assertEquals(jsonPath.getString("data"),"","Error Response data ");
        Assert.assertEquals(jsonPath.getString("message"), ConstantMessage.SUCCESS_REGIS,"Error Response status ");
        Assert.assertTrue(jsonPath.getBoolean("success"),"Error Response success ");
        Assert.assertNotNull(jsonPath.getString("timestamp"),"Error Response timestamp ");
    }

    @Test(priority = 20)
    public void login(){
        requestBody.clear();
        requestBody.put("username",username);
        requestBody.put("password",password);
        Response response = given()
                .header("Content-Type","application/json")
                .body(requestBody)
                .request(Method.POST,"/auth/login");
        int responseCode = response.getStatusCode();
        JsonPath jsonPath = response.jsonPath();
//        jsonPath.prettyPrint();
        Assert.assertEquals(responseCode,200,"Error Response code ");
        Assert.assertEquals(jsonPath.getInt("status"),200,"Error Response status ");
        Assert.assertNotNull(jsonPath.getString("data.token"));
        Assert.assertEquals(jsonPath.getString("message"), ConstantMessage.SUCCESS_LOGIN,"Error Response status ");
        Assert.assertTrue(jsonPath.getBoolean("success"),"Error Response success ");
        Assert.assertNotNull(jsonPath.getString("timestamp"),"Error Response timestamp ");
    }

    @Test(priority = 1)
    public void loginAdmin(){
        requestBody.clear();
        requestBody.put("username",ADMIN_USERNAME);
        requestBody.put("password",ADMIN_PASSWORD);
        Response response = given()
                .header("Content-Type","application/json")
                .body(requestBody)
                .request(Method.POST,"/auth/login");
        int responseCode = response.getStatusCode();
        JsonPath jsonPath = response.jsonPath();
        jsonPath.prettyPrint();
        jwt = "Bearer "+jsonPath.getString("data.token");
        System.out.println("JWT BRAY : "+jwt);
        Assert.assertEquals(responseCode,200,"Error Response code ");
        Assert.assertEquals(jsonPath.getInt("status"),200,"Error Response status ");
        Assert.assertNotNull(jsonPath.getString("data.token"));
        Assert.assertEquals(jsonPath.getString("message"), ConstantMessage.SUCCESS_LOGIN,"Error Response status ");
        Assert.assertTrue(jsonPath.getBoolean("success"),"Error Response success ");
        Assert.assertNotNull(jsonPath.getString("timestamp"),"Error Response timestamp ");
    }
}