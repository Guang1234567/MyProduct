package test.autovalue;

import com.example.myproduct.app.demo.autovalue.MyGsonTypeAdapterFactory;
import com.example.myproduct.app.demo.autovalue.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author lihanguang
 * @date 2017/5/4 20:03:54
 */
public class UserTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test(expected = NullPointerException.class)
    public void testUserNullPointException() throws Exception {
        User.create(100, null, new Date(0), "xxx123@gmail.com");
    }

    @Test
    public void testUserToJson() {
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create()).create();

        Date curTime = new Date();
        User user = User.create(100, "小强", curTime, "xxx123@gmail.com");

        String json = gson.toJson(user);
        System.out.println("#testUserToJson : " + json);

        DateFormat enUsFormat
                = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US);
        Assert.assertEquals("{\"id\":100,\"name\":\"小强\",\"timestamp\":\"" + enUsFormat.format(curTime) + "\",\"email\":\"xxx123@gmail.com\"}", json);
    }

    @Test
    public void testUserParseFromJson() {
        String json = "{\"id\":100,\"name\":\"test\"}";

        // 自定义的Gson对象，需要配置 MyAdapterFactory
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create()).create();

        User user = gson.fromJson(json, User.class);
        System.out.println("#testUserParseFromJson : " + user);
        Assert.assertNotNull(user);
        Assert.assertEquals(user.name(), "test");
        Assert.assertEquals(user.id(), 100);

        String json2 = "{\"id\":100,\"name\":\"\"}";
        User user2 = gson.fromJson(json2, User.class);
        Assert.assertEquals(user2.name(), "");

        String json3 = "{\"id\":100}";
        User user3 = gson.fromJson(json3, User.class);
        Assert.assertEquals(user3.name(), "小明");
    }
}