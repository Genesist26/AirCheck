package com.example.aircheck.pm25;

public class Item {

        private String pm;
        private String province;
        private String latitude;
        private String longitude;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Item(String pm, String province, String latitude, String longitude) {
            this.pm = pm;
            this.province = province;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Item() {

        }

        public String getPm() {
            return pm;
        }

        public void setPm(String pm) {
            this.pm = pm;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

}
