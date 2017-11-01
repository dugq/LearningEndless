package com.example.shiro;

import com.example.service.OperationsService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Created by dugq on 2017/11/1.
 */
public class MyPermissionsAuthorizationFilter extends PermissionsAuthorizationFilter {

    @Override
    public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return super.onPreHandle(request, response, mappedValue);
    }

    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        String str = httpServletRequest.getRequestURI();
        List<String> strings = null;
//        List<String> strings = "operationsService.selectPermsListByUrl(str)";
        String[] perms = strings.toArray(new String[]{});
        boolean isPermitted = true;
        if (perms != null && perms.length > 0) {
            if (perms.length == 1) {
                if (!subject.isPermitted(perms[0])) {
                    isPermitted = false;
                }
            } else {
                if (!subject.isPermittedAll(perms)) {
                    isPermitted = false;
                }
            }
        }


        return isPermitted;
    }


}
