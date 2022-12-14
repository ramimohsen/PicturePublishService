package com.ims.picturepublishservice.exception;

public class AlreadyExistException extends Exception {
    static final long serialVersionUID = -3387516993334224948L;

    public AlreadyExistException(String message) {
        super(message);
    }
}
