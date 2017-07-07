package com.feiliu.flgamesdk.bean.netresultbean;

/**
 * Created by Administrator on 2016/11/29.
 */
public class ResetPasswordResultJsonBean {

    /**
     * actionId : 4011
     * result : {"code":"0","tips":"密码重置成功"}
     * userInfo : {"firstLogin":1,"nickName":"18500319748","password":"004876","phone":"18500319748","sign":{"code":"fz/WS50WrP+I9xsrA6mQ7mUASTgdfZVNKN6huHk4yneafP7EbSjUlkr9pQkP54ufSRG5lM3/QrV1CANdPOxnaNHhRec68VXTqjVFJd3jrnPmWQWaEacxpCQEetk87R9/tgiLMA9KLZhBx/fwJRQBHQAasr3ufOeTeD8GF6X4qqKeOMAVMu0dU7NgLqhQVyOBJw6hf4auI68LPve+GkRr949AUDYCd52Y1DUh2JdstXXfcEMGYBKG/GDP18yUpVulb4lIEpYlH+WQiFcma75ZFPaj8lqGLJUlrY1uV/TU0rBkbrZcv7SurP3hFHgR5pjdc6/o2KqNmQreh1CfCwmNhg==","currentTimeSeconds":1480391119},"userId":739216,"userName":"18500319748","userType":1}
     */

    private String actionId;
    /**
     * code : 0
     * tips : 密码重置成功
     */

    private ResultBean result;
    /**
     * firstLogin : 1
     * nickName : 18500319748
     * password : 004876
     * phone : 18500319748
     * sign : {"code":"fz/WS50WrP+I9xsrA6mQ7mUASTgdfZVNKN6huHk4yneafP7EbSjUlkr9pQkP54ufSRG5lM3/QrV1CANdPOxnaNHhRec68VXTqjVFJd3jrnPmWQWaEacxpCQEetk87R9/tgiLMA9KLZhBx/fwJRQBHQAasr3ufOeTeD8GF6X4qqKeOMAVMu0dU7NgLqhQVyOBJw6hf4auI68LPve+GkRr949AUDYCd52Y1DUh2JdstXXfcEMGYBKG/GDP18yUpVulb4lIEpYlH+WQiFcma75ZFPaj8lqGLJUlrY1uV/TU0rBkbrZcv7SurP3hFHgR5pjdc6/o2KqNmQreh1CfCwmNhg==","currentTimeSeconds":1480391119}
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
        private String nickName;
        private String password;
        private String phone;
        /**
         * code : fz/WS50WrP+I9xsrA6mQ7mUASTgdfZVNKN6huHk4yneafP7EbSjUlkr9pQkP54ufSRG5lM3/QrV1CANdPOxnaNHhRec68VXTqjVFJd3jrnPmWQWaEacxpCQEetk87R9/tgiLMA9KLZhBx/fwJRQBHQAasr3ufOeTeD8GF6X4qqKeOMAVMu0dU7NgLqhQVyOBJw6hf4auI68LPve+GkRr949AUDYCd52Y1DUh2JdstXXfcEMGYBKG/GDP18yUpVulb4lIEpYlH+WQiFcma75ZFPaj8lqGLJUlrY1uV/TU0rBkbrZcv7SurP3hFHgR5pjdc6/o2KqNmQreh1CfCwmNhg==
         * currentTimeSeconds : 1480391119
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
