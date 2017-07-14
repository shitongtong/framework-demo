package cn.stt.jersey.helloworld;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/4/27.
 */
public class HelloWorld {
    String name;

    public HelloWorld(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
