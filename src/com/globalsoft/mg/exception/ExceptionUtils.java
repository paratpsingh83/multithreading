//package com.jobportal.exception;
//
//public class ExceptionUtils {
//
//    public static ResourceNotFoundException resourceNotFound(String resourceName, String fieldName, Object fieldValue) {
//        return new ResourceNotFoundException(resourceName, fieldName, fieldValue);
//    }
//
//    public static DuplicateResourceException duplicateResource(String resourceName, String fieldName, Object fieldValue) {
//        return new DuplicateResourceException(resourceName, fieldName, fieldValue);
//    }
//
//    public static BusinessException businessError(String message) {
//        return new BusinessException(message);
//    }
//
//    public static ValidationException validationError(String message) {
//        return new ValidationException(message);
//    }
//
//    public static AuthenticationException authenticationError(String message) {
//        return new AuthenticationException(message);
//    }
//
//    public static InvalidOperationException invalidOperation(String message) {
//        return new InvalidOperationException(message);
//    }
//}
package com.jobportal.exception;

public class ExceptionUtils {

    public static ResourceNotFoundException resourceNotFound(String resource, String field, Object value) {
        return new ResourceNotFoundException(String.format("%s not found with %s: %s", resource, field, value));
    }

    public static DuplicateResourceException duplicateResource(String resource, String field, Object value) {
        return new DuplicateResourceException(String.format("%s already exists with %s: %s", resource, field, value));
    }

    public static ValidationException validationError(String message) {
        return new ValidationException(message);
    }

    public static BusinessException businessError(String message) {
        return new BusinessException(message);
    }

    public static ResourceNotFoundException resourceNotFound(String message) {
        return new ResourceNotFoundException(message);
    }
}