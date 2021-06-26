/*
This is free and unencumbered software released into the public domain.

Anyone is free to copy, modify, publish, use, compile, sell, or
distribute this software, either in source code form or as a compiled
binary, for any purpose, commercial or non-commercial, and by any
means.

In jurisdictions that recognize copyright laws, the author or authors
of this software dedicate any and all copyright interest in the
software to the public domain. We make this dedication for the benefit
of the public at large and to the detriment of our heirs and
successors. We intend this dedication to be an overt act of
relinquishment in perpetuity of all present and future rights to this
software under copyright law.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

For more information, please refer to <http://unlicense.org/>
*/
package avaliacao.desenvolvedor.senior.handler;

import avaliacao.desenvolvedor.senior.dto.ApiErrorDto;
import avaliacao.desenvolvedor.senior.dto.ApiErrorFieldDto;
import br.com.caelum.stella.ValidationMessage;
import br.com.caelum.stella.validation.InvalidStateException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

// Reference: https://www.baeldung.com/global-error-handler-in-a-spring-rest-api
@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<Object> errors = new ArrayList<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }

        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        ApiErrorDto apiError = new ApiErrorDto(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);

        return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";
        ApiErrorDto apiError = new ApiErrorDto(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        List<Object> errors = new ArrayList<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            ApiErrorFieldDto errorField = new ApiErrorFieldDto();
            errorField.setClassName(violation.getRootBeanClass().getName());
            errorField.setField(violation.getPropertyPath().toString());
            errorField.setMessage(violation.getMessage());

            errors.add(errorField);
        }

        ApiErrorDto apiError = new ApiErrorDto(HttpStatus.UNPROCESSABLE_ENTITY, ex.getLocalizedMessage(), errors);

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

//	@ExceptionHandler({ MethodArgumentTypeMismatchException.class })
//	public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
//			WebRequest request) {
//		String error = ex.getName() + " should be of type " + ex.getRequiredType().getName();
//
//		ApiErrorDto apiError = new ApiErrorDto(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
//
//		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
//	}

    @ExceptionHandler({IllegalAccessException.class})
    public ResponseEntity<Object> handleIllegalAccess(IllegalAccessException ex, WebRequest request) {
        ApiErrorDto apiError = new ApiErrorDto(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), ex.getMessage());

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({InvocationTargetException.class})
    public ResponseEntity<Object> handleInvocationTarget(InvocationTargetException ex, WebRequest request) {
        ApiErrorDto apiError = new ApiErrorDto(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), ex.getMessage());

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({InvalidStateException.class})
    public ResponseEntity<Object> handleInvalidStateException(InvalidStateException ex, WebRequest request) {
        List<Object> errors = new ArrayList<>();

        for (ValidationMessage validation : ex.getInvalidMessages()) {
            errors.add(validation.getMessage());
        }

        ApiErrorDto apiError = new ApiErrorDto(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

}
