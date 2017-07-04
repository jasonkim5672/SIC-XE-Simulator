import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class SicSimulator {
	private ArrayList<Inst> inst_table=new ArrayList<Inst>();
	private int inst_line =0;
	private int Done=0;
	
	
	public void initialize(File f){ //inst.data 불러와서 초기화 하는 부분.
		FileReader fr = null;
		BufferedReader br = null;

		try {
			fr = new FileReader(f.getName());
			br = new BufferedReader(fr);
			String tmp = null;
			while ((tmp = br.readLine()) != null) {
				 inst_table.add(new Inst()); //새로운 Inst객체 생성및 추가후.
   	    	  inst_table.get(inst_table.size()-1).Tokenizer(tmp); //Inst.토크나이저 함수로 데이터 파싱.
			}

		} catch (Exception e) { //예외핸들링
			JOptionPane.showMessageDialog(null,"Error has occured while Loading \"inst.data\"\n");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
				}
				;
			}
		}
		
		
	}
	public void onestep(){  // 한 스텝.
		if(Done>=1){ //마지막에 함수가 종료되고 기존에 호출했던 PC값으로 돌아가는데 
			//이떄  J @RETADDR 를 쓰게되는데 이때 Done 값을 1로 증가 시켜주어서 더이상 진행되지 않게함 .
			JOptionPane.showMessageDialog(null, Controller.view.Hname.getText()+"Function has been done.");
			return ;
		}
		//현재 진행 위치를 찾아서 InstList에서 현재 수행되야할  위치를 찾아주는  루프.
		for (inst_line = 0; inst_line < Controller.sic_loader.Text_table.size(); inst_line++) {
			String now = String.format("%04X", Controller.rmgr.getRegister(8));
			if (now.equals(Controller.sic_loader.Text_table.get(inst_line).start)){
				Controller.view.s_addr.setText(String.format("0x%06X", Controller.rmgr.getRegister(8)));
				Controller.view.InstList.ensureIndexIsVisible(inst_line);
				Controller.view.InstList.setSelectedIndex(inst_line);
				break;
				}
		}
		//인스트럭션 저장
		String inst = Controller.sic_loader.Text_table.get(inst_line).data;
		//0,1 앞에 두자리만 때어내서 연산자로 저장.
		String operator = inst.substring(0, 2);
		//중요한 정보를 담고있는 nixbpe 비트의 크기를 Int형으로 저장
		int nixbpe = Integer.parseInt(inst.substring(1, 3), 16);
		//오퍼레이터의 ni비트 부분을 제거하기위해 int변환
		int op = Integer.parseInt(operator, 16);
		//하위 2비트는 0,1,2,3이때문에 4로나눈 나머지를 빼줌.
		op=op-(op%4);
		//연산자를 다시 2자리로 HEX스트링으로 파싱뒤 OPTABLE에서 이름 찾기.
		operator = Controller.sic_simulator.searchName(String.format("%02X", op));
		int n,i,x,b,p,e;
		//각 자리에 맞춰 세팅 돼있으면 1을 만들기 위한 비트연산. 논리곱
		if( (nixbpe& 0x20)==32)
			n=1;
		else n=0;
		if((nixbpe& 0x10)==16)
			i=1;
		else i=0;
		
		if(( nixbpe& 0x08)==8)
			x=1;
		else x=0;
		if((nixbpe& 0x04)==4)
			b=1;
		else b=0;
		if((nixbpe& 0x02)==2)
			p=1; 
		else p=0;
		if((nixbpe& 0x01)==1)
			e=1;
		else e=0;
		int pc = 0;
		int base = 0;
		int disp = 0;
		int r1 = 0;
		int r2 = 0;
		//이하 나머지 레지스터 값들 세팅.
		//포맷에 따라서 뒤에 얼마나 더 읽어야하는지 Displacement 를 얻는 과정
		if (searchFormat(String.format("%02X", op)).equals("2")) {
			disp = Integer.parseInt(inst.substring(2, 4), 16);
		} else if (e == 1) {
			disp = Integer.parseInt(inst.substring(3, 8), 16);
		} else if (e == 0) {
			disp = Integer.parseInt(inst.substring(3, 6), 16);
		}
		if(operator!=null)
		switch(operator){  //각 연산자 분기문
			case "STL"	:
				if (e == 0) {  //앞으로 계속나오는 바이트 수 계산해서 PC값을 먼저 세팅해놓는 과정
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 3);
				} else if (e == 1) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 4);
				}
				pc = Controller.rmgr.getRegister(8);
				base = Controller.rmgr.getRegister(3);
				String get_l = String.format("%06X", Controller.rmgr.getRegister(2));
				if (n == 1 && i == 1) {
					if (x == 1) {
						disp += Controller.rmgr.getRegister(1);
					}
					if (p == 1) {
						Controller.rmgr.setMemory((disp + pc) ,  Controller.sic_loader.packing(get_l.getBytes()), 3);
					} else if (b == 1) {
						Controller.rmgr.setMemory((disp + base),  Controller.sic_loader.packing(get_l.getBytes()),3);
					} else {
						Controller.rmgr.setMemory((disp) , Controller.sic_loader.packing(get_l.getBytes()), 3);
					}
					//연산 과정에서 스트링에서 getBytes하면 2바이트씩 쪼개져서 나오는데 이것을 1바이트로 합쳐주는  packing  과정 실행.
				}
				
				break;
			case "STA"	:
				if (e == 0) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 3);
				} else if (e == 1) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 4);
				}
				pc = Controller.rmgr.getRegister(8);
				int A = Controller.rmgr.getRegister(0);
				if (n == 1 && i == 1) {
					String data = String.format("%06X", A);
					
					Controller.rmgr.setMemory((pc + disp) , Controller.sic_loader.packing(data.getBytes()), 3);
				}
				break;
			case "STX"	:
				if (e == 0) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 3);
				} else if (e == 1) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 4);
				}
				pc = Controller.rmgr.getRegister(8);
				r1 = Controller.rmgr.getRegister(1);
				String tmp1 = String.format("%06X", r1);
				Controller.rmgr.setMemory(disp ,  Controller.sic_loader.packing(tmp1.getBytes()), 3);
				break;
			case "STCH"	:
				if (e == 0) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 3);
				} else if (e == 1) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 4);
				}
				pc = Controller.rmgr.getRegister(8);
				r1 = Controller.rmgr.getRegister(1);
				int data = Controller.rmgr.getRegister(0);
				String s = String.format("%02X", data);
				Controller.rmgr.setMemory((disp + r1) ,  Controller.sic_loader.packing(s.getBytes()), 1);
				break;
			case "LDA"	:
				if (e == 0) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 3);
				} else if (e == 1) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 4);
				}
				pc = Controller.rmgr.getRegister(8);
				byte[] bt = Controller.rmgr.getMemory((pc + disp), 3);
				int tmp = 0;
				if (n == 1 && i == 1) {
					for (int j = 0; j < 3; j++) {
						tmp *= 256;
						tmp += bt[j];
					}
					Controller.rmgr.setRegister(0, tmp);
				}

				else if (n == 0 && i == 1) {
					Controller.rmgr.setRegister(0, disp);
				}
				break;
			case "LDT"	:
				if (e == 0) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 3);
				} else if (e == 1) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 4);
				}
				pc = Controller.rmgr.getRegister(8);
				byte[] be;
				if (e == 0) {
					be = Controller.rmgr.getMemory((pc + disp), 3);
				} else {
					be = Controller.rmgr.getMemory(disp , 3);
				}
				int val = 0;
				for (int j = 0; j < 3; j++) {
					val *= 256;
					val += be[j];
				}
				Controller.rmgr.setRegister(5, val);
				break;
			case "LDCH"	:
				if (e == 0) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 3);
				} else if (e == 1) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 4);
				}
				pc = Controller.rmgr.getRegister(8);
				r1 = Controller.rmgr.getRegister(0);
				String buf = String.format("%06X", r1);
				byte[] bch = Controller.rmgr.getMemory((disp + Controller.rmgr.getRegister(1)), 1);
				int c = bch[0];
				Controller.rmgr.setRegister(0, c);
				break;
			case "J"	:
				if (e == 0) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 3);
				} else if (e == 1) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 4);
				}
				pc = Controller.rmgr.getRegister(8);
				if (n == 1 && i == 1) {
					if (disp > 3840) {
						Controller.rmgr.setRegister(8, pc + disp - 4096);
					} else {
						Controller.rmgr.setRegister(8, pc + disp);
					}
				} else if (n == 1 && i == 0) {
					Done++;
					byte[] y = Controller.rmgr.getMemory((pc + disp) , 3);
					int var = 0;
					for (int j = 0; j < 3; j++) {
						var *= 16;
						var += y[j];
					}

					Controller.rmgr.setRegister(8, var);
				}
				break;
			case "JEQ"	:
				if (e == 0) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 3);
				} else if (e == 1) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 4);
				}
				pc = Controller.rmgr.getRegister(8);
				int sw = Controller.rmgr.getRegister(9);
				if (sw == 0) {
					Controller.rmgr.setRegister(8, pc + disp);
				}
				break;
			case "JSUB"	:
				if (e == 0) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 3);
				} else if (e == 1) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 4);
				}
				pc = Controller.rmgr.getRegister(8);
				if (n == 1 && i == 1) {
					Controller.rmgr.setRegister(2, pc);
					Controller.rmgr.setRegister(8, disp);
				}
				break;
			case "JLT"	:
				if (e == 0) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 3);
				} else if (e == 1) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 4);
				}
				pc = Controller.rmgr.getRegister(8);
				if (Controller.rmgr.getRegister(9) == -1) {
					if (disp > 3840) {
						Controller.rmgr.setRegister(8, pc + disp - 4096);
					} else {
						Controller.rmgr.setRegister(8, pc + disp);
					}
				}
				break;
			case "RSUB" :
				Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(2));
				
				break;
			case "COMP"	:
				if (e == 0) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 3);
				} else if (e == 1) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 4);
				}
				pc = Controller.rmgr.getRegister(8);

				if (n == 0 && i == 1) {
					int regA = Controller.rmgr.getRegister(0);
					if (regA < disp) {
						Controller.rmgr.setRegister(9, -1);
					} else if (regA > disp) {
						Controller.rmgr.setRegister(9, 1);
					} else {
						Controller.rmgr.setRegister(9, 0);
					}
				}
				break;
			case "COMPR":
				Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 2);
				pc = Controller.rmgr.getRegister(8);
				r1 = Controller.rmgr.getRegister(0);
				r2 = Controller.rmgr.getRegister(4);
				if (r1 < r2) {
					Controller.rmgr.setRegister(9, -1);
				} else if (r1 > r2) {
					Controller.rmgr.setRegister(9, 1);
				} else {
					Controller.rmgr.setRegister(9, 0);
				}
				break;
			case "TIXR" :
				Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 2);
				pc = Controller.rmgr.getRegister(8);
				Controller.rmgr.setRegister(1, Controller.rmgr.getRegister(1) + 1);
				r1 = Controller.rmgr.getRegister(1);
				r2 = Controller.rmgr.getRegister(5);
				if (r1 < r2) {
					Controller.rmgr.setRegister(9, -1);
				} else if (r1 > r2) {
					Controller.rmgr.setRegister(9, 1);
				} else {
					Controller.rmgr.setRegister(9, 0);
				}
				break;
			case "CLEAR":
				Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 2);
				pc = Controller.rmgr.getRegister(8);
				Controller.rmgr.setRegister(disp / 16, 0);
				break;
			case "TD"	:
				if (e == 0) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 3);
				} else if (e == 1) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 4);
				}
				pc = Controller.rmgr.getRegister(8);
				Controller.rmgr.setRegister(9, 1);
				break;
			case "RD"	:
				if (e == 0) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 3);
				} else if (e == 1) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 4);
				}
				pc = Controller.rmgr.getRegister(8);
				byte[] rdname = Controller.rmgr.getMemory((pc + disp) , 1);
				String rdvn = String.format("%02X", rdname[0]);
				
				Controller.view.device.setText(rdvn);
				byte[] by = Controller.rmgr.readDevice(rdvn,Controller.rmgr.getRegister(1));
				if (by[0] == -1) {
					Controller.rmgr.setRegister(0, 0);
				} else {
					Controller.rmgr.setRegister(0, by[0]);
				}
				break;
			case "WD"	:
				if (e == 0) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 3);
				} else if (e == 1) {
					Controller.rmgr.setRegister(8, Controller.rmgr.getRegister(8) + 4);
				}
				pc = Controller.rmgr.getRegister(8);
				byte[] wdname = Controller.rmgr.getMemory((pc + disp), 1);
				String wdvn= String.format("%02X", wdname[0]);
				
				Controller.view.device.setText(wdvn);
				Controller.rmgr.writeDevice(wdvn,String.format("%02X", Controller.rmgr.getRegister(0)).getBytes(), 1);
				break;
			default:
				break;
					
		}
		Log();
		// Controller.view.s_addr.setText(String.format("%06X", Controller.sic_loader.Text_table.get(inst_line).start.getBytes()));
		if (searchFormat(String.format("%02X", op)).equals("2")) {
			Controller.view.t_addr.setText("");
		}
		else if (disp + pc > 3840*2) {
			Controller.view.t_addr.setText(String.format("0x%06X", disp + pc - 4096));
		} else {
			Controller.view.t_addr.setText(String.format("0x%06X", disp + pc));
		}
		
		
		update_memory();
	}
	
	public void Log() {
		// 현재 실행되고있는 Instructions이 실제로 어떤 명령어 수행중인지 보여주는 로그를 생성해주는 함수.
		String str = Controller.sic_loader.Text_table.get(inst_line).data;
		String operator = str.substring(0, 2);
		int op = Integer.parseInt(operator, 16);
		op /= 4;
		op *= 4;
		operator = Controller.sic_simulator.searchName(String.format("%02X", op));
		switch (operator) {
		case "STL":
			Controller.view.logs.addElement("STL\n");
			break;
		case "STA":
			Controller.view.logs.addElement("STA\n");
			break;
		case "STX":
			Controller.view.logs.addElement("STX\n");
			break;
		case "STCH":
			Controller.view.logs.addElement("STCH\n");
			break;
		case "LDA":
			Controller.view.logs.addElement("LDA\n");
			break;
		case "LDT":
			Controller.view.logs.addElement("LDT\n");
			break;
		case "LDCH":
			Controller.view.logs.addElement("LDCH\n");
			break;
		case "J":
			Controller.view.logs.addElement("J\n");
			break;
		case "JEQ":
			Controller.view.logs.addElement("JEQ\n");
			break;
		case "JSUB":
			Controller.view.logs.addElement("JSUB\n");
			break;
		case "JLT":
			Controller.view.logs.addElement("JLT\n");
			break;
		case "RSUB":
			Controller.view.logs.addElement("RSUB\n");
			break;
		case "COMP":
			Controller.view.logs.addElement("COMP\n");
			break;
		case "COMPR":
			Controller.view.logs.addElement("COMPR\n");
			break;
		case "TIXR":
			Controller.view.logs.addElement("TIXR\n");
			break;
		case "CLEAR":
			Controller.view.logs.addElement("CLEAR\n");
			break;
		case "TD":
			Controller.view.logs.addElement("TD\n");
			break;
		case "RD":
			Controller.view.logs.addElement("RD\n");
			break;
		case "WD":
			Controller.view.logs.addElement("WD\n");
			
			break;
		}
		
		Controller.view.LogList.setModel(Controller.view.logs); //모델 등록
		Controller.view.LogList.ensureIndexIsVisible(Controller.view.logs.size()-1);// 가장최근에 추가된것 스크롤 넘어가도 보이게.
		Controller.view.LogList.setSelectedIndex(Controller.view.logs.size()-1); // 가장 최근에 추가된것 선택되어있게.
		
	}

	public void allstep(){
		while (true) {
			if (Done >=1) {//이미 호출함수를 빠져나가는 J @RETADDR 을 실행하면 더이상 진행 안되게 해주는 부분.
				JOptionPane.showMessageDialog(null, Controller.view.Hname.getText()+"Function has been done.");
				break;
			}
			onestep();
		}
	}
	
	
	public String searchFormat(String op){
		//instruction 테이블을 참조하여 Format을 구하는 함수.
		int i;
		for(i=0;i<inst_table.size();i++)
		{
			if(inst_table.get(i).getOP_code().equals(op))
			return inst_table.get(i).getFormat();
		}
		return null;
	}
	public String searchName(String op){
		//instruction 테이블을 참조하여 연산자이름을 구하는 함수.
		int i;
		for(i=0;i<inst_table.size();i++)
		{
			if(inst_table.get(i).getOP_code().equals(op))
			return inst_table.get(i).getName();
		}
		return null;
	}

	public void update_memory(){
		//읽기시에 실시간으로 변하는 메모리 값을 업데이트 해주는 함수.
		String dt =new String(Controller.rmgr.memory,0,Controller.rmgr.memory.length);
		StringBuilder str= new StringBuilder();
		
		for(int i=0;i<5000;i++)
		{
			if(i%16==0)
				str.append(String.format("\n%04X  | ",i));
			str.append(String.format("%02X  ",Controller.rmgr.memory[i]));
			
		}
		String tmp =str.toString();
		Controller.view.MemoryArea.setText(tmp);
		Controller.view.MemoryArea.setCaretPosition(1); // 하단이 아닌 00000주소가 보이게 세팅
		Controller.view.MemoryArea.requestFocus();
		
		
	}
	
}

