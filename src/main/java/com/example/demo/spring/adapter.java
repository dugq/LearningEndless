package com.example.demo.spring;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * Created by dugq on 2018/4/19.
 */
@Component
public class adapter extends AbstractHandlerMapping {

    @Override
    protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
        String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
        if(Objects.equals(lookupPath,"/test")){
         return Six.man;
        }else{
            return null;
        }
    }
}
