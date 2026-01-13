package com.catagris.streamdeck;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.inject.Inject;
import java.io.IOException;

@Slf4j
@PluginDescriptor(
	name = "Stream Deck Integration",
	description = "Sends game state to Stream Deck server",
	tags = {"integration", "streamdeck", "api"}
)
public class StreamDeckPlugin extends Plugin
{
	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	@Inject
	private Client client;

	@Inject
	private StreamDeckConfig config;

	@Inject
	private GameStateTracker gameStateTracker;

	@Inject
	private OkHttpClient okHttpClient;

	private boolean lastRequestFailed = false;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Stream Deck Integration plugin started");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Stream Deck Integration plugin stopped");
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (!config.enableClient())
		{
			return;
		}

		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		sendStateUpdate();
	}

	private void sendStateUpdate()
	{
		String json = gameStateTracker.getStateJson();

		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder()
			.url(config.serverUrl())
			.post(body)
			.build();

		okHttpClient.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
				if (!lastRequestFailed)
				{
					log.debug("Failed to send state to Stream Deck server: {}", e.getMessage());
					lastRequestFailed = true;
				}
			}

			@Override
			public void onResponse(Call call, Response response)
			{
				response.close();
				if (lastRequestFailed)
				{
					log.debug("Connection to Stream Deck server restored");
					lastRequestFailed = false;
				}
			}
		});
	}

	@Provides
	StreamDeckConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(StreamDeckConfig.class);
	}
}