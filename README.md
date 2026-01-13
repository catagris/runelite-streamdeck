# RuneLite Stream Deck Plugin

![Stream Deck RuneLite](streamdeck-runelite.png)

A RuneLite plugin that exposes OSRS game state via a local HTTP API for Stream Deck integration.

## Features

- Exposes game state as JSON via HTTP endpoint
- Tracks prayer states (all prayers + quick prayer)
- Monitors player stats (HP, Prayer Points, Run Energy, Special Attack)
- HP status tracking (normal, poisoned, venomed, diseased, and combinations)
- Special attack availability detection (knows which weapons have specs)
- Run toggle state
- Detects active interface tab
- Configurable server port
- CORS-enabled for browser-based clients

## Installation

Install from the RuneLite Plugin Hub by searching for "Stream Deck Integration".

## Configuration

In RuneLite settings, navigate to the Stream Deck Integration plugin:

- **Enable HTTP Server**: Toggle the HTTP server on/off (default: ON)
- **Server Port**: Set the port for the HTTP server (default: 8085)

## API Endpoint

### GET /state

Returns current game state as JSON.

**URL:** `http://localhost:8085/state`

**Response Example:**
```json
{
  "player": {
    "name": "PlayerName",
    "world": 420
  },
  "stats": {
    "hp": {
      "current": 85,
      "max": 99,
      "status": "normal"
    },
    "prayer": {
      "current": 60,
      "max": 70
    },
    "runEnergy": 10000,
    "runEnabled": true,
    "specialAttack": 100,
    "specialAttackEnabled": false,
    "weaponItemId": 11802,
    "specialAttackAvailable": true
  },
  "prayers": {
    "quickPrayerActive": false,
    "thick_skin": false,
    "burst_of_strength": false,
    "protect_from_melee": true,
    "piety": true
  },
  "activeTab": "inventory",
  "timestamp": 1699999999999
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
| `weaponItemId` | int | Item ID of equipped weapon (-1 if none) |
| `specialAttackAvailable` | boolean | Whether equipped weapon has a special attack |

### Active Tab Values

`none`, `combat`, `skills`, `quests`, `inventory`, `equipment`, `prayer`, `magic`, `grouping`, `account`, `friends`, `settings`, `emotes`, `music`

**Error Response (Not Logged In):**
```json
{
  "error": "Not logged in"
}
```

## Testing

Test the endpoint using curl:

```bash
curl http://localhost:8085/state
```

Or open in your browser:
```
http://localhost:8085/state
```

## Security Notes

- The HTTP server only binds to `localhost` (127.0.0.1)
- No external network access is possible
- No authentication is required (local-only access)
- Does not expose any account credentials or sensitive data

## License

BSD 2-Clause License - see [LICENSE](LICENSE) file.

## Support

For issues or feature requests, please open an issue on GitHub:
https://github.com/catagris/runelite-streamdeck/issues