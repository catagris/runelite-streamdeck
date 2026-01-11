package com.catagris.streamdeck;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

@Slf4j
public class StateHttpServer
{
	private HttpServer server;
	private final GameStateTracker gameStateTracker;
	private final int port;

	public StateHttpServer(GameStateTracker gameStateTracker, int port)
	{
		this.gameStateTracker = gameStateTracker;
		this.port = port;
	}

	public void start() throws IOException
	{
		if (server != null)
		{
			log.warn("HTTP server already running");
			return;
		}

		server = HttpServer.create(new InetSocketAddress("localhost", port), 0);

		// Create /state endpoint
		server.createContext("/state", this::handleStateRequest);

		// Start server
		server.setExecutor(null); // Use default executor
		server.start();

		log.info("Stream Deck HTTP server started on port {}", port);
	}

	public void stop()
	{
		if (server != null)
		{
			server.stop(0);
			server = null;
			log.info("Stream Deck HTTP server stopped");
		}
	}

	private void handleStateRequest(HttpExchange exchange) throws IOException
	{
		// Add CORS headers to allow Stream Deck plugin to access
		exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
		exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
		exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

		// Handle OPTIONS request for CORS preflight
		if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS"))
		{
			exchange.sendResponseHeaders(204, -1);
			exchange.close();
			return;
		}

		// Handle GET request
		if (exchange.getRequestMethod().equalsIgnoreCase("GET"))
		{
			try
			{
				String jsonResponse = gameStateTracker.getStateJson();
				byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);

				exchange.getResponseHeaders().set("Content-Type", "application/json");
				exchange.sendResponseHeaders(200, responseBytes.length);

				try (OutputStream os = exchange.getResponseBody())
				{
					os.write(responseBytes);
				}
			}
			catch (Exception e)
			{
				log.error("Error handling state request", e);
				String errorResponse = "{\"error\": \"Internal server error\"}";
				byte[] errorBytes = errorResponse.getBytes(StandardCharsets.UTF_8);

				exchange.sendResponseHeaders(500, errorBytes.length);
				try (OutputStream os = exchange.getResponseBody())
				{
					os.write(errorBytes);
				}
			}
		}
		else
		{
			exchange.sendResponseHeaders(405, -1); // Method not allowed
		}

		exchange.close();
	}
}
