package com.feiliu.flgamesdk.bean.netresultbean;

/**
 * Created by Administrator on 2016/11/16.
 */
public class GuestLoginResultJsonBean {

    /**
     * actionId : 4002
     * result : {"code":"0","tips":"游客初次登录，生成游客账号信息"}
     * userInfo : {"firstLogin":1,"giftStatus":0,"nickName":"游客739211","sign":{"code":"FPOxXNCyidj3upqGPYWSn9UQnZf4YAsmodygt5I6BTT+/G+I65IA6BSqgweSJ+gvel3X7H3bjMpf39v8GQwQoNIaOE/YCW8FTinPrZ1AacIyfcuskF52HMr7YES3cZlRXFwkUZh+pmkauBTn1DCy8Ismj9aEHaEskuFaLmX4OGtKaJQSaTo3UvhxibI54+Uu6A9U7fQsVRmdT5PX0Mihel9jHrEBAfAhlTHo+9e4s3BVA5Mxx0HaGy89t+4xNyoGbb7pJPXLvrAywAGd8gXeR/fpj52T9bsYB4u9daWaN0HSvnvE/oSWPdpdW9NIFkK0af7FkONp+bSSVUOq2THZaw==","currentTimeSeconds":1479284738},"userId":739211,"userName":"游客739211"}
     */

    private String actionId;
    /**
     * code : 0
     * tips : 游客初次登录，生成游客账号信息
     */

    private ResultBean result;
    /**
     * firstLogin : 1
     * giftStatus : 0
     * nickName : 游客739211
     * sign : {"code":"FPOxXNCyidj3upqGPYWSn9UQnZf4YAsmodygt5I6BTT+/G+I65IA6BSqgweSJ+gvel3X7H3bjMpf39v8GQwQoNIaOE/YCW8FTinPrZ1AacIyfcuskF52HMr7YES3cZlRXFwkUZh+pmkauBTn1DCy8Ismj9aEHaEskuFaLmX4OGtKaJQSaTo3UvhxibI54+Uu6A9U7fQsVRmdT5PX0Mihel9jHrEBAfAhlTHo+9e4s3BVA5Mxx0HaGy89t+4xNyoGbb7pJPXLvrAywAGd8gXeR/fpj52T9bsYB4u9daWaN0HSvnvE/oSWPdpdW9NIFkK0af7FkONp+bSSVUOq2THZaw==","currentTimeSeconds":1479284738}
     * userId : 739211
     * userName : 游客739211
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
        /**
         * code : FPOxXNCyidj3upqGPYWSn9UQnZf4YAsmodygt5I6BTT+/G+I65IA6BSqgweSJ+gvel3X7H3bjMpf39v8GQwQoNIaOE/YCW8FTinPrZ1AacIyfcuskF52HMr7YES3cZlRXFwkUZh+pmkauBTn1DCy8Ismj9aEHaEskuFaLmX4OGtKaJQSaTo3UvhxibI54+Uu6A9U7fQsVRmdT5PX0Mihel9jHrEBAfAhlTHo+9e4s3BVA5Mxx0HaGy89t+4xNyoGbb7pJPXLvrAywAGd8gXeR/fpj52T9bsYB4u9daWaN0HSvnvE/oSWPdpdW9NIFkK0af7FkONp+bSSVUOq2THZaw==
         * currentTimeSeconds : 1479284738
         */

        private SignBean sign;
        private int userId;
        private String userName;

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
