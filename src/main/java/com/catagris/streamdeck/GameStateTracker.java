package com.catagris.streamdeck;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

@Slf4j
@Singleton
public class GameStateTracker
{
	// Weapons with special attacks - from special_attack_weapons.csv
	private static final Set<Integer> SPECIAL_ATTACK_WEAPONS = Set.of(
		6739,   // Dragon axe
		13241,  // Infernal axe
		20011,  // 3rd age axe
		23673,  // Crystal axe
		28217,  // Dragon felling axe
		28226,  // 3rd age felling axe
		28220,  // Crystal felling axe
		21028,  // Dragon harpoon
		21031,  // Infernal harpoon
		23762,  // Crystal harpoon
		11920,  // Dragon pickaxe
		13243,  // Infernal pickaxe
		20014,  // 3rd age pickaxe
		23680,  // Crystal pickaxe
		26233,  // Ancient godsword
		24425,  // Eldritch nightmare staff
		27291,  // Keris partisan of the sun
		29594,  // Purging staff
		12926,  // Toxic blowpipe
		11806,  // Saradomin godsword
		11037,  // Brine sabre
		1377,   // Dragon battleaxe
		35,     // Excalibur
		21015,  // Dinh's bulwark
		21902,  // Dragon crossbow
		3204,   // Dragon halberd
		23987,  // Crystal halberd
		7158,   // Dragon 2h sword
		805,    // Rune thrownaxe
		22610,  // Vesta's spear
		4151,   // Abyssal whip
		27665,  // Accursed sceptre
		11061,  // Ancient mace
		11804,  // Bandos godsword
		10887,  // Barrelchest anchor
		8872,   // Bone dagger
		8874,   // Bone dagger (p)
		8876,   // Bone dagger (p+)
		8878,   // Bone dagger (p++)
		6746,   // Darklight
		19675,  // Arclight
		29589,  // Emberlight
		8880,   // Dorgeshuun crossbow
		4587,   // Dragon scimitar
		13576,  // Dragon warhammer
		22622,  // Statius's warhammer
		21003,  // Elder maul
		31113,  // Eye of ayak
		22634,  // Morrigan's throwing axe
		6724,   // Seercull
		11791,  // Staff of the dead
		12904,  // Toxic staff of the dead
		22296,  // Staff of light
		24144,  // Staff of balance
		28922,  // Tonalztics of ralos
		13263,  // Abyssal bludgeon
		11785,  // Armadyl crossbow
		30955,  // Arkan blade
		11802,  // Armadyl godsword
		28988,  // Blue moon spear
		22516,  // Dawnbringer
		22731,  // Dragon hasta
		22734,  // Dragon hasta (p)
		22737,  // Dragon hasta (p+)
		22740,  // Dragon hasta (p++)
		22743,  // Dragon hasta (kp)
		1305,   // Dragon longsword
		1434,   // Dragon mace
		21009,  // Dragon sword
		20849,  // Dragon thrownaxe
		28997,  // Dual macuahuitl
		29000,  // Eclipse atlatl
		21742,  // Granite hammer
		27287,  // Keris partisan of corruption
		19478,  // Light ballista
		19481,  // Heavy ballista
		859,    // Magic longbow
		10284,  // Magic comp bow
		22636,  // Morrigan's javelin
		29796,  // Noxious halberd
		26219,  // Osmumten's fang
		3101,   // Rune claws
		12808,  // Saradomin's blessed sword
		30759,  // Soulflame horn
		22613,  // Vesta's longsword
		24617,  // Vesta's blighted longsword
		27690,  // Voidwaker
		24424,  // Volatile nightmare staff
		26374,  // Zaryte crossbow
		13265,  // Abyssal dagger
		13267,  // Abyssal dagger (p)
		13269,  // Abyssal dagger (p+)
		13271,  // Abyssal dagger (p++)
		29577,  // Burning claws
		11235,  // Dark bow
		13652,  // Dragon claws
		1215,   // Dragon dagger
		1231,   // Dragon dagger (p)
		5680,   // Dragon dagger (p+)
		5698,   // Dragon dagger (p++)
		22804,  // Dragon knife
		22806,  // Dragon knife (p)
		22808,  // Dragon knife (p+)
		22810,  // Dragon knife (p++)
		4153,   // Granite maul
		24225,  // Granite maul (ornate handle)
		31583,  // Rosewood blowpipe
		861,    // Magic shortbow
		12788,  // Magic shortbow (i)
		11838,  // Saradomin sword
		27900,  // Vesta's spear (bh)
		27655,  // Webweaver bow
		12006,  // Abyssal tentacle
		1249,   // Dragon spear
		1263,   // Dragon spear (p)
		5716,   // Dragon spear (p+)
		5730,   // Dragon spear (p++)
		3176,   // Dragon spear (kp)
		11889,  // Zamorakian hasta
		11824,  // Zamorakian spear
		7639,   // Rod of ivandis
		22398,  // Ivandis flail
		24699,  // Blisterwood flail
		29591,  // Scorching bow
		27660,  // Ursine chainmace
		11808,  // Zamorak godsword
		21633,  // Ancient wyvern shield
		11283,  // Dragonfire shield
		22002,  // Dragonfire ward
		28338,  // Soulreaper axe
		25110,  // Echo axe
		25114,  // Echo harpoon
		25112,  // Echo pickaxe
		30340,  // Crystal dagger (perfected)
		30367,  // The dogsword
		30369,  // Sunlight spear
		30388   // Thunder khopesh
	);

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
		hp.addProperty("status", getHpStatus());
		stats.add("hp", hp);

		JsonObject prayer = new JsonObject();
		prayer.addProperty("current", client.getBoostedSkillLevel(Skill.PRAYER));
		prayer.addProperty("max", client.getRealSkillLevel(Skill.PRAYER));
		stats.add("prayer", prayer);

		stats.addProperty("runEnergy", client.getEnergy());
		stats.addProperty("runEnabled", client.getVarpValue(173) == 1); // VarPlayer 173 (OPTION_RUN)
		stats.addProperty("specialAttack", client.getVarpValue(300) / 10); // VarPlayer 300 (SPECIAL_ATTACK_PERCENT)
		stats.addProperty("specialAttackEnabled", client.getVarpValue(301) == 1); // VarPlayer 301 (SA_ATTACK / SPECIAL_ATTACK_ENABLED)
		int weaponId = getEquippedWeaponId();
		stats.addProperty("weaponItemId", weaponId);
		stats.addProperty("specialAttackAvailable", SPECIAL_ATTACK_WEAPONS.contains(weaponId));

		root.add("stats", stats);

		// Prayers
		JsonObject prayers = new JsonObject();
		prayers.addProperty("quickPrayerActive", client.getVarbitValue(Varbits.QUICK_PRAYER) == 1);
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

	private String getHpStatus()
	{
		// VarPlayer 102 (POISON):
		// [1, 100] = poisoned, damage = ceil(val / 5.0f)
		// [1000000, inf) = venomed, damage = min(20, (val - 999997) * 2)
		int poisonVal = client.getVarpValue(102);

		// VarPlayer 456 (DISEASE_VALUE):
		// > 0 = diseased, starts at 50, decreases by 1 every 30 seconds
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

	private int getEquippedWeaponId()
	{
		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment == null)
		{
			return -1;
		}
		Item[] items = equipment.getItems();
		int weaponSlot = EquipmentInventorySlot.WEAPON.getSlotIdx();
		if (weaponSlot >= items.length)
		{
			return -1;
		}
		return items[weaponSlot].getId();
	}

	private String getActiveTab()
	{
		// VarClientInt for TOPLEVEL_PANEL - tracks which game tab is active
		// This constant may not be in the public VarClientInt enum yet
		int topLevelPanel = client.getVarcIntValue(171); // TOPLEVEL_PANEL

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
