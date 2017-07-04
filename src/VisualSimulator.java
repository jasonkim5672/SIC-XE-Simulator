import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JLayeredPane;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

public class VisualSimulator extends JFrame implements ActionListener{ 
	//버튼 클릭 이벤트를 위한 액션리스너 구현.
	//Frame GUI를 만들기 위한 JFrame 상속.

	private JPanel contentPane;
	public JTextField SicMachine;
	public JTextField FileField;
	public JTextField Hname;
	public JTextField Hstart;
	public JTextField Hlength;
	public JTextField Eadr;
	public JTextField s_addr;
	public JTextField t_addr;
	public JTextField a_dec;
	public JTextField x_dec;
	public JTextField l_dec;
	public JTextField pc_dec;
	public JTextField sw_dec;
	public JTextField a_hex;
	public JTextField x_hex;
	public JTextField l_hex;
	public JTextField pc_hex;
	public JTextField sw_hex;
	public JTextField b_dec;
	public JTextField s_dec;
	public JTextField t_dec;
	public JTextField f_dec;
	public JTextField b_hex;
	public JTextField s_hex;
	public JTextField t_hex;
	public JTextField f_hex;
	public JTextField device;
	private JFrame frm = new JFrame();
	private JFileChooser fileChooser = new JFileChooser();  
	
