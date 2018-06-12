package com.whr.analysis;

public class AnalysisInfoVo {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String status;
    private String msg;
    private String telPhone;
    private String address;
    private String name;
    private String idcn;


    public String getIdcn() {
        return idcn;
    }
    public void setIdcn(String idcn) {
        this.idcn = idcn;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getTelPhone() {
        return telPhone;
    }
    public void setTelPhone(String telPhone) {
        this.telPhone = telPhone;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    @Override
    public String toString() {
        return "AnalysisInfoVo [status=" + status + ", msg=" + msg
                + ", telPhone=" + telPhone + ", address=" + address + ", name="
                + name + ", idcn=" + idcn + "]";
    }
}
