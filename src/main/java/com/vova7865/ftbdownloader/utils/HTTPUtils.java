package com.vova7865.ftbdownloader.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.vova7865.ftbdownloader.DownloadProgressCallback;

public class HTTPUtils {
	public static byte[] readFully(String url) {
		return readFully(url, null);
	}

	public static byte[] readFully(String url, DownloadProgressCallback callback) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			InputStream inputStream = conn.getInputStream();
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int readBytes, totalReadBytes = 0;
			if (callback != null)
				callback.progress(0);
			while ((readBytes = inputStream.read(buf)) != -1) {
				data.write(buf, 0, readBytes);
				totalReadBytes += readBytes;
				if (callback != null)
					callback.progress(((float) totalReadBytes) / conn.getContentLength() * 100);
			}
			return data.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException("Failed to download " + url, e);
		}
	}
}
