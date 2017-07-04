import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class SicLoader {
	private ArrayList<Define> Define_table; 
	private ArrayList<Refer> Refer_table;
	private ArrayList<String> Modi_table;
	public ArrayList<Text> Text_table;
	private ArrayList<Inst> inst_table;
//inst_table및 로드시 필요한 Define,Refer,Modi,text Array 선언;

	private int start = 0;
	private int size = 0;
	private int cursor = 0;
	private int sect = 0;
	public class Text{
		//text 이너 클라스
		public String start;
		public String end;
		public String data;
		public Text(String s,String e,String d){start=s;end=e;data=d;}
	}
	
	public class Define{
		//Define 이너클라스
		private String label;
		private String addr;
		public Define(String Dlabel,String Daddr){
			label = Dlabel;
			addr = Daddr;
		}
		public String getLabel(){return label;}
		public String getAddr(){return addr;}
	}
	public class Refer{
		//Refer이너클라스
		private String label;
		public Refer(String Dlabel){
			label = Dlabel;
						
		}
		public String getLabel(){return label;}
		
	}
	

	public SicLoader() { //생성자
		Define_table = new ArrayList<Define>();
		Refer_table =new ArrayList<Refer>();
		Modi_table = new ArrayList<String>();
		Text_table = new ArrayList<Text>();
		inst_table=new ArrayList<Inst>();
		sect = 0;
	}
	public void initialize(File f){
		//inst.data초기화 위한 함수.
		FileReader fr = null;
		BufferedReader br = null;

		try {
			fr = new FileReader(f.getName());
			br = new BufferedReader(fr);
			String tmp = null;
			while ((tmp = br.readLine()) != null) {
				 inst_table.add(new Inst());
   	    	  inst_table.get(inst_table.size()-1).Tokenizer(tmp);
			}

		} catch (Exception e) {
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
	
	
	
	
	
	public void load(File objFile, ResourceManager rmgr){
		//실제 ObjectCode로드 하는 함수.
		FileReader fr = null;
		BufferedReader br = null;

		try {
			fr = new FileReader(objFile.getName());
			br = new BufferedReader(fr);
			String tmp = null;
			while ((tmp = br.readLine()) != null) {
				if (!tmp.equals("")) {
					read_obj(tmp); // 라인 별로  read_obj 함수를 통해 파싱
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
		update_record(); // 1차적으로 다 파싱한뒤 Modi 수정을 위한 레코드 업데이트 함수
		for(int i = 0; i < Controller.sic_loader.Text_table.size(); i++) {
			Controller.view.instructions.addElement("0x"+Controller.sic_loader.Text_table.get(i).start+"\t|\t"+Controller.sic_loader.Text_table.get(i).data);
		}
		Controller.view.InstList.setModel(Controller.view.instructions);
		//뷰에 가시적으로 보이는 리스트 생성. 
	}
	public void read_obj(String s){
		//라인단위로 파싱하는 부분
		char first = s.charAt(0);
		//첫 레코드 판독 (H,D,R,T,M,E)
		int cnt=0;
		String ni;
		String xbpe;
		switch(first){ // 첫 레코드 판독
		case 'H' :
			String name = s.substring(1, 7);
			int addr = cursor+Integer.parseInt(s.substring(7, 13), 16);
			Define_table.add(new Define(name,String.format("%06X", addr)));
			size = Integer.parseInt(s.substring(13, 19), 16);
			if (sect == 0) { //첫 섹션 즉 프로그램의 이름및 사이즈 정보 얻기위한 조건문.
				Controller.view.Hname.setText(name);
				Controller.view.Hlength.setText(String.format("%06X", size));
				Controller.view.Hstart.setText(String.format("%06X", addr));
				Controller.view.Eadr.setText(String.format("%06X", addr));
				sect++;
			}
			break;
		case 'R' :
			cnt = (s.length() - 1) / 6;
			for (int i = 0; i < cnt; i++)
				Refer_table.add(new Refer(s.substring(1 + 6 * i,(1 + 6 * i) + 6)));
			//단위별로 끊어서 Refer테이블 에 추가
			break;
		case 'D' :
			cnt = (s.length() - 1) / 12;
			for (int i = 0; i < cnt; i++)
				Define_table.add(new Define(s.substring(1 + 6 * 2 * i,(1 + 6 * 2 * i) + 6),s.substring(7 + 6 * 2 * i,(7 + 6 * 2 * i) + 6)));
			//단위별로 끊어서 Define 테이블 에 추가
			break;
		case 'T' :
			start = cursor+ Integer.parseInt(s.substring(1, 7), 16);
			// 시작 주소 얻기 (시작주소 = 그 루틴의 절대주소+ 상대주소 )
			cnt = (s.length() - 9) / 2; // 몇 개 바이트 인지.
			int begin = 9; //9번째부터 실제 인스트럭션 
			int length = 0;
			while(begin<s.length()){
				start += length;
				int tmp = Integer.parseInt(s.substring(begin, begin + 2), 16);
			    ni=Integer.toBinaryString(tmp%4);
				tmp=tmp-(tmp%4);
				String str = String.format("%02X", tmp);
				String format = searchFormat(str);
				//포맷 정보 얻어오기.
				length=0;
				if(format.equals("1")) //읽어들인것부터 총 몇바이트를 읽어야할지 포맷 나누는 부분.
						length=1;
				else if (format.equals("2"))
						length=2;
				else if(format.equals("3/4")){		
					if(s.substring(begin).length()>=3){
					xbpe=Integer.toBinaryString(Integer.parseInt(s.substring(begin+2, begin + 3), 16));
					if(xbpe.charAt(xbpe.length()-1)=='1') length=4;
					else length =3;}
					else{ length=1; format="non";}//명령어가 아닌부분
					}
				else{ length=3;  // 명령어가 아닌 부분
				format="non";
				}
				Text_table.add(new Text(String.format("%04X", start), String.format("%04X", start+length-1), s.substring(begin, begin + length * 2)));
				byte[] packed = packing(s.substring(begin, begin + length * 2).getBytes());
				
				Controller.rmgr.setMemory(start ,packed, length);
				cnt -= length;
				begin += length * 2;
			}
			break;
		case 'M' :
			int tmp = Integer.parseInt(s.substring(1, 7), 16);
			tmp += cursor;
			Modi_table.add(String.format("%06X%s", tmp, s.substring(7, s.length())));
			 //파싱하여 Modi테이블에 추가
			break;
		case 'E' :
			cursor += size; //프로그램 사이즈 더해주기 
			break;
		default :
			break;
		}
	}
	public byte[] packing(byte[] in){ 
		//2개의 바이트를 1개로 합쳐주는 패킹함수
		byte[] packed = null;
		int l = in.length;
		int j=l/2;
		packed= new byte[j];
		int m=0;
		for(int i=0;i<l;i+=2)
		{
			byte tmp= in[i];
			if(tmp>=65) // 앞에것이 A~F일때 
				tmp-=55;
			else		//앞에것이 0~9일때 
				tmp-=48;
			tmp=(byte) (tmp<<4); //4비트 상승 및 0으로채움
			if(in[i+1]>=65)
			tmp+=(in[i+1]-55);//위와같은 원리로 더함
			else
			tmp+=(in[i+1]-48); //위와같은 원리로 더함
			packed[m++]=(byte)tmp;//리턴값에 저장
		}
		
		
		return packed;
	} 
	
	
	public String searchFormat(String op){
		//OPformat찾는 함수
		int i;
		for(i=0;i<inst_table.size();i++)
		{
			if(inst_table.get(i).getOP_code().equals(op))
			return inst_table.get(i).getFormat();
		}
		return null;
	}
	
	public void update_record(){
		//메모리에 다 올린뒤 다른 프로그램의 부분을 참조하여 00000이었던 부분 수정해주는 함수
		if (Modi_table.size() != 0) {
			for (int i = 0; i < Modi_table.size(); i++) {
				String str = Modi_table.get(i);
				int addr = Integer.parseInt(str.substring(0, 6), 16);
				int len = Integer.parseInt(str.substring(6, 8), 16);
				for (int j = 0; j < Text_table.size(); j++) {
					int start = Integer.parseInt(Text_table.get(j).start, 16);
					int end = Integer.parseInt(Text_table.get(j).end, 16);
					if (start <= addr && addr < end) {
						int orig = Integer.parseInt(Text_table.get(j).data, 16); // 원래 들어있던 데이터 (..00000)
						for (int k = 0; k < Define_table.size(); k++) {
							String name = str.substring(9, str.length());
							if (Define_table.get(k).label.trim().equals(name)) {
								int modi = Integer.parseInt(Define_table.get(k).addr,16); //수정해야할 상대주소 크기
								if (str.charAt(8) == '+') {
									orig += modi; //+일떄 
								} else {
									orig -= modi; //-일때
								}
								if (len == 6) {
									//짝수로 3바이트 다 바꿀때.
									Text_table.get(j).data = String.format("%06X",orig);
									Controller.rmgr.setMemory(start, packing(Text_table.get(j).data.getBytes()), 3);
								}
								else if (len == 5) {
									//홀수로 2.5바이트 바꿀때.
									Text_table.get(j).data= String.format("%08X",orig);
									Controller.rmgr.setMemory(start, packing(Text_table.get(j).data.getBytes()), 4);
								}
								break;
							}
						}
						break;
					}
				}
			}
		}
		
	}
	

}
