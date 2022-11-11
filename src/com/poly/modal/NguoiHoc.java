
package com.poly.modal;

import com.poly.jdbc.DateHelper;
import java.util.Date;


public class NguoiHoc {
    private String maNH;
    private String hoTen;
    private boolean gioiTinh;
    private Date ngaySinh;
    private String sdt;
    private String email;
    private String ghiChu;
    private String maNV;
    private Date ngayDK = DateHelper.now();

    public NguoiHoc() {
    }

    public NguoiHoc(String maNH, String hoTen, boolean gioiTinh, Date ngaySinh, String sdt, String email, String ghiChu, String maNV, Date ngayDK) {
        this.maNH = maNH;
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.sdt = sdt;
        this.email = email;
        this.ghiChu = ghiChu;
        this.maNV = maNV;
        this.ngayDK = ngayDK;
    }
    
    public String toString(){
        return this.hoTen;
    }

    public String getMaNH() {
        return maNH;
    }

    public void setMaNH(String maNH) {
        this.maNH = maNH;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public Boolean getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(Boolean gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public Date getNgayDK() {
        return ngayDK;
    }

    public void setNgayDK(Date ngayDK) {
        this.ngayDK = ngayDK;
    }
    
}
