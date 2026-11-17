
package com.example.demo.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.response.ResponseDTO;
import com.example.demo.response.ResponseErrors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO> handleValidationErrors(
            MethodArgumentNotValidException exception) {

        ResponseDTO responseDTO = new ResponseDTO();

        List<ResponseErrors> errors = new ArrayList<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(fieldError -> {

                    ResponseErrors error = new ResponseErrors();

                    error.setMessage(fieldError.getField());
                    error.setDetails(fieldError.getDefaultMessage());

                    errors.add(error);
                });

        responseDTO.setStatus(false);
        responseDTO.setStatusFlag(ResponseDTO.ERROR);
        responseDTO.setErrors(errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseDTO);
    }
    
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ResponseDTO> handleResponseStatusException(
            ResponseStatusException exception) {

        ResponseDTO responseDTO = new ResponseDTO();

        ResponseErrors error = new ResponseErrors();
        error.setMessage("Request Error");
        error.setDetails(exception.getReason());

        List<ResponseErrors> errors = new ArrayList<>();
        errors.add(error);

        responseDTO.setStatus(false);
        responseDTO.setStatusFlag(ResponseDTO.ERROR);
        responseDTO.setErrors(errors);

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(responseDTO);
    }
}
