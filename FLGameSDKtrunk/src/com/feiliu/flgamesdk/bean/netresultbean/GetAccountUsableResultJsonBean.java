package com.feiliu.flgamesdk.bean.netresultbean;

/**
 * 账号重复性检测
 * Created by Administrator on 2016/11/23.
 */
public class GetAccountUsableResultJsonBean {

    /**
     * actionId : 4004
     * result : {"code":"0","opcode":"40040","tips":"账号可用"}
     */

    private String actionId;
    /**
     * code : 0
     * opcode : 40040
     * tips : 账号可用
     */

    private ResultBean result;

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

    public static class ResultBean {
        private String code;
        private String opcode;
        private String tips;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getOpcode() {
            return opcode;
        }

        public void setOpcode(String opcode) {
            this.opcode = opcode;
        }

        public String getTips() {
            return tips;
        }

        public void setTips(String tips) {
            this.tips = tips;
        }
    }
}