	private JButton btnOpen;
	private JButton btnQuit;
	private JButton btn1step;
	private JButton btnAllStep ;
	private File file ;
	private boolean first;
	private JScrollPane instscrollPane;
	private JScrollPane logscrollPane;
	public JScrollPane MemoryPane;
	public JList InstList;		//인스트럭션 리스트가 보여질 영역
	public JList LogList;		//로그 리스트가 보여질 영역
	public DefaultListModel<String> instructions;
	public DefaultListModel<String> logs;	
	public JTextArea MemoryArea;	//메모리가 보여질 영역 
	/**
	 * Launch the application.
	 */
	/*
	
	/**
	 * Create the frame.
	 */
	public VisualSimulator() {  //생성자, GUI 구성을 만들어주는 부분/ WindowBuilder  플러그인을 사용하면 편하게 구현할수있다. 
		first=false;
		setTitle("SIC/XE SIMULATOR");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setBounds(100, 100, 600, 750);
		contentPane = new JPanel();
		contentPane.setBorder(new LineBorder(new Color(0, 0, 0)));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblFilename = new JLabel("FileName :");
		lblFilename.setBounds(6, 6, 74, 16);
		contentPane.add(lblFilename);
		
		FileField = new JTextField();
		FileField.setEditable(false);
		FileField.setBounds(76, 1, 327, 26);
		contentPane.add(FileField);
		FileField.setColumns(10);
		
		btnOpen = new JButton("Open");
		btnOpen.setBounds(415, 1, 90, 29);
		btnOpen.addActionListener(this);
		contentPane.add(btnOpen);
		
		JPanel Hpanel = new JPanel();
		Hpanel.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		Hpanel.setToolTipText("");
		Hpanel.setBounds(6, 55, 238, 125);
		contentPane.add(Hpanel);
		Hpanel.setLayout(null);
		
		JLabel lblProgramName = new JLabel("PROGRAM NAME :");
		lblProgramName.setBounds(6, 6, 119, 16);
		Hpanel.add(lblProgramName);
		
		JLabel lblStartAddressOf = new JLabel("Start address of Object Program");
		lblStartAddressOf.setBounds(6, 34, 226, 16);
		Hpanel.add(lblStartAddressOf);
		
		JLabel lblLengthOfProgram = new JLabel("Length of Program :");
		lblLengthOfProgram.setBounds(6, 90, 133, 16);
		Hpanel.add(lblLengthOfProgram);
		
		Hname = new JTextField();
		Hname.setEditable(false);
		Hname.setBounds(121, 1, 111, 26);
		Hpanel.add(Hname);
		Hname.setColumns(10);
		
		Hstart = new JTextField();
		Hstart.setEditable(false);
		Hstart.setBounds(6, 57, 226, 26);
		Hpanel.add(Hstart);
		Hstart.setColumns(10);
		
		Hlength = new JTextField();
		Hlength.setEditable(false);
		Hlength.setBounds(133, 85, 99, 26);
		Hpanel.add(Hlength);
		Hlength.setColumns(10);
		
		JLabel lblHheaderRecord = new JLabel("H (Header Record)");
		lblHheaderRecord.setBounds(6, 34, 124, 16);
		contentPane.add(lblHheaderRecord);
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		layeredPane.setBounds(256, 55, 227, 65);
		contentPane.add(layeredPane);
		
		JLabel lblAddressOfFirst = new JLabel("Address of First Instruction");
		lblAddressOfFirst.setBounds(6, 6, 192, 16);
		layeredPane.add(lblAddressOfFirst);
		
		JLabel lblInObjectProgram = new JLabel("In Object Program :");
		lblInObjectProgram.setBounds(6, 30, 130, 16);
		layeredPane.add(lblInObjectProgram);
		
		Eadr = new JTextField();
		Eadr.setEditable(false);
		Eadr.setBounds(130, 25, 91, 26);
		layeredPane.add(Eadr);
		Eadr.setColumns(10);
		
		JLabel lblEendOf = new JLabel("E (End of Record)");
		lblEendOf.setBounds(256, 34, 117, 16);
		contentPane.add(lblEendOf);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		panel.setBounds(256, 126, 227, 54);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblStartAddress = new JLabel("Start Address :");
		lblStartAddress.setBounds(6, 6, 95, 16);
		panel.add(lblStartAddress);
		
		JLabel lblTargetAddress = new JLabel("Target Address :");
		lblTargetAddress.setBounds(6, 32, 103, 16);
		panel.add(lblTargetAddress);
		
		s_addr = new JTextField();
		s_addr.setEditable(false);
		s_addr.setBounds(113, 1, 108, 26);
		panel.add(s_addr);
		s_addr.setColumns(10);
		
		t_addr = new JTextField();
		t_addr.setEditable(false);
		t_addr.setBounds(113, 27, 108, 26);
		panel.add(t_addr);
		t_addr.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		panel_1.setBounds(6, 204, 238, 164);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblA = new JLabel("A (#0)");
		lblA.setBounds(6, 27, 61, 16);
		panel_1.add(lblA);
		
		JLabel lblDec = new JLabel("Dec");
		lblDec.setBounds(80, 6, 61, 16);
		panel_1.add(lblDec);
		
		JLabel lblHex = new JLabel("Hex");
		lblHex.setBounds(151, 6, 61, 16);
		panel_1.add(lblHex);
		
		JLabel lblNewLabel = new JLabel("X (#1)");
		lblNewLabel.setBounds(6, 55, 61, 16);
		panel_1.add(lblNewLabel);
		
		JLabel lblL = new JLabel("L (#2)");
		lblL.setBounds(6, 83, 61, 16);
		panel_1.add(lblL);
		
		JLabel lblPc = new JLabel("PC (#8)");
		lblPc.setBounds(6, 111, 61, 16);
		panel_1.add(lblPc);
		
		JLabel lblSw = new JLabel("SW (#9)");
		lblSw.setBounds(6, 139, 61, 16);
		panel_1.add(lblSw);
		
		a_dec = new JTextField();
		a_dec.setEditable(false);
		a_dec.setBounds(55, 22, 85, 26);
		panel_1.add(a_dec);
		a_dec.setColumns(10);
		
		x_dec = new JTextField();
		x_dec.setEditable(false);
		x_dec.setColumns(10);
		x_dec.setBounds(55, 50, 85, 26);
		panel_1.add(x_dec);
		
		l_dec = new JTextField();
		l_dec.setEditable(false);
		l_dec.setColumns(10);
		l_dec.setBounds(56, 78, 85, 26);
		panel_1.add(l_dec);
		
		pc_dec = new JTextField();
		pc_dec.setEditable(false);
		pc_dec.setColumns(10);
		pc_dec.setBounds(56, 106, 85, 26);
		panel_1.add(pc_dec);
		
		sw_dec = new JTextField();
		sw_dec.setEditable(false);
		sw_dec.setColumns(10);
		sw_dec.setBounds(56, 134, 85, 26);
		panel_1.add(sw_dec);
		
		a_hex = new JTextField();
		a_hex.setEditable(false);
		a_hex.setColumns(10);
		a_hex.setBounds(147, 22, 85, 26);
		panel_1.add(a_hex);
		
		x_hex = new JTextField();
		x_hex.setEditable(false);
		x_hex.setColumns(10);
		x_hex.setBounds(147, 50, 85, 26);
		panel_1.add(x_hex);
		
		l_hex = new JTextField();
		l_hex.setEditable(false);
		l_hex.setColumns(10);
		l_hex.setBounds(147, 78, 85, 26);
		panel_1.add(l_hex);
		
		pc_hex = new JTextField();
		pc_hex.setEditable(false);
		pc_hex.setColumns(10);
		pc_hex.setBounds(147, 106, 85, 26);
		panel_1.add(pc_hex);
		
		sw_hex = new JTextField();
		sw_hex.setEditable(false);
		sw_hex.setColumns(10);
		sw_hex.setBounds(147, 134, 85, 26);
		panel_1.add(sw_hex);
		
		JLabel lblRegister = new JLabel("Register");
		lblRegister.setBounds(6, 182, 61, 16);
		contentPane.add(lblRegister);
		
		JPanel panel_2 = new JPanel();
		panel_2.setLayout(null);
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		panel_2.setBounds(6, 389, 238, 136);
		contentPane.add(panel_2);
		
		JLabel label = new JLabel("B (#3)");
		label.setBounds(6, 27, 61, 16);
		panel_2.add(label);
		
		JLabel label_1 = new JLabel("Dec");
		label_1.setBounds(80, 6, 61, 16);
		panel_2.add(label_1);
		
		JLabel label_2 = new JLabel("Hex");
		label_2.setBounds(151, 6, 61, 16);
		panel_2.add(label_2);
		
		JLabel label_3 = new JLabel("S (#4)");
		label_3.setBounds(6, 55, 61, 16);
		panel_2.add(label_3);
		
		JLabel label_4 = new JLabel("T (#5)");
		label_4.setBounds(6, 83, 61, 16);
		panel_2.add(label_4);
		
		JLabel label_5 = new JLabel("F (#9)");
		label_5.setBounds(6, 111, 61, 16);
		panel_2.add(label_5);
		
		b_dec = new JTextField();
		b_dec.setEditable(false);
		b_dec.setColumns(10);
		b_dec.setBounds(55, 22, 85, 26);
		panel_2.add(b_dec);
		
		s_dec = new JTextField();
		s_dec.setEditable(false);
		s_dec.setColumns(10);
		s_dec.setBounds(55, 50, 85, 26);
		panel_2.add(s_dec);
		
		t_dec = new JTextField();
		t_dec.setEditable(false);
		t_dec.setColumns(10);
		t_dec.setBounds(56, 78, 85, 26);
		panel_2.add(t_dec);
		
		f_dec = new JTextField();
		f_dec.setEditable(false);
		f_dec.setColumns(10);
		f_dec.setBounds(56, 106, 85, 26);
		panel_2.add(f_dec);
		
		b_hex = new JTextField();
		b_hex.setEditable(false);
		b_hex.setColumns(10);
		b_hex.setBounds(147, 22, 85, 26);
		panel_2.add(b_hex);
		
		s_hex = new JTextField();
		s_hex.setEditable(false);
		s_hex.setColumns(10);
		s_hex.setBounds(147, 50, 85, 26);
		panel_2.add(s_hex);
		
		t_hex = new JTextField();
		t_hex.setEditable(false);
		t_hex.setColumns(10);
		t_hex.setBounds(147, 78, 85, 26);
		panel_2.add(t_hex);
		
		f_hex = new JTextField();
		f_hex.setEditable(false);
		f_hex.setColumns(10);
		f_hex.setBounds(147, 106, 85, 26);
		panel_2.add(f_hex);
		
		JLabel lblRegisterforXe = new JLabel("Register(for XE)");
		lblRegisterforXe.setBounds(6, 369, 124, 16);
		contentPane.add(lblRegisterforXe);
		
		JLabel lblInstructions = new JLabel("Instructions :");
		lblInstructions.setBounds(256, 182, 98, 16);
		contentPane.add(lblInstructions);
		
		JLabel lblUsingDevice = new JLabel("Using Device :");
		lblUsingDevice.setBounds(495, 136, 98, 16);
		contentPane.add(lblUsingDevice);
		
		device = new JTextField();
		device.setEditable(false);
		device.setBounds(490, 154, 100, 26);
		contentPane.add(device);
		device.setColumns(10);
		
		btn1step = new JButton("1 Step");
		btn1step.setBounds(300, 532, 90, 29);
		btn1step.addActionListener(this);
		contentPane.add(btn1step);
		
		btnAllStep = new JButton("All Step");
		btnAllStep.setBounds(395, 532, 90, 29);
		btnAllStep.addActionListener(this);
		contentPane.add(btnAllStep);
		
		btnQuit = new JButton("Quit");
		btnQuit.setBounds(490, 532, 90, 29);
		btnQuit.addActionListener(this);
		contentPane.add(btnQuit);
		
		JLabel lblLog = new JLabel("Log :");
		lblLog.setBounds(6, 545, 61, 16);
		contentPane.add(lblLog);
		instructions = new DefaultListModel<>();
		logs=new DefaultListModel<>();
		
		instscrollPane = new JScrollPane();
		instscrollPane.setBounds(255, 205, 330, 320);
		contentPane.add(instscrollPane);
		
		InstList = new JList(instructions);
		instscrollPane.setViewportView(InstList);
		
		logscrollPane = new JScrollPane();
		logscrollPane.setBounds(6, 565, 110, 150);
		contentPane.add(logscrollPane);
		
		LogList = new JList(logs);
		logscrollPane.setViewportView(LogList);
		
		MemoryPane = new JScrollPane();
		MemoryPane.setBounds(128, 565, 460, 150);
		contentPane.add(MemoryPane);
		
		MemoryArea = new JTextArea();
		MemoryPane.setViewportView(MemoryArea);
		
		
		JLabel lblMemory = new JLabel("Memory :");
		lblMemory.setBounds(128, 545, 61, 16);
		contentPane.add(lblMemory);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {  //액션리스너 인터페이스의 구현부분 
		 if(e.getSource() == btnOpen) //열기 버튼을 눌렀을 시 
	        {
			 fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(".txt", "txt"));
	            int returnVal = fileChooser.showOpenDialog(frm);
	            if( returnVal == JFileChooser.APPROVE_OPTION)
	            {
	                //확인 눌렀을때.
	                file = fileChooser.getSelectedFile();
	                initialize(file,Controller.rmgr);
	                FileField.setText(file.toString());
	                first=true;
	                Controller.view.update();   // 초기화된 레지스터값을 바로 띄워줌. 
	                Controller.sic_simulator.update_memory(); //올라간 가상의 메모리 영역을 보여줌.
	            }
	            else
	            {
	                //취소 눌렀을떄.
	                FileField.setText(" ");
	            }
	        }else if (e.getSource()==btnQuit){ //종료버튼 눌렀을때
	        	System.exit(1);
	        }else if(e.getSource()==btn1step){ // onestep버튼 눌렀을때.
	        	this.onestep();
	        }else if(e.getSource()==btnAllStep){ // AllStep버튼을 눌렀을떄.
	        	allstep();
	        }
		
		
		
	}
	public void initialize(File objFile, ResourceManager rmgr){
		Controller.sic_loader.load(objFile, rmgr);
		
	}
	public void onestep(){
		if(first==true){ //로드 됐을때만.
		Controller.sic_simulator.onestep(); // 실제 작동이 이뤄지는 sic_simulator의 onestep 실행
		Controller.view.update();
		}else //로드 되지 않았을때는 파일 먼저 고르라는 메시지 창 생성.
			JOptionPane.showMessageDialog(null, "Please, Load File First");

	}
	public void allstep(){
		if(first==true){  //로드 됐을때
			Controller.sic_simulator.allstep();// 실제 작동이 이뤄지는 sic_simulator의 AllStep 실행
			update();
		}
		else
				JOptionPane.showMessageDialog(null, "Please, Load File First");

	}
	public void update() { //레지스터들 업데이트 해주는 부분 
		// TODO Auto-generated method stub
		int A = Controller.rmgr.getRegister(0);
		int X = Controller.rmgr.getRegister(1);
		int L = Controller.rmgr.getRegister(2);
		int PC = Controller.rmgr.getRegister(8);
		int SW = Controller.rmgr.getRegister(9);
		int B = Controller.rmgr.getRegister(3);
		int S = Controller.rmgr.getRegister(4);
		int T = Controller.rmgr.getRegister(5);
		int F = Controller.rmgr.getRegister(6);
		Controller.view.a_dec.setText(String.format("%d", A));
		Controller.view.a_hex.setText(String.format("0x%06X", A));
		Controller.view.x_dec.setText(String.format("%d", X));
		Controller.view.x_hex.setText(String.format("0x%06X", X));
		Controller.view.l_dec.setText(String.format("%d", L));
		Controller.view.l_hex.setText(String.format("0x%06X", L));
		Controller.view.pc_dec.setText(String.format("%d", PC));
		Controller.view.pc_hex.setText(String.format("0x%06X", PC));
		Controller.view.sw_dec.setText(String.format("%d", SW));
		Controller.view.sw_hex.setText(String.format("0x%06X", SW));
		Controller.view.b_dec.setText(String.format("%d", B));
		Controller.view.b_hex.setText(String.format("0x%06X", B));
		Controller.view.s_dec.setText(String.format("%d", S));
		Controller.view.s_hex.setText(String.format("0x%06X", S));
		Controller.view.t_dec.setText(String.format("%d", T));
		Controller.view.t_hex.setText(String.format("0x%06X", T));
		Controller.view.f_dec.setText(String.format("%d", F));
		Controller.view.f_hex.setText(String.format("0x%06X", F));
		
	

	}
}
