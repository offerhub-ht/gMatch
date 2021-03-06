package nlp.tool;

public class test {
	public static void main(String []args) {
		String str="which films were starred by julia roberts and richard gere?";
		String r=str.substring(str.indexOf("by julia roberts"),str.indexOf("richard gere"));
		System.out.println(r);
	}
}
