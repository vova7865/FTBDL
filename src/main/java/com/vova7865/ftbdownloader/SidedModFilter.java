package com.vova7865.ftbdownloader;

import com.vova7865.ftbdownloader.api.model.VersionManifest.File;
import com.vova7865.ftbdownloader.utils.Side;

public class SidedModFilter implements ModFilter {
	private Side side;

	public SidedModFilter(Side side) {
		this.side = side;
	}

	@Override
	public boolean shouldDownload(File file) {
		switch (side) {
			case SERVER:
				return !file.clientonly;
			case CLIENT:
				return !file.serveronly;
		}
		return true;
	}
}
