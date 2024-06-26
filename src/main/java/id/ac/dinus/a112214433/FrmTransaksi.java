/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package id.ac.dinus.a112214433;

import java.awt.print.PrinterException;
import java.sql.*;
import java.text.MessageFormat;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author a112214433
 */
public class FrmTransaksi extends javax.swing.JFrame {
  Connection Con;
  ResultSet RsBrg;
  ResultSet RsKons;
  Statement stm;
  PreparedStatement pstmt;
  double total = 0;
  String tanggal;
  Boolean edit = false;
  DefaultTableModel tableModel = new DefaultTableModel(
      new Object[][] {},
      new String[] {
          "Kd Barang", "Nama Barang", "Harga Barang", "Jumlah", "Total"
      });
  // Var Pencarian Kode Barang
  String idBrg;
  String namaBrg;
  String hargaBrg;

  /**
   * Creates new form FrmTransaksi
   */
  public FrmTransaksi() {
    initComponents();
    open_db();
    inisialisasi_tabel();
    aktif(false);
    setTombol(true);
    txtTgl.setEditor(new JSpinner.DateEditor(txtTgl, "yyyy/MM/dd"));
  }

  // methohd baca data konsumen
  private void baca_konsumen() {
    DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

    try {
      stm = Con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      RsKons = stm.executeQuery("SELECT kd_kons FROM konsumen");

      while (RsKons.next()) {
        String kodeKonsumen = RsKons.getString("kd_kons");
        model.addElement(kodeKonsumen);
      }

      RsKons.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    cmbKd_Kons.setModel(model);
  }

  private void baca_barang() {
    DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

    try {
      stm = Con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      RsBrg = stm.executeQuery("SELECT kd_brg FROM barang");

      while (RsBrg.next()) {
        String kodeBarang = RsBrg.getString("kd_brg");
        model.addElement(kodeBarang);
      }

      RsBrg.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    cmbKd_Brg.setModel(model);
  }

  // method baca barang setelah combo barang di klik
  private void detail_barang(String xkode) {
    String sql = "SELECT * FROM barang WHERE kd_brg = ?";

    try {
      pstmt = Con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      pstmt.setString(1, xkode);
      RsBrg = pstmt.executeQuery();

      if (RsBrg.next()) {
        String namaBrg = RsBrg.getString("nm_brg");
        int hargaBrg = RsBrg.getInt("harga");

        txtNm_Brg.setText(namaBrg);
        txtHarga.setText(Integer.toString(hargaBrg));
      } else {
        txtNm_Brg.setText("");
        txtHarga.setText("");
      }
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, e);
    } finally {
      try {
        if (RsBrg != null)
          RsBrg.close();
        if (pstmt != null)
          pstmt.close();
      } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, e);
      }
    }
  }

  // method baca konsumen setelah combo konsumen di klik
  private void detail_konsumen(String xkode) {
    String sql = "SELECT * FROM konsumen WHERE kd_kons = ?";

    try {
      pstmt = Con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      pstmt.setString(1, xkode);
      RsKons = pstmt.executeQuery();

      if (RsKons.next()) {
        String namaKons = RsKons.getString("nm_kons");
        txtNama.setText(namaKons);
      } else {
        txtNama.setText("");
      }
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, e);
    } finally {
      try {
        if (RsKons != null)
          RsKons.close();
        if (pstmt != null)
          pstmt.close();
      } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, e);
      }
    }
  }

  // method set model tabel
  public void inisialisasi_tabel() {
    tblJual.setModel(tableModel);
  }

  // method pengkosongan isian
  private void kosong() {
    txtNoJual.setText("");
    txtNama.setText("");
    txtHarga.setText("");
    txtTotal.setText("");
  }

  // method kosongkan detail jual
  private void kosong_detail() {
    txtNm_Brg.setText("");
    txtHarga.setText("");
    txtJml.setText("");
    txtTotal.setText("");
  }

  // method set tombol on/off
  private void setTombol(boolean t) {
    cmdTambah.setEnabled(t);
    cmdSimpan.setEnabled(!t);
    cmdBatal.setEnabled(!t);
    cmdKeluar.setEnabled(t);
    cmdHapusItem.setEnabled(!t);
    btnPilih.setEnabled(!t);
  }

  // method buat nomor jual otomatis
  private void nomor_jual() {
    try {
      stm = Con.createStatement();
      ResultSet rs = stm.executeQuery("select no_jual from jual");
      int brs = 0;

      while (rs.next()) {
        brs = rs.getRow();
      }
      if (brs == 0)
        txtNoJual.setText("1");
      else {
        int nom = brs + 1;
        txtNoJual.setText(Integer.toString(nom));
      }
      rs.close();
    } catch (SQLException e) {
      System.out.println("Error : " + e);
    }
  }

  // method simpan transaksi penjualan pada table di MySql
  private void simpan_transaksi() {
    String sqlInsertJual = "INSERT INTO jual (no_jual, kd_kons, tgl_jual) VALUES (?, ?, ?)";
    String sqlInsertDjual = "INSERT INTO djual (no_jual, kd_brg, harga_jual, jml_jual) VALUES (?, ?, ?, ?)";

    try {
      // Start a transaction
      Con.setAutoCommit(false);

      String xnojual = txtNoJual.getText();
      format_tanggal();
      String xkode = cmbKd_Kons.getSelectedItem().toString();

      // Insert into jual table
      pstmt = Con.prepareStatement(sqlInsertJual);
      pstmt.setString(1, xnojual);
      pstmt.setString(2, xkode);
      pstmt.setString(3, tanggal);
      pstmt.executeUpdate();

      // Insert into djual table
      pstmt = Con.prepareStatement(sqlInsertDjual);
      for (int i = 0; i < tblJual.getRowCount(); i++) {
        String xkd = (String) tblJual.getValueAt(i, 0);
        double xhrg = (Double) tblJual.getValueAt(i, 2);
        int xjml = (Integer) tblJual.getValueAt(i, 3);

        pstmt.setString(1, xnojual);
        pstmt.setString(2, xkd);
        pstmt.setDouble(3, xhrg);
        pstmt.setInt(4, xjml);
        pstmt.addBatch();
      }
      pstmt.executeBatch();

      // Commit the transaction
      Con.commit();

      JOptionPane.showMessageDialog(null, "Data penjualan berhasil disimpan.");
    } catch (SQLException e) {
      // Rollback transaction if there is an error
      try {
        if (Con != null) {
          Con.rollback();
        }
      } catch (SQLException ex) {
        System.out.println("Rollback failed: " + ex);
      }
      System.out.println("Error: " + e);
    } finally {
      // Restore auto-commit mode
      try {
        if (Con != null) {
          Con.setAutoCommit(true);
        }
      } catch (SQLException ex) {
        System.out.println("Failed to restore auto-commit: " + ex);
      }

      // Close the prepared statement
      try {
        if (pstmt != null) {
          pstmt.close();
        }
      } catch (SQLException ex) {
        System.out.println("Failed to close statement: " + ex);
      }
    }
  }

  // method membuat format tanggal sesuai dengan MySQL
  private void format_tanggal() {
    Calendar c1 = Calendar.getInstance();
    int year = c1.get(Calendar.YEAR);
    int month = c1.get(Calendar.MONTH) + 1;
    int day = c1.get(Calendar.DAY_OF_MONTH);
    tanggal = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(day);
  }

  private class PrintingTask extends SwingWorker<Object, Object> {
    private final MessageFormat headerFormat;
    private final MessageFormat footerFormat;
    private final boolean interactive;
    private volatile boolean complete = false;
    private volatile String message;

    public PrintingTask(MessageFormat header, MessageFormat footer, boolean interactive) {
      this.headerFormat = header;
      this.footerFormat = footer;
      this.interactive = interactive;
    }

    @Override
    protected Object doInBackground() {
      try {
        complete = text.print(headerFormat, footerFormat,
            true, null, null, interactive);
        message = "Printing " + (complete ? "complete" : "canceled");
      } catch (PrinterException ex) {
        message = "Sorry, a printer error occurred";
      } catch (SecurityException ex) {
        message = "Sorry, cannot access the printer due to security reasons";
      }
      return null;
    }

    @Override
    protected void done() {
      showMessage(!complete, message);
    }
  }

  private void showMessage(boolean isError, String message) {
    if (isError) {
      JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  public void itemTerpilih() {
    FrmSelectBarang fDB = new FrmSelectBarang();
    fDB.fAB = this;
    txtId.setText(idBrg);
    cmbKd_Brg.setSelectedItem(idBrg);
    txtNm_Brg.setText(namaBrg);
    txtHarga.setText(hargaBrg);
  }

  // Menghitung Kembalian
  private void hitung_bayar() {
    double xtotal, xbayar, xkembali;

    xtotal = Double.parseDouble(txtTotal.getText());
    xbayar = Double.parseDouble(txtBayar.getText());
    xkembali = xbayar - xtotal;
    String xkembalixx = Double.toString(xkembali);
    txtKembali.setText(xkembalixx);
  }

  // kosongi table penjualan
  private void kosong_table() {
    DefaultTableModel model = (DefaultTableModel) tblJual.getModel();
    model.setRowCount(0); // Menghapus semua baris dalam tabel
  }

  private void aktif(boolean x) {
    txtNoJual.setEnabled(x);
    txtNoJual.setEditable(false);

    txtNama.setEnabled(x);
    txtNama.setEditable(false);

    txtNm_Brg.setEnabled(x);
    txtNm_Brg.setEditable(false);

    txtHarga.setEnabled(x);
    txtHarga.setEditable(false);

    txtJml.setEnabled(x);
    txtTotal.setEnabled(x);
    txtTotal.setEditable(false);

    txtTotal.setEnabled(x);
    txtTotal.setEditable(false);

    txtBayar.setEnabled(x);

    txtKembali.setEnabled(x);
    txtKembali.setEditable(false);

    txtTotal.setEnabled(x);
    txtTotal.setEnabled(x);
    txtId.setEnabled(x);

    cmbKd_Kons.setEnabled(x);
    cmbKd_Brg.setEnabled(x);
    txtTgl.setEnabled(x);
    txtJml.setEditable(x);
  }

  private void open_db() {
    try {
      KoneksiMysql kon = new KoneksiMysql("localhost", "root", "", "pbo");
      Con = kon.getConnection();
    } catch (Exception e) {
      System.out.println("Error : " + e);
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
  // <editor-fold defaultstate="collapsed" desc="Generated
  // <editor-fold defaultstate="collapsed" desc="Generated
  // <editor-fold defaultstate="collapsed" desc="Generated
  // Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    txtNoJual = new javax.swing.JTextField();
    jLabel1 = new javax.swing.JLabel();
    jLabel2 = new javax.swing.JLabel();
    txtTgl = new javax.swing.JSpinner();
    jLabel3 = new javax.swing.JLabel();
    cmbKd_Kons = new javax.swing.JComboBox<>();
    jLabel4 = new javax.swing.JLabel();
    txtNama = new javax.swing.JTextField();
    cmbKd_Brg = new javax.swing.JComboBox<>();
    txtNm_Brg = new javax.swing.JTextField();
    txtHarga = new javax.swing.JTextField();
    txtJml = new javax.swing.JTextField();
    txtTotal = new javax.swing.JTextField();
    cmdHapusItem = new javax.swing.JButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    tblJual = new javax.swing.JTable();
    jLabel5 = new javax.swing.JLabel();
    txtTot = new javax.swing.JTextField();
    cmdTambah = new javax.swing.JButton();
    cmdSimpan = new javax.swing.JButton();
    cmdBatal = new javax.swing.JButton();
    cmdCetak = new javax.swing.JButton();
    cmdKeluar = new javax.swing.JButton();
    btnPilih = new javax.swing.JButton();
    txtId = new javax.swing.JTextField();
    jLabel6 = new javax.swing.JLabel();
    txtBayar = new javax.swing.JTextField();
    txtKembali = new javax.swing.JTextField();
    jLabel7 = new javax.swing.JLabel();
    jScrollPane2 = new javax.swing.JScrollPane();
    text = new javax.swing.JTextArea();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

    jLabel1.setText("No Jual");

    jLabel2.setText("Tgl. Jual");

    txtTgl.setModel(new javax.swing.SpinnerDateModel());

    jLabel3.setText("Kode Konsumen");

    cmbKd_Kons
        .setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
    cmbKd_Kons.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmbKd_KonsActionPerformed(evt);
      }
    });

    jLabel4.setText("Nama Konsumen");

    txtNama.setText("jTextField2");

    cmbKd_Brg.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
    cmbKd_Brg.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmbKd_BrgActionPerformed(evt);
      }
    });

    txtNm_Brg.setText("jTextField3");

    txtHarga.setText("jTextField4");

    txtJml.setText("jTextField5");

    txtTotal.setText("jTextField6");

    cmdHapusItem.setText("Hapus Item");
    cmdHapusItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmdHapusItemActionPerformed(evt);
      }
    });

    tblJual.setModel(new javax.swing.table.DefaultTableModel(
        new Object[][] {
            { null, null, null, null, null },
            { null, null, null, null, null },
            { null, null, null, null, null },
            { null, null, null, null, null }
        },
        new String[] {
            "Kd Barang", "Nama Barang", "Harga Barang", "Jumlah", "Total"
        }) {
      Class[] types = new Class[] {
          java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Integer.class,
          java.lang.Float.class
      };

      public Class getColumnClass(int columnIndex) {
        return types[columnIndex];
      }
    });
    jScrollPane1.setViewportView(tblJual);

    jLabel5.setText("Total");

    txtTot.setText("jTextField7");

    cmdTambah.setText("Tambah");
    cmdTambah.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmdTambahActionPerformed(evt);
      }
    });

    cmdSimpan.setText("Simpan");
    cmdSimpan.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmdSimpanActionPerformed(evt);
      }
    });

    cmdBatal.setText("Batal");
    cmdBatal.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmdBatalActionPerformed(evt);
      }
    });

    cmdCetak.setText("Cetak");
    cmdCetak.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmdCetakActionPerformed(evt);
      }
    });

    cmdKeluar.setText("Keluar");
    cmdKeluar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmdKeluarActionPerformed(evt);
      }
    });

    btnPilih.setText("Pilih Barang");
    btnPilih.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnPilihActionPerformed(evt);
      }
    });

    txtId.setText("jTextField8");

    jLabel6.setText("Bayar");

    txtBayar.setText("jTextField7");
    txtBayar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        txtBayarActionPerformed(evt);
      }
    });

    txtKembali.setText("jTextField7");

    jLabel7.setText("Kembali");

    text.setColumns(20);
    text.setRows(5);
    jScrollPane2.setViewportView(text);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNoJual, javax.swing.GroupLayout.PREFERRED_SIZE, 100,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTgl, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbKd_Kons, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cmdTambah)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdSimpan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdBatal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdCetak)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdKeluar))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cmbKd_Brg, javax.swing.GroupLayout.PREFERRED_SIZE, 92,
                                            javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtNm_Brg, javax.swing.GroupLayout.PREFERRED_SIZE, 118,
                                            javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cmdHapusItem)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnPilih)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 118,
                                            javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtJml, javax.swing.GroupLayout.PREFERRED_SIZE, 118,
                                            javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 119,
                                            javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTot, javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBayar, javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtKembali, javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap()));

    layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
        new java.awt.Component[] { btnPilih, cmbKd_Brg, cmbKd_Kons, cmdBatal, cmdCetak, cmdHapusItem, cmdKeluar,
            cmdSimpan, cmdTambah, jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, txtBayar, txtHarga,
            txtId, txtJml, txtKembali, txtNama, txtNm_Brg, txtNoJual, txtTgl, txtTot, txtTotal });

    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(txtNoJual, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtTgl, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel2)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(cmbKd_Kons, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbKd_Brg, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNm_Brg, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtJml, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdHapusItem)
                    .addComponent(btnPilih)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275,
                    javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTot, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBayar, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtKembali, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdTambah)
                    .addComponent(cmdSimpan)
                    .addComponent(cmdBatal)
                    .addComponent(cmdCetak)
                    .addComponent(cmdKeluar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

    layout.linkSize(javax.swing.SwingConstants.VERTICAL,
        new java.awt.Component[] { cmbKd_Kons, jLabel1, jLabel2, jLabel3, jLabel4, txtNama, txtNoJual, txtTgl });

    layout.linkSize(javax.swing.SwingConstants.VERTICAL,
        new java.awt.Component[] { btnPilih, cmbKd_Brg, cmdBatal, cmdCetak, cmdHapusItem, cmdKeluar, cmdSimpan,
            cmdTambah, jLabel5, jLabel6, jLabel7, txtBayar, txtHarga, txtId, txtJml, txtKembali, txtNm_Brg, txtTot,
            txtTotal });

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void cmdHapusItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmdHapusItemActionPerformed
    try {
      int row = tblJual.getSelectedRow(); // Mendapatkan baris yang dipilih

      if (row != -1) { // Memastikan ada baris yang dipilih
        tableModel.removeRow(row); // Menghapus baris dari tableModel
        inisialisasi_tabel(); // Memperbarui tampilan tabel
      } else {
        JOptionPane.showMessageDialog(null, "Pilih baris yang ingin dihapus");
      }
    } catch (Exception e) {
      System.out.println("Error: " + e);
    }
  }// GEN-LAST:event_cmdHapusItemActionPerformed

  private void cmbKd_BrgActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmbKd_BrgActionPerformed
    String kdBrg = cmbKd_Brg.getSelectedItem().toString();
    detail_barang(kdBrg);
  }// GEN-LAST:event_cmbKd_BrgActionPerformed

  private void txtBayarActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtBayarActionPerformed
    hitung_bayar();
  }// GEN-LAST:event_txtBayarActionPerformed

  private void btnPilihActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnPilihActionPerformed
    FrmSelectBarang fDB = new FrmSelectBarang();
    fDB.fAB = this;
    fDB.setVisible(true);
    fDB.setResizable(false);
  }// GEN-LAST:event_btnPilihActionPerformed

  private void cmdCetakActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmdCetakActionPerformed
    format_tanggal();
    String ctk = "Nota Penjualan\nNo:" + txtNoJual.getText() + "\nTanggal : " + tanggal;
    ctk = ctk + "\n"
        + "--------------------------------------------------------------------------------------------------------------------------------";
    ctk = ctk + "\n" + "Kode\tNama Barang\t\tHarga\tJml\tTotal";
    ctk = ctk + "\n"
        + "--------------------------------------------------------------------------------------------------------------------------------";

    for (int i = 0; i < tblJual.getRowCount(); i++) {
      String xkd = (String) tblJual.getValueAt(i, 0);
      String xnama = (String) tblJual.getValueAt(i, 1);
      double xhrg = (Double) tblJual.getValueAt(i, 2);
      int xjml = (Integer) tblJual.getValueAt(i, 3);
      double xtot = (Double) tblJual.getValueAt(i, 4);
      ctk = ctk + "\n" + xkd + "\t" + xnama + "\t\t" + xhrg + "\t" + xjml + "\t" + xtot;
    }

    ctk = ctk + "\n"
        + "--------------------------------------------------------------------------------------------------------------------------------";
    ctk = ctk + "\n\t\t\t\t\t" + txtTotal.getText();
    text.setText(ctk);

    String headerField = "";
    String footerField = "";
    MessageFormat header = new MessageFormat(headerField);
    MessageFormat footer = new MessageFormat(footerField);
    boolean interactive = true;// interactiveCheck.isSelected();
    boolean background = true;// backgroundCheck.isSelected();
    PrintingTask task = new PrintingTask(header, footer, interactive);

    if (background) {
      task.execute();
    } else {
      task.run();
    }
  }// GEN-LAST:event_cmdCetakActionPerformed

  private void cmdSimpanActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmdSimpanActionPerformed
    simpan_transaksi();
    inisialisasi_tabel();
  }// GEN-LAST:event_cmdSimpanActionPerformed

  private void cmdBatalActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmdBatalActionPerformed
    aktif(false);
    setTombol(true);
    kosong();
    kosong_detail();
    kosong_table();
    text.setText("");
  }// GEN-LAST:event_cmdBatalActionPerformed

  private void cmdKeluarActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmdKeluarActionPerformed
    dispose();
  }// GEN-LAST:event_cmdKeluarActionPerformed

  private void cmdTambahActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmdTambahActionPerformed
    aktif(true);
    setTombol(false);
    baca_barang();
    baca_konsumen();
    kosong();
    kosong_detail();
    kosong_table();
    nomor_jual();
  }// GEN-LAST:event_cmdTambahActionPerformed

  private void cmbKd_KonsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmbKd_KonsActionPerformed
    String kdKons = cmbKd_Kons.getSelectedItem().toString();
    detail_konsumen(kdKons);
  }// GEN-LAST:event_cmbKd_KonsActionPerformed

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
      java.util.logging.Logger.getLogger(FrmTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(FrmTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(FrmTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(FrmTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    // </editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new FrmTransaksi().setVisible(true);
      }
    });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnPilih;
  private javax.swing.JComboBox<String> cmbKd_Brg;
  private javax.swing.JComboBox<String> cmbKd_Kons;
  private javax.swing.JButton cmdBatal;
  private javax.swing.JButton cmdCetak;
  private javax.swing.JButton cmdHapusItem;
  private javax.swing.JButton cmdKeluar;
  private javax.swing.JButton cmdSimpan;
  private javax.swing.JButton cmdTambah;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JTable tblJual;
  private javax.swing.JTextArea text;
  private javax.swing.JTextField txtBayar;
  private javax.swing.JTextField txtHarga;
  private javax.swing.JTextField txtId;
  private javax.swing.JTextField txtJml;
  private javax.swing.JTextField txtKembali;
  private javax.swing.JTextField txtNama;
  private javax.swing.JTextField txtNm_Brg;
  private javax.swing.JTextField txtNoJual;
  private javax.swing.JSpinner txtTgl;
  private javax.swing.JTextField txtTot;
  private javax.swing.JTextField txtTotal;
  // End of variables declaration//GEN-END:variables
}
