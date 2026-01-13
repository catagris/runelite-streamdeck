package com.catagris.streamdeck;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("streamdeck")
public interface StreamDeckConfig extends Config
{
	@ConfigItem(
		keyName = "enableClient",
		name = "Enable Client",
		description = "Enable sending game state to Stream Deck server"
	)
	default boolean enableClient()
	{
		return true;
	}

	@ConfigItem(
		keyName = "serverUrl",
		name = "Server URL",
		description = "URL of the Stream Deck server to send game state to"
	)
	default String serverUrl()
	{
		return "http://localhost:8085/state";
	}
}
