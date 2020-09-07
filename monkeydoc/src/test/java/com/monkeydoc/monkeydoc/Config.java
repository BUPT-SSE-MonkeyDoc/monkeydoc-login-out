package com.monkeydoc.monkeydoc;

import com.monkey.entity.base.BaseEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.lang.reflect.Field;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class Config {
    @Before
    public void init() {
        System.out.println("开始测试");
    }
    @After
    public void after() {
        System.out.println("测试结束");
    }

    public boolean equal(BaseEntity baseEntity1, BaseEntity baseEntity2)
    {
        Class clazz = baseEntity1.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for(int i = 0; i < fields.length; ++i) {
                fields[i].setAccessible(true);
                if(fields[i].get(baseEntity1) == null && fields[i].get(baseEntity1) == null)
                    continue;
                if(fields[i].getType() == Date.class)
                    continue;
                if(!fields[i].get(baseEntity1).equals(fields[i].get(baseEntity2)))
                    return false;
            }
        } catch (IllegalAccessException var5) {
            var5.printStackTrace();
        }
        return true;
    }
}
