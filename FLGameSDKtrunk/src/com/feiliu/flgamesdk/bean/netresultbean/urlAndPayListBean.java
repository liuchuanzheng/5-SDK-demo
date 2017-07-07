package com.feiliu.flgamesdk.bean.netresultbean;

import java.util.List;

/**
 * Created by Administrator on 2017/3/3.
 */

public class urlAndPayListBean {

    /**
     * payCenterUrl : http://pay.feiliu.com/payCenter/style0
     * payChannelList : [{"payCompanyId":"9001","payChannelId":"1001","payChannelName":"weChat","pageSort":"1"},{"payCompanyId":"9002","payChannelId":"1002","payChannelName":"ali","pageSort":"2"},{"payCompanyId":"9001","payChannelId":"1003","payChannelName":"mo9","pageSort":"3"}]
     */

    private PayCenterInfoBean payCenterInfo;
    /**
     * appName : 三国群英传
     * amount : 100
     * goodsId : 元宝
     */

    private OrderInfoBean orderInfo;

    public PayCenterInfoBean getPayCenterInfo() {
        return payCenterInfo;
    }

    public void setPayCenterInfo(PayCenterInfoBean payCenterInfo) {
        this.payCenterInfo = payCenterInfo;
    }

    public OrderInfoBean getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderInfoBean orderInfo) {
        this.orderInfo = orderInfo;
    }

    public static class PayCenterInfoBean {
        private String payCenterUrl;
        /**
         * payCompanyId : 9001
         * payChannelId : 1001
         * payChannelName : weChat
         * pageSort : 1
         */

        private List<PayChannelListBean> payChannelList;

        public String getPayCenterUrl() {
            return payCenterUrl;
        }

        public void setPayCenterUrl(String payCenterUrl) {
            this.payCenterUrl = payCenterUrl;
        }

        public List<PayChannelListBean> getPayChannelList() {
            return payChannelList;
        }

        public void setPayChannelList(List<PayChannelListBean> payChannelList) {
            this.payChannelList = payChannelList;
        }

        public static class PayChannelListBean {
            private String payCompanyId;
            private String payChannelId;
            private String payChannelName;
            private String pageSort;

            public String getPayCompanyId() {
                return payCompanyId;
            }

            public void setPayCompanyId(String payCompanyId) {
                this.payCompanyId = payCompanyId;
            }

            public String getPayChannelId() {
                return payChannelId;
            }

            public void setPayChannelId(String payChannelId) {
                this.payChannelId = payChannelId;
            }

            public String getPayChannelName() {
                return payChannelName;
            }

            public void setPayChannelName(String payChannelName) {
                this.payChannelName = payChannelName;
            }

            public String getPageSort() {
                return pageSort;
            }

            public void setPageSort(String pageSort) {
                this.pageSort = pageSort;
            }
        }
    }

    public static class OrderInfoBean {
        private String appName;
        private String amount;
        private String goodsId;

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(String goodsId) {
            this.goodsId = goodsId;
        }
    }
}
