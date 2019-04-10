package game;

public enum RequestType {
	PRINT("print."),
	ATTEMPT("attempt."),
	PERSONA("persona."),
	QUESTION("question."),
	ANSWER("answer."),
	CONFIMED("ok."),
	NOT_CONFIRMED("not_ok."),
	TIP("tip.");
	
	private final String request;
	
	RequestType(final String request) {
		this.request = request;
	}
	
	@Override
    public String toString() {
        return this.request;
    }
}
