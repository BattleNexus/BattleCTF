package com.gamezgalaxy.ctf.map;

public class InvalidConfigException extends Exception {
	private static final long serialVersionUID = 4149165457661662184L;
	private final Throwable cause;
	
    /**
     * Constructs a new InvalidConfigException based on the given Exception
     *
     * @param throwable Exception that triggered this Exception
     */
    public InvalidConfigException(Throwable throwable) {
        cause = throwable;
    }

    /**
     * Constructs a new EventException
     */
    public InvalidConfigException() {
        cause = null;
    }

    /**
     * Constructs a new InvalidConfigException with the given message
     *
     * @param cause The exception that caused this
     * @param message The message
     */
    public InvalidConfigException(Throwable cause, String message) {
        super(message);
        this.cause = cause;
    }

    /**
     * Constructs a new InvalidConfigException with the given message
     *
     * @param message The message
     */
    public InvalidConfigException(String message) {
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
