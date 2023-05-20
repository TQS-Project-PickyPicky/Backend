package ies.project.backend.util;

public class CantAccessParcelException extends Exception{

    public CantAccessParcelException(String errorMessage) {
        super(errorMessage);
    }
}
