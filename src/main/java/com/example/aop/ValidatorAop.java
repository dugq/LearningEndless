package com.example.aop;

import com.example.pojo.annotation.MyValidator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;


/**
 * Created by dugq on 2017/7/12.
 */
@Aspect
@Configuration
public class ValidatorAop {
    @Autowired
    Validator validator;

    @Pointcut("execution(public * com.example.controller..*.*(..))")
    public void validate(){}

    @Before("validate()")
    public void validating(JoinPoint point)throws Throwable{
        Object[] args = point.getArgs();
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Parameter[] parameters = method.getParameters();
        for(int i = 0 ; i < parameters.length; i++){
            Parameter parameter = parameters[i];
            Object value = args[i];
            MyValidator annotation = parameter.getAnnotation(MyValidator.class);
            if(!Objects.isNull(annotation)){
                ValidatorByType(args, i, value, annotation);
            }
        }
    }



    private void ValidatorByType(Object[] args, int i, Object value, MyValidator annotation) {
        if(Map.class.isAssignableFrom(value.getClass())){
            Map<Object,Object> newValue = (Map<Object,Object>) value;
            for(Map.Entry<Object,Object> entry : newValue.entrySet()){
                Object value1 = entry.getValue();
                if(!value1.getClass().isPrimitive() && !(value1 instanceof String)){
                    validatedUseHibernateValidator(value1, annotation);
                }
            }
        }else if(Collection.class.isAssignableFrom(value.getClass())){
            Collection newValue = (Collection) value;
            Iterator iterator = newValue.iterator();
            while (iterator.hasNext()){
                validatedUseHibernateValidator(iterator.next(), annotation);
            }
        }else{
            validatedUseHibernateValidator(args[i], annotation);
        }
    }

    private void validatedUseHibernateValidator(Object value, MyValidator annotation) {
        Set<ConstraintViolation<Object>> validate = validator.validate(value, annotation.value());
        if(!CollectionUtils.isEmpty(validate)){
            throw new ConstraintViolationException(validate);
        }
    }

}
