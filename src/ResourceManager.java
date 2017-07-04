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
	
	
	
	public ResourceManager(){// ������
		initializeMemory();
		initializeRegister();
		read_num=0;
	}
	public void initializeMemory() {
		memory = new byte[5000]; // �⺻������ 5000����Ʈ�� �ο�
	}
	public void initializeRegister() {
		register = new int[10];  //10���� �������͵� 
	}
	public int getRegister(int regNum) {//�������Ͱ� ��� �Լ�
		return register[regNum];
	}
	
	public void initialDevice(String devName){
		//���� ��Ⱑ �ƴϱ� ������ ���ʿ�...
	}
	public void writeDevice(String devName, byte[] data,int size){
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(new File(devName),true);
			//�� ���α׷��� �� ĳ���;� �޾Ƽ� �߰��ϴ� �����̹Ƿ� 
			//true�ɼ��� �༭ append�����ϰ� �ؾ��Ѵ�.
			bw = new BufferedWriter(fw);
			byte[] s= Controller.sic_loader.packing(data);
			//data��ŷ 
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
		//�� ĳ���;� �о���̴� �Լ�
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
		//Ư�� ��ġ��  ũ�⸸ŭ ������ �������ִ� �Լ�
		for (int i = 0; i < size; i++) {
			memory[locate+i]=(byte)data[i];
		}
		
		
	}
	public void setRegister(int regNum,int value){
		//�������Ͱ� �����ϴ� �Լ�.
		register[regNum] = value;
	}
	public byte[] getMemory(int locate, int size){
		//�޸� �� ��� �Լ�.
		byte[] tmp = new byte[size];
		for (int i = 0; i < size; i++) {
			tmp[i] = memory[locate + i];
		}
		return tmp;
	}
	
}
