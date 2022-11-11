/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poly.ui;

import com.poly.DAO.ChuyenDeDAO;
import com.poly.DAO.HocVienDAO;
import com.poly.DAO.KhoaHocDAO;
import com.poly.DAO.NguoiHocDAO;
import com.poly.modal.ChuyenDe;
import com.poly.modal.HocVien;
import com.poly.modal.KhoaHoc;
import com.poly.modal.NguoiHoc;
import com.poly.jdbc.Auth;
import com.poly.jdbc.MsgBox;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import jdk.jfr.consumer.EventStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author lanpr
 */
public class HocVienJDialog extends javax.swing.JDialog {

    /**
     * Creates new form HocVienJDialog
     */
    public HocVienJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.init();
    }
    ChuyenDeDAO cddao = new ChuyenDeDAO();
    KhoaHocDAO khdao = new KhoaHocDAO();
    NguoiHocDAO nhdao = new NguoiHocDAO();
    HocVienDAO hvdao = new HocVienDAO();
    
    void init() {
        this.setLocationRelativeTo(null);
        this.fillComboBoxChuyenDe();
        
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment(JLabel.CENTER);
        
        TableColumnModel model = tblHocVien.getColumnModel();
        model.getColumn(0).setMaxWidth(40);
        model.getColumn(1).setMaxWidth(50);
        model.getColumn(2).setMaxWidth(80);
        model.getColumn(4).setMaxWidth(50);
        
        model.getColumn(0).setCellRenderer(render);
        model.getColumn(1).setCellRenderer(render);
        model.getColumn(4).setCellRenderer(render);
    }
    void fillComboBoxChuyenDe(){
        DefaultComboBoxModel model = (DefaultComboBoxModel) cboChuyenDe.getModel();
        model.removeAllElements();
        List<ChuyenDe> list = cddao.selectAll();
        for(ChuyenDe cd: list){
            model.addElement(cd);
        }
        this.fillComboBoxKhoaHoc();
    }
    void fillComboBoxKhoaHoc(){
        DefaultComboBoxModel model = (DefaultComboBoxModel) cboKhoaHoc.getModel();
        model.removeAllElements();
        ChuyenDe chuyenDe = (ChuyenDe) cboChuyenDe.getSelectedItem();
        if(chuyenDe != null){
            List<KhoaHoc> list = khdao.selectByChuyenDe(chuyenDe.getMaCD());
            for(KhoaHoc kh: list){
                model.addElement(kh);
            }
            this.fillTableHocVien();
        }
    }   
    void fillTableHocVien(){
        DefaultTableModel model = (DefaultTableModel) tblHocVien.getModel();
        model.setRowCount(0);
        KhoaHoc khoaHoc = (KhoaHoc) cboKhoaHoc.getSelectedItem();
        if(khoaHoc != null){
            List<HocVien> list = hvdao.selectByKhoaHoc(khoaHoc.getMaKH());
            for(int i=0; i<list.size(); i++){
                HocVien hv = list.get(i);
                String hoten = nhdao.selectById(hv.getMaNH()).getHoTen();
                model.addRow(new Object[]{
                    i + 1,
                    hv.getMaHV(),
                    hv.getMaNH(),
                    hoten,
                    hv.getDiem()
                });
            }
            this.fillTableNguoiHoc();
        }
    }    
    void fillTableNguoiHoc(){
        DefaultTableModel model = (DefaultTableModel) tblNguoiHoc.getModel();
        model.setRowCount(0);
        KhoaHoc khoaHoc = (KhoaHoc) cboKhoaHoc.getSelectedItem();
        String keyword = txtTimKiem.getText();
        List<NguoiHoc> list = nhdao.selectNotInCourse(khoaHoc.getMaKH(), keyword);
        for(NguoiHoc nh: list){
            model.addRow(new Object[]{
                nh.getMaNH(),
                nh.getHoTen(),
                nh.getGioiTinh()?"Nam":"Nữ",
                nh.getNgaySinh(),
                nh.getSdt(),
                nh.getEmail()
            });                
        }
    }

    void addHocVien(){
        KhoaHoc khoaHoc = (KhoaHoc) cboKhoaHoc.getSelectedItem();
        int[] rows = tblNguoiHoc.getSelectedRows();
        for(int row : rows){
            String manh = (String) tblNguoiHoc.getValueAt(row, 0);
            HocVien hv = new HocVien();
            hv.setMaKH(khoaHoc.getMaKH());
            hv.setDiem(0);
            hv.setMaNH(manh);
            hvdao.insert(hv);
        }
        this.fillTableHocVien();
        this.tabs.setSelectedIndex(0);
    }
    void removeHocVien(){
        if(!Auth.isManager()){
            MsgBox.alert(this, "Bạn không có quyền xóa học viên!");
        }
        else{
            int[] rows = tblHocVien.getSelectedRows();
            if(rows.length > 0 && 
                    MsgBox.confirm(this, "Bạn muốn xóa các học viên được chọn?")){
                for(int row : rows){
                    int mahv = (Integer) tblHocVien.getValueAt(row, 0);
                    hvdao.delete(mahv);
                }
                this.fillTableHocVien();
            }
        }
    }
    void updateDiem(){
        int n = tblHocVien.getRowCount();
        for(int i=0; i<n; i++){
            int mahv = (Integer) tblHocVien.getValueAt(i, 1);
            double diem = (Double) tblHocVien.getValueAt(i, 4);
            HocVien hv = hvdao.selectById(mahv);
            hv.setDiem(diem);
            hvdao.update(hv);
        }
        MsgBox.alert(this, "Cập nhật điểm thành công!");
    }
    public void openFile(String file){
        try{
            File path=new File(file);
            Desktop.getDesktop().open(path);
        }catch(Exception ioe){
            System.out.println(ioe);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        cboChuyenDe = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        cboKhoaHoc = new javax.swing.JComboBox<>();
        tabs = new javax.swing.JTabbedPane();
        pnlHocVien = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblHocVien = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        btnRemoveHocVien = new javax.swing.JButton();
        btnUpdateDiem = new javax.swing.JButton();
        pnlNguoiHoc = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblNguoiHoc = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        btnExcel = new javax.swing.JButton();
        btnAddHocVien = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        txtTimKiem = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("EduSys - Quản lý học viên");

        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("CHUYÊN ĐỀ"));
        jPanel5.setLayout(new java.awt.GridLayout(1, 0));

        cboChuyenDe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboChuyenDeActionPerformed(evt);
            }
        });
        jPanel5.add(cboChuyenDe);

        jPanel1.add(jPanel5);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("KHÓA HỌC"));
        jPanel2.setLayout(new java.awt.GridLayout(1, 0));

        cboKhoaHoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboKhoaHocActionPerformed(evt);
            }
        });
        jPanel2.add(cboKhoaHoc);

        jPanel1.add(jPanel2);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        pnlHocVien.setLayout(new java.awt.BorderLayout());

        tblHocVien.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null}
            },
            new String [] {
                "TT", "MÃ HV", "MÃ NH", "HỌ TÊN", "ĐIỂM"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblHocVien.setRowHeight(25);
        tblHocVien.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane1.setViewportView(tblHocVien);

        pnlHocVien.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnRemoveHocVien.setText("Xóa khỏi khóa học");
        btnRemoveHocVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveHocVienActionPerformed(evt);
            }
        });
        jPanel4.add(btnRemoveHocVien);

        btnUpdateDiem.setText("Cập nhật điểm");
        btnUpdateDiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateDiemActionPerformed(evt);
            }
        });
        jPanel4.add(btnUpdateDiem);

        pnlHocVien.add(jPanel4, java.awt.BorderLayout.PAGE_END);

        tabs.addTab("HỌC VIÊN", pnlHocVien);

        pnlNguoiHoc.setLayout(new java.awt.BorderLayout());

        tblNguoiHoc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "MÃ NH", "HỌ VÀ TÊN", "GIỚI TÍNH", "NGÀY SINH", "ĐIỆN THOẠI", "EMAIL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblNguoiHoc.setRowHeight(22);
        tblNguoiHoc.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane3.setViewportView(tblNguoiHoc);

        pnlNguoiHoc.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnExcel.setText("Xuất Excel");
        btnExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcelActionPerformed(evt);
            }
        });
        jPanel3.add(btnExcel);

        btnAddHocVien.setText("Thêm vào khóa học");
        btnAddHocVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddHocVienActionPerformed(evt);
            }
        });
        jPanel3.add(btnAddHocVien);

        pnlNguoiHoc.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Tìm kiếm"));
        jPanel6.setLayout(new java.awt.GridLayout(1, 0));

        txtTimKiem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTimKiemKeyReleased(evt);
            }
        });
        jPanel6.add(txtTimKiem);

        pnlNguoiHoc.add(jPanel6, java.awt.BorderLayout.PAGE_START);

        tabs.addTab("NGƯỜI HỌC", pnlNguoiHoc);

        getContentPane().add(tabs, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboChuyenDeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboChuyenDeActionPerformed
        // TODO add your handling code here:
        this.fillComboBoxKhoaHoc();
    }//GEN-LAST:event_cboChuyenDeActionPerformed

    private void cboKhoaHocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboKhoaHocActionPerformed
        // TODO add your handling code here:
        this.fillTableHocVien();
    }//GEN-LAST:event_cboKhoaHocActionPerformed

    private void btnRemoveHocVienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveHocVienActionPerformed
        // TODO add your handling code here:
        this.removeHocVien();
    }//GEN-LAST:event_btnRemoveHocVienActionPerformed

    private void btnUpdateDiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateDiemActionPerformed
        // TODO add your handling code here:
        this.updateDiem();
    }//GEN-LAST:event_btnUpdateDiemActionPerformed

    private void btnAddHocVienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddHocVienActionPerformed
        // TODO add your handling code here:
        this.addHocVien();
    }//GEN-LAST:event_btnAddHocVienActionPerformed

    private void txtTimKiemKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTimKiemKeyReleased
        // TODO add your handling code here:
        this.fillTableNguoiHoc();
    }//GEN-LAST:event_txtTimKiemKeyReleased

    private void btnExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelActionPerformed
        
        try{
            JFileChooser excelChooser = new JFileChooser();
            excelChooser.showSaveDialog(this);
            File saveFile= excelChooser.getSelectedFile();
            if(saveFile != null){
                saveFile=new File(saveFile.toString()+".xlsx");
                Workbook wb=new XSSFWorkbook();
                Sheet sheet=wb.createSheet("customer");
                Row rowCol = sheet.createRow(0);
                for(int i=0;i<tblNguoiHoc.getColumnCount();i++){
                    Cell cell=rowCol.createCell(i);
                    cell.setCellValue(tblNguoiHoc.getColumnName(i));
                }
                for(int j=0;j<tblNguoiHoc.getRowCount();j++){
                    Row row=sheet.createRow(j+1);
                    for(int k=0;k<tblNguoiHoc.getColumnCount();k++){
                        Cell cell=row.createCell(k);
                        if(tblNguoiHoc.getValueAt(j, k)!=null){
                            cell.setCellValue(tblNguoiHoc.getValueAt(j, k).toString());
                        }
                    }
                }
                FileOutputStream out =new FileOutputStream(new File(saveFile.toString()));
                wb.write(out);
                wb.close();
                out.close();
                openFile(saveFile.toString());
            
            }else{
                JOptionPane.showMessageDialog(null, "Lỗi xuất file");
            }
        }catch(Exception e){
            System.out.println(""+ e);
        }
                
        
    }//GEN-LAST:event_btnExcelActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HocVienJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HocVienJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HocVienJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HocVienJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                HocVienJDialog dialog = new HocVienJDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddHocVien;
    private javax.swing.JButton btnExcel;
    private javax.swing.JButton btnRemoveHocVien;
    private javax.swing.JButton btnUpdateDiem;
    private javax.swing.JComboBox<String> cboChuyenDe;
    private javax.swing.JComboBox<String> cboKhoaHoc;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel pnlHocVien;
    private javax.swing.JPanel pnlNguoiHoc;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblHocVien;
    private javax.swing.JTable tblNguoiHoc;
    private javax.swing.JTextField txtTimKiem;
    // End of variables declaration//GEN-END:variables

    
}
