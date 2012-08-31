package com.gamezgalaxy.ctf.exceptions;

public class VoteDidntStartException extends Exception {
	private static final long serialVersionUID = 4149165457661662184L;
	private final Throwable cause;
	
    /**
     * Constructs a new VoteDidntStartException based on the given Exception
     *
     * @param throwable Exception that triggered this Exception
     */
    public VoteDidntStartException(Throwable throwable) {
        cause = throwable;
    }

    /**
     * Constructs a new EventException
     */
    public VoteDidntStartException() {
        cause = null;
    }

    /**
     * Constructs a new VoteDidntStartException with the given message
     *
     * @param cause The exception that caused this
     * @param message The message
     */
    public VoteDidntStartException(Throwable cause, String message) {
        super(message);
        this.cause = cause;
    }

    /**
     * Constructs a new VoteDidntStartException with the given message
     *
     * @param message The message
     */
    public VoteDidntStartException(String message) {
        super(message);
        cause = null;
    }

    /**
     * If applicable, returns the Exception that triggered this Exception
     *
     * @return Inner exception, or null if one does not exist
     */
    @Override
    public Throwable getCause() {
        return cause;
    }
}
