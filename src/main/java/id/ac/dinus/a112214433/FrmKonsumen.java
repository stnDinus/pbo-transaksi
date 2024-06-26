/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package id.ac.dinus.a112214433;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author a112214433
 */
public class FrmKonsumen extends javax.swing.JFrame {
  Connection Con;
  ResultSet RsKns;
  Statement stm;
  Boolean edit = false;
  private Object[][] dataTable = null;
  private String[] header = { "Kode", "Nama", "Alamat", "Kota", "Kode Pos", "Telepon", "Email" };

  /**
   * Creates new form FrmBarang
   */
  public FrmKonsumen() {
    initComponents();
    open_db();
    baca_data();
    aktif(false);
    setTombol(true);
  }

  // method untuk memindahkan data dr table ke form
  private void setField() {
    int row = tblBrg.getSelectedRow();

    txtKode.setText((String) tblBrg.getValueAt(row, 0));
    txtNama.setText((String) tblBrg.getValueAt(row, 1));
    txtAlamat.setText((String) tblBrg.getValueAt(row, 2));
    txtKota.setText((String) tblBrg.getValueAt(row, 3));
    txtKodePos.setText((String) tblBrg.getValueAt(row, 4));
    txtTelepon.setText((String) tblBrg.getValueAt(row, 5));
    txtEmail.setText((String) tblBrg.getValueAt(row, 6));
  }

  // method membuka database server database disesuaikan
  private void open_db() {
    try {
      KoneksiMysql kon = new KoneksiMysql("pbo");
      Con = kon.getConnection();
      // System.out.println("Berhasil ");
    } catch (Exception e) {
      System.out.println("Error : " + e);
    }
  }

