package com.feiliu.flgamesdk.bean.netresultbean;

/**
 * Created by Administrator on 2016/12/12.
 */
public class ChangePasswordResultJsonBean {

    /**
     * actionId : 4010
     * result : {"code":"0","tips":"密码更新成功"}
     * userInfo : {"password":"004876","userName":"18500319748"}
     */

    private String actionId;
    /**
     * code : 0
     * tips : 密码更新成功
     */

    private ResultBean result;
    /**
     * password : 004876
     * userName : 18500319748
     */

    private UserInfoBean userInfo;

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public static class ResultBean {
        private String code;
        private String tips;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getTips() {
            return tips;
        }

        public void setTips(String tips) {
            this.tips = tips;
        }
    }

    public static class UserInfoBean {
        private String password;
        private String userName;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
