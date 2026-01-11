package com.catagris.streamdeck;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Singleton
public class GameStateTracker
{
	@Inject
	private Client client;

	private final Gson gson = new Gson();

	public String getStateJson()
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return "{\"error\": \"Not logged in\"}";
		}

		JsonObject root = new JsonObject();

		// Player info
		JsonObject player = new JsonObject();
		Player localPlayer = client.getLocalPlayer();
		if (localPlayer != null)
		{
			player.addProperty("name", localPlayer.getName());
		}
		player.addProperty("world", client.getWorld());
		root.add("player", player);

		// Stats
		JsonObject stats = new JsonObject();

		JsonObject hp = new JsonObject();
		hp.addProperty("current", client.getBoostedSkillLevel(Skill.HITPOINTS));
		hp.addProperty("max", client.getRealSkillLevel(Skill.HITPOINTS));
		stats.add("hp", hp);

		JsonObject prayer = new JsonObject();
		prayer.addProperty("current", client.getBoostedSkillLevel(Skill.PRAYER));
		prayer.addProperty("max", client.getRealSkillLevel(Skill.PRAYER));
		stats.add("prayer", prayer);

		stats.addProperty("runEnergy", client.getEnergy());
		stats.addProperty("specialAttack", client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) / 10);

		root.add("stats", stats);

		// Prayers
		JsonObject prayers = new JsonObject();
		for (Prayer prayerEnum : Prayer.values())
		{
			boolean active = client.isPrayerActive(prayerEnum);
			prayers.addProperty(prayerEnum.name().toLowerCase(), active);
		}
		root.add("prayers", prayers);

		// Active tab
		String activeTab = getActiveTab();
		root.addProperty("activeTab", activeTab);

		// Timestamp
		root.addProperty("timestamp", System.currentTimeMillis());

		return gson.toJson(root);
	}

	private String getActiveTab()
	{
		// Get the current tab widget
		int currentTab = client.getVarpValue(VarPlayer.INVENTORY_TAB);

		switch (currentTab)
		{
			case 0:
				return "combat";
			case 1:
				return "skills";
			case 2:
				return "quests";
			case 3:
				return "inventory";
			case 4:
				return "equipment";
			case 5:
				return "prayer";
			case 6:
				return "magic";
			case 7:
				return "clan";
			case 8:
				return "account";
			case 9:
				return "friends";
			case 10:
				return "logout";
			case 11:
				return "settings";
			case 12:
				return "emotes";
			case 13:
				return "music";
			default:
				return "unknown";
		}
	}
}
