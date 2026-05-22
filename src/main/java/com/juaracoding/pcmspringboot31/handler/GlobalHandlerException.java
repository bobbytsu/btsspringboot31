package com.juaracoding.pcmspringboot31.handler;

import com.juaracoding.pcmspringboot31.util.LoggingFile;
import com.juaracoding.pcmspringboot31.util.RequestCapture;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 1. data
 * 2. security
 * 3. media
 * 4. database
 * 5. 3rd party api
 * 6. OS
 * 7. other
 */
//work motion
//time motion
@RestControllerAdvice
@Configuration
public class GlobalHandlerException extends ResponseEntityExceptionHandler {

    @Nullable
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
//        return super.handleMethodArgumentNotValid(ex, headers, status, request);
        List<Map<String,Object>>  errors = new ArrayList<>();
        for ( FieldError fieldError : ex.getBindingResult().getFieldErrors()){
            Map<String,Object> m = Map.of(
                    "field",fieldError.getField(),
                    "default_message",fieldError.getDefaultMessage(),
                    "rejected_value",fieldError.getRejectedValue()
            );
            errors.add(m);
        }
        LoggingFile.logException(this.getClass().getName(),"handleMethodArgumentNotValid + Request "+ RequestCapture.allRequest(request),ex);
        return new ResponseHandler().handleResponse("Data Tidak Valid",
                HttpStatus.BAD_REQUEST,errors,"X01001",request);
    }

    @ExceptionHandler(value = ArithmeticException.class)
    protected ResponseEntity<Object> handleArithmetic(ArithmeticException e, HttpServletRequest request) {
        LoggingFile.logException(this.getClass().getName(),"handleArithmetic",e);
        return new ResponseHandler().handleResponse("Proses Gagal",
                HttpStatus.INTERNAL_SERVER_ERROR,null,"X01002",request);
    }
    @ExceptionHandler(value = IndexOutOfBoundsException.class)
    protected ResponseEntity<Object> handleIndexOutOfBound(IndexOutOfBoundsException e, HttpServletRequest request) {
        LoggingFile.logException(this.getClass().getName(),"handleIndexOutOfBound",e);
        return new ResponseHandler().handleResponse("Proses Gagal",
                HttpStatus.INTERNAL_SERVER_ERROR,null,"X01003",request);
    }
    @ExceptionHandler(value = NullPointerException.class)
    protected ResponseEntity<Object> handleNullPointer(NullPointerException e, HttpServletRequest request) {
        LoggingFile.logException(this.getClass().getName(),"handleNullPointer",e);
        return new ResponseHandler().handleResponse("Proses Gagal",
                HttpStatus.INTERNAL_SERVER_ERROR,null,"X01004",request);
    }
    @ExceptionHandler(value = UsernameNotFoundException.class)
    protected ResponseEntity<Object> handleUsernameNotFound(UsernameNotFoundException e, HttpServletRequest request) {
        LoggingFile.logException(this.getClass().getName(),"handleUsernameNotFound",e);
        return new ResponseHandler().handleResponse(e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,null,"X02001",request);
    }
    @ExceptionHandler(value = AccessDeniedException.class)
    protected ResponseEntity<Object> handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {
        System.out.println("Thread 403  : "+Thread.currentThread().getName());
        LoggingFile.logException(this.getClass().getName(),"handleAccessDenied",e);
        return new ResponseHandler().handleResponse(e.getMessage(),
                HttpStatus.FORBIDDEN,null,"X02002",request);
    }
    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<Object> handleException(Exception e, HttpServletRequest request) {
        //input ke file log Log4J2
        LoggingFile.logException(this.getClass().getName(),"handleException",e);
        return new ResponseHandler().handleResponse("Proses Gagal",
                HttpStatus.BAD_REQUEST,null,"X07001",request);
    }
}
