package com.catagris.streamdeck;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.io.IOException;

@Slf4j
@PluginDescriptor(
	name = "Stream Deck Integration",
	description = "Exposes game state via HTTP for Stream Deck integration",
	tags = {"integration", "streamdeck", "api"}
)
public class StreamDeckPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private StreamDeckConfig config;

	@Inject
	private GameStateTracker gameStateTracker;

	@Inject
	private ClientThread clientThread;

	private StateHttpServer httpServer;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Stream Deck Integration plugin started");

		if (config.enableServer())
		{
			startHttpServer();
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Stream Deck Integration plugin stopped");
		stopHttpServer();
	}

	private void startHttpServer()
	{
		try
		{
			httpServer = new StateHttpServer(gameStateTracker, clientThread, config.serverPort());
			httpServer.start();
		}
		catch (IOException e)
		{
			log.error("Failed to start HTTP server", e);
		}
	}

	private void stopHttpServer()
	{
		if (httpServer != null)
		{
			httpServer.stop();
			httpServer = null;
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		// Handle game state changes if needed
		log.debug("Game state changed to: {}", event.getGameState());
	}

	@Provides
	StreamDeckConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(StreamDeckConfig.class);
	}
}
