import java.io.File;

public class Controller {
	public static VisualSimulator view;
	public static SicSimulator sic_simulator;
	public static SicLoader sic_loader;
	public static ResourceManager rmgr;
	
	public static void main(String[] args) {
		view = new VisualSimulator();      // ����� �ùķ����� ��ü��
		sic_simulator = new SicSimulator();  // sic �ùķ����� ��ü����
		sic_loader = new SicLoader();      //sic �δ� ��ü����
		rmgr = new ResourceManager();		//���ҽ��Ŵ��� ��ü ���� 
		File inst = new File("inst.data");  //inst.data ���� ��ü ����
		
		sic_loader.initialize(inst);    	//�δ��� �ʱ�ȭ �Լ� ����
		sic_simulator.initialize(inst);		//�ùķ������� �ʱ�ȭ�Լ� ����
	}
}
