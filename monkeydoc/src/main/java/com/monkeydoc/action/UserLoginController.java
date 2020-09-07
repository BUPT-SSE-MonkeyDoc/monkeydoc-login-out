package com.monkeydoc.action;

import com.monkeydoc.Bean.Message;
import com.monkeydoc.Bean.Responsemsg;
import com.monkeydoc.Bean.TokenBean;
import com.monkeydoc.Bean.UserBean;
import com.monkeydoc.Service.TokenService;
import com.monkeydoc.Service.UserService;
import com.monkeydoc.tools.Security;
import com.monkeydoc.tools.TokenProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;

@Controller
public class UserLoginController {

    @Autowired
    UserService userService;
    @Autowired
    TokenService tokenService;
    @CrossOrigin(origins = "*")
    @ResponseBody
    @RequestMapping(value ="/login",method = RequestMethod.POST)
    public Responsemsg login(@RequestBody Message mes) throws IOException {
        Map<String ,Object> map = mes.getMap();
        String usrtoken=mes.getHeader();
        String loginfor= (String) map.get("tel");
        String password="";
        if (map.get("password")!=null) {
            password = Security.encryptPwd((String) map.get("password"));
        }
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
            else if(!map.get("tel").equals("")&&!map.get("email").equals("")) {
                return new Responsemsg("error","");
            }
            else if(map.get("tel").equals("")&&map.get("email").equals("")) {
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
                return new Responsemsg("user_does_not_exists","");
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
                    return new Responsemsg(token,userid);
                }
                else{
                    return new Responsemsg("psw_is_wrong","");
                }
            }
        }
        else {
            TokenBean tokenBean=tokenService.loginbytoken(usrtoken);
            if (tokenBean==null) {
                return new Responsemsg("","");
            }
            else {
                String userid=tokenBean.getUserid();
                Timestamp timestamp2=tokenBean.getTime_stamp();
                Date date = new Date(System.currentTimeMillis());
                Timestamp timestamp = new Timestamp(date.getTime());
                float m=(timestamp.getTime()-timestamp2.getTime()+13*1000*60*60)/1000/60;
                System.out.println(m);
                if(m>120){
                    return new Responsemsg("token_expired","");
                }
                else
                    return new Responsemsg("login_succeed",userid);
            }
        }
    }

}