  // method baca data dari Mysql dimasukkan ke table pada form
  private void baca_data() {
    try {
      stm = Con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      // stm = Con.createStatement();
      RsKns = stm.executeQuery("select * from konsumen");
      ResultSetMetaData meta = RsKns.getMetaData();
      int col = meta.getColumnCount();
      int baris = 0;
      while (RsKns.next()) {
        baris = RsKns.getRow();
      }

      dataTable = new Object[baris][col];
      int x = 0;
      RsKns.beforeFirst();

      while (RsKns.next()) {
        dataTable[x][0] = RsKns.getString("kd_kons");
        dataTable[x][1] = RsKns.getString("nm_kons");
        dataTable[x][2] = RsKns.getString("alm_kons");
        dataTable[x][3] = RsKns.getString("kota_kons");
        dataTable[x][4] = RsKns.getString("kd_pos");
        dataTable[x][5] = RsKns.getString("phone");
        dataTable[x][6] = RsKns.getString("email");
        x++;
      }

      tblBrg.setModel(new DefaultTableModel(dataTable, header));
      // DefaultTableModel model = (DefaultTableModel) tblBrg.getModel();
      // model.setDataVector(dataTable, header);
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, e);
    }
  }

  // untuk mengkosongkan isian data
  private void kosong() {
    txtKode.setText("");
    txtNama.setText("");
    txtAlamat.setText("");
    txtKota.setText("");
    txtKodePos.setText("");
    txtTelepon.setText("");
    txtEmail.setText("");
  }

  // mengset aktif tidak isian data
  private void aktif(boolean x) {
    txtKode.setEditable(x);
    txtNama.setEditable(x);
    txtAlamat.setEditable(x);
    txtKota.setEditable(x);
    txtKodePos.setEditable(x);
    txtTelepon.setEditable(x);
    txtEmail.setEditable(x);
  }

  // mengset tombol on/off
  private void setTombol(boolean t) {
    cmdTambah.setEnabled(t);
    cmdKoreksi.setEnabled(t);
    cmdHapus.setEnabled(t);
    cmdSimpan.setEnabled(!t);
    cmdBatal.setEnabled(!t);
    cmdKeluar.setEnabled(t);
  }

  private void cmdKeluarActionPerformed(java.awt.event.ActionEvent evt) {
    dispose();
  }

  private void cmdSimpanMouseClicked(java.awt.event.MouseEvent evt) {
    String tKode = txtKode.getText();
    String tNama = txtNama.getText();
    String tAlmt = txtAlamat.getText();
    String tKota = txtKota.getText();
    String tKdPos = txtKodePos.getText();
    String tTelp = txtTelepon.getText();
    String tEmail = txtEmail.getText();

    try {
      if (edit == true) {
        PreparedStatement s = Con.prepareStatement(
            "UPDATE konsumen SET nm_kons=?, alm_kons=?, kota_kons=?, kd_pos=?, phone=?, email=? WHERE kd_kons=?");

        s.setString(1, tNama);
        s.setString(2, tAlmt);
        s.setString(3, tKota);
        s.setString(4, tKdPos);
        s.setString(5, tTelp);
        s.setString(6, tEmail);
        s.setString(7, tKode);

        s.executeUpdate();
      } else {
        PreparedStatement s = Con.prepareStatement(
            "INSERT INTO konsumen VALUES(?, ?, ?, ? ,?, ?, ?)");

        s.setString(1, tKode);
        s.setString(2, tNama);
        s.setString(3, tAlmt);
        s.setString(4, tKota);
        s.setString(5, tKdPos);
        s.setString(6, tTelp);
        s.setString(7, tEmail);

        s.executeUpdate();
      }

      tblBrg.setModel(new DefaultTableModel(dataTable, header));
      baca_data();
      aktif(false);
      setTombol(true);
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, e);
    }
  }

  private void cmdTambahActionPerformed(java.awt.event.ActionEvent evt) {
    aktif(true);
    setTombol(false);
    kosong();
  }

  private void cmdBatalActionPerformed(java.awt.event.ActionEvent evt) {
    aktif(false);
    setTombol(true);
  }

  private void tblBrgMouseClicked(java.awt.event.MouseEvent evt) {
    setField();
  }

  private void cmdKoreksiActionPerformed(java.awt.event.ActionEvent evt) {
    edit = true;
    aktif(true);
    setTombol(false);
    txtKode.setEditable(false);
  }

  private void cmdHapusActionPerformed(java.awt.event.ActionEvent evt) {
    try {
      PreparedStatement s = Con.prepareStatement("DELETE FROM konsumen WHERE kd_kons=?");

      s.setString(1, txtKode.getText());

      s.executeUpdate();

      baca_data();

      edit = false; // set ulang edit agar form tidak masuk ke mode edit
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, e);
    }
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated
  // <editor-fold defaultstate="collapsed" desc="Generated
  // <editor-fold defaultstate="collapsed" desc="Generated
  // <editor-fold defaultstate="collapsed" desc="Generated
  // <editor-fold defaultstate="collapsed" desc="Generated
  // <editor-fold defaultstate="collapsed" desc="Generated
  // <editor-fold defaultstate="collapsed" desc="Generated
  // <editor-fold defaultstate="collapsed" desc="Generated
  // <editor-fold defaultstate="collapsed" desc="Generated
  // Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel1 = new javax.swing.JLabel();
    txtKodeLabel = new javax.swing.JLabel();
    txtKode = new javax.swing.JTextField();
    txtNamaLabel = new javax.swing.JLabel();
    txtNama = new javax.swing.JTextField();
    txtAlamatLabel = new javax.swing.JLabel();
    txtAlamat = new javax.swing.JTextField();
    txtKotaLabel = new javax.swing.JLabel();
    txtKota = new javax.swing.JTextField();
    txtKodePosLabel = new javax.swing.JLabel();
    txtKodePos = new javax.swing.JTextField();
    txtTeleponLabel = new javax.swing.JLabel();
    txtTelepon = new javax.swing.JTextField();
    jScrollPane1 = new javax.swing.JScrollPane();
    tblBrg = new javax.swing.JTable();
    cmdTambah = new javax.swing.JButton();
    cmdSimpan = new javax.swing.JButton();
    cmdKoreksi = new javax.swing.JButton();
    cmdHapus = new javax.swing.JButton();
    cmdBatal = new javax.swing.JButton();
    cmdKeluar = new javax.swing.JButton();
    txtEmailLabel = new javax.swing.JLabel();
    txtEmail = new javax.swing.JTextField();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

    jLabel1.setFont(new java.awt.Font("sansserif", 0, 24)); // NOI18N
    jLabel1.setText("Data Konsumen");

    txtKodeLabel.setLabelFor(txtKode);
    txtKodeLabel.setText("Kode Konsumen");

    txtNamaLabel.setLabelFor(txtNama);
    txtNamaLabel.setText("Nama Konsumen");

    txtAlamatLabel.setLabelFor(txtAlamat);
    txtAlamatLabel.setText("Alamat");

    txtKotaLabel.setLabelFor(txtKota);
    txtKotaLabel.setText("Kota");

    txtKodePosLabel.setLabelFor(txtKodePos);
    txtKodePosLabel.setText("Kode Pos");

    txtTeleponLabel.setLabelFor(txtTelepon);
    txtTeleponLabel.setText("Telepon");
    txtTeleponLabel.setToolTipText("");

    tblBrg.setModel(new javax.swing.table.DefaultTableModel(
        new Object[][] {
            { null, null, null, null, null, null, null },
            { null, null, null, null, null, null, null },
            { null, null, null, null, null, null, null },
            { null, null, null, null, null, null, null }
        },
        new String[] {
            "Kode", "Nama", "Alamat", "Kota", "Kode Pos", "Telepon", "Email"
        }) {
      Class[] types = new Class[] {
          java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
          java.lang.String.class, java.lang.String.class, java.lang.String.class
      };

      public Class getColumnClass(int columnIndex) {
        return types[columnIndex];
      }
    });
    tblBrg.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        tblBrgMouseClicked(evt);
      }
    });
    jScrollPane1.setViewportView(tblBrg);

    cmdTambah.setText("Tambah");
    cmdTambah.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmdTambahActionPerformed(evt);
      }
    });

    cmdSimpan.setText("Simpan");
    cmdSimpan.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        cmdSimpanMouseClicked(evt);
      }
    });

    cmdKoreksi.setText("Koreksi");
    cmdKoreksi.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmdKoreksiActionPerformed(evt);
      }
    });

    cmdHapus.setText("Hapus");
    cmdHapus.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmdHapusActionPerformed(evt);
      }
    });

    cmdBatal.setText("Batal");
    cmdBatal.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmdBatalActionPerformed(evt);
      }
    });

    cmdKeluar.setText("Keluar");
    cmdKeluar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmdKeluarActionPerformed(evt);
      }
    });

    txtEmailLabel.setLabelFor(txtTelepon);
    txtEmailLabel.setText("Email");
    txtEmailLabel.setToolTipText("");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jScrollPane1)
                .addGap(6, 6, 6))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cmdTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 83,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 83,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdKoreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 83,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 83,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 83,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, 100,
                            javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtNamaLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                .addComponent(txtAlamatLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtKodeLabel, javax.swing.GroupLayout.Alignment.TRAILING,
                                    javax.swing.GroupLayout.PREFERRED_SIZE, 150,
                                    javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtKotaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(txtKodePosLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTeleponLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEmailLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtKota, javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtKodePos, javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtKode, javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                            .addComponent(txtTelepon, javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNama, javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                            .addComponent(txtAlamat, javax.swing.GroupLayout.PREFERRED_SIZE, 150,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEmail, javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

    layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] { txtAlamatLabel, txtKode,
        txtKodePos, txtKodePosLabel, txtKota, txtKotaLabel, txtNama, txtNamaLabel, txtTelepon, txtTeleponLabel });

    layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
        new java.awt.Component[] { cmdBatal, cmdHapus, cmdKeluar, cmdKoreksi, cmdSimpan, cmdTambah });

    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtKodeLabel)
                    .addComponent(txtKode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNamaLabel)
                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAlamatLabel)
                    .addComponent(txtAlamat, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtKotaLabel)
                    .addComponent(txtKota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtKodePos, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtKodePosLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTelepon, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTeleponLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEmailLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 443,
                    javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdTambah)
                    .addComponent(cmdSimpan)
                    .addComponent(cmdKoreksi)
                    .addComponent(cmdHapus)
                    .addComponent(cmdBatal)
                    .addComponent(cmdKeluar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtEmailActionPerformed
    // TODO add your handling code here:
  }// GEN-LAST:event_txtEmailActionPerformed

  private void txtKodeActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtKodeActionPerformed
    // TODO add your handling code here:
  }// GEN-LAST:event_txtKodeActionPerformed

  private void txtNamaActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtNamaActionPerformed
    // TODO add your handling code here:
  }// GEN-LAST:event_txtNamaActionPerformed

  private void txtHargaActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtHargaActionPerformed
    // TODO add your handling code here:
  }// GEN-LAST:event_txtHargaActionPerformed

  private void txtStokActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtStokActionPerformed
    // TODO add your handling code here:
  }// GEN-LAST:event_txtStokActionPerformed

  private void txtStokMinActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtStokMinActionPerformed
    // TODO add your handling code here:
  }// GEN-LAST:event_txtStokMinActionPerformed

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    /* Set the Nimbus look and feel */
    // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
    // (optional) ">
    /*
     * If Nimbus (introduced in Java SE 6) is not available, stay with the default
     * look and feel.
     * For details see
     * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
     */
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException ex) {
      java.util.logging.Logger.getLogger(FrmBarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(FrmBarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(FrmBarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(FrmBarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    // </editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new FrmBarang().setVisible(true);
      }
    });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton cmdBatal;
  private javax.swing.JButton cmdHapus;
  private javax.swing.JButton cmdKeluar;
  private javax.swing.JButton cmdKoreksi;
  private javax.swing.JButton cmdSimpan;
  private javax.swing.JButton cmdTambah;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JTable tblBrg;
  private javax.swing.JTextField txtAlamat;
  private javax.swing.JLabel txtAlamatLabel;
  private javax.swing.JTextField txtEmail;
  private javax.swing.JLabel txtEmailLabel;
  private javax.swing.JTextField txtKode;
  private javax.swing.JLabel txtKodeLabel;
  private javax.swing.JTextField txtKodePos;
  private javax.swing.JLabel txtKodePosLabel;
  private javax.swing.JTextField txtKota;
  private javax.swing.JLabel txtKotaLabel;
  private javax.swing.JTextField txtNama;
  private javax.swing.JLabel txtNamaLabel;
  private javax.swing.JTextField txtTelepon;
  private javax.swing.JLabel txtTeleponLabel;
  // End of variables declaration//GEN-END:variables
}
