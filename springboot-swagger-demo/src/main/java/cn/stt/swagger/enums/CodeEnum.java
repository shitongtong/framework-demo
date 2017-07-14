package cn.stt.swagger.enums;

public enum CodeEnum {

    SUCCESS(0, "成功"),
    FAILURE(1, "错误"),
    INVALID_TOKEN(112, "登录失效，请重新登录"),
    ALREADY_BING(113, "该学生账号已经绑定"),
    NO_BING(114, "没有绑定学生账号"),
    PARAMETER_ERROR(115, "参数不全"),
    INVALID_MOBILE(116, "该手机号码未注册"),
    INVALID_USERNAME_PWD(117, "密码不正确"),
    INVALID_OLD_USERNAME_PWD(118, "原密码不正确"),
    INVALID_FILEFORMAT(119, "文件格式不合规范"),
    INVALID_ACCOUNT(120, "无此账号"),
    NO_COURSE(121, "无此课程"),
    NO_WECHAT(122, "微信信息不存在"),
    NO_QQ(123, "qq信息不存在"),
    NO_SINAMICROBLOG(124, "新浪微博信息不存在"),
    BING_FAIL(125, "绑定失败"),
    IMUSER_NO_EXIST(126, "用户不存在"),
    INVALID_VERIFY_CODE(144, "无效验证码"),
    ROOM_NUMBER_FULL(300,"房间人数已满"),
    MOBILE_HAS_REGISTERED(301, "该手机号码已经注册"),
    SMS_SEND_FAIL(302, "验证码发送失败"),
    AUTHCODE_TIMEOUT(303, "验证码超时,请重新获取"),
    AUTHCODE_ERROR(304, "验证码输入错误"),
    INVALID_FILE(400, "上传文件不合法"),
    UNAUTHORIZED(401, "未授权"),
    SERRVER_ERROR(500, "服务器错误"),
    FILECONVERT_ERROR(501, "服务器出错");


    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    private CodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
