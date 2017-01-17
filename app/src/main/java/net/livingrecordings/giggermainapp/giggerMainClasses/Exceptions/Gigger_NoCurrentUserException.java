package net.livingrecordings.giggermainapp.giggerMainClasses.Exceptions;

/**
 * Created by Kraetzig Neu on 03.01.2017.
 */

public class Gigger_NoCurrentUserException extends Exception {

    String mMessage;

    public Gigger_NoCurrentUserException() { super(); }
    public Gigger_NoCurrentUserException(String message) { super(message); }
    public Gigger_NoCurrentUserException(String message, Throwable cause) { super(message, cause); }
    public Gigger_NoCurrentUserException(Throwable cause) { super(cause); }

}