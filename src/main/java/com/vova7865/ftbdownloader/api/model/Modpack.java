package com.vova7865.ftbdownloader.api.model;

public class Modpack {
	public String name;
	public int id;
	public String synopsis;
	public ModpackVersion[] versions;
	
	public static class ModpackVersion {
		public int id;
		public String name;
		public int updated;
	}
}
