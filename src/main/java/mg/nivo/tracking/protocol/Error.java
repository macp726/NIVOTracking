package mg.nivo.tracking.protocol;


public class Error {
	public String code;
	public String message;
	public static Error build(ErrorType type, String msg, String...detail) {
		Error err = new Error();
		
		err.code = type.name();
		for(String d:detail)
			err.code += "."+d; 
		
		err.message = msg;
		return err;
	}
	
	public static void main(String[] args) {
		Error.build(ErrorType.unsupportedOper, "oo", "yy");
	}
}
