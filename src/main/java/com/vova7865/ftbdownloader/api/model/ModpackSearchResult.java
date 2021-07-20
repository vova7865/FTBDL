package com.vova7865.ftbdownloader.api.model;

import com.google.gson.annotations.SerializedName;

public class ModpackSearchResult {
	@SerializedName("packs")
	public int[] ftbPacks;
	@SerializedName("curseforge")
	public int[] cursePacks;
	
}
