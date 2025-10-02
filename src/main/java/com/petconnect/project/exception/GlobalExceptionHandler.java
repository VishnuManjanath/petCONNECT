package com.petconnect.project.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {} at {}", ex.getMessage(), request.getRequestURI());
        
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("error", "Resource Not Found");
        mav.addObject("message", ex.getMessage());
        mav.addObject("timestamp", LocalDateTime.now());
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied: {} at {}", ex.getMessage(), request.getRequestURI());
        
        ModelAndView mav = new ModelAndView("error/403");
        mav.addObject("error", "Access Denied");
        mav.addObject("message", "You don't have permission to access this resource.");
        mav.addObject("timestamp", LocalDateTime.now());
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        log.warn("No handler found for: {}", request.getRequestURI());
        
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("error", "Page Not Found");
        mav.addObject("message", "The page you're looking for doesn't exist.");
        mav.addObject("timestamp", LocalDateTime.now());
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.error("Data integrity violation: {} at {}", ex.getMessage(), request.getRequestURI());
        
        ModelAndView mav = new ModelAndView("error/400");
        mav.addObject("error", "Data Integrity Error");
        mav.addObject("message", "The operation could not be completed due to data constraints.");
        mav.addObject("timestamp", LocalDateTime.now());
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Illegal argument: {} at {}", ex.getMessage(), request.getRequestURI());
        
        ModelAndView mav = new ModelAndView("error/400");
        mav.addObject("error", "Invalid Request");
        mav.addObject("message", ex.getMessage());
        mav.addObject("timestamp", LocalDateTime.now());
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ModelAndView handleUnauthorizedException(UnauthorizedException ex, HttpServletRequest request) {
        log.warn("Unauthorized access: {} at {}", ex.getMessage(), request.getRequestURI());
        
        ModelAndView mav = new ModelAndView("error/401");
        mav.addObject("error", "Unauthorized");
        mav.addObject("message", ex.getMessage());
        mav.addObject("timestamp", LocalDateTime.now());
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: {} at {}", ex.getMessage(), request.getRequestURI(), ex);
        
        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("error", "Internal Server Error");
        mav.addObject("message", "An unexpected error occurred. Please try again later.");
        mav.addObject("timestamp", LocalDateTime.now());
        mav.addObject("path", request.getRequestURI());
        
        // In development, show the actual error message
        if (isDevelopmentMode()) {
            mav.addObject("debugMessage", ex.getMessage());
            mav.addObject("stackTrace", getStackTraceAsString(ex));
        }
        
        return mav;
    }

    private boolean isDevelopmentMode() {
        // Check if we're in development mode
        String activeProfile = System.getProperty("spring.profiles.active", "");
        return activeProfile.contains("dev") || activeProfile.contains("local");
    }

    private String getStackTraceAsString(Exception ex) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }
}
