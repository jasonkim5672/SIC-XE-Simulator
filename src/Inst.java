import java.lang.String;

public class Inst {
	private	String name;     //���� OP�ڵ��� ���Ӹ� �̸� ���� ���ִ� �ʵ� 
	private String format;    // � ������ �����͸� �������ִ��� ǥ���ϴ� �ʵ� 
	private String OP_code;     // ���� OP�ڵ��� ��Ʈ������ �ʵ�
	private int OP_num;        // ������ �ִ� �ǿ������� ������ �����س��� �ʵ�  (,X �ε����� ���⼭ ��� )
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
	public void Tokenizer(String input){    //  "inst.data" ȭ�Ϸ� ���� ���δ����� ���ö� ������ ������ �԰ݿ� ���� �����ִ� �Լ�. 
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
