package com.wisdge.web.verifier;

import org.junit.Test;
import com.wisdge.utils.RandomUtils;

public class VerifyFactory {
	private String codeType = "digit";
	private int codeLength = 6;
	private Timer timer;
	private long expires = 5 * 60;

	public VerifyFactory() {
		timer = new Timer();
	}
	
	public String printTimerHash() {
		return timer.toString();
	}

	public String getCodeType() {
		return codeType;
	}

	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}

	public int getCodeLength() {
		return codeLength;
	}

	public void setCodeLength(int codeLength) {
		this.codeLength = codeLength;
	}

	public long getExpires() {
		return expires;
	}

	public void setExpires(long expires) {
		this.expires = expires;
	}

	/**
	 * 创建一个校验码
	 * 
	 * @return String
	 */
	public String createCode() {
		return createCode(null);
	}

	/**
	 * 为申请人创建一个校验码
	 * 
	 * @param sponsor
	 *            校验申请人
	 * @return String
	 */
	public String createCode(String sponsor) {
		String code;
		if (codeType.equalsIgnoreCase("DIGIT"))
			code = RandomUtils.getNumber(codeLength);
		else if (codeType.equalsIgnoreCase("ALPHA"))
			code = RandomUtils.getAlphabetic(codeLength);
		else
			code = RandomUtils.getString(codeLength);
		Verify verify = new Verify(code, sponsor);
		timer.schedule(verify, expires * 1000);
		return code;
	}

	/**
	 * 校验code
	 * 
	 * @param code
	 *            校验码
	 * @param cancel
	 *            是否校验后失效？true=校验成功后该校验码失效，false=不失效
	 * @return int 1:校验成功， 0:校验码过期， -1:校验失败
	 */
	public int isAlive(String code, boolean cancel) {
		return timer.isAlive(code, null, cancel);
	}

	/**
	 * 校验code，必须由申请人发起的校验
	 * 
	 * @param code
	 *            校验码
	 * @param sponsor
	 *            校验申请人
	 * @param cancel
	 *            是否校验后失效？true=校验成功后该校验码失效，false=不失效
	 * @return int 1:校验成功， 0:校验码过期， -1:校验失败
	 */
	public int isAlive(String code, String sponsor, boolean cancel) {
		return timer.isAlive(code, sponsor, cancel);
	}
	
	public void destroy() {
		timer.cancel();
	}
	
	@Test
	public void test() throws InterruptedException {
		VerifyFactory factory = new VerifyFactory();
		factory.setCodeLength(6);
		factory.setCodeType("DIGIT");
		factory.setExpires(1);
		String mobile = "18621991973";
		String code = factory.createCode(mobile);
		System.out.println(code);
		System.out.println(factory.isAlive(code, mobile, true));
		
		code = factory.createCode(mobile);
		System.out.println(code);
		Thread.sleep(1000*2);
		System.out.println(factory.isAlive(code, mobile, true));
	}
}
