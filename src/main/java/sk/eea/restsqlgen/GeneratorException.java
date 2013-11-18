package sk.eea.restsqlgen;

public class GeneratorException extends Exception {
	private static final long serialVersionUID = -1832029639072234607L;

	public GeneratorException() {
		super();
	}
	
	public GeneratorException(String msg) {
		super(msg);
	}

	public GeneratorException(Throwable cause) {
		super(cause);
	}

	public GeneratorException(String message, Throwable cause) {
		super(message, cause);
	}


}
