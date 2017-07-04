import java.io.File;

public class Controller {
	public static VisualSimulator view;
	public static SicSimulator sic_simulator;
	public static SicLoader sic_loader;
	public static ResourceManager rmgr;
	
	public static void main(String[] args) {
		view = new VisualSimulator();      // 비쥬얼 시뮬레이터 객체생
		sic_simulator = new SicSimulator();  // sic 시뮬레이터 객체생성
		sic_loader = new SicLoader();      //sic 로더 객체생성
		rmgr = new ResourceManager();		//리소스매니저 객체 생성 
		File inst = new File("inst.data");  //inst.data 파일 객체 생성
		
		sic_loader.initialize(inst);    	//로더의 초기화 함수 실행
		sic_simulator.initialize(inst);		//시뮬레이터의 초기화함수 실행
	}
}
