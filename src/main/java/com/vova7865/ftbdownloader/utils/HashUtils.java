package com.vova7865.ftbdownloader.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class HashUtils {
	public static String sha1(File file) {
		try (FileInputStream inputStream = new FileInputStream(file)) {
			MessageDigest sha1 = MessageDigest.getInstance("SHA1");
			byte[] buf = new byte[1024];
			int read;
			while ((read = inputStream.read(buf)) != -1)
				sha1.update(buf, 0, read);
			byte[] digest = sha1.digest();
			String res = "";
			for (int i = 0; i < digest.length; i++) {
				res += String.format("%02x", digest[i] & 0xFF);
			}
			return res;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
