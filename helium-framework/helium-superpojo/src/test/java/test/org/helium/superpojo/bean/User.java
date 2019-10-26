package test.org.helium.superpojo.bean;

import com.alibaba.fastjson.JSONObject;
import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.generator.ObjectUtils;
import com.feinno.superpojo.util.JavaEval;
import com.feinno.superpojo.util.ServiceEnvironment;
import com.google.gson.Gson;

public class User extends SuperPojo {

    @Field(id = 1)
    private String name;

    @Field(id = 2)
    private int id;

    @Field(id = 3)
    private String email;
    private int a;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static void main(String[] args) {
		JavaEval.setSaveClassPath("/tmp/java/javaeval-" + ServiceEnvironment.getPid());
        User user = new User();
        int a = 0;
        ObjectUtils.isNull(a);
        System.out.println(JSONObject.toJSONString(user));
        System.out.println(user.toJsonObject().toString());
        System.out.println(user.toXmlString().toString());
        Gson gs = new Gson();
        System.out.println(gs.toJson(user));

    }



}
