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
//inst_table�� �ε�� �ʿ��� Define,Refer,Modi,text Array ����;

	private int start = 0;
	private int size = 0;
	private int cursor = 0;
	private int sect = 0;
	public class Text{
		//text �̳� Ŭ��
		public String start;
		public String end;
		public String data;
		public Text(String s,String e,String d){start=s;end=e;data=d;}
	}
	
	public class Define{
		//Define �̳�Ŭ��
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
		//Refer�̳�Ŭ��
		private String label;
		public Refer(String Dlabel){
			label = Dlabel;
						
		}
		public String getLabel(){return label;}
		
	}
	

	public SicLoader() { //������
		Define_table = new ArrayList<Define>();
		Refer_table =new ArrayList<Refer>();
		Modi_table = new ArrayList<String>();
		Text_table = new ArrayList<Text>();
		inst_table=new ArrayList<Inst>();
		sect = 0;
	}
	public void initialize(File f){
		//inst.data�ʱ�ȭ ���� �Լ�.
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
		//���� ObjectCode�ε� �ϴ� �Լ�.
		FileReader fr = null;
		BufferedReader br = null;

		try {
			fr = new FileReader(objFile.getName());
			br = new BufferedReader(fr);
			String tmp = null;
			while ((tmp = br.readLine()) != null) {
				if (!tmp.equals("")) {
					read_obj(tmp); // ���� ����  read_obj �Լ��� ���� �Ľ�
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
		update_record(); // 1�������� �� �Ľ��ѵ� Modi ������ ���� ���ڵ� ������Ʈ �Լ�
		for(int i = 0; i < Controller.sic_loader.Text_table.size(); i++) {
			Controller.view.instructions.addElement("0x"+Controller.sic_loader.Text_table.get(i).start+"\t|\t"+Controller.sic_loader.Text_table.get(i).data);
		}
		Controller.view.InstList.setModel(Controller.view.instructions);
		//�信 ���������� ���̴� ����Ʈ ����. 
	}
	public void read_obj(String s){
		//���δ����� �Ľ��ϴ� �κ�
		char first = s.charAt(0);
		//ù ���ڵ� �ǵ� (H,D,R,T,M,E)
		int cnt=0;
		String ni;
		String xbpe;
		switch(first){ // ù ���ڵ� �ǵ�
		case 'H' :
			String name = s.substring(1, 7);
			int addr = cursor+Integer.parseInt(s.substring(7, 13), 16);
			Define_table.add(new Define(name,String.format("%06X", addr)));
			size = Integer.parseInt(s.substring(13, 19), 16);
			if (sect == 0) { //ù ���� �� ���α׷��� �̸��� ������ ���� ������� ���ǹ�.
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
			//�������� ��� Refer���̺� �� �߰�
			break;
		case 'D' :
			cnt = (s.length() - 1) / 12;
			for (int i = 0; i < cnt; i++)
				Define_table.add(new Define(s.substring(1 + 6 * 2 * i,(1 + 6 * 2 * i) + 6),s.substring(7 + 6 * 2 * i,(7 + 6 * 2 * i) + 6)));
			//�������� ��� Define ���̺� �� �߰�
			break;
		case 'T' :
			start = cursor+ Integer.parseInt(s.substring(1, 7), 16);
			// ���� �ּ� ��� (�����ּ� = �� ��ƾ�� �����ּ�+ ����ּ� )
			cnt = (s.length() - 9) / 2; // �� �� ����Ʈ ����.
			int begin = 9; //9��°���� ���� �ν�Ʈ���� 
			int length = 0;
			while(begin<s.length()){
				start += length;
				int tmp = Integer.parseInt(s.substring(begin, begin + 2), 16);
			    ni=Integer.toBinaryString(tmp%4);
				tmp=tmp-(tmp%4);
				String str = String.format("%02X", tmp);
				String format = searchFormat(str);
				//���� ���� ������.
				length=0;
				if(format.equals("1")) //�о���ΰͺ��� �� �����Ʈ�� �о������ ���� ������ �κ�.
						length=1;
				else if (format.equals("2"))
						length=2;
				else if(format.equals("3/4")){		
					if(s.substring(begin).length()>=3){
					xbpe=Integer.toBinaryString(Integer.parseInt(s.substring(begin+2, begin + 3), 16));
					if(xbpe.charAt(xbpe.length()-1)=='1') length=4;
					else length =3;}
					else{ length=1; format="non";}//��ɾ �ƴѺκ�
					}
				else{ length=3;  // ��ɾ �ƴ� �κ�
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
			 //�Ľ��Ͽ� Modi���̺� �߰�
			break;
		case 'E' :
			cursor += size; //���α׷� ������ �����ֱ� 
			break;
		default :
			break;
		}
	}
	public byte[] packing(byte[] in){ 
		//2���� ����Ʈ�� 1���� �����ִ� ��ŷ�Լ�
		byte[] packed = null;
		int l = in.length;
		int j=l/2;
		packed= new byte[j];
		int m=0;
		for(int i=0;i<l;i+=2)
		{
			byte tmp= in[i];
			if(tmp>=65) // �տ����� A~F�϶� 
				tmp-=55;
			else		//�տ����� 0~9�϶� 
				tmp-=48;
			tmp=(byte) (tmp<<4); //4��Ʈ ��� �� 0����ä��
			if(in[i+1]>=65)
			tmp+=(in[i+1]-55);//���Ͱ��� ������ ����
			else
			tmp+=(in[i+1]-48); //���Ͱ��� ������ ����
			packed[m++]=(byte)tmp;//���ϰ��� ����
		}
		
		
		return packed;
	} 
	
	
	public String searchFormat(String op){
		//OPformatã�� �Լ�
		int i;
		for(i=0;i<inst_table.size();i++)
		{
			if(inst_table.get(i).getOP_code().equals(op))
			return inst_table.get(i).getFormat();
		}
		return null;
	}
	
	public void update_record(){
		//�޸𸮿� �� �ø��� �ٸ� ���α׷��� �κ��� �����Ͽ� 00000�̾��� �κ� �������ִ� �Լ�
		if (Modi_table.size() != 0) {
			for (int i = 0; i < Modi_table.size(); i++) {
				String str = Modi_table.get(i);
				int addr = Integer.parseInt(str.substring(0, 6), 16);
				int len = Integer.parseInt(str.substring(6, 8), 16);
				for (int j = 0; j < Text_table.size(); j++) {
					int start = Integer.parseInt(Text_table.get(j).start, 16);
					int end = Integer.parseInt(Text_table.get(j).end, 16);
					if (start <= addr && addr < end) {
						int orig = Integer.parseInt(Text_table.get(j).data, 16); // ���� ����ִ� ������ (..00000)
						for (int k = 0; k < Define_table.size(); k++) {
							String name = str.substring(9, str.length());
							if (Define_table.get(k).label.trim().equals(name)) {
								int modi = Integer.parseInt(Define_table.get(k).addr,16); //�����ؾ��� ����ּ� ũ��
								if (str.charAt(8) == '+') {
									orig += modi; //+�ϋ� 
								} else {
									orig -= modi; //-�϶�
								}
								if (len == 6) {
									//¦���� 3����Ʈ �� �ٲܶ�.
									Text_table.get(j).data = String.format("%06X",orig);
									Controller.rmgr.setMemory(start, packing(Text_table.get(j).data.getBytes()), 3);
								}
								else if (len == 5) {
									//Ȧ���� 2.5����Ʈ �ٲܶ�.
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
