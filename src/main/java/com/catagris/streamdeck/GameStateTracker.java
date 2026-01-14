package com.catagris.streamdeck;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class GameStateTracker
{
	// Spec orb sprite IDs
	private static final int SPEC_DISABLED = 1064;
	private static final int SPEC_SELECTED = 1608;

	@Inject
	private Client client;

	@Inject
	private Gson gson;

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
		hp.addProperty("status", getHpStatus());
		stats.add("hp", hp);

		JsonObject prayer = new JsonObject();
		prayer.addProperty("current", client.getBoostedSkillLevel(Skill.PRAYER));
		prayer.addProperty("max", client.getRealSkillLevel(Skill.PRAYER));
		stats.add("prayer", prayer);

		stats.addProperty("runEnergy", client.getEnergy());
		stats.addProperty("runEnabled", client.getVarpValue(173) == 1);
		stats.addProperty("specialAttack", client.getVarpValue(300) / 10);

		// Get spec orb state from widget sprite, fall back to VarPlayer if widget unavailable
		Widget specOrb = client.getWidget(ComponentID.MINIMAP_SPEC_ORB);
		if (specOrb != null)
		{
			int specSpriteId = specOrb.getSpriteId();
			stats.addProperty("specialAttackAvailable", specSpriteId != SPEC_DISABLED);
			stats.addProperty("specialAttackEnabled", specSpriteId == SPEC_SELECTED);
		}
		else
		{
			// Fallback: assume spec not available, use VarPlayer for enabled state
			stats.addProperty("specialAttackAvailable", false);
			stats.addProperty("specialAttackEnabled", client.getVarpValue(301) == 1);
		}

		root.add("stats", stats);

		// Active prayers (as array of names)
		JsonArray activePrayers = new JsonArray();
		for (Prayer prayerEnum : Prayer.values())
		{
			if (client.isPrayerActive(prayerEnum))
			{
				activePrayers.add(prayerEnum.name().toLowerCase());
			}
		}
		root.add("activePrayers", activePrayers);

		// Active tab
		root.addProperty("activeTab", getActiveTab());

		return gson.toJson(root);
	}

	private String getHpStatus()
	{
		int poisonVal = client.getVarpValue(102);
		int diseaseVal = client.getVarpValue(456);

		boolean isVenomed = poisonVal >= 1000000;
		boolean isPoisoned = poisonVal >= 1 && poisonVal <= 100;
		boolean isDiseased = diseaseVal > 0;

		if (isVenomed && isDiseased)
		{
			return "venomed_diseased";
		}
		else if (isPoisoned && isDiseased)
		{
			return "poisoned_diseased";
		}
		else if (isVenomed)
		{
			return "venomed";
		}
		else if (isPoisoned)
		{
			return "poisoned";
		}
		else if (isDiseased)
		{
			return "diseased";
		}
		return "normal";
	}

	private String getActiveTab()
	{
		int topLevelPanel = client.getVarcIntValue(171);

		switch (topLevelPanel)
		{
			case -1:
				return "none";
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
				return "grouping";
			case 8:
				return "account";
			case 9:
				return "friends";
			case 11:
				return "settings";
			case 12:
				return "emotes";
			case 13:
				return "music";
			default:
				return "unknown_" + topLevelPanel;
		}
	}
}