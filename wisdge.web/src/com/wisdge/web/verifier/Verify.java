package com.wisdge.web.verifier;

public class Verify extends TimerTask {
	protected Verify(String id) {
		super(id);
	}
	
	protected Verify(String id, String sponsor) {
		super(id, sponsor);
	}
	
	@Override
	public void run() {
		this.cancel();	
	}

}
