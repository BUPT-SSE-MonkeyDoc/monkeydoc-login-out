package com.monkeydoc.action;

import com.google.gson.Gson;
import com.monkeydoc.Bean.TokenBean;
import com.monkeydoc.Bean.UserBean;
import com.monkeydoc.Service.TokenService;
import com.monkeydoc.Service.UserService;
import com.monkeydoc.token.TokenProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@Controller
public class UserLoginController {


    //将Service注入Web层
    @Autowired
    UserService userService;
    @Autowired
    TokenService tokenService;
    @CrossOrigin(origins = "*")
    @RequestMapping(value ="/login",method = RequestMethod.POST)
    public String login() throws IOException {
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
        String loginfor= (String) map.get("tel");
        String password=(String)map.get("password");
        String resu="";
        if(usrtoken==null||usrtoken.equals("")) {
            UserBean userBean=null;
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
            if (userBean==null) {
                response.setHeader("responsemsg", "user_does_not_exists");
            } else {
                if (userBean.getPassword().equals(password)){
                    String userid=userBean.getId();
                    TokenBean tokenBean=tokenService.tokenbyuserid(userid);
                    String token = TokenProcessor.getInstance().makeToken();
                    TokenBean t=new TokenBean();
                    t.setToken(token);
                    t.setUserid(userid);
                    if(tokenBean!=null)
                        tokenService.changetoken(token,userid);
                    else
                        tokenService.storetoken(t);
                    response.setHeader("userid",String.valueOf(userid));
                    response.setHeader("responsemsg", token);
                }
                else{
                    response.setHeader("responsemsg", "psw_is_wrong");
                }
            }
        }
        else {
            TokenBean tokenBean=tokenService.loginbytoken(usrtoken);
            if (tokenBean==null) {
                return null;
            }
            else {
                String userid=tokenBean.getId();
                response.setHeader("responsemsg", "login_succeed");
                response.setHeader("userid",String.valueOf(userid));
            }
        }
        return null;
    }

}
