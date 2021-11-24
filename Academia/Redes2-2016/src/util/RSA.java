package util;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class RSA {
	private Cipher cipher;
	private KeyPair keypair;
	
	public RSA() throws Exception {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(1024);
		keypair = kpg.generateKeyPair();
		cipher = Cipher.getInstance("RSA");			
	}
	
	public KeyPair getKeys() {
		return this.keypair;
	}
	
	public byte[] getEncodedPrivateKey() {
		return this.keypair.getPrivate().getEncoded();
	}
	
	public byte[] getEncodedPublicKey() {
		return this.keypair.getPublic().getEncoded();
	}
	
	public String encrypt(String plaintext, Key key) throws Exception{
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] bytes = plaintext.getBytes("ISO-8859-1");
		byte[] encrypted = blockCipher(bytes,Cipher.ENCRYPT_MODE);
		return Base64.getEncoder().encodeToString(encrypted);
	}
	
	public String decrypt(String encrypted, Key key) throws Exception{
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] bts = Base64.getDecoder().decode(encrypted.getBytes());
		byte[] decrypted = blockCipher(bts,Cipher.DECRYPT_MODE);
		return new String(decrypted,"ISO-8859-1").trim();
	}
	
	private byte[] blockCipher(byte[] bytes, int mode) throws IllegalBlockSizeException, BadPaddingException{
		byte[] scrambled = new byte[0];
		byte[] toReturn = new byte[0];
		int length = (mode == Cipher.ENCRYPT_MODE)? 100 : 128;
		byte[] buffer = new byte[length];
		for (int i=0; i< bytes.length; i++){
			if ((i > 0) && (i % length == 0)){
				scrambled = cipher.doFinal(buffer);
				toReturn = append(toReturn,scrambled);
				int newlength = length;

				if (i + length > bytes.length) {
					 newlength = bytes.length - i;
				}
				buffer = new byte[newlength];
			}
			buffer[i%length] = bytes[i];
		}
		scrambled = cipher.doFinal(buffer);
		toReturn = append(toReturn,scrambled);
		return toReturn;
	}
	
	private byte[] append(byte[] prefix, byte[] suffix){
		byte[] toReturn = new byte[prefix.length + suffix.length];
		for (int i=0; i< prefix.length; i++){
			toReturn[i] = prefix[i];
		}
		for (int i=0; i< suffix.length; i++){
			toReturn[i+prefix.length] = suffix[i];
		}
		return toReturn;
	}
}
