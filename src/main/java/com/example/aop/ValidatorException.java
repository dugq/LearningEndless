package com.example.aop;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * Created by dugq on 2017/7/12.
 */
public class ValidatorException extends ConstraintViolationException {

    private static final long serialVersionUID = -320962785183947955L;


    public ValidatorException(String message, Set<? extends ConstraintViolation<?>> constraintViolations) {
        super(message, constraintViolations);
    }
}
