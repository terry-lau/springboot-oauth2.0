package com.easydatalink.tech.utils.captcha;

/**
 * 验证码异常
 * 
 * @author Terry
 */
public class CaptchaException extends RuntimeException {

	private static final long serialVersionUID = -6834765632942616744L;

	public CaptchaException() {
		super();
	}

	public CaptchaException(String message) {
		super(message);
	}

	public CaptchaException(Throwable cause) {
		super(cause);
	}

	public CaptchaException(String message, Throwable cause) {
		super(message, cause);
	}
}
