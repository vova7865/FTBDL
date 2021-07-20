package com.vova7865.ftbdownloader.api;

import com.google.gson.Gson;
import com.vova7865.ftbdownloader.DownloadProgressCallback;
import com.vova7865.ftbdownloader.api.model.Modpack;
import com.vova7865.ftbdownloader.api.model.ModpackSearchResult;
import com.vova7865.ftbdownloader.api.model.VersionManifest;
import com.vova7865.ftbdownloader.utils.HTTPUtils;

public class FTBApi {
	private final Gson gson = new Gson();
	private final String baseUrl;

	private DownloadProgressCallback callback;

	public FTBApi(String baseUrl) {
		this.baseUrl = baseUrl.endsWith("/") ? baseUrl : (baseUrl + "/");
	}

	public void setDownloadCallback(DownloadProgressCallback callback) {
		this.callback = callback;
	}

	/* <api methods> */
	public Modpack getModpack(int id) {
		return jsonRequest("public/modpack/" + id, Modpack.class);
	}

	public ModpackSearchResult searchForModpacks(String term) {
		return jsonRequest("public/modpack/search/50?term=" + term, ModpackSearchResult.class);
	}

	public VersionManifest getVersionManifest(int packId, int versionId) {
		return jsonRequest("public/modpack/" + packId + "/" + versionId, VersionManifest.class);
	}
	/* </api methods> */

	private <T> T jsonRequest(String url, Class<T> clazz) {
		byte[] bytes = HTTPUtils.readFully(this.baseUrl + url, callback);
		try {
			return (T) gson.fromJson(new String(bytes, "UTF-8"), clazz);
		} catch (Exception e) {
			throw new RuntimeException("Exception parsing " + url, e);
		}
	}
}
