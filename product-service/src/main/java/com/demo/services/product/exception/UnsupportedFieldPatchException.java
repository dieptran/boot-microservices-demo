package com.demo.services.product.exception;

import java.util.Set;

public class UnsupportedFieldPatchException extends RuntimeException {
	public UnsupportedFieldPatchException(Set<String> keys) {
        super("Field " + keys.toString() + "is not supported for updating.");
    }
}
