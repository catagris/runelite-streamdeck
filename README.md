# RuneLite Stream Deck Plugin

A RuneLite plugin that exposes OSRS game state via a local HTTP API for Stream Deck integration.

## Features

- Exposes game state as JSON via HTTP endpoint
- Tracks prayer states (all prayers on/off)
- Monitors player stats (HP, Prayer Points, Run Energy, Special Attack)
- Detects active interface tab
- Configurable server port
- CORS-enabled for browser-based clients

## Installation

### Development Mode

1. Clone the RuneLite repository:
   ```bash
   git clone https://github.com/runelite/runelite.git
   ```

2. Clone this plugin into the RuneLite plugins directory:
   ```bash
   cd runelite/runelite-client/src/main/java/
   git clone https://github.com/catagris/runelite-streamdeck-plugin.git com/catagris/streamdeck
   ```

3. Build and run RuneLite:
   ```bash
   cd runelite
   ./gradlew run
   ```

### Using External Plugin Loader

1. Build the plugin JAR:
   ```bash
   ./gradlew build
   ```

2. Copy the JAR from `build/libs/` to your RuneLite plugins folder:
   - Windows: `%USERPROFILE%\.runelite\plugins`
   - Mac: `~/.runelite/plugins`
   - Linux: `~/.runelite/plugins`

3. Enable "External Plugin Manager" in RuneLite settings
4. Enable the Stream Deck Integration plugin

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
    "hp": { "current": 85, "max": 99 },
    "prayer": { "current": 60, "max": 70 },
    "runEnergy": 100,
    "specialAttack": 100
  },
  "prayers": {
    "thick_skin": false,
    "burst_of_strength": false,
    "protect_from_melee": true,
    "piety": true,
    ...
  },
  "activeTab": "inventory",
  "timestamp": 1699999999999
}
```

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

## Troubleshooting

### Server won't start
- Check if port 8085 is already in use
- Try changing the port in plugin settings
- Check RuneLite logs for error messages

### No data returned
- Ensure you're logged into the game
- Verify the plugin is enabled in RuneLite
- Check that "Enable HTTP Server" is turned on in settings

### CORS errors
- The server includes CORS headers, but some browsers may still block localhost
- Use the Stream Deck plugin instead of testing in browser

## Security Notes

- The HTTP server only binds to `localhost` (127.0.0.1)
- No external network access is possible
- No authentication is required (local-only access)
- Does not expose any account credentials or sensitive data

## License

This plugin is provided as-is for use with RuneLite and Stream Deck integration.

## Support

For issues or feature requests, please open an issue on GitHub:
https://github.com/catagris/runelite-streamdeck-plugin/issues
