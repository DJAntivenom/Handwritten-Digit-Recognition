package ch.elste.hdr.IO.exceptions;

import java.io.FileNotFoundException;

public class FileNotSpecifiedException extends FileNotFoundException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -412125694977195018L;

	/**
     * Constructs a <code>FileNotSpecifiedException</code> with
     * <code>null</code> as its error detail message.
     */
	public FileNotSpecifiedException() {
		super();
	}

	/**
	 * Constructs a <code>FileNotSpecifiedException</code> with the specified detail
	 * message. The string s can be retrieved later by the
	 * {@link java.lang.Throwable#getMessage} method of class {@link java.lang.Throwable}.
	 * 
	 * @param s
	 */
	public FileNotSpecifiedException(String s) {
		super(s);
	}

}
