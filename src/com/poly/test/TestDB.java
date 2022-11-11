
package com.poly.test;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.poly.modal.NhanVien;
import java.sql.*;

public class TestDB {
    static String url = "jdbc:sqlserver://localhost:1433;databaseName=Polypro;encrypt=true;trustServerCertificate=true";
    static String userName = "as";
    static String pass = "";
    public static void main(String[] args) {
        
        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection con = DriverManager.getConnection(url, userName, pass);
//            PreparedStatement ps = con.prepareStatement("select * from NhanVien");
//            ResultSet rs = ps.executeQuery();
//            NhanVien nv;
//            while(rs.next()){
//                System.out.println("Mã nhân viên: "+rs.getString("MaNV")+" Mật khẩu : "+rs.getString("MatKhau"));
//            }


//            PreparedStatement ps = con.prepareStatement("insert into NhanVien values (N'huydt', N'songlong', N'Nguyễn Đình Huy', 0)");
//            int kq = ps.executeUpdate();
//            if(kq==1){
//                System.out.println("successful");
//            }else{
//                System.out.println("fail");
//            }
            
            CallableStatement cs = con.prepareCall("{call sp_LuongNguoiHoc}");
            ResultSet rs =cs.executeQuery();
            while(rs.next()){
                System.out.println(rs.getString("Nam")+" "+ rs.getString("SoLuong"));
            }
            
            rs.close();
//            ps.close();
            cs.close();
            con.close();
        }catch(Exception e){
            System.out.println(e);
        }
        
    }
    
//    public void ThemNhanVienTest(Connection con) throws SQLException{
//        try {
////            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
////            Connection con = DriverManager.getConnection(url, userName, pass);
//            PreparedStatement ps = con.prepareStatement("insert into NhanVien values (N'huydt', N'songlong', N'Nguyễn Đình Huy', 0)");
//            int kq = ps.executeUpdate();
//            if(kq==1){
//                System.out.println("successful");
//            }else{
//                System.out.println("fail");
//            }
//        } catch (Exception e) {
//        }
//        
//    }
    
}
