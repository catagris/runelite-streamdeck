package com.catagris.streamdeck;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameTick;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Slf4j
@PluginDescriptor(
	name = "Stream Deck Integration",
	description = "Writes game state to a file for Stream Deck integration",
	tags = {"integration", "streamdeck", "file"}
)
public class StreamDeckPlugin extends Plugin
{
	private static final String DEFAULT_FILENAME = "streamdeck-state.json";

	@Inject
	private Client client;

	@Inject
	private StreamDeckConfig config;

	@Inject
	private GameStateTracker gameStateTracker;

	private File outputFile;
	private boolean lastWriteFailed = false;

	@Override
	protected void startUp() throws Exception
	{
		outputFile = getOutputFile();
		log.info("Stream Deck Integration plugin started, writing to: {}", outputFile.getAbsolutePath());
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Stream Deck Integration plugin stopped");
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (!config.enableFileOutput())
		{
			return;
		}

		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		writeStateToFile();
	}

	private File getOutputFile()
	{
		String configPath = config.outputFilePath();
		if (configPath != null && !configPath.trim().isEmpty())
		{
			return new File(configPath);
		}
		return new File(RuneLite.RUNELITE_DIR, DEFAULT_FILENAME);
	}

	private void writeStateToFile()
	{
		String json = gameStateTracker.getStateJson();

		try
		{
			// Write to temp file first, then rename for atomic operation
			File tempFile = new File(outputFile.getParentFile(), outputFile.getName() + ".tmp");
			Files.writeString(tempFile.toPath(), json, StandardCharsets.UTF_8,
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

			// Atomic rename
			if (!tempFile.renameTo(outputFile))
			{
				// Fallback: direct write if rename fails (e.g., cross-filesystem)
				Files.writeString(outputFile.toPath(), json, StandardCharsets.UTF_8,
					StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
				tempFile.delete();
			}

			if (lastWriteFailed)
			{
				log.debug("File write restored to: {}", outputFile.getAbsolutePath());
				lastWriteFailed = false;
			}
		}
		catch (IOException e)
		{
			if (!lastWriteFailed)
			{
				log.warn("Failed to write state to file {}: {}", outputFile.getAbsolutePath(), e.getMessage());
				lastWriteFailed = true;
			}
		}
	}

	@Provides
	StreamDeckConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(StreamDeckConfig.class);
	}
}