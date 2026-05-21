package com.juaracoding.pcmspringboot31.service;

import com.juaracoding.pcmspringboot31.config.JwtConfig;
import com.juaracoding.pcmspringboot31.config.OtherConfig;
import com.juaracoding.pcmspringboot31.core.SMTPCore;
import com.juaracoding.pcmspringboot31.dto.report.ReportAksesDTO;
import com.juaracoding.pcmspringboot31.dto.report.ReportMenuDTO;
import com.juaracoding.pcmspringboot31.dto.validation.LoginDTO;
import com.juaracoding.pcmspringboot31.dto.validation.RegisDTO;
import com.juaracoding.pcmspringboot31.dto.validation.VerifyRegisDTO;
import com.juaracoding.pcmspringboot31.handler.ResponseHandler;
import com.juaracoding.pcmspringboot31.model.Akses;
import com.juaracoding.pcmspringboot31.model.AksesMenu;
import com.juaracoding.pcmspringboot31.model.Menu;
import com.juaracoding.pcmspringboot31.model.User;
import com.juaracoding.pcmspringboot31.repo.UserRepo;
import com.juaracoding.pcmspringboot31.security.BcryptImpl;
import com.juaracoding.pcmspringboot31.security.CryptoJwt;
import com.juaracoding.pcmspringboot31.security.JwtUtility;
import com.juaracoding.pcmspringboot31.util.ConstantMessage;
import com.juaracoding.pcmspringboot31.util.EmailTemplate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * modul code : 00
 */
@Service
@Transactional
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private Random random;

    @Autowired
    private ModelMapper modelMapper ;

    @Autowired
    private JwtUtility jwtUtility;
    /**
     * Login
     * Regis
     * Lupa Password
     */

    public ResponseEntity<Object> regis(User user, HttpServletRequest request){
        if(user==null){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.FAILED_REGIS, HttpStatus.BAD_REQUEST,null,"USM00001",request);
        }
    /**
     *
     */
     String otp = String.valueOf(random.nextInt(10))+String.valueOf(random.nextInt(10000,99999));
//     String otp = "241223";
     user.setOtp(BcryptImpl.hash(user.getUsername()+otp));
     user.setPassword(BcryptImpl.hash(user.getUsername()+user.getPassword()));
//    user.setOtp(otp);
//    user.setPassword(user.getPassword());
     user.setIsRegistered(false);
     user.setCreatedBy("{\"id\":\"1\",\"nama\":\"System\"}");
     Akses akses = new Akses();
     akses.setId(2L);
     user.setAkses(akses);
        new Thread(new Runnable() {
            @Override
            public void run() {
                new SMTPCore().sendMailWithAttachment(new String[]{user.getEmail()},
                        "OTP Registrasi",
                        EmailTemplate.sentOtp(user.getNamaLengkap(),"Registrasi",otp),
                        "TLS",null);
            }
        }).start();
        userRepo.save(user);
        Map<String,Object> mapResponse = new HashMap<>();
        mapResponse.put("email",user.getEmail());
        if(OtherConfig.getEnableAutomationTesting().equals("y")){
            mapResponse.put("otp",otp);
        }
        return new ResponseHandler().
                handleResponse(ConstantMessage.SUCCESS_SAVE, HttpStatus.CREATED,mapResponse,null,request);
    }
    public ResponseEntity<Object> verifyRegis(User user, HttpServletRequest request){
        if(user==null){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.FAILED_REGIS, HttpStatus.BAD_REQUEST,null,"USM00021",request);
        }
        Optional<User> optionalUser = userRepo.findByEmail(user.getEmail());
        if(optionalUser.isEmpty()){
            throw new UsernameNotFoundException(ConstantMessage.USER_NOT_FOUND);
        }

        User userDB = optionalUser.get();
        if(!BcryptImpl.verifyHash(userDB.getUsername()+user.getOtp(),userDB.getOtp())){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.OTP_SALAH, HttpStatus.BAD_REQUEST,null,"USM00022",request);
        }
        userDB.setIsRegistered(true);
        userDB.setOtp("123");
        userDB.setUpdatedBy("{\"id\":\""+userDB.getId()+"\",\"nama\":\""+userDB.getNamaLengkap()+"\"}");
        return new ResponseHandler().
                handleResponse(ConstantMessage.SUCCESS_REGIS, HttpStatus.OK,null,null,request);
    }
    public ResponseEntity<Object> login(User user, HttpServletRequest request){
        if(user==null){
            return new ResponseHandler().
                    handleResponse(ConstantMessage.FAILED_REGIS, HttpStatus.BAD_REQUEST,null,"USM00021",request);
        }
        Optional<User> optionalUser = userRepo.findByUsernameOrEmailOrNoHp(user.getUsername(),user.getUsername(),user.getUsername());
        if(optionalUser.isEmpty()){
            throw new UsernameNotFoundException(ConstantMessage.USER_PWD_SALAH);
        }
        User userDb = optionalUser.get();
        if(!BcryptImpl.verifyHash(userDb.getUsername()+user.getPassword(),userDb.getPassword())){
            throw new UsernameNotFoundException(ConstantMessage.USER_PWD_SALAH);
        }

        /** PAYLOAD */
        Map<String,Object> claims = new HashMap<>();
        claims.put("id",userDb.getId());
        claims.put("na_leng",userDb.getNamaLengkap());
        claims.put("akses",userDb.getAkses().getNama());
        claims.put("email",userDb.getEmail());
        claims.put("no_hp",userDb.getNoHp());

        String token = jwtUtility.doGenerateToken(claims,userDb.getUsername());
        if(JwtConfig.getTokenEncryptEnable().equals("y")){
            token = CryptoJwt.performEncrypt(token);
        }
        List<AksesMenu>  aksesMenus = userDb.getAkses().getAksesMenuList();
        List<Menu> listMenu =  new ArrayList<>();
        for( AksesMenu aksesMenu : aksesMenus){
            Menu m = new Menu();
            m.setId(aksesMenu.getId());
            m.setPath(aksesMenu.getMenu().getPath());
            m.setNama(aksesMenu.getMenu().getNama());
            listMenu.add(m);
        }

        Map<String,Object> mapResponse = new HashMap<>();
        mapResponse.put("menu",modelMapper.map(listMenu,new TypeToken<List<ReportMenuDTO>>() {}.getType()));
        mapResponse.put("token",token);
        mapResponse.put("username",userDb.getUsername());
        mapResponse.put("nama_lengkap",userDb.getNamaLengkap());
        mapResponse.put("role",userDb.getAkses().getNama());

        return new ResponseHandler().
                handleResponse(ConstantMessage.SUCCESS_LOGIN, HttpStatus.OK,mapResponse,null,request);
    }

    /**
     * method ini digunakan untuk validasi username dari token jwt
     * @param username the username identifying the user whose data is required.
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepo.findByUsernameWithAkses(username);
        if(userOptional.isEmpty()){
            throw new UsernameNotFoundException("User Tidak Ditemukan");
        }
        User user = userOptional.get();
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),user.getAuthorities());
    }

    public User mapToEntity(RegisDTO regisDTO){
        return modelMapper.map(regisDTO,User.class);
    }
    public User mapToEntity(VerifyRegisDTO verifyRegisDTO){
        return modelMapper.map(verifyRegisDTO,User.class);
    }
    public User mapToEntity(LoginDTO loginDTO){
        return modelMapper.map(loginDTO,User.class);
    }
}