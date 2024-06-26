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

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author a112214433
 */
public class FrmBarang extends javax.swing.JFrame {
  Connection Con;
  ResultSet RsBrg;
  Statement stm;
  String sSatuan;
  Boolean edit = false;
  private Object[][] dataTable = null;
  private String[] header = { "Kode", "Nama Barang", "Satuan", "Harga", "Stok", "Stok Min" };

  /**
   * Creates new form FrmBarang
   */
  public FrmBarang() {
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
    cmbSatuan.setSelectedItem((String) tblBrg.getValueAt(row, 2));
    String harga = Double.toString((Double) tblBrg.getValueAt(row, 3));
    txtHarga.setText(harga);
    String stok = Integer.toString((Integer) tblBrg.getValueAt(row, 4));
    txtStok.setText(stok);
    String stokmin = Integer.toString((Integer) tblBrg.getValueAt(row, 5));
    txtStokMin.setText(stokmin);
  }

  // method membuka database server, user, pass, database disesuaikan
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
      RsBrg = stm.executeQuery("select * from barang");
      ResultSetMetaData meta = RsBrg.getMetaData();
      int col = meta.getColumnCount();
      int baris = 0;
      while (RsBrg.next()) {
        baris = RsBrg.getRow();
      }

      dataTable = new Object[baris][col];
      int x = 0;
      RsBrg.beforeFirst();

      while (RsBrg.next()) {
        dataTable[x][0] = RsBrg.getString("kd_brg");
        dataTable[x][1] = RsBrg.getString("nm_brg");
        dataTable[x][2] = RsBrg.getString("satuan");
        dataTable[x][3] = RsBrg.getDouble("harga");
        dataTable[x][4] = RsBrg.getInt("stok");
        dataTable[x][5] = RsBrg.getInt("stok_min");
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
    cmbSatuan.setSelectedIndex(0);
    txtHarga.setText("");
    txtStok.setText("");
    txtStokMin.setText("");
  }

  // mengset aktif tidak isian data
  private void aktif(boolean x) {
    txtKode.setEditable(x);
    txtNama.setEditable(x);
    // cmbSatuan.setEditable(x);
    cmbSatuan.setEnabled(x);
    txtHarga.setEditable(x);
    txtStok.setEditable(x);
    txtStokMin.setEditable(x);
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
    double hrg = Double.parseDouble(txtHarga.getText());
    int stk = Integer.parseInt(txtStok.getText());
    int stkMin = Integer.parseInt(txtStokMin.getText());

    try {
      if (edit == true) {
        stm.executeUpdate("update barang set nm_brg='" + tNama + "',satuan='" + sSatuan + "',"
            + "harga=" + hrg + ",stok=" + stk + ",stok_min=" + stkMin + " where kd_brg='" + tKode + "'");
      } else {
        stm.executeUpdate(
            "INSERT into barang(kd_brg, nm_brg, satuan, harga, stok, stok_min) "
                + "VALUES('" + tKode + "','" + tNama + "','" + sSatuan + "'," + hrg + "," + stk + "," + stkMin + ")");
      }

      tblBrg.setModel(new DefaultTableModel(dataTable, header));
      baca_data();
      aktif(false);
      setTombol(true);
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, e);
    }
  }

  private void cmbSatuanActionPerformed(java.awt.event.ActionEvent evt) {
    @SuppressWarnings("unchecked")
    JComboBox<String> cSatuan = (javax.swing.JComboBox<String>) evt.getSource();
    // Membaca Item Yang Terpilih — > String
    sSatuan = (String) cSatuan.getSelectedItem();
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
      String sql = "delete from barang where kd_brg='" + txtKode.getText() + "'";
      stm.executeUpdate(sql);
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtKodeLabel = new javax.swing.JLabel();
        txtKode = new javax.swing.JTextField();
        txtNamaLabel = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        cmbSatuanLabel = new javax.swing.JLabel();
        cmbSatuan = new javax.swing.JComboBox<>();
        txtHargaLabel = new javax.swing.JLabel();
        txtHarga = new javax.swing.JTextField();
        txtStokLabel = new javax.swing.JLabel();
        txtStok = new javax.swing.JTextField();
        txtStokMinLabel = new javax.swing.JLabel();
        txtStokMin = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBrg = new javax.swing.JTable();
        cmdTambah = new javax.swing.JButton();
        cmdSimpan = new javax.swing.JButton();
        cmdKoreksi = new javax.swing.JButton();
        cmdHapus = new javax.swing.JButton();
        cmdBatal = new javax.swing.JButton();
        cmdKeluar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("sansserif", 0, 24)); // NOI18N
        jLabel1.setText("Data Barang");

        txtKodeLabel.setLabelFor(txtKode);
        txtKodeLabel.setText("Kode Barang");

        txtKode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKodeActionPerformed(evt);
            }
        });

        txtNamaLabel.setLabelFor(txtNama);
        txtNamaLabel.setText("Nama Barang");

        txtNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaActionPerformed(evt);
            }
        });

        cmbSatuanLabel.setLabelFor(cmbSatuan);
        cmbSatuanLabel.setText("Satuan");

        cmbSatuan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Buah", "Lembar", "Batang" }));
        cmbSatuan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSatuanActionPerformed(evt);
            }
        });

        txtHargaLabel.setLabelFor(txtHarga);
        txtHargaLabel.setText("Harga");

        txtHarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHargaActionPerformed(evt);
            }
        });

        txtStokLabel.setLabelFor(txtStok);
        txtStokLabel.setText("Stok");

        txtStok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtStokActionPerformed(evt);
            }
        });

        txtStokMinLabel.setLabelFor(txtStokMin);
        txtStokMinLabel.setText("Stok Minimal");
        txtStokMinLabel.setToolTipText("");

        txtStokMin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtStokMinActionPerformed(evt);
            }
        });

        tblBrg.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Kode", "Nama Barang", "Satuan", "Harga", "Stok", "Stok Minimal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
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
                        .addComponent(cmdTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdKoreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtNamaLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                .addComponent(cmbSatuanLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtKodeLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtHargaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(txtStokLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtStokMinLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtHarga, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtStok, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtKode, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                            .addComponent(txtStokMin, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNama, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                            .addComponent(cmbSatuan, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmbSatuan, cmbSatuanLabel, txtHarga, txtHargaLabel, txtKode, txtNama, txtNamaLabel, txtStok, txtStokLabel, txtStokMin, txtStokMinLabel});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdBatal, cmdHapus, cmdKeluar, cmdKoreksi, cmdSimpan, cmdTambah});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtKodeLabel)
                    .addComponent(txtKode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNamaLabel)
                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbSatuanLabel)
                    .addComponent(cmbSatuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtHargaLabel)
                    .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtStokLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStokMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtStokMinLabel))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdTambah)
                    .addComponent(cmdSimpan)
                    .addComponent(cmdKoreksi)
                    .addComponent(cmdHapus)
                    .addComponent(cmdBatal)
                    .addComponent(cmdKeluar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
    private javax.swing.JComboBox<String> cmbSatuan;
    private javax.swing.JLabel cmbSatuanLabel;
    private javax.swing.JButton cmdBatal;
    private javax.swing.JButton cmdHapus;
    private javax.swing.JButton cmdKeluar;
    private javax.swing.JButton cmdKoreksi;
    private javax.swing.JButton cmdSimpan;
    private javax.swing.JButton cmdTambah;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblBrg;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JLabel txtHargaLabel;
    private javax.swing.JTextField txtKode;
    private javax.swing.JLabel txtKodeLabel;
    private javax.swing.JTextField txtNama;
    private javax.swing.JLabel txtNamaLabel;
    private javax.swing.JTextField txtStok;
    private javax.swing.JLabel txtStokLabel;
    private javax.swing.JTextField txtStokMin;
    private javax.swing.JLabel txtStokMinLabel;
    // End of variables declaration//GEN-END:variables
}
