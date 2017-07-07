package com.feiliu.flgamesdk.bean.netresultbean;

/**
 * Created by Administrator on 2016/12/9.
 */
public class BindResultJsonBean {

    /**
     * actionId : 4001
     * result : {"code":"0","tips":"成功"}
     * userInfo : {"email":"15632714307@163.com","firstLogin":1,"nickName":"18500319748","phone":"18500319748","sign":{"code":"a02a0sGaka8SxND7BS1tZ6hSY2ittkwVDgXYoJS9Y5n1VxWUl0xUiMi2MmssTMhv44APJ79oD3LqMhGMh2e167brTpHaPrh5lPD7fh58KLNiKxi430NRg+K9DK7salwNVnqnKztib8gnLD5w3+ZeN1UYBpRwNPn6n4/fWQGDCeYjKBpq0TJE+Q7MKObxnjiwmP07WmEwgj6BFxZSAWRzshtsZyrUSlDgru4piAzTrtplv22BrHrbr5yPtUnfJVUCRNN2/JCEpyqIcZP756KN1maWDpVE/PR+KRomXaLV6PUcWkwOp2+D9ySI4Eg9RBkxMrHKrgRkJIuCcjODT+LkXw==","currentTimeSeconds":1481253087},"userId":739216,"userName":"18500319748","userType":1}
     */

    private String actionId;
    /**
     * code : 0
     * tips : 成功
     */

    private ResultBean result;
    /**
     * email : 15632714307@163.com
     * firstLogin : 1
     * nickName : 18500319748
     * phone : 18500319748
     * sign : {"code":"a02a0sGaka8SxND7BS1tZ6hSY2ittkwVDgXYoJS9Y5n1VxWUl0xUiMi2MmssTMhv44APJ79oD3LqMhGMh2e167brTpHaPrh5lPD7fh58KLNiKxi430NRg+K9DK7salwNVnqnKztib8gnLD5w3+ZeN1UYBpRwNPn6n4/fWQGDCeYjKBpq0TJE+Q7MKObxnjiwmP07WmEwgj6BFxZSAWRzshtsZyrUSlDgru4piAzTrtplv22BrHrbr5yPtUnfJVUCRNN2/JCEpyqIcZP756KN1maWDpVE/PR+KRomXaLV6PUcWkwOp2+D9ySI4Eg9RBkxMrHKrgRkJIuCcjODT+LkXw==","currentTimeSeconds":1481253087}
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
        private String email;
        private int firstLogin;
        private String nickName;
        private String phone;
        /**
         * code : a02a0sGaka8SxND7BS1tZ6hSY2ittkwVDgXYoJS9Y5n1VxWUl0xUiMi2MmssTMhv44APJ79oD3LqMhGMh2e167brTpHaPrh5lPD7fh58KLNiKxi430NRg+K9DK7salwNVnqnKztib8gnLD5w3+ZeN1UYBpRwNPn6n4/fWQGDCeYjKBpq0TJE+Q7MKObxnjiwmP07WmEwgj6BFxZSAWRzshtsZyrUSlDgru4piAzTrtplv22BrHrbr5yPtUnfJVUCRNN2/JCEpyqIcZP756KN1maWDpVE/PR+KRomXaLV6PUcWkwOp2+D9ySI4Eg9RBkxMrHKrgRkJIuCcjODT+LkXw==
         * currentTimeSeconds : 1481253087
         */

        private SignBean sign;
        private int userId;
        private String userName;
        private int userType;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

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
