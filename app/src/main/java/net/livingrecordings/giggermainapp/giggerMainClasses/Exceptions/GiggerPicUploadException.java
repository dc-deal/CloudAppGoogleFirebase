package net.livingrecordings.giggermainapp.giggerMainClasses.Exceptions;

/**
 * Created by Kraetzig Neu on 28.12.2016.
 */

public class GiggerPicUploadException extends Exception {

    String mMessage;

    public GiggerPicUploadException() { super(); }
    public GiggerPicUploadException(String message) { super(message); }
    public GiggerPicUploadException(String message, Throwable cause) { super(message, cause); }
    public GiggerPicUploadException(Throwable cause) { super(cause); }

}
