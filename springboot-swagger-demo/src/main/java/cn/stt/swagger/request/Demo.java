package cn.stt.swagger.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by stt on 2017/7/6.
 */
@ApiModel()
public class Demo {
    @NotBlank(message = "name is not null")
    private String name;
    @ApiParam(name = "content" ,value = "content不可为空！", required = true)
    private String content;
    @NotBlank(message = "message不可为空！")
    @ApiModelProperty(name = "ss",value = "message不可为空!",required = true)
    private String message;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Demo{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

