package com.feiliu.flgamesdk.bean.netresultbean;

/**
 * Created by Administrator on 2016/11/23.
 */
public class RegisterResultJsonBean {

    /**
     * actionId : 4000
     * result : {"code":"0","tips":"成功"}
     * userInfo : {"firstLogin":1,"giftStatus":0,"nickName":"18500319748","password":"004876","phone":"18500319748","sign":{"code":"jDKntbNuwORZINWIy8JDL2JStxDvAyvngw/XZY6oXgfOPOwWYSWOCYzvNZC5Wsw/dti25G9y0SQeb+V6aB4beHX0GgiuR+s5rVYPq+BwtCQAceENGjQPvzcPPfdTC2F1d2tF0oijRzE4HrcnetlP32RK8YarIJf+GjR3Qae68o3Fm0srMR5zF1aJ4IrF0NKaZdWZbxBqggMcOlLpdcbRnGiy5ngaDEa4mqhJk8sAIGEXCVu95iVyYos8izNyfBJx4sp7swgruR6VsQdjyhFMmZg6IOBptXhz5lwHako/vg15O9a/SME0iCuggJn07VtD+I/fSXsjlW6OpdS/RJq5sQ==","currentTimeSeconds":1479890645},"userId":739216,"userName":"18500319748","userType":1}
     */

    private String actionId;
    /**
     * code : 0
     * tips : 成功
     */

    private ResultBean result;
    /**
     * firstLogin : 1
     * giftStatus : 0
     * nickName : 18500319748
     * password : 004876
     * phone : 18500319748
     * sign : {"code":"jDKntbNuwORZINWIy8JDL2JStxDvAyvngw/XZY6oXgfOPOwWYSWOCYzvNZC5Wsw/dti25G9y0SQeb+V6aB4beHX0GgiuR+s5rVYPq+BwtCQAceENGjQPvzcPPfdTC2F1d2tF0oijRzE4HrcnetlP32RK8YarIJf+GjR3Qae68o3Fm0srMR5zF1aJ4IrF0NKaZdWZbxBqggMcOlLpdcbRnGiy5ngaDEa4mqhJk8sAIGEXCVu95iVyYos8izNyfBJx4sp7swgruR6VsQdjyhFMmZg6IOBptXhz5lwHako/vg15O9a/SME0iCuggJn07VtD+I/fSXsjlW6OpdS/RJq5sQ==","currentTimeSeconds":1479890645}
     * userId : 739216
     * userName : 18500319748
     * userType : 1
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
        private int firstLogin;
        private int giftStatus;
        private String nickName;
        private String password;
        private String phone;
        /**
         * code : jDKntbNuwORZINWIy8JDL2JStxDvAyvngw/XZY6oXgfOPOwWYSWOCYzvNZC5Wsw/dti25G9y0SQeb+V6aB4beHX0GgiuR+s5rVYPq+BwtCQAceENGjQPvzcPPfdTC2F1d2tF0oijRzE4HrcnetlP32RK8YarIJf+GjR3Qae68o3Fm0srMR5zF1aJ4IrF0NKaZdWZbxBqggMcOlLpdcbRnGiy5ngaDEa4mqhJk8sAIGEXCVu95iVyYos8izNyfBJx4sp7swgruR6VsQdjyhFMmZg6IOBptXhz5lwHako/vg15O9a/SME0iCuggJn07VtD+I/fSXsjlW6OpdS/RJq5sQ==
         * currentTimeSeconds : 1479890645
         */

        private SignBean sign;
        private int userId;
        private String userName;
        private int userType;

        public int getFirstLogin() {
            return firstLogin;
        }

        public void setFirstLogin(int firstLogin) {
            this.firstLogin = firstLogin;
        }

        public int getGiftStatus() {
            return giftStatus;
        }

        public void setGiftStatus(int giftStatus) {
            this.giftStatus = giftStatus;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public SignBean getSign() {
            return sign;
        }

        public void setSign(SignBean sign) {
            this.sign = sign;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public int getUserType() {
            return userType;
        }

        public void setUserType(int userType) {
            this.userType = userType;
        }

        public static class SignBean {
            private String code;
            private int currentTimeSeconds;

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public int getCurrentTimeSeconds() {
                return currentTimeSeconds;
            }

            public void setCurrentTimeSeconds(int currentTimeSeconds) {
                this.currentTimeSeconds = currentTimeSeconds;
            }
        }
    }
}
