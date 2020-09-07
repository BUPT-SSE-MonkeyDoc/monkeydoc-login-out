package com.monkeydoc.monkeydoc;

import com.monkeydoc.Bean.Message;
import com.monkeydoc.Bean.Responsemsg;
import com.monkeydoc.action.UserLoginController;
import com.monkeydoc.action.UserLogonController;
import com.monkeydoc.action.UserPswModifyController;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MonkeyDocLogTest extends Config {
    @Autowired
    private UserLoginController userLoginController;
    @Autowired
    private UserLogonController userLogonController;
    @Autowired
    private UserPswModifyController userPswModifyController;

    @Test
    public void LoginTest() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("tel","");
        params.put("email","6488967@qq.com");
        params.put("password","751603s");
        String header = "";
        Message message = new Message(params,header);
        Responsemsg response = userLoginController.login(message);
        assert response.getUserid().equals("121");
        assert !response.getResponsemsg().equals("psw_is_wrong");

        params.clear();
        params.put("tel","");
        params.put("email","6488967@qq.com");
        params.put("password","751603");
        message.setMap(params);
        Responsemsg response1 = userLoginController.login(message);
        assert response1.getResponsemsg().equals("psw_is_wrong");

        params.clear();
        params.put("tel","");
        params.put("email","123@qq.com");
        params.put("password","751603s");
        message.setMap(params);
        Responsemsg response2 = userLoginController.login(message);
        assert response2.getResponsemsg().equals("user_does_not_exists");
    }

    @Test
    public void LogonTest() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("tel","18076834876");
        params.put("email","JUnitTest@qq.com");
        params.put("password","123456");
        params.put("userName","JunitTest");
        String header = "";
        Message message = new Message(params,header);
        Responsemsg response = userLogonController.logon(message);
        assert response.getResponsemsg().equals("logon_succeed");
        assert userLogonController.logon(message).getResponsemsg().equals("both_have_been_logon");

        params.clear();
        params.put("tel","18618453558");
        params.put("email","JUnitTest111@qq.com");
        params.put("password","123456");
        params.put("userName","JunitTest");
        message.setMap(params);
        assert userLogonController.logon(message).getResponsemsg().equals("phone_has_been_logon");

        params.clear();
        params.put("tel","17512349789");
        params.put("email","JUnitTest@qq.com");
        params.put("password","123456");
        params.put("userName","JunitTest");
        message.setMap(params);
        assert userLogonController.logon(message).getResponsemsg().equals("email_has_been_logon");
    }

    @Test
    public void PswModifyTest() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("tel","");
        params.put("email","JUnitTest@qq.com");
        params.put("password","123456");
        params.put("newpassword","1234567");
        String header = "";
        Message message = new Message(params,header);
        Responsemsg response = userPswModifyController.pswmodify(message);
        assert response.getResponsemsg().equals("psw_reset_success");
        assert userPswModifyController.pswmodify(message).getResponsemsg().equals("psw_is_wrong");

        params.clear();
        params.put("tel","");
        params.put("email","dfhfxghgjgf@qq.com");
        params.put("password","123456");
        params.put("newpassword","1234567");
        message.setMap(params);
        assert userPswModifyController.pswmodify(message).getResponsemsg().equals("user_does_not_exists");
    }

    @Test
    public void tokenLoginTest() throws IOException {
        Map<String,String> params = new HashMap<String, String>();
        params.put("tel","");
        String header = "f2Ea8D4evgV89SBcthhpCA==";
        Message message = new Message(params, header);
        Responsemsg response = userLoginController.login(message);
        assert response.getResponsemsg().equals("login_succeed");
        assert response.getUserid().equals("96");

        header = "/T8WWZ1/F9IEvNg+U3Zanw==";
        message.setHeader(header);
        response = userLoginController.login(message);
        assert response.getResponsemsg().equals("token_expired") ;
    }
}
