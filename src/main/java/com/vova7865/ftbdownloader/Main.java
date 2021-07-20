package com.vova7865.ftbdownloader;

import java.io.IOException;
import java.util.Arrays;

import com.vova7865.ftbdownloader.utils.Side;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

public class Main {
	public static void main(String[] args) {
		OptionParser parser = new OptionParser();

		OptionSpecBuilder downloadMode = parser.acceptsAll(Arrays.asList("download"), "Download a modpack");
		OptionSpecBuilder searchMode = parser.acceptsAll(Arrays.asList("search"), "Search for modpack");
		searchMode.withRequiredArg().ofType(String.class);

		downloadMode.requiredUnless("search");
		searchMode.requiredUnless("download");
		parser.mutuallyExclusive(downloadMode, searchMode);

		parser.acceptsAll(Arrays.asList("p", "path"), "Where to download the modpack").availableIf(downloadMode)
				.withRequiredArg()
				.ofType(String.class).defaultsTo(".");
		parser.acceptsAll(Arrays.asList("s", "side"), "Side to download for. Values: client, server")
				.availableIf(downloadMode).withRequiredArg()
				.withValuesConvertedBy(new ValueConverter<Side>() {
					@Override
					public Side convert(String value) {
						for (Side side : Side.values()) {
							if (side.name().equalsIgnoreCase(value))
								return side;
						}
						throw new ValueConversionException("side");
					}

					@Override
					public String revert(Object value) {
						return ((Side) value).name().toLowerCase();
					}

					@Override
					public Class<? extends Side> valueType() {
						return Side.class;
					}

					@Override
					public String valuePattern() {
						return null;
					}
				}).defaultsTo(Side.CLIENT);
		parser.acceptsAll(Arrays.asList("m", "modpack"), "Modpack ID").requiredIf(downloadMode).withRequiredArg()
				.ofType(Integer.class);
		parser.acceptsAll(Arrays.asList("v", "version"), "Modpack version").availableIf(downloadMode).withRequiredArg()
				.ofType(String.class)
				.defaultsTo("latest");
		parser.acceptsAll(Arrays.asList("n", "skip-check"), "Skip SHA1 mod file hashing").availableIf(downloadMode);
		try {
			OptionSet options = parser.parse(args);
			if (options.has("download")) {
				int modpack = (int) options.valueOf("modpack");
				String version = (String) options.valueOf("version");
				Side side = (Side) options.valueOf("side");
				String path = (String) options.valueOf("path");
				boolean skipHash = options.has("skip-check");
				ModPackDownloader.start(modpack, version, side, path, skipHash);
			} else if (options.has("search")) {
				String searchTerm = (String) options.valueOf("search");
				ModPackDownloader.search(searchTerm);
			}
		} catch (OptionException e) {
			try {
				System.out.println("Error: " + e.getMessage());
				System.out.println();
				parser.printHelpOn(System.out);
			} catch (IOException e1) {}
		}
	}
}
