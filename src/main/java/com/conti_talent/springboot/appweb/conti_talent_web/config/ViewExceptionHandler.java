package com.conti_talent.springboot.appweb.conti_talent_web.config;

import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice(basePackages = "com.conti_talent.springboot.appweb.conti_talent_web.controller.view")
public class ViewExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ViewExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(ResourceNotFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("error/404");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        modelAndView.addObject("status", HttpStatus.NOT_FOUND.value());
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneric(Exception ex) {
        log.error("Error no controlado en vista MVC", ex);
        ModelAndView modelAndView = new ModelAndView("error/500");
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        modelAndView.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return modelAndView;
    }
}
