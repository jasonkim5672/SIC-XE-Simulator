import java.lang.String;

public class Inst {
	private	String name;     //실제 OP코드의 뉴머릭 이름 값이 들어가있는 필드 
	private String format;    // 어떤 형식의 데이터를 갖을수있는지 표시하는 필드 
	private String OP_code;     // 실제 OP코드의 스트링형태 필드
	private int OP_num;        // 갖을수 있는 피연산자의 갯수를 정의해놓은 필드  (,X 인덱스는 여기서 논외 )
	///////////////getter setter ///////////////////////
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getOP_code() {
		return OP_code;
	}
	public void setOP_code(String oP_code) {
		OP_code = oP_code;
	}
	public int getOP_num() {
		return OP_num;
	}
	public void setOP_num(int oP_num) {
		OP_num = oP_num;
	}
	public void Tokenizer(String input){    //  "inst.data" 화일로 부터 라인단위로 들어올때 라인을 정해진 규격에 따라 나눠주는 함수. 
		try{
		String[] values = input.split("\t");
		this.name=values[0];
		this.format=values[1];
		this.OP_code=values[2];
		this.OP_num=Integer.parseInt(values[3]);
		}catch(Exception e){
			System.out.println("Appendix parsing Error ! ");
		}
	}
}
