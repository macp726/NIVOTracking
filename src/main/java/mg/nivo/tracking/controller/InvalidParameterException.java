package mg.nivo.tracking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST ,reason="Invalid format in parameter")
public class InvalidParameterException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
