

package keuangan;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariPetugas;
import simrskhanza.DlgPenanggungJawab;

/**
 *
 * @author perpustakaan
 */
public final class KeuanganPenagihanPiutangPasien extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private DlgPenanggungJawab penjab=new DlgPenanggungJawab(null,false);
    private DlgAkunPenagihanPiutang akuntagih=new DlgAkunPenagihanPiutang(null,false);
    private int jml=0,i=0,index=0;
    private String status="";
    private double total=0;
    private boolean sukses=true;
    private double[] piutang;
    private String[] norawat,tglpiutang,norm,pasien,statusrawat,carabayar,nokartu,asalperusahaan,nip,nonota;
    private boolean[] pilih;
    private DlgCariPetugas petugas=new DlgCariPetugas(null,false);

    /** Creates new form DlgLhtBiaya
     * @param parent
     * @param modal */
    public KeuanganPenagihanPiutangPasien(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);
        setSize(885,674);

        Object[] rowRwJlDr={
            "P","No.Rawat/No.tagihan","Tgl.Piutang","No.RM","Nama Pasien","Status","Piutang","Penjamin","No.Peserta","Asal Perusahaan","NIP/NRP","No.Nota"
        };
        tabMode=new DefaultTableModel(null,rowRwJlDr){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if (colIndex==0) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.Boolean.class,java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,
                java.lang.Object.class,java.lang.Double.class,java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,
                java.lang.Object.class,java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbBangsal.setModel(tabMode);
        //tbBangsal.setDefaultRenderer(Object.class, new WarnaTable(jPanel2.getBackground(),tbBangsal.getBackground()));
        tbBangsal.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbBangsal.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 12; i++) {
            TableColumn column = tbBangsal.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(115);
            }else if(i==2){
                column.setPreferredWidth(70);
            }else if(i==3){
                column.setPreferredWidth(65);
            }else if(i==4){
                column.setPreferredWidth(170);
            }else if(i==5){
                column.setPreferredWidth(50);
            }else if(i==6){
                column.setPreferredWidth(90);
            }else if(i==7){
                column.setPreferredWidth(130);
            }else if(i==8){
                column.setPreferredWidth(110);
            }else if(i==9){
                column.setPreferredWidth(130);
            }else if(i==10){
                column.setPreferredWidth(110);
            }else if(i==11){
                column.setPreferredWidth(110);
            }
        }
        tbBangsal.setDefaultRenderer(Object.class, new WarnaTable());

        NoPenagihan.setDocument(new batasInput((byte)17).getKata(NoPenagihan));
        Catatan.setDocument(new batasInput((int)100).getKata(Catatan));
        TCari.setDocument(new batasInput((int)100).getKata(TCari));
        
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
            });
        }  
        
        penjab.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(penjab.getTable().getSelectedRow()!= -1){
                    kdpenjab.setText(penjab.getTable().getValueAt(penjab.getTable().getSelectedRow(),1).toString());
                    nmpenjab.setText(penjab.getTable().getValueAt(penjab.getTable().getSelectedRow(),2).toString());
                    tampil();
                }      
                kdpenjab.requestFocus();
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {penjab.emptTeks();}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });   
        
        penjab.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    penjab.dispose();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        akuntagih.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(akuntagih.getTable().getSelectedRow()!= -1){
                    KdAkun.setText(akuntagih.getTable().getValueAt(akuntagih.getTable().getSelectedRow(),1).toString());
                    NamaBank.setText(akuntagih.getTable().getValueAt(akuntagih.getTable().getSelectedRow(),3).toString());
                    AtasNama.setText(akuntagih.getTable().getValueAt(akuntagih.getTable().getSelectedRow(),4).toString());
                    NoRek.setText(akuntagih.getTable().getValueAt(akuntagih.getTable().getSelectedRow(),5).toString());
                }      
                KdAkun.requestFocus();
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });   
        
        akuntagih.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    akuntagih.dispose();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        petugas.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(petugas.getTable().getSelectedRow()!= -1){                   
                    kdptg.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                    nmptg.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                }            
                kdptg.requestFocus();
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        }); 

    }
    

     double sisapiutang=0,cicilan=0;

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TKd = new widget.TextBox();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnDetailPiutang = new javax.swing.JMenuItem();
        KdAkun = new widget.TextBox();
        AtasNama = new widget.TextBox();
        NoRek = new widget.TextBox();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbBangsal = new widget.Table();
        panelisi4 = new widget.panelisi();
        label32 = new widget.Label();
        Tanggal = new widget.Tanggal();
        label19 = new widget.Label();
        kdpenjab = new widget.TextBox();
        nmpenjab = new widget.TextBox();
        BtnPenjamin = new widget.Button();
        label13 = new widget.Label();
        kdptg = new widget.TextBox();
        nmptg = new widget.TextBox();
        btnPetugas = new widget.Button();
        label15 = new widget.Label();
        NoPenagihan = new widget.TextBox();
        label33 = new widget.Label();
        Tanggal1 = new widget.Tanggal();
        label23 = new widget.Label();
        Catatan = new widget.TextBox();
        label24 = new widget.Label();
        NoOrder1 = new widget.TextBox();
        jLabel13 = new widget.Label();
        cmbStatus = new widget.ComboBox();
        label20 = new widget.Label();
        BtnPenagihan = new widget.Button();
        NamaBank = new widget.TextBox();
        label14 = new widget.Label();
        kdptg1 = new widget.TextBox();
        nmptg1 = new widget.TextBox();
        btnPetugas1 = new widget.Button();
        panelisi1 = new widget.panelisi();
        label10 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari1 = new widget.Button();
        BtnCari = new widget.Button();
        BtnKeluar = new widget.Button();
        BtnSimpan = new widget.Button();
        BtnPrint = new widget.Button();
        jLabel10 = new javax.swing.JLabel();
        LCountDipilih2 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        LCountBelumDibayar2 = new javax.swing.JLabel();
        jLabel11 = new widget.Label();
        LCountBelumDibayar1 = new widget.Label();
        LCountDipilih1 = new widget.Label();
        jLabel14 = new widget.Label();
        BtnAll = new widget.Button();

        TKd.setForeground(new java.awt.Color(255, 255, 255));
        TKd.setName("TKd"); // NOI18N

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnDetailPiutang.setBackground(new java.awt.Color(255, 255, 254));
        MnDetailPiutang.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnDetailPiutang.setForeground(new java.awt.Color(50, 50, 50));
        MnDetailPiutang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnDetailPiutang.setText("Detail Piutang");
        MnDetailPiutang.setName("MnDetailPiutang"); // NOI18N
        MnDetailPiutang.setPreferredSize(new java.awt.Dimension(200, 28));
        MnDetailPiutang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnDetailPiutangActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnDetailPiutang);

        KdAkun.setEditable(false);
        KdAkun.setName("KdAkun"); // NOI18N
        KdAkun.setPreferredSize(new java.awt.Dimension(60, 23));
        KdAkun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdAkunKeyPressed(evt);
            }
        });

        AtasNama.setEditable(false);
        AtasNama.setName("AtasNama"); // NOI18N
        AtasNama.setPreferredSize(new java.awt.Dimension(170, 23));

        NoRek.setEditable(false);
        NoRek.setName("NoRek"); // NOI18N
        NoRek.setPreferredSize(new java.awt.Dimension(170, 23));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Penagihan Piutang Pasien ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setComponentPopupMenu(jPopupMenu1);
        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbBangsal.setComponentPopupMenu(jPopupMenu1);
        tbBangsal.setName("tbBangsal"); // NOI18N
        tbBangsal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbBangsalMouseClicked(evt);
            }
        });
        tbBangsal.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbBangsalPropertyChange(evt);
            }
        });
        tbBangsal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbBangsalKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbBangsal);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        panelisi4.setName("panelisi4"); // NOI18N
        panelisi4.setPreferredSize(new java.awt.Dimension(100, 134));
        panelisi4.setLayout(null);

        label32.setText("Tgl. Penagihan :");
        label32.setName("label32"); // NOI18N
        label32.setPreferredSize(new java.awt.Dimension(90, 23));
        panelisi4.add(label32);
        label32.setBounds(0, 40, 92, 23);

        Tanggal.setDisplayFormat("dd-MM-yyyy");
        Tanggal.setName("Tanggal"); // NOI18N
        Tanggal.setPreferredSize(new java.awt.Dimension(90, 23));
        Tanggal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalKeyPressed(evt);
            }
        });
        panelisi4.add(Tanggal);
        Tanggal.setBounds(96, 40, 90, 23);

        label19.setText("Penjamin :");
        label19.setName("label19"); // NOI18N
        label19.setPreferredSize(new java.awt.Dimension(80, 23));
        panelisi4.add(label19);
        label19.setBounds(360, 10, 90, 23);

        kdpenjab.setEditable(false);
        kdpenjab.setName("kdpenjab"); // NOI18N
        kdpenjab.setPreferredSize(new java.awt.Dimension(60, 23));
        kdpenjab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdpenjabKeyPressed(evt);
            }
        });
        panelisi4.add(kdpenjab);
        kdpenjab.setBounds(454, 10, 64, 23);

        nmpenjab.setEditable(false);
        nmpenjab.setName("nmpenjab"); // NOI18N
        nmpenjab.setPreferredSize(new java.awt.Dimension(170, 23));
        panelisi4.add(nmpenjab);
        nmpenjab.setBounds(520, 10, 213, 23);

        BtnPenjamin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPenjamin.setMnemonic('3');
        BtnPenjamin.setToolTipText("Alt+3");
        BtnPenjamin.setName("BtnPenjamin"); // NOI18N
        BtnPenjamin.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnPenjamin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPenjaminActionPerformed(evt);
            }
        });
        BtnPenjamin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnPenjaminKeyPressed(evt);
            }
        });
        panelisi4.add(BtnPenjamin);
        BtnPenjamin.setBounds(735, 10, 28, 23);

        label13.setText("Bagian Penagihan :");
        label13.setName("label13"); // NOI18N
        label13.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi4.add(label13);
        label13.setBounds(330, 40, 120, 23);

        kdptg.setEditable(false);
        kdptg.setName("kdptg"); // NOI18N
        kdptg.setPreferredSize(new java.awt.Dimension(80, 23));
        kdptg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdptgKeyPressed(evt);
            }
        });
        panelisi4.add(kdptg);
        kdptg.setBounds(454, 40, 95, 23);

        nmptg.setEditable(false);
        nmptg.setName("nmptg"); // NOI18N
        nmptg.setPreferredSize(new java.awt.Dimension(207, 23));
        panelisi4.add(nmptg);
        nmptg.setBounds(551, 40, 182, 23);

        btnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPetugas.setMnemonic('2');
        btnPetugas.setToolTipText("Alt+2");
        btnPetugas.setName("btnPetugas"); // NOI18N
        btnPetugas.setPreferredSize(new java.awt.Dimension(28, 23));
        btnPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPetugasActionPerformed(evt);
            }
        });
        btnPetugas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnPetugasKeyPressed(evt);
            }
        });
        panelisi4.add(btnPetugas);
        btnPetugas.setBounds(735, 40, 28, 23);

        label15.setText("No.Penagihan :");
        label15.setName("label15"); // NOI18N
        label15.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi4.add(label15);
        label15.setBounds(0, 10, 92, 23);

        NoPenagihan.setName("NoPenagihan"); // NOI18N
        NoPenagihan.setPreferredSize(new java.awt.Dimension(207, 23));
        NoPenagihan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoPenagihanKeyPressed(evt);
            }
        });
        panelisi4.add(NoPenagihan);
        NoPenagihan.setBounds(96, 10, 226, 23);

        label33.setText("Jatuh Tempo :");
        label33.setName("label33"); // NOI18N
        label33.setPreferredSize(new java.awt.Dimension(90, 23));
        panelisi4.add(label33);
        label33.setBounds(0, 70, 92, 23);

        Tanggal1.setDisplayFormat("dd-MM-yyyy");
        Tanggal1.setName("Tanggal1"); // NOI18N
        Tanggal1.setPreferredSize(new java.awt.Dimension(90, 23));
        Tanggal1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Tanggal1KeyPressed(evt);
            }
        });
        panelisi4.add(Tanggal1);
        Tanggal1.setBounds(96, 70, 90, 23);

        label23.setText("Catatan :");
        label23.setName("label23"); // NOI18N
        label23.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi4.add(label23);
        label23.setBounds(360, 100, 90, 23);

        Catatan.setName("Catatan"); // NOI18N
        Catatan.setPreferredSize(new java.awt.Dimension(207, 23));
        Catatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CatatanKeyPressed(evt);
            }
        });
        panelisi4.add(Catatan);
        Catatan.setBounds(453, 100, 310, 23);

        label24.setText("Tempo (Hari) :");
        label24.setName("label24"); // NOI18N
        label24.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi4.add(label24);
        label24.setBounds(190, 70, 76, 23);

        NoOrder1.setEditable(false);
        NoOrder1.setText("0");
        NoOrder1.setName("NoOrder1"); // NOI18N
        NoOrder1.setPreferredSize(new java.awt.Dimension(207, 23));
        NoOrder1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoOrder1KeyPressed(evt);
            }
        });
        panelisi4.add(NoOrder1);
        NoOrder1.setBounds(270, 70, 52, 23);

        jLabel13.setText("Status :");
        jLabel13.setName("jLabel13"); // NOI18N
        jLabel13.setPreferredSize(new java.awt.Dimension(50, 23));
        panelisi4.add(jLabel13);
        jLabel13.setBounds(190, 40, 43, 23);

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semua", "Ralan", "Ranap" }));
        cmbStatus.setName("cmbStatus"); // NOI18N
        cmbStatus.setPreferredSize(new java.awt.Dimension(115, 23));
        cmbStatus.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbStatusItemStateChanged(evt);
            }
        });
        panelisi4.add(cmbStatus);
        cmbStatus.setBounds(237, 40, 85, 23);

        label20.setText("Transfer :");
        label20.setName("label20"); // NOI18N
        label20.setPreferredSize(new java.awt.Dimension(80, 23));
        panelisi4.add(label20);
        label20.setBounds(0, 100, 92, 23);

        BtnPenagihan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPenagihan.setMnemonic('3');
        BtnPenagihan.setToolTipText("Alt+3");
        BtnPenagihan.setName("BtnPenagihan"); // NOI18N
        BtnPenagihan.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnPenagihan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPenagihanActionPerformed(evt);
            }
        });
        BtnPenagihan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnPenagihanKeyPressed(evt);
            }
        });
        panelisi4.add(BtnPenagihan);
        BtnPenagihan.setBounds(294, 100, 28, 23);

        NamaBank.setEditable(false);
        NamaBank.setName("NamaBank"); // NOI18N
        NamaBank.setPreferredSize(new java.awt.Dimension(170, 23));
        panelisi4.add(NamaBank);
        NamaBank.setBounds(96, 100, 195, 23);

        label14.setText("Menyetujui :");
        label14.setName("label14"); // NOI18N
        label14.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi4.add(label14);
        label14.setBounds(330, 70, 120, 23);

        kdptg1.setEditable(false);
        kdptg1.setName("kdptg1"); // NOI18N
        kdptg1.setPreferredSize(new java.awt.Dimension(80, 23));
        kdptg1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdptg1KeyPressed(evt);
            }
        });
        panelisi4.add(kdptg1);
        kdptg1.setBounds(454, 70, 95, 23);

        nmptg1.setEditable(false);
        nmptg1.setName("nmptg1"); // NOI18N
        nmptg1.setPreferredSize(new java.awt.Dimension(207, 23));
        panelisi4.add(nmptg1);
        nmptg1.setBounds(551, 70, 182, 23);

        btnPetugas1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPetugas1.setMnemonic('2');
        btnPetugas1.setToolTipText("Alt+2");
        btnPetugas1.setName("btnPetugas1"); // NOI18N
        btnPetugas1.setPreferredSize(new java.awt.Dimension(28, 23));
        btnPetugas1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPetugas1ActionPerformed(evt);
            }
        });
        btnPetugas1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnPetugas1KeyPressed(evt);
            }
        });
        panelisi4.add(btnPetugas1);
        btnPetugas1.setBounds(735, 70, 28, 23);

        internalFrame1.add(panelisi4, java.awt.BorderLayout.PAGE_START);

        panelisi1.setName("panelisi1"); // NOI18N
        panelisi1.setPreferredSize(new java.awt.Dimension(100, 87));
        panelisi1.setLayout(null);

        label10.setText("Key Word :");
        label10.setName("label10"); // NOI18N
        label10.setPreferredSize(new java.awt.Dimension(75, 23));
        panelisi1.add(label10);
        label10.setBounds(210, 45, 75, 23);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(150, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelisi1.add(TCari);
        TCari.setBounds(289, 45, 200, 23);

        BtnCari1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari1.setMnemonic('1');
        BtnCari1.setToolTipText("Alt+1");
        BtnCari1.setName("BtnCari1"); // NOI18N
        BtnCari1.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCari1ActionPerformed(evt);
            }
        });
        BtnCari1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCari1KeyPressed(evt);
            }
        });
        panelisi1.add(BtnCari1);
        BtnCari1.setBounds(491, 45, 28, 23);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnCari.setMnemonic('C');
        BtnCari.setText("Cari");
        BtnCari.setToolTipText("Alt+C");
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        BtnCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariKeyPressed(evt);
            }
        });
        panelisi1.add(BtnCari);
        BtnCari.setBounds(565, 42, 100, 30);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        BtnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnKeluarKeyPressed(evt);
            }
        });
        panelisi1.add(BtnKeluar);
        BtnKeluar.setBounds(670, 42, 100, 30);

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });
        BtnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSimpanKeyPressed(evt);
            }
        });
        panelisi1.add(BtnSimpan);
        BtnSimpan.setBounds(5, 42, 100, 30);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setName("BtnPrint"); // NOI18N
        BtnPrint.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrintActionPerformed(evt);
            }
        });
        BtnPrint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnPrintKeyPressed(evt);
            }
        });
        panelisi1.add(BtnPrint);
        BtnPrint.setBounds(110, 42, 100, 30);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(50, 50, 50));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Belum Dibayar :");
        jLabel10.setName("jLabel10"); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(87, 23));
        panelisi1.add(jLabel10);
        jLabel10.setBounds(108, 10, 87, 23);

        LCountDipilih2.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        LCountDipilih2.setForeground(new java.awt.Color(50, 50, 50));
        LCountDipilih2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCountDipilih2.setText("0");
        LCountDipilih2.setName("LCountDipilih2"); // NOI18N
        LCountDipilih2.setPreferredSize(new java.awt.Dimension(230, 23));
        panelisi1.add(LCountDipilih2);
        LCountDipilih2.setBounds(602, 10, 170, 23);

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(50, 50, 50));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Piutang Dipilih :");
        jLabel12.setName("jLabel12"); // NOI18N
        jLabel12.setPreferredSize(new java.awt.Dimension(107, 23));
        panelisi1.add(jLabel12);
        jLabel12.setBounds(507, 10, 90, 23);

        LCountBelumDibayar2.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        LCountBelumDibayar2.setForeground(new java.awt.Color(50, 50, 50));
        LCountBelumDibayar2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCountBelumDibayar2.setText("0");
        LCountBelumDibayar2.setName("LCountBelumDibayar2"); // NOI18N
        LCountBelumDibayar2.setPreferredSize(new java.awt.Dimension(230, 23));
        panelisi1.add(LCountBelumDibayar2);
        LCountBelumDibayar2.setBounds(200, 10, 170, 23);

        jLabel11.setText("Record :");
        jLabel11.setName("jLabel11"); // NOI18N
        jLabel11.setPreferredSize(new java.awt.Dimension(70, 30));
        panelisi1.add(jLabel11);
        jLabel11.setBounds(0, 10, 55, 23);

        LCountBelumDibayar1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCountBelumDibayar1.setText("0");
        LCountBelumDibayar1.setName("LCountBelumDibayar1"); // NOI18N
        LCountBelumDibayar1.setPreferredSize(new java.awt.Dimension(72, 30));
        panelisi1.add(LCountBelumDibayar1);
        LCountBelumDibayar1.setBounds(60, 10, 50, 23);

        LCountDipilih1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCountDipilih1.setText("0");
        LCountDipilih1.setName("LCountDipilih1"); // NOI18N
        LCountDipilih1.setPreferredSize(new java.awt.Dimension(72, 30));
        panelisi1.add(LCountDipilih1);
        LCountDipilih1.setBounds(460, 10, 50, 23);

        jLabel14.setText("Record Dipilih :");
        jLabel14.setName("jLabel14"); // NOI18N
        jLabel14.setPreferredSize(new java.awt.Dimension(70, 30));
        panelisi1.add(jLabel14);
        jLabel14.setBounds(375, 10, 80, 23);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllActionPerformed(evt);
            }
        });
        BtnAll.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnAllKeyPressed(evt);
            }
        });
        panelisi1.add(BtnAll);
        BtnAll.setBounds(521, 45, 28, 23);

        internalFrame1.add(panelisi1, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tbBangsalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbBangsalMouseClicked
        if(tabMode.getRowCount()!=0){
            if(tbBangsal.getSelectedColumn()==0){
                getdata();
            }
        }
}//GEN-LAST:event_tbBangsalMouseClicked

    private void tbBangsalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbBangsalKeyPressed
        if(tabMode.getRowCount()!=0){
            if(evt.getKeyCode()==KeyEvent.VK_ENTER){
                getdata();
            }
        }
}//GEN-LAST:event_tbBangsalKeyPressed

private void MnDetailPiutangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnDetailPiutangActionPerformed
     if(tabMode.getRowCount()==0){
        JOptionPane.showMessageDialog(null,"Maaf, table masih kosong...!!!!");
        TCari.requestFocus();
    }else{
         if(tbBangsal.getSelectedRow()!= -1){
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            status=Sequel.cariIsi("select status_lanjut from reg_periksa where no_rawat=?",tbBangsal.getValueAt(tbBangsal.getSelectedRow(),1).toString());   
            if(status.equals("Ralan")){
                DlgBilingRalan billing=new DlgBilingRalan(null,false);
                billing.TNoRw.setText(tbBangsal.getValueAt(tbBangsal.getSelectedRow(),1).toString());
                billing.isCek();
                billing.isRawat();
                if(Sequel.cariInteger("select count(no_rawat) from piutang_pasien where no_rawat=?",billing.TNoRw.getText())>0){
                    billing.setPiutang();
                }
                billing.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
                billing.setLocationRelativeTo(internalFrame1);
                billing.setVisible(true); 
            }else if(status.equals("Ranap")){
                DlgBilingRanap billing=new DlgBilingRanap(null,false);
                billing.TNoRw.setText(tbBangsal.getValueAt(tbBangsal.getSelectedRow(),1).toString());            
                billing.isCek();
                billing.isRawat();
                billing.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
                billing.setLocationRelativeTo(internalFrame1);
                billing.setVisible(true); 
            }
            this.setCursor(Cursor.getDefaultCursor());
         }else{
             JOptionPane.showMessageDialog(null,"Silahkan pilih data terlebih dahulu...!!");
         }                   
    } 
}//GEN-LAST:event_MnDetailPiutangActionPerformed

    private void kdpenjabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdpenjabKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            Sequel.cariIsi("select png_jawab from penjab where kd_pj=?", nmpenjab,kdpenjab.getText());
        }else if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            Sequel.cariIsi("select png_jawab from penjab where kd_pj=?", nmpenjab,kdpenjab.getText());
            BtnSimpan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            Sequel.cariIsi("select png_jawab from penjab where kd_pj=?", nmpenjab,kdpenjab.getText());
            TCari.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            BtnPenjaminActionPerformed(null);
        }
    }//GEN-LAST:event_kdpenjabKeyPressed

    private void BtnPenjaminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPenjaminActionPerformed
        penjab.isCek();
        penjab.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        penjab.setLocationRelativeTo(internalFrame1);
        penjab.setAlwaysOnTop(false);
        penjab.setVisible(true);
    }//GEN-LAST:event_BtnPenjaminActionPerformed

    private void BtnPenjaminKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPenjaminKeyPressed
        Valid.pindah(evt,NoPenagihan,btnPetugas);
    }//GEN-LAST:event_BtnPenjaminKeyPressed

    private void TanggalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalKeyPressed
        //Valid.pindah(evt,kdpenjab,nama_bayar);
    }//GEN-LAST:event_TanggalKeyPressed

    private void tbBangsalPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbBangsalPropertyChange
        
    }//GEN-LAST:event_tbBangsalPropertyChange

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            tampil();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari1.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            //kdsup.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            //tbDokter.requestFocus();
        }
    }//GEN-LAST:event_TCariKeyPressed

    private void BtnCari1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCari1ActionPerformed
        tampil();
    }//GEN-LAST:event_BtnCari1ActionPerformed

    private void BtnCari1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCari1KeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            tampil();
        }else{
            Valid.pindah(evt, BtnSimpan, BtnKeluar);
        }
    }//GEN-LAST:event_BtnCari1KeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        /*form.emptTeks();
        form.isCek();
        form.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        form.setLocationRelativeTo(internalFrame1);
        form.setAlwaysOnTop(false);
        form.setVisible(true);*/
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnSimpan,BtnKeluar);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        penjab.dispose();
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{Valid.pindah(evt,BtnSimpan,TCari);}
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(NoPenagihan.getText().trim().equals("")){
            Valid.textKosong(NoPenagihan,"No.Penagihan");
        }else if(kdpenjab.getText().trim().equals("")||nmpenjab.getText().trim().equals("")){
            Valid.textKosong(BtnPenjamin,"Penjamin");
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
            Valid.pindah(evt,BtnPenagihan,TCari);
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnPrintActionPerformed(null);
        }else{
            Valid.pindah(evt,BtnSimpan,TCari);
        }
    }//GEN-LAST:event_BtnPrintKeyPressed

    private void kdptgKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdptgKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            Sequel.cariIsi("select nama from petugas where nip=?", nmptg,kdptg.getText());
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            Sequel.cariIsi("select nama from petugas where nip=?", nmptg,kdptg.getText());
            kdpenjab.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            Sequel.cariIsi("select nama from petugas where nip=?", nmptg,kdptg.getText());
            Catatan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            btnPetugasActionPerformed(null);
        }
    }//GEN-LAST:event_kdptgKeyPressed

    private void btnPetugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPetugasActionPerformed
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setAlwaysOnTop(false);
        petugas.setVisible(true);
    }//GEN-LAST:event_btnPetugasActionPerformed

    private void NoPenagihanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoPenagihanKeyPressed
        Valid.pindah(evt, BtnSimpan,BtnPenjamin);
    }//GEN-LAST:event_NoPenagihanKeyPressed

    private void Tanggal1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Tanggal1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Tanggal1KeyPressed

    private void CatatanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CatatanKeyPressed
        Valid.pindah(evt, btnPetugas, BtnPenagihan);
    }//GEN-LAST:event_CatatanKeyPressed

    private void NoOrder1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoOrder1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoOrder1KeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        cmbStatus.setSelectedItem(0);
        kdpenjab.setText("");
        nmpenjab.setText("");
        tampil();
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnAllActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnPrint, BtnKeluar);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void cmbStatusItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbStatusItemStateChanged
        tampil();
    }//GEN-LAST:event_cmbStatusItemStateChanged

    private void KdAkunKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdAkunKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_KdAkunKeyPressed

    private void BtnPenagihanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPenagihanActionPerformed
        akuntagih.isCek();
        akuntagih.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        akuntagih.setLocationRelativeTo(internalFrame1);
        akuntagih.setAlwaysOnTop(false);
        akuntagih.setVisible(true);
    }//GEN-LAST:event_BtnPenagihanActionPerformed

    private void BtnPenagihanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPenagihanKeyPressed
        Valid.pindah(evt, Catatan, BtnSimpan);
    }//GEN-LAST:event_BtnPenagihanKeyPressed

    private void btnPetugasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnPetugasKeyPressed
        Valid.pindah(evt, BtnPenjamin,Catatan);
    }//GEN-LAST:event_btnPetugasKeyPressed

    private void kdptg1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdptg1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_kdptg1KeyPressed

    private void btnPetugas1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPetugas1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPetugas1ActionPerformed

    private void btnPetugas1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnPetugas1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPetugas1KeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            KeuanganPenagihanPiutangPasien dialog = new KeuanganPenagihanPiutangPasien(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.TextBox AtasNama;
    private widget.Button BtnAll;
    private widget.Button BtnCari;
    private widget.Button BtnCari1;
    private widget.Button BtnKeluar;
    private widget.Button BtnPenagihan;
    private widget.Button BtnPenjamin;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.TextBox Catatan;
    private widget.TextBox KdAkun;
    private widget.Label LCountBelumDibayar1;
    private javax.swing.JLabel LCountBelumDibayar2;
    private widget.Label LCountDipilih1;
    private javax.swing.JLabel LCountDipilih2;
    private javax.swing.JMenuItem MnDetailPiutang;
    private widget.TextBox NamaBank;
    private widget.TextBox NoOrder1;
    private widget.TextBox NoPenagihan;
    private widget.TextBox NoRek;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TKd;
    private widget.Tanggal Tanggal;
    private widget.Tanggal Tanggal1;
    private widget.Button btnPetugas;
    private widget.Button btnPetugas1;
    private widget.ComboBox cmbStatus;
    private widget.InternalFrame internalFrame1;
    private javax.swing.JLabel jLabel10;
    private widget.Label jLabel11;
    private javax.swing.JLabel jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.TextBox kdpenjab;
    private widget.TextBox kdptg;
    private widget.TextBox kdptg1;
    private widget.Label label10;
    private widget.Label label13;
    private widget.Label label14;
    private widget.Label label15;
    private widget.Label label19;
    private widget.Label label20;
    private widget.Label label23;
    private widget.Label label24;
    private widget.Label label32;
    private widget.Label label33;
    private widget.TextBox nmpenjab;
    private widget.TextBox nmptg;
    private widget.TextBox nmptg1;
    private widget.panelisi panelisi1;
    private widget.panelisi panelisi4;
    private widget.Table tbBangsal;
    // End of variables declaration//GEN-END:variables

    public void tampil(){
        try{
            jml=0;
            for(i=0;i<tbBangsal.getRowCount();i++){
                if(tbBangsal.getValueAt(i,0).toString().equals("true")){
                    jml++;
                }           
            }

            pilih=null;
            pilih=new boolean[jml]; 
            norawat=null;
            norawat=new String[jml];
            tglpiutang=null;
            tglpiutang=new String[jml];
            norm=null;
            norm=new String[jml];
            pasien=null;
            pasien=new String[jml];
            statusrawat=null;
            statusrawat=new String[jml];
            carabayar=null;
            carabayar=new String[jml];
            asalperusahaan=null;
            asalperusahaan=new String[jml];
            nonota=null;
            nonota=new String[jml];
            nokartu=null;
            nokartu=new String[jml];
            nip=null;
            nip=new String[jml];
            piutang=null;
            piutang=new double[jml];

            index=0;        
            for(i=0;i<tbBangsal.getRowCount();i++){
                if(tbBangsal.getValueAt(i,0).toString().equals("true")){
                    pilih[index]=true;
                    norawat[index]=tbBangsal.getValueAt(i,1).toString();
                    tglpiutang[index]=tbBangsal.getValueAt(i,2).toString();
                    norm[index]=tbBangsal.getValueAt(i,3).toString();
                    pasien[index]=tbBangsal.getValueAt(i,4).toString();
                    statusrawat[index]=tbBangsal.getValueAt(i,5).toString();
                    piutang[index]=Double.parseDouble(tbBangsal.getValueAt(i,6).toString());
                    carabayar[index]=tbBangsal.getValueAt(i,7).toString();
                    nokartu[index]=tbBangsal.getValueAt(i,8).toString();
                    asalperusahaan[index]=tbBangsal.getValueAt(i,9).toString();
                    nip[index]=tbBangsal.getValueAt(i,10).toString();
                    nonota[index]=tbBangsal.getValueAt(i,11).toString();
                    index++;
                }
            } 
            
            Valid.tabelKosong(tabMode);
            
            for(i=0;i<jml;i++){
                tabMode.addRow(new Object[] {
                    pilih[i],norawat[i],tglpiutang[i],norm[i],pasien[i],statusrawat[i],piutang[i],carabayar[i],nokartu[i],asalperusahaan[i],nip[i],nonota[i]
                });
            }
            
            sisapiutang=0;
            if(cmbStatus.getSelectedItem().equals("Semua")){
                ps=koneksi.prepareStatement(
                       "select piutang_pasien.no_rawat, piutang_pasien.tgl_piutang, piutang_pasien.no_rkm_medis,pasien.nm_pasien,penjab.png_jawab,pasien.no_peserta,"+
                       "detail_piutang_pasien.totalpiutang,detail_piutang_pasien.sisapiutang,perusahaan_pasien.nama_perusahaan,nota_jalan.no_nota,pasien.nip "+
                       "from piutang_pasien inner join pasien on piutang_pasien.no_rkm_medis=pasien.no_rkm_medis "+
                       "inner join reg_periksa on piutang_pasien.no_rawat=reg_periksa.no_rawat "+
                       "inner join detail_piutang_pasien on piutang_pasien.no_rawat=detail_piutang_pasien.no_rawat "+
                       "inner join penjab on detail_piutang_pasien.kd_pj=penjab.kd_pj "+
                       "inner join nota_jalan on nota_jalan.no_rawat=reg_periksa.no_rawat "+
                       "inner join perusahaan_pasien on perusahaan_pasien.kode_perusahaan=pasien.perusahaan_pasien "+
                       "where "+
                       "detail_piutang_pasien.sisapiutang>=1 and concat(detail_piutang_pasien.kd_pj,penjab.png_jawab) like ? and piutang_pasien.no_rawat like ? or "+ 
                       "detail_piutang_pasien.sisapiutang>=1 and concat(detail_piutang_pasien.kd_pj,penjab.png_jawab) like ? and piutang_pasien.no_rkm_medis like ? or "+
                       "detail_piutang_pasien.sisapiutang>=1 and concat(detail_piutang_pasien.kd_pj,penjab.png_jawab) like ? and pasien.nm_pasien like ? or "+
                       "detail_piutang_pasien.sisapiutang>=1 and concat(detail_piutang_pasien.kd_pj,penjab.png_jawab) like ? and nota_jalan.no_nota like ? order by piutang_pasien.tgl_piutang");
                try {
                    ps.setString(1,"%"+kdpenjab.getText()+nmpenjab.getText()+"%");
                    ps.setString(2,"%"+TCari.getText()+"%");
                    ps.setString(3,"%"+kdpenjab.getText()+nmpenjab.getText()+"%");
                    ps.setString(4,"%"+TCari.getText()+"%");
                    ps.setString(5,"%"+kdpenjab.getText()+nmpenjab.getText()+"%");
                    ps.setString(6,"%"+TCari.getText()+"%");
                    ps.setString(7,"%"+kdpenjab.getText()+nmpenjab.getText()+"%");
                    ps.setString(8,"%"+TCari.getText()+"%");
                    rs=ps.executeQuery();
                    while(rs.next()){
                        tabMode.addRow(new Object[]{
                            false,rs.getString("no_rawat"),rs.getString("tgl_piutang"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),
                            "Ralan",rs.getDouble("sisapiutang"),rs.getString("png_jawab"),rs.getString("no_peserta"),rs.getString("nama_perusahaan"),
                            rs.getString("nip"),rs.getString("no_nota")
                        });
                        sisapiutang=sisapiutang+rs.getDouble("sisapiutang");
                    }
                } catch (Exception e) {
                    System.out.println(e);
                } finally{
                    if(rs!=null){
                        rs.close();
                    }
                    if(ps!=null){
                        ps.close();
                    }
                }
                
                ps=koneksi.prepareStatement(
                       "select piutang_pasien.no_rawat, piutang_pasien.tgl_piutang, piutang_pasien.no_rkm_medis,pasien.nm_pasien,penjab.png_jawab,pasien.no_peserta,"+
                       "detail_piutang_pasien.totalpiutang,detail_piutang_pasien.sisapiutang,perusahaan_pasien.nama_perusahaan,nota_inap.no_nota,pasien.nip "+
                       "from piutang_pasien inner join pasien on piutang_pasien.no_rkm_medis=pasien.no_rkm_medis "+
                       "inner join reg_periksa on piutang_pasien.no_rawat=reg_periksa.no_rawat "+
                       "inner join detail_piutang_pasien on piutang_pasien.no_rawat=detail_piutang_pasien.no_rawat "+
                       "inner join penjab on detail_piutang_pasien.kd_pj=penjab.kd_pj "+
                       "inner join nota_inap on nota_inap.no_rawat=reg_periksa.no_rawat "+
                       "inner join perusahaan_pasien on perusahaan_pasien.kode_perusahaan=pasien.perusahaan_pasien "+
                       "where "+
                       "detail_piutang_pasien.sisapiutang>=1 and concat(detail_piutang_pasien.kd_pj,penjab.png_jawab) like ? and piutang_pasien.no_rawat like ? or "+ 
                       "detail_piutang_pasien.sisapiutang>=1 and concat(detail_piutang_pasien.kd_pj,penjab.png_jawab) like ? and piutang_pasien.no_rkm_medis like ? or "+
                       "detail_piutang_pasien.sisapiutang>=1 and concat(detail_piutang_pasien.kd_pj,penjab.png_jawab) like ? and pasien.nm_pasien like ? or "+
                       "detail_piutang_pasien.sisapiutang>=1 and concat(detail_piutang_pasien.kd_pj,penjab.png_jawab) like ? and nota_inap.no_nota like ? order by piutang_pasien.tgl_piutang");
                try {
                    ps.setString(1,"%"+kdpenjab.getText()+nmpenjab.getText()+"%");
                    ps.setString(2,"%"+TCari.getText()+"%");
                    ps.setString(3,"%"+kdpenjab.getText()+nmpenjab.getText()+"%");
                    ps.setString(4,"%"+TCari.getText()+"%");
                    ps.setString(5,"%"+kdpenjab.getText()+nmpenjab.getText()+"%");
                    ps.setString(6,"%"+TCari.getText()+"%");
                    ps.setString(7,"%"+kdpenjab.getText()+nmpenjab.getText()+"%");
                    ps.setString(8,"%"+TCari.getText()+"%");
                    rs=ps.executeQuery();
                    while(rs.next()){
                        tabMode.addRow(new Object[]{
                            false,rs.getString("no_rawat"),rs.getString("tgl_piutang"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),
                            "Ralan",rs.getDouble("sisapiutang"),rs.getString("png_jawab"),rs.getString("no_peserta"),rs.getString("nama_perusahaan"),
                            rs.getString("nip"),rs.getString("no_nota")
                        });
                        sisapiutang=sisapiutang+rs.getDouble("sisapiutang");
                    }
                } catch (Exception e) {
                    System.out.println(e);
                } finally{
                    if(rs!=null){
                        rs.close();
                    }
                    if(ps!=null){
                        ps.close();
                    }
                }
            }else if(cmbStatus.getSelectedItem().equals("Ralan")){
                ps=koneksi.prepareStatement(
                       "select piutang_pasien.no_rawat, piutang_pasien.tgl_piutang, piutang_pasien.no_rkm_medis,pasien.nm_pasien,penjab.png_jawab,pasien.no_peserta,"+
                       "detail_piutang_pasien.totalpiutang,detail_piutang_pasien.sisapiutang,perusahaan_pasien.nama_perusahaan,nota_jalan.no_nota,pasien.nip "+
                       "from piutang_pasien inner join pasien on piutang_pasien.no_rkm_medis=pasien.no_rkm_medis "+
                       "inner join reg_periksa on piutang_pasien.no_rawat=reg_periksa.no_rawat "+
                       "inner join detail_piutang_pasien on piutang_pasien.no_rawat=detail_piutang_pasien.no_rawat "+
                       "inner join penjab on detail_piutang_pasien.kd_pj=penjab.kd_pj "+
                       "inner join nota_jalan on nota_jalan.no_rawat=reg_periksa.no_rawat "+
                       "inner join perusahaan_pasien on perusahaan_pasien.kode_perusahaan=pasien.perusahaan_pasien "+
                       "where "+
                       "detail_piutang_pasien.sisapiutang>=1 and concat(detail_piutang_pasien.kd_pj,penjab.png_jawab) like ? and piutang_pasien.no_rawat like ? or "+ 
                       "detail_piutang_pasien.sisapiutang>=1 and concat(detail_piutang_pasien.kd_pj,penjab.png_jawab) like ? and piutang_pasien.no_rkm_medis like ? or "+
                       "detail_piutang_pasien.sisapiutang>=1 and concat(detail_piutang_pasien.kd_pj,penjab.png_jawab) like ? and pasien.nm_pasien like ? or "+
                       "detail_piutang_pasien.sisapiutang>=1 and concat(detail_piutang_pasien.kd_pj,penjab.png_jawab) like ? and nota_jalan.no_nota like ? order by piutang_pasien.tgl_piutang");
                try {
                    ps.setString(1,"%"+kdpenjab.getText()+nmpenjab.getText()+"%");
                    ps.setString(2,"%"+TCari.getText()+"%");
                    ps.setString(3,"%"+kdpenjab.getText()+nmpenjab.getText()+"%");
                    ps.setString(4,"%"+TCari.getText()+"%");
                    ps.setString(5,"%"+kdpenjab.getText()+nmpenjab.getText()+"%");
                    ps.setString(6,"%"+TCari.getText()+"%");
                    ps.setString(7,"%"+kdpenjab.getText()+nmpenjab.getText()+"%");
                    ps.setString(8,"%"+TCari.getText()+"%");
                    rs=ps.executeQuery();
                    while(rs.next()){
                        tabMode.addRow(new Object[]{
                            false,rs.getString("no_rawat"),rs.getString("tgl_piutang"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),
                            "Ralan",rs.getDouble("sisapiutang"),rs.getString("png_jawab"),rs.getString("no_peserta"),rs.getString("nama_perusahaan"),
                            rs.getString("nip"),rs.getString("no_nota")
                        });
                        sisapiutang=sisapiutang+rs.getDouble("sisapiutang");
                    }
                } catch (Exception e) {
                    System.out.println(e);
                } finally{
                    if(rs!=null){
                        rs.close();
                    }
                    if(ps!=null){
                        ps.close();
                    }
                }
            }else if(cmbStatus.getSelectedItem().equals("Ranap")){
                ps=koneksi.prepareStatement(
                       "select piutang_pasien.no_rawat, piutang_pasien.tgl_piutang, piutang_pasien.no_rkm_medis,pasien.nm_pasien,penjab.png_jawab,pasien.no_peserta,"+
                       "detail_piutang_pasien.totalpiutang,detail_piutang_pasien.sisapiutang,perusahaan_pasien.nama_perusahaan,nota_inap.no_nota,pasien.nip "+
                       "from piutang_pasien inner join pasien on piutang_pasien.no_rkm_medis=pasien.no_rkm_medis "+
                       "inner join reg_periksa on piutang_pasien.no_rawat=reg_periksa.no_rawat "+
                       "inner join detail_piutang_pasien on piutang_pasien.no_rawat=detail_piutang_pasien.no_rawat "+
                       "inner join penjab on detail_piutang_pasien.kd_pj=penjab.kd_pj "+
                       "inner join nota_inap on nota_inap.no_rawat=reg_periksa.no_rawat "+
                       "inner join perusahaan_pasien on perusahaan_pasien.kode_perusahaan=pasien.perusahaan_pasien "+
                       "where "+
                       "detail_piutang_pasien.sisapiutang>=1 and concat(detail_piutang_pasien.kd_pj,penjab.png_jawab) like ? and piutang_pasien.no_rawat like ? or "+ 
                       "detail_piutang_pasien.sisapiutang>=1 and concat(detail_piutang_pasien.kd_pj,penjab.png_jawab) like ? and piutang_pasien.no_rkm_medis like ? or "+
                       "detail_piutang_pasien.sisapiutang>=1 and concat(detail_piutang_pasien.kd_pj,penjab.png_jawab) like ? and pasien.nm_pasien like ? or "+
                       "detail_piutang_pasien.sisapiutang>=1 and concat(detail_piutang_pasien.kd_pj,penjab.png_jawab) like ? and nota_inap.no_nota like ? order by piutang_pasien.tgl_piutang");
                try {
                    ps.setString(1,"%"+kdpenjab.getText()+nmpenjab.getText()+"%");
                    ps.setString(2,"%"+TCari.getText()+"%");
                    ps.setString(3,"%"+kdpenjab.getText()+nmpenjab.getText()+"%");
                    ps.setString(4,"%"+TCari.getText()+"%");
                    ps.setString(5,"%"+kdpenjab.getText()+nmpenjab.getText()+"%");
                    ps.setString(6,"%"+TCari.getText()+"%");
                    ps.setString(7,"%"+kdpenjab.getText()+nmpenjab.getText()+"%");
                    ps.setString(8,"%"+TCari.getText()+"%");
                    rs=ps.executeQuery();
                    while(rs.next()){
                        tabMode.addRow(new Object[]{
                            false,rs.getString("no_rawat"),rs.getString("tgl_piutang"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),
                            "Ralan",rs.getDouble("sisapiutang"),rs.getString("png_jawab"),rs.getString("no_peserta"),rs.getString("nama_perusahaan"),
                            rs.getString("nip"),rs.getString("no_nota")
                        });
                        sisapiutang=sisapiutang+rs.getDouble("sisapiutang");
                    }
                } catch (Exception e) {
                    System.out.println(e);
                } finally{
                    if(rs!=null){
                        rs.close();
                    }
                    if(ps!=null){
                        ps.close();
                    }
                }
            }
                
            LCountBelumDibayar1.setText(tabMode.getRowCount()+"");
            LCountBelumDibayar2.setText(Valid.SetAngka(sisapiutang));
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
    }


    private void getdata() {
        if(kdpenjab.getText().equals("")||nmpenjab.getText().equals("")){
            tbBangsal.setValueAt(false,tbBangsal.getSelectedRow(),0);
            JOptionPane.showMessageDialog(null,"Silahkan pilih penjamin terlebih dahulu");
        }else{
            total=0;
            jml=0;
            for(i=0;i<tbBangsal.getRowCount();i++){  
                if(tbBangsal.getValueAt(i,0).toString().equals("true")){
                     total=total+(Double.parseDouble(tbBangsal.getValueAt(i,6).toString()));   
                     jml++;
                }
            }
            LCountDipilih1.setText(jml+"");
            LCountDipilih2.setText(Valid.SetAngka(total));
        }   
    }
    
    public void isCek(){
        autoNomor();
        TCari.requestFocus();
        if(akses.getjml2()>=1){
            kdptg.setEditable(false);
            btnPetugas.setEnabled(false);
            kdptg.setText(akses.getkode());
            BtnSimpan.setEnabled(akses.getpenagihan_piutang_pasien());
            Sequel.cariIsi("select nama from petugas where nip=?", nmptg,kdptg.getText());
        }        
    }
    
    private void autoNomor() {
        Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(no_tagihan,3),signed)),0) from penagihan_piutang where tanggal='"+Valid.SetTgl(Tanggal.getSelectedItem()+"")+"' ",
                "PP"+Tanggal.getSelectedItem().toString().substring(6,10)+Tanggal.getSelectedItem().toString().substring(3,5)+Tanggal.getSelectedItem().toString().substring(0,2),3,NoPenagihan); 
    }
}