package test.autovalue;

import com.example.myproduct.app.demo.autovalue.MyGsonTypeAdapterFactory;
import com.example.myproduct.app.demo.autovalue.NullableUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author lihanguang
 * @date 2017/5/4 9:32:52
 */
public class NullableUserTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test(expected = Test.None.class)
    public void testUserNullPointException() throws Exception {
        NullableUser.create(100, null);
    }

    @Test
    public void testUserNullable() {
        NullableUser user = NullableUser.create(100, "test");

        System.out.println("user = " + user);
        Assert.assertEquals(user.id(), 100);
        Assert.assertEquals(user.name(), "test");
    }

    @Test
    public void testUserParseFromJson() {
        String json = "{\"id\":100,\"name\":\"test\"}";

        // 自定义的Gson对象，需要配置 MyAdapterFactory
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create()).create();

        NullableUser nullableUser = gson.fromJson(json, NullableUser.class);
        System.out.println(nullableUser);
        Assert.assertNotNull(nullableUser);
        Assert.assertEquals(nullableUser.name(), "test");
        Assert.assertEquals(nullableUser.id(), 100);
    }
}