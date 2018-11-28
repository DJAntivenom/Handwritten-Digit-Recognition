package ch.elste.hdr.GUI;

import ch.elste.hdr.Main;
import ch.elste.hdr.IO.IODataLoader;

/**
 * Abstract controller class.
 * 
 * @author Dillon Elste
 *
 */
public abstract class Controller {
	/**
	 * The owner of this controller.
	 */
	protected Main owner;
	
	/**
	 * The {@link IODataLoader loader} for handling data IO.
	 */
	protected IODataLoader loader;
	
	/**
	 * Set this controllers parent.
	 * @param parent to be set.
	 */
	public abstract void setOwner(Main parent);

	/**
	 * Returns the Parent of this Controller.
	 * @return the Parent of the Controller.
	 */
	public abstract Main getOwner();
}