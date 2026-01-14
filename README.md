# RuneLite Stream Deck Plugin

![Stream Deck RuneLite](streamdeck-runelite.png)

A RuneLite plugin that writes OSRS game state to a local file for Stream Deck integration.

## Features

- Writes game state as JSON to a local file every game tick (~600ms)
- Tracks active prayers
- Monitors player stats (HP, Prayer Points, Run Energy, Special Attack)
- HP status tracking (normal, poisoned, venomed, diseased, and combinations)
- Special attack availability detection (knows which weapons have specs)
- Run toggle state
- Detects active interface tab
- Configurable output file path

## Installation

Install from the RuneLite Plugin Hub by searching for "Stream Deck Integration".

## Configuration

In RuneLite settings, navigate to the Stream Deck Integration plugin:

- **Enable File Output**: Toggle writing game state on/off (default: ON)
- **Output File Path**: Path to write JSON file (default: `.runelite/streamdeck-state.json`)

## How It Works

The plugin writes game state to a local JSON file every game tick (~600ms). Your Stream Deck application reads this file to display game information.

Default file location:
- **Windows**: `%USERPROFILE%\.runelite\streamdeck-state.json`
- **macOS/Linux**: `~/.runelite/streamdeck-state.json`

## JSON Format

The plugin writes JSON data in the following format:

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

## Stream Deck Integration

Your Stream Deck application needs to:
1. Watch/read the JSON file at the configured path
2. Parse the JSON to extract game state
3. Update Stream Deck buttons based on the data

Example file watcher (Node.js):
```javascript
const fs = require('fs');
const path = require('path');

const filePath = path.join(process.env.USERPROFILE || process.env.HOME, '.runelite', 'streamdeck-state.json');

fs.watch(filePath, (eventType) => {
  if (eventType === 'change') {
    const data = JSON.parse(fs.readFileSync(filePath, 'utf8'));
    console.log('HP:', data.stats.hp.current, '/', data.stats.hp.max);
  }
});
```

## License

BSD 2-Clause License - see [LICENSE](LICENSE) file.

## Support

For issues or feature requests, please open an issue on GitHub:
https://github.com/catagris/runelite-streamdeck/issues