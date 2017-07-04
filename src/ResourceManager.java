import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ResourceManager {
	public byte[] memory;
	private int[] register;
	private int read_num;
	
	
	
	public ResourceManager(){// 생성자
		initializeMemory();
		initializeRegister();
		read_num=0;
	}
	public void initializeMemory() {
		memory = new byte[5000]; // 기본적으로 5000바이트만 부여
	}
	public void initializeRegister() {
		register = new int[10];  //10개의 레지스터들 
	}
	public int getRegister(int regNum) {//레지스터값 얻는 함수
		return register[regNum];
	}
	
	public void initialDevice(String devName){
		//실제 기기가 아니기 때문에 불필요...
	}
	public void writeDevice(String devName, byte[] data,int size){
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(new File(devName),true);
			//이 프로그램은 한 캐릭터씩 받아서 추가하는 형태이므로 
			//true옵션을 줘서 append가능하게 해야한다.
			bw = new BufferedWriter(fw);
			byte[] s= Controller.sic_loader.packing(data);
			//data패킹 
			bw.write(String.format("%c", s[0]));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bw != null)
				try {
					bw.close();
				} catch (IOException e) {
				}
			if (fw != null)
				try {
					fw.close();
				} catch (IOException e) {
				}
		}
		
	}
	
	public byte[] readDevice(String devName, int size) {
		//한 캐릭터씩 읽어들이는 함수
		File f = new File(devName);
		FileReader fr = null;
		BufferedReader br = null;
		byte[] b = new byte[1];
		try {
			if (f.length() <= read_num) {
				b[0] = -1;
				return b;
			}
			fr = new FileReader(devName);
			br = new BufferedReader(fr);
			br.skip(size);
			b[0] = (byte) br.read();
			read_num++;
		} catch (Exception e) {
			e.setStackTrace(null);
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
			}
		}
		return b;
	}
	
	public void setMemory(int locate, byte[] data,int size){
		//특정 위치에  크기만큼 데이터 저장해주는 함수
		for (int i = 0; i < size; i++) {
			memory[locate+i]=(byte)data[i];
		}
		
		
	}
	public void setRegister(int regNum,int value){
		//레지스터값 세팅하는 함수.
		register[regNum] = value;
	}
	public byte[] getMemory(int locate, int size){
		//메모리 값 얻는 함수.
		byte[] tmp = new byte[size];
		for (int i = 0; i < size; i++) {
			tmp[i] = memory[locate + i];
		}
		return tmp;
	}
	
}
