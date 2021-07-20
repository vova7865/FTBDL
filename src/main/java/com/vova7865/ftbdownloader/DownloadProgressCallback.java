package com.vova7865.ftbdownloader;

@FunctionalInterface
public interface DownloadProgressCallback {
	void progress(float percentage);
}
