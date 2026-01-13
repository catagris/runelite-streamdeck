# RuneLite Stream Deck Plugin

![Stream Deck RuneLite](streamdeck-runelite.png)

A RuneLite plugin that sends OSRS game state to a local Stream Deck server for integration.

## Features

- Sends game state as JSON to your Stream Deck server
- Tracks active prayers
- Monitors player stats (HP, Prayer Points, Run Energy, Special Attack)
- HP status tracking (normal, poisoned, venomed, diseased, and combinations)
- Special attack availability detection (knows which weapons have specs)
- Run toggle state
- Detects active interface tab
- Configurable server URL

## Installation

Install from the RuneLite Plugin Hub by searching for "Stream Deck Integration".

## Configuration

In RuneLite settings, navigate to the Stream Deck Integration plugin:

- **Enable Client**: Toggle sending game state on/off (default: ON)
- **Server URL**: URL of your Stream Deck server (default: `http://localhost:8085/state`)

## How It Works

The plugin sends game state to your Stream Deck server every game tick (~600ms) via HTTP POST. Your Stream Deck application must host a server to receive this data.

## JSON Format

The plugin POSTs JSON data in the following format:

```json
{
  "player": {
    "name": "PlayerName",
    "world": 301
  },
  "stats": {
    "hp": {
      "current": 99,
      "max": 99,
      "status": "normal"
    },
    "prayer": {
      "current": 70,
      "max": 70
    },
    "runEnergy": 10000,
    "runEnabled": true,
    "specialAttack": 100,
    "specialAttackEnabled": false,
    "specialAttackAvailable": true
  },
  "activePrayers": ["protect_from_melee", "piety"],
  "activeTab": "inventory"
}
```

### Stats Fields

| Field | Type | Description |
|-------|------|-------------|
| `hp.current` | int | Current hitpoints |
| `hp.max` | int | Maximum hitpoints |
| `hp.status` | string | `normal`, `poisoned`, `venomed`, `diseased`, `poisoned_diseased`, or `venomed_diseased` |
| `prayer.current` | int | Current prayer points |
| `prayer.max` | int | Maximum prayer points |
| `runEnergy` | int | Current run energy (0-10000) |
| `runEnabled` | boolean | Whether run is toggled on |
| `specialAttack` | int | Special attack percentage (0-100) |
| `specialAttackEnabled` | boolean | Whether special attack is toggled on |
| `specialAttackAvailable` | boolean | Whether equipped weapon has a special attack |

### Active Tab Values

`none`, `combat`, `skills`, `quests`, `inventory`, `equipment`, `prayer`, `magic`, `grouping`, `account`, `friends`, `settings`, `emotes`, `music`

## Security Notes

- The plugin only makes outbound connections to localhost by default
- No external network access unless you configure a different URL
- Does not expose any account credentials or sensitive data

## License

BSD 2-Clause License - see [LICENSE](LICENSE) file.

## Support

For issues or feature requests, please open an issue on GitHub:
https://github.com/catagris/runelite-streamdeck/issues
