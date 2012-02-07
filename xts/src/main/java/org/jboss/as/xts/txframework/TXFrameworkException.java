package org.jboss.as.xts.txframework;

/**
 * @author paul.robinson@redhat.com, 2012-02-06
 */
public class TXFrameworkException extends Exception {

    public TXFrameworkException(String message) {
        super(message);
    }

    public TXFrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
