package com.wisdge.utils;

import java.text.MessageFormat;

/**
 * Assert扩展类
 * 
 * @author Kevin MOU
 */
public final class Assert {
	public static boolean isLegal(boolean expression) {
		return isLegal(expression, "");
	}

	public static boolean isLegal(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
		return expression;
	}

	public static void isNull(Object object) {
		isNull(object, "null argument expected");
	}

	public static void isNull(Object object, String message) {
		if (object != null) {
			throw new AssertionFailedException(message);
		}
	}

	public static void isNull(Object object, String errorFormat, Object[] args) {
		if (object != null) {
			String message = String.format(errorFormat, args);
			fail(message);
		}
	}

	public static void isNull2(Object object, String errorFormat, Object[] args) {
		if (object != null) {
			String message = MessageFormat.format(errorFormat, args);
			fail(message);
		}
	}

	public static void isNotNull(Object object) {
		isNotNull(object, "null argument");
	}

	public static void isNotNull(Object object, String message) {
		if (object == null) {
			fail(message);
		}
	}

	public static void isNotNull(Object object, String errorFormat, Object[] args) {
		if (object == null) {
			fail(String.format(errorFormat, args));
		}
	}

	public static void isNotNull2(Object object, String errorFormat, Object[] args) {
		if (object == null) {
			String message = MessageFormat.format(errorFormat, args);
			fail(message);
		}
	}

	public static void fail(String message) {
		throw new AssertionFailedException(message);
	}

	public static void fail(String errorFormat, Object[] args) {
		String message = MessageFormat.format(errorFormat, args);
		throw new AssertionFailedException(message);
	}

	public static boolean isTrue(boolean expression) {
		return isTrue(expression, "assertion failed");
	}

	public static boolean isTrue(boolean expression, String message) {
		if (!expression) {
			fail(message);
		}
		return expression;
	}

	public static boolean isTrue(boolean expression, String errorFormat, Object[] args) {
		if (!expression) {
			String message = String.format(errorFormat, args);
			fail(message);
		}
		return expression;
	}

	public static boolean isTrue2(boolean expression, String errorFormat, Object[] args) {
		if (!expression) {
			fail(errorFormat, args);
		}
		return expression;
	}

	public static void equals(int expected, int actual) {
		equals(expected, actual, expected + " expected, but " + actual + " found");
	}

	public static void equals(int expected, int actual, String message) {
		if (expected != actual) {
			fail(message);
		}
	}

	public static void instanceOf(Class<?> expectedClass, Object o) {
		if (o == null) {
			fail(expectedClass.getName() + " expected, but 'null' found.");
		}
		if (!expectedClass.isAssignableFrom(o.getClass())) {
			fail(expectedClass.getName() + " expected, but " + o.getClass().getName() + " found.");
		}
	}
}

final class AssertionFailedException extends RuntimeException {
	private static final long serialVersionUID = 0L;

	public AssertionFailedException(String detail) {
		super(detail);
	}
}
