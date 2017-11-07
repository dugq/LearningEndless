package com.example.shiro;

import com.example.pojo.entry.Module;
import com.example.pojo.entry.Role;
import com.example.pojo.entry.User;
import com.example.service.OperationsService;
import com.example.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by dugq on 2017/10/26.
 */
public class AuthRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;
    @Autowired
    private OperationsService operationsService;


    //认证.登录
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken utoken=(UsernamePasswordToken) token;//获取用户输入的token
        String username = utoken.getUsername();
        User user = userService.selectByName(username);
        if(Objects.isNull(user))
           return null;
        return new SimpleAuthenticationInfo(user, user.getPassword(),getName());//放入shiro.调用CredentialsMatcher检验密码
    }


    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
        User user=(User) principal.getPrimaryPrincipal();//获取session中的用户
        List<String> strings = operationsService.selectPermsListByUser(user.getUid());
        SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
        info.addStringPermissions(strings);//将权限放入shiro中.
        return info;
    }

}