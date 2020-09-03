package com.monkeydoc.action;

import com.google.gson.Gson;
import com.monkeydoc.Bean.UserBean;
import com.monkeydoc.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@Controller
public class UserPswModifyController {
    @Autowired
    UserService userService;
    @CrossOrigin(origins = "*")
    @ResponseBody
    @RequestMapping(value ="/pswmodify",method = RequestMethod.POST)
    public String pswmodify() throws IOException {
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
        UserBean userBean=null;
        String loginfor= (String) map.get("tel");
        String password=(String)map.get("password");
        String newpsw=(String) map.get("newpassword");
        String resu="";
        if(map.get("tel").equals("")&&!map.get("email").equals("")) {
            loginfor = (String) map.get("email");
            resu="email";
        }
        else if(!map.get("tel").equals("")&&map.get("email").equals("")) {
            loginfor = (String) map.get("tel");
            resu="tel";
        }
        if(resu.equals("tel")){
            userBean = userService.loginbytel(loginfor);
        }
        else if(resu.equals("email")){
            userBean = userService.loginbyemail(loginfor);
        }
        if (userBean==null)
            response.setHeader("responsemsg","user_does_not_exists");
        else {
            if(userBean.getPassword().equals(password)){
                userService.changepsw(userBean.getId(),newpsw);
                response.setHeader("responsemsg","psw_reset_success");
            }
            else
                response.setHeader("responsemsg","psw_is_wrong");
        }
        return null;
    }
}
