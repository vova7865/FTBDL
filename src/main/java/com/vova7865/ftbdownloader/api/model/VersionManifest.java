package com.vova7865.ftbdownloader.api.model;

public class VersionManifest {
	public File[] files;
	public static class File {
		public String path;
		public String name;
		public String url;
		public String sha1;
		public boolean clientonly, serveronly;
		public boolean optional;
		public int id;
		public String type;
	}
}
