package com.catagris.streamdeck;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("streamdeck")
public interface StreamDeckConfig extends Config
{
	@ConfigItem(
		keyName = "enableServer",
		name = "Enable HTTP Server",
		description = "Enable the HTTP server for Stream Deck integration"
	)
	default boolean enableServer()
	{
		return true;
	}

	@ConfigItem(
		keyName = "serverPort",
		name = "Server Port",
		description = "Port for the HTTP server"
	)
	default int serverPort()
	{
		return 8085;
	}
}
