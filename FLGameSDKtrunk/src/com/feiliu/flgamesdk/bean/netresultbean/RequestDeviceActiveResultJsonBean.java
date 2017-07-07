package com.feiliu.flgamesdk.bean.netresultbean;

/**
 * Created by Administrator on 2016/11/7.
 */
public class RequestDeviceActiveResultJsonBean {
    @Override
    public String toString() {
        return "ResultJsonBean{" +
                "actionId='" + actionId + '\'' +
                ", deviceInfo=" + deviceInfo +
                ", result=" + result +
                '}';
    }

    /**
     * actionId : 2000
     * deviceInfo : {"apn":"wifi","country":"86","deviceId":303931,"deviceKey":"9DB27557-E1CA-4C8E-A164-5A246C39DB77","language":"31","model":"iPhone","os":"IOS","osVersion":"9.3.2","platformModel":"iPhone7,1"}
     * result : {"code":"0","tips":"首次激活成功"}
     */

    private String actionId;
    /**
     * apn : wifi
     * country : 86
     * deviceId : 303931
     * deviceKey : 9DB27557-E1CA-4C8E-A164-5A246C39DB77
     * language : 31
     * model : iPhone
     * os : IOS
     * osVersion : 9.3.2
     * platformModel : iPhone7,1
     */

    private DeviceInfoBean deviceInfo;
    /**
     * code : 0
     * tips : 首次激活成功
     */

    private ResultBean result;

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public DeviceInfoBean getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfoBean deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class DeviceInfoBean {
        @Override
        public String toString() {
            return "DeviceInfoBean{" +
                    "apn='" + apn + '\'' +
                    ", country='" + country + '\'' +
                    ", deviceId=" + deviceId +
                    ", deviceKey='" + deviceKey + '\'' +
                    ", language='" + language + '\'' +
                    ", model='" + model + '\'' +
                    ", os='" + os + '\'' +
                    ", osVersion='" + osVersion + '\'' +
                    ", platformModel='" + platformModel + '\'' +
                    '}';
        }

        private String apn;
        private String country;
        private int deviceId;
        private String deviceKey;
        private String language;
        private String model;
        private String os;
        private String osVersion;
        private String platformModel;

        public String getApn() {
            return apn;
        }

        public void setApn(String apn) {
            this.apn = apn;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public int getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(int deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceKey() {
            return deviceKey;
        }

        public void setDeviceKey(String deviceKey) {
            this.deviceKey = deviceKey;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }

        public String getOsVersion() {
            return osVersion;
        }

        public void setOsVersion(String osVersion) {
            this.osVersion = osVersion;
        }

        public String getPlatformModel() {
            return platformModel;
        }

        public void setPlatformModel(String platformModel) {
            this.platformModel = platformModel;
        }
    }

    public static class ResultBean {
        @Override
        public String toString() {
            return "ResultBean{" +
                    "code='" + code + '\'' +
                    ", tips='" + tips + '\'' +
                    '}';
        }

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
}
