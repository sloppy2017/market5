package com.c2b.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;

import com.c2b.wallet.controller.AjaxResponse;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 */
@ControllerAdvice
public class GlobalExceptionController {

    Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private CoreMessageSource messageSource;
    
    @ExceptionHandler(IOException.class)
    @ResponseBody
    public AjaxResponse handleIOException(IOException e) {
        
        log.info("服务器io异常: " + e.getMessage(),e);

        return AjaxResponse.falied(messageSource.getMessage("ioException"));

    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public AjaxResponse handleServiceException(RuntimeException e) {
        
        log.info("服务器RuntimeException: " + e.getMessage(),e);

        return AjaxResponse.falied(messageSource.getMessage("runTimeException"));

    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public AjaxResponse handleException(Exception e) {
        
        log.info("服务器Exception: " + e.getMessage(),e);

        return AjaxResponse.falied(messageSource.getMessage("exception"));

    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public AjaxResponse handleException(MissingServletRequestParameterException e) {
        
        log.info("服务器MissingServletRequestParameterException: " + e.getMessage(),e);

        return AjaxResponse.falied(messageSource.getMessage("exception"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public AjaxResponse processValidationError(MethodArgumentNotValidException e) {
        
        BindingResult bindingResult = e.getBindingResult();
        if (bindingResult != null && bindingResult.hasErrors()) {
            for(FieldError error : bindingResult.getFieldErrors()){
                //当多个验证同时成立时
                StringBuilder errorMsg = new StringBuilder();
                for (FieldError er : bindingResult.getFieldErrors(error.getField())) {
                    errorMsg.append(er.getDefaultMessage()+"，");
                }
                
                log.info("服务器MethodArgumentNotValidException: " 
                        + errorMsg.deleteCharAt(errorMsg.length() -1).toString());
                
                if(errorMsg.toString().endsWith("，")){
                    errorMsg.deleteCharAt(errorMsg.length() -1).toString();
                }
                
                return AjaxResponse.falied(errorMsg.toString());
            }
        }
        log.info("服务器MethodArgumentNotValidException: " 
                + e.getMessage(), e);
        return AjaxResponse.falied(e.getMessage());
    }
    
    /* 处理上传文件异常 */
    @ExceptionHandler(value = {MultipartException.class})
    @ResponseBody
    public AjaxResponse handleMultipartException(MultipartException e) {
        
        log.info("服务器MultipartException: " 
                + e.getMessage(), e );
        
        return AjaxResponse.falied(messageSource.getMessage("uploadException"));
    }

//    /* 未获取session访问后台url */
//    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
//    @ResponseBody
//    public ResultDto<Void> handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException e) {
//        e.printStackTrace();
//        return new ResultDto<>("4300", "session过期或您的账号已在其他地方登录，请及时修改密码");
//
//    }
    

}
