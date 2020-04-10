package com.wisdge.utils.security.sm;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public abstract class GMBaseUtil {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
}
