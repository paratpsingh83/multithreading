package com.jobportal.exception;

public class FileAccessDeniedException extends FileStorageException {
    public FileAccessDeniedException(String message) {
        super(message);
    }
    
    public FileAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}