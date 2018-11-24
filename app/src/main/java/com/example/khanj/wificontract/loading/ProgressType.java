package com.example.khanj.wificontract.loading;

public enum ProgressType {
    UPLOAD {
        public String toString() {
            return "올리는중...";
        }
    },

    LOAD {
        public String toString() {
            return "불러오는중...";
        }
    },

    MODIFY {
        public String toString() {
            return "수정중...";
        }
    }
}