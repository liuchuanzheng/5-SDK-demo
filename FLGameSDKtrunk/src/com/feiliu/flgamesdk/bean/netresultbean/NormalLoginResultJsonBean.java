package com.feiliu.flgamesdk.bean.netresultbean;

/**
 * Created by Administrator on 2016/11/23.
 */
public class NormalLoginResultJsonBean {

    /**
     * actionId : 4003
     * result : {"code":"0","tips":"登录成功"}
     * userInfo : {"firstLogin":1,"nickName":"18500319748","password":"004876","phone":"18500319748","email":"18500319748@qq.com","sign":{"code":"ojnqE+VjdoRsukA6PTGqOVakrfE69SarmVPhcot1vvXZRXjtwfNhg57VGXzimrhzRLX4ywP/BeCIOpZqioeUDFoIJtlBi+StoUbdxMoJ2NfEtl/t2SddlyII5sZLearsIWxZrnjblq0qjuy3D4esmzXZnrbyEKk4LJZAYOmLnKsPqkmpO+TnSJoJIiF5dy7PEsO2gBIM5DeOMoYKfLO/XWuM9GrPh2+HpoeRWeCN3/bPNJzcqf6Lq4JvZzrk/ywdyS7mKQtmb0qVs0niYWQHzmVG2LMY10KuKCbdRn55VtUqYGAD6V5Bf3O4DE/2QO3o5c6PFInq10mG68LMhbxT/Q==","currentTimeSeconds":1479893830},"userId":739216,"userName":"18500319748","userType":1}
     */

    private String actionId;
    /**
     * code : 0
     * tips : 登录成功
     */

    private ResultBean result;
    /**
     * firstLogin : 1
     * nickName : 18500319748
     * password : 004876
     * phone : 18500319748
     * sign : {"code":"ojnqE+VjdoRsukA6PTGqOVakrfE69SarmVPhcot1vvXZRXjtwfNhg57VGXzimrhzRLX4ywP/BeCIOpZqioeUDFoIJtlBi+StoUbdxMoJ2NfEtl/t2SddlyII5sZLearsIWxZrnjblq0qjuy3D4esmzXZnrbyEKk4LJZAYOmLnKsPqkmpO+TnSJoJIiF5dy7PEsO2gBIM5DeOMoYKfLO/XWuM9GrPh2+HpoeRWeCN3/bPNJzcqf6Lq4JvZzrk/ywdyS7mKQtmb0qVs0niYWQHzmVG2LMY10KuKCbdRn55VtUqYGAD6V5Bf3O4DE/2QO3o5c6PFInq10mG68LMhbxT/Q==","currentTimeSeconds":1479893830}
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
        private String opcode;

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

		public String getOpcode() {
			return opcode;
		}

		public void setOpcode(String opcode) {
			this.opcode = opcode;
		}
        
    }

    public static class UserInfoBean {
        private int firstLogin;
        private String nickName;
        private String password;
        private String phone;
        private String email;
        /**
         * code : ojnqE+VjdoRsukA6PTGqOVakrfE69SarmVPhcot1vvXZRXjtwfNhg57VGXzimrhzRLX4ywP/BeCIOpZqioeUDFoIJtlBi+StoUbdxMoJ2NfEtl/t2SddlyII5sZLearsIWxZrnjblq0qjuy3D4esmzXZnrbyEKk4LJZAYOmLnKsPqkmpO+TnSJoJIiF5dy7PEsO2gBIM5DeOMoYKfLO/XWuM9GrPh2+HpoeRWeCN3/bPNJzcqf6Lq4JvZzrk/ywdyS7mKQtmb0qVs0niYWQHzmVG2LMY10KuKCbdRn55VtUqYGAD6V5Bf3O4DE/2QO3o5c6PFInq10mG68LMhbxT/Q==
         * currentTimeSeconds : 1479893830
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
        
        public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
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
