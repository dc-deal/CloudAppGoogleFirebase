package net.livingrecordings.giggermainapp.giggerMainClasses.Exceptions;

/**
 * Created by Kraetzig Neu on 03.01.2017.
 */

public class Gigger_NoItemInputException extends Exception {

    String mMessage;

    public Gigger_NoItemInputException() { super(); }
    public Gigger_NoItemInputException(String message) { super(message); }
    public Gigger_NoItemInputException(String message, Throwable cause) { super(message, cause); }
    public Gigger_NoItemInputException(Throwable cause) { super(cause); }

}