package vn.com.fwd.importtool.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandlerAdvice {
	@ResponseStatus(code = HttpStatus.OK, reason = "Error, unknown error")
    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) throws Exception {
        log.error(ex.getMessage());
        System.out.println(ex.getMessage());
//        String str = ex.getMessage();
//        if (str.equalsIgnoreCase("Bạn chưa có quyền vào trang này !")) {
        	throw ex;
//        }
//        model.addAttribute("messageExceptionHHHH", str);
//        return "error";
    }
}
