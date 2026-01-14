package com.catagris.streamdeck;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("streamdeck")
public interface StreamDeckConfig extends Config
{
	@ConfigItem(
		keyName = "enableFileOutput",
		name = "Enable File Output",
		description = "Enable writing game state to a file for Stream Deck integration"
	)
	default boolean enableFileOutput()
	{
		return true;
	}

	@ConfigItem(
		keyName = "outputFilePath",
		name = "Output File Path",
		description = "Path to write game state JSON (leave empty for default: .runelite/streamdeck-state.json)"
	)
	default String outputFilePath()
	{
		return "";
	}
}
