package com.wisdge.utils.security.sm;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.junit.Assert;
import org.junit.Test;

import java.security.KeyPair;
import java.util.Arrays;

public class SM2UtilTest {
	public static final byte[] SRC_DATA = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
	public static final byte[] WITH_ID = new byte[] { 1, 2, 3, 4 };

	public void testSignAndVerify() {
		try {
			AsymmetricCipherKeyPair keyPair = SM2Util.generateKeyPairParameter();
			ECPrivateKeyParameters priKey = (ECPrivateKeyParameters) keyPair.getPrivate();
			ECPublicKeyParameters pubKey = (ECPublicKeyParameters) keyPair.getPublic();

			System.out.println("Pri Hex:" + ByteUtils.toHexString(priKey.getD().toByteArray()).toUpperCase());
			System.out.println("Pub X Hex:" + ByteUtils.toHexString(pubKey.getQ().getAffineXCoord().getEncoded()).toUpperCase());
			System.out.println("Pub Y Hex:" + ByteUtils.toHexString(pubKey.getQ().getAffineYCoord().getEncoded()).toUpperCase());
			System.out.println("Pub Point Hex:" + ByteUtils.toHexString(pubKey.getQ().getEncoded(false)).toUpperCase());

			byte[] sign = SM2Util.sign(priKey, WITH_ID, SRC_DATA);
			System.out.println("SM2 sign with withId result:\n" + ByteUtils.toHexString(sign));
			byte[] rawSign = SM2Util.decodeDERSM2Sign(sign);
			sign = SM2Util.encodeSM2SignToDER(rawSign);
			System.out.println("SM2 sign with withId result:\n" + ByteUtils.toHexString(sign));
			boolean flag = SM2Util.verify(pubKey, WITH_ID, SRC_DATA, sign);
			if (!flag) {
				Assert.fail("verify failed");
			}
			sign = SM2Util.sign(priKey, SRC_DATA);
			System.out.println("SM2 sign without withId result:\n" + ByteUtils.toHexString(sign));
			flag = SM2Util.verify(pubKey, SRC_DATA, sign);
			if (!flag) {
				Assert.fail("verify failed");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testEncryptAndDecrypt() {
	   	String message = "Hello";
		try {
			AsymmetricCipherKeyPair keyPair = SM2Util.generateKeyPairParameter();
			ECPrivateKeyParameters priKey = (ECPrivateKeyParameters) keyPair.getPrivate();
			ECPublicKeyParameters pubKey = (ECPublicKeyParameters) keyPair.getPublic();

			System.out.println("Pri Hex:" + ByteUtils.toHexString(priKey.getD().toByteArray()).toUpperCase());
			System.out.println("Pub X Hex:" + ByteUtils.toHexString(pubKey.getQ().getAffineXCoord().getEncoded()).toUpperCase());
			System.out.println("Pub Y Hex:" + ByteUtils.toHexString(pubKey.getQ().getAffineYCoord().getEncoded()).toUpperCase());
			System.out.println("Pub Point Hex:" + ByteUtils.toHexString(pubKey.getQ().getEncoded(false)).toUpperCase());

			String encrypt = ByteUtils.toHexString(SM2Util.encrypt(pubKey, message.getBytes()));
			System.out.println(encrypt);
			System.out.println(new String(SM2Util.decrypt(priKey, ByteUtils.fromHexString(encrypt))));
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail();
		}
	}

	public void testKeyPairEncoding() {
		try {
			AsymmetricCipherKeyPair keyPair = SM2Util.generateKeyPairParameter();
			ECPrivateKeyParameters priKey = (ECPrivateKeyParameters) keyPair.getPrivate();
			ECPublicKeyParameters pubKey = (ECPublicKeyParameters) keyPair.getPublic();

			byte[] priKeyPkcs8Der = BCECUtil.convertECPrivateKeyToPKCS8(priKey, pubKey);
			System.out.println("private key pkcs8 der length:" + priKeyPkcs8Der.length);
			System.out.println("private key pkcs8 der:" + ByteUtils.toHexString(priKeyPkcs8Der));
			FileUtil.writeFile("/Users/kevinmou/Documents/ec.pkcs8.pri.der", priKeyPkcs8Der);

			String priKeyPkcs8Pem = BCECUtil.convertECPrivateKeyPKCS8ToPEM(priKeyPkcs8Der);
			FileUtil.writeFile("/Users/kevinmou/Documents/ec.pkcs8.pri.pem", priKeyPkcs8Pem.getBytes("UTF-8"));
			byte[] priKeyFromPem = BCECUtil.convertECPrivateKeyPEMToPKCS8(priKeyPkcs8Pem);
			if (!Arrays.equals(priKeyFromPem, priKeyPkcs8Der)) {
				throw new Exception("priKeyFromPem != priKeyPkcs8Der");
			}

			//BCECPrivateKey newPriKey = BCECUtil.convertPKCS8ToECPrivateKey(priKeyPkcs8Der);

			byte[] priKeyPkcs1Der = BCECUtil.convertECPrivateKeyToSEC1(priKey, pubKey);
			System.out.println("private key pkcs1 der length:" + priKeyPkcs1Der.length);
			System.out.println("private key pkcs1 der:" + ByteUtils.toHexString(priKeyPkcs1Der));
			FileUtil.writeFile("/Users/kevinmou/Documents/ec.pkcs1.pri", priKeyPkcs1Der);

			byte[] pubKeyX509Der = BCECUtil.convertECPublicKeyToX509(pubKey);
			System.out.println("public key der length:" + pubKeyX509Der.length);
			System.out.println("public key der:" + ByteUtils.toHexString(pubKeyX509Der));
			FileUtil.writeFile("/Users/kevinmou/Documents/ec.x509.pub.der", pubKeyX509Der);

			String pubKeyX509Pem = BCECUtil.convertECPublicKeyX509ToPEM(pubKeyX509Der);
			FileUtil.writeFile("/Users/kevinmou/Documents/ec.x509.pub.pem", pubKeyX509Pem.getBytes("UTF-8"));
			byte[] pubKeyFromPem = BCECUtil.convertECPublicKeyPEMToX509(pubKeyX509Pem);
			if (!Arrays.equals(pubKeyFromPem, pubKeyX509Der)) {
				throw new Exception("pubKeyFromPem != pubKeyX509Der");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail();
		}
	}

	public void testSM2KeyRecovery() {
		try {
			AsymmetricCipherKeyPair keyPair = SM2Util.generateKeyPairParameter();
			ECPrivateKeyParameters priKey = (ECPrivateKeyParameters) keyPair.getPrivate();
			ECPublicKeyParameters pubKey = (ECPublicKeyParameters) keyPair.getPublic();

			String priHex = ByteUtils.toHexString(priKey.getD().toByteArray()).toUpperCase();
			String pubHex = ByteUtils.toHexString(pubKey.getQ().getEncoded(false)).toUpperCase();
			System.out.println("Pri Hex:" + priHex);
			System.out.println("Pub Hex:" + pubHex);

			String encrypt = SM2Util.encrypt(pubHex, "Hello world");
			System.out.println(encrypt);
			System.out.println(SM2Util.decrypt(priHex, encrypt));
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail();
		}
	}

	public void testSM2KeyGen2() {
		try {
			AsymmetricCipherKeyPair keyPair = SM2Util.generateKeyPairParameter();
			ECPrivateKeyParameters priKey = (ECPrivateKeyParameters) keyPair.getPrivate();
			ECPublicKeyParameters pubKey = (ECPublicKeyParameters) keyPair.getPublic();

			System.out.println("Pri Hex:" + ByteUtils.toHexString(priKey.getD().toByteArray()).toUpperCase());
			System.out.println("Pub X Hex:" + ByteUtils.toHexString(pubKey.getQ().getAffineXCoord().getEncoded()).toUpperCase());
			System.out.println("Pub Y Hex:" + ByteUtils.toHexString(pubKey.getQ().getAffineYCoord().getEncoded()).toUpperCase());
			System.out.println("Pub Point Hex:" + ByteUtils.toHexString(pubKey.getQ().getEncoded(false)).toUpperCase());
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail();
		}
	}

	public void testEncodeSM2CipherToDER() {
		try {
			AsymmetricCipherKeyPair keyPair = SM2Util.generateKeyPairParameter();
			ECPrivateKeyParameters priKey = (ECPrivateKeyParameters) keyPair.getPrivate();
			ECPublicKeyParameters pubKey = (ECPublicKeyParameters) keyPair.getPublic();

			byte[] encryptedData = SM2Util.encrypt(pubKey, SRC_DATA);

			byte[] derCipher = SM2Util.encodeSM2CipherToDER(encryptedData);
			FileUtil.writeFile("derCipher.dat", derCipher);

			byte[] decryptedData = SM2Util.decrypt(priKey, SM2Util.decodeDERSM2Cipher(derCipher));
			if (!Arrays.equals(decryptedData, SRC_DATA)) {
				Assert.fail();
			}

			Assert.assertTrue(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail();
		}
	}

	public void testGenerateBCECKeyPair() {
		try {
			KeyPair keyPair = SM2Util.generateKeyPair();
			ECPrivateKeyParameters priKey = BCECUtil.convertPrivateKeyToParameters((BCECPrivateKey) keyPair.getPrivate());
			ECPublicKeyParameters pubKey = BCECUtil.convertPublicKeyToParameters((BCECPublicKey) keyPair.getPublic());

			byte[] sign = SM2Util.sign(priKey, WITH_ID, SRC_DATA);
			boolean flag = SM2Util.verify(pubKey, WITH_ID, SRC_DATA, sign);
			if (!flag) {
				Assert.fail("verify failed");
			}

			sign = SM2Util.sign(priKey, SRC_DATA);
			flag = SM2Util.verify(pubKey, SRC_DATA, sign);
			if (!flag) {
				Assert.fail("verify failed");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail();
		}
	}
}
