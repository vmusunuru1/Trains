package trains.exercise.exception;

public class InvalidRouteException extends Exception{
	
	private static final long serialVersionUID = -8576969300126424977L;

	public InvalidRouteException(String message){
		super(message);
	}
}