/**
 * 
 */
package com.lifeForce.storage;

/**
 * @author arun_malik
 *
 */
public class ImageNotFoundException extends Exception {

	  public ImageNotFoundException(Throwable cause)
	    {
	        super(cause);
	    }

	    public ImageNotFoundException(String message, Throwable cause)
	    {
	        super(message, cause);
	    }

	    public ImageNotFoundException(String message)
	    {
	        super(message);
	    }

	    public ImageNotFoundException()
	    {
	        super();
	    }
}
