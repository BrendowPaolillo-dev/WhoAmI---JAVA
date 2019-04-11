package game;

public enum RequestType {
	PRINT("print."),
	CONFIMED("ok."),
	NOT_CONFIRMED("not_ok.");

	private final String request;
	
	RequestType(final String request) {
		this.request = request;
	}
	
	@Override
    public String toString() {
        return this.request;
    }
}
