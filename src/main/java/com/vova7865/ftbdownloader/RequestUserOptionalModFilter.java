package com.vova7865.ftbdownloader;

import java.util.Scanner;

import com.vova7865.ftbdownloader.api.model.VersionManifest.File;

public class RequestUserOptionalModFilter implements ModFilter {
	@Override
	public boolean shouldDownload(File file) {
		if (file.optional) {
			Scanner scanner = new Scanner(System.in);
			System.out.print("Do you want to download optional file " + file.path + file.name + "?");
			while (true) {
				String line = scanner.nextLine();
				if (line.startsWith("y"))
					return true;
				if (line.startsWith("n"))
					return false;
			}
		}
		return true;
	}
}
