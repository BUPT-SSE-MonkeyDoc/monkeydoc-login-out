package com.monkeydoc.action;

import com.google.gson.Gson;
import com.monkeydoc.Bean.UserBean;
import com.monkeydoc.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@Controller
public class UserLogonController {
    @Autowired
    UserService userService;
    @CrossOrigin(origins = "*")
    @RequestMapping(value ="/logon",method = RequestMethod.POST)
    public String logon() throws IOException {
        HttpServletRequest request =((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response =((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
        String method = request.getMethod();
        response.setHeader("Access-Control-Allow-Origin","*");
        response.setHeader("Access-Control-Expose-Headers","responsemsg,token");
        response.setCharacterEncoding("UTF-8");
        BufferedReader streamReader = new BufferedReader( new InputStreamReader(request.getInputStream(), "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();
        String inputStr;
        String usrtoken = request.getHeader("token");
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);
        Gson gson=new Gson();
        Map<String ,Object> map = gson.fromJson(responseStrBuilder.toString(),Map.class);
        UserBean newuser=new UserBean((String) map.get("tel"),(String) map.get("email"),(String) map.get("userName"),(String) map.get("password"));
        UserBean u1=userService.loginbyemail((String) map.get("email"));
        UserBean u2=userService.loginbytel((String) map.get("tel"));
        if(u1!=null && u2!=null)
            response.setHeader("responsemsg","both_have_been_logon");
        else if(u1!=null)
            response.setHeader("responsemsg","email_has_been_logon");
        else if(u2!=null)
            response.setHeader("responsemsg","phone_has_been_logon");
        else if(u1==null && u2==null){
            userService.logonuser(newuser);
            response.setHeader("responsemsg","logon_succeed");
        }
        return null;
    }
}
