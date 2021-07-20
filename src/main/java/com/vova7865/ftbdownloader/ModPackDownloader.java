package com.vova7865.ftbdownloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import com.vova7865.ftbdownloader.api.FTBApi;
import com.vova7865.ftbdownloader.api.model.Modpack;
import com.vova7865.ftbdownloader.api.model.Modpack.ModpackVersion;
import com.vova7865.ftbdownloader.api.model.ModpackSearchResult;
import com.vova7865.ftbdownloader.api.model.VersionManifest;
import com.vova7865.ftbdownloader.utils.HTTPUtils;
import com.vova7865.ftbdownloader.utils.HashUtils;
import com.vova7865.ftbdownloader.utils.Side;

public class ModPackDownloader {
	public static void main(String[] args) {
		search("revelation");
		start(35, "latest", Side.SERVER, ".", false);
	}

	public static void search(String term) {
		FTBApi client = new FTBApi("https://api.modpacks.ch/");
		ModpackSearchResult result = client.searchForModpacks(term);
		if (result.ftbPacks.length == 0) {
			System.out.println("Nothing found :P");
			return;
		}
		System.out.println("Search results: ");
		for (int ftbPackId : result.ftbPacks) {
			Modpack pack = client.getModpack(ftbPackId);
			System.out.println("    \"" + pack.name + "\" id: " + pack.id);
			System.out.println("    Synopsis: " + pack.synopsis);
			System.out.println("    Versions: ");
			for (ModpackVersion version : pack.versions) {
				System.out.println("        " + version.name + " id: " + version.id + " (updated "
						+ Instant.ofEpochSecond(version.updated) + ")");
			}
			System.out.println();
		}
	}

	public static void start(int modpackId, String version, Side side, String path, boolean skipHashing) {
		FTBApi client = new FTBApi("https://api.modpacks.ch/");
		Modpack pack = client.getModpack(modpackId);
		System.out.println("Pack Name: " + pack.name);
		System.out.println("Pack ID: " + pack.id);
		System.out.println("Pack Synopsis: " + pack.synopsis);
		int versionId = -1;
		if (version.equalsIgnoreCase("latest")) {
			versionId = getLatestModpackVersion(pack.versions).id;
		} else {
			for (ModpackVersion packVersion : pack.versions) {
				if (packVersion.name.equals(version)) {
					versionId = packVersion.id;
					break;
				}
			}
			if (versionId == -1) {
				versionId = Integer.parseInt(version);
			}
		}
		VersionManifest versionManifest = client.getVersionManifest(modpackId, versionId);
		for (VersionManifest.File modFile : versionManifest.files) {
			if (modFile.path.contains("..") || modFile.path.startsWith("/") || modFile.name.contains("..")
					|| modFile.name.startsWith("/")) {
				System.out.println("!!! DANGEROUS PATH " + modFile.path + " FOR FILE " + modFile.name + " !!!");
				System.out.println("Download aborted");
				return;
			}
		}
		downloadFiles(versionManifest.files, Paths.get(path), skipHashing, new SidedModFilter(side),
				new RequestUserOptionalModFilter());
	}

	private static void downloadFiles(VersionManifest.File[] files, Path basePath, boolean skipHashing,
			ModFilter... filters) {
		if (!basePath.toFile().isDirectory()) {
			System.out.println(basePath + " exists and is not a directory");
			return;
		}
		System.out.println("Starting to download pack files");
		downloadLoop: for (VersionManifest.File modFile : files) {
			for (ModFilter filter : filters) {
				if (!filter.shouldDownload(modFile)) {
					System.out.println("Refusing to download " + modFile.name);
					continue downloadLoop;
				}
			}
			byte[] modBytes = HTTPUtils.readFully(modFile.url, createConsoleCallback("Downloading " + modFile.name));
			Path targetDir = basePath.resolve(modFile.path).normalize();
			targetDir.toFile().mkdirs();
			Path target = targetDir.resolve(modFile.name).normalize();
			try {
				System.out.println("Writing to " + target);
				Files.write(target, modBytes);
			} catch (IOException e) {
				System.err.println("Could not write file " + target + "!");
				e.printStackTrace();
				return;
			}
			if (!skipHashing) {
				String sha1 = HashUtils.sha1(target.toFile());
				if (!modFile.sha1.equals(sha1)) {
					System.out.println("Integrity check failed for " + target);
					System.out.println("Expected: " + modFile.sha1);
					System.out.println("Downloaded: " + sha1);
					return;
				}
			}
		}
		System.out.println("Download complete");
	}

	private static ModpackVersion getLatestModpackVersion(ModpackVersion[] candidates) {
		ModpackVersion latest = null;
		for (ModpackVersion version : candidates) {
			latest = latest == null ? version : version.updated > latest.updated ? version : latest;
		}
		return latest;
	}

	private static DownloadProgressCallback createConsoleCallback(String text) {
		return (percent) -> {
			String hashes = "";
			int totalHashes = 50;
			for (int i = 0; i < totalHashes; i++) {
				if (i * (100 / totalHashes) < percent)
					hashes += "#";
				else
					hashes += ".";
			}
			System.out.print("\r" + text + " [" + hashes + "] " + (int) percent + "%");
			if (percent == 100) {
				System.out.println();
			}
		};
	}
}
