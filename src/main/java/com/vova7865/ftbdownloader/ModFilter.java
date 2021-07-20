package com.vova7865.ftbdownloader;

import com.vova7865.ftbdownloader.api.model.VersionManifest;

@FunctionalInterface
public interface ModFilter {
	boolean shouldDownload(VersionManifest.File file);
}
