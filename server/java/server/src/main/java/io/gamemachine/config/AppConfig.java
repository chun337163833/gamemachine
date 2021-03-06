package io.gamemachine.config;

import java.util.List;

import com.typesafe.config.Config;


public class AppConfig {
	
	private static String env;
	private static String defaultGameId;
	private static Config gameConfig;

	public static String getEnv() {
		return env;
	}

	public static void setEnv(String env) {
		AppConfig.env = env;
	}

	public static String getDefaultGameId() {
		return defaultGameId;
	}

	public static void setDefaultGameId(String defaultGameId) {
		AppConfig.defaultGameId = defaultGameId;
	}

	public static Config getGameConfig() {
		return gameConfig;
	}

	public static void setGameConfig(Config gameConfig) {
		AppConfig.gameConfig = gameConfig;
	}
	
	public static class Client {
		private static int idleTimeout;

		public static int getIdleTimeout() {
			return idleTimeout;
		}

		public static void setIdleTimeout(int idleTimeout) {
			Client.idleTimeout = idleTimeout;
		}

	}
	
	public static class GridConfig {
		private final String name;
		private final int gridSize;
		private final int cellSize;
		
		public GridConfig(String name, String config) {
			String[] parts = config.split(",");
			this.name = name;
			this.gridSize = Integer.parseInt(parts[0].trim());
			this.cellSize = Integer.parseInt(parts[1].trim());
		}

		public String getName() {
			return name;
		}

		public int getGridSize() {
			return gridSize;
		}

		public int getCellSize() {
			return cellSize;
		}
	}
	
	
	
	
    public static class Datastore {
    	private static String store;
    	private static String serialization;
    	private static long cacheWriteInterval;
    	private static long cacheWritesPerSecond;
    	private static String mapdbPath;
    	
		public static String getStore() {
			return store;
		}
		public static void setStore(String store) {
			Datastore.store = store;
		}
		public static String getSerialization() {
			return serialization;
		}
		public static void setSerialization(String serialization) {
			Datastore.serialization = serialization;
		}
		public static long getCacheWriteInterval() {
			return cacheWriteInterval;
		}
		public static void setCacheWriteInterval(long cacheWriteInterval) {
			Datastore.cacheWriteInterval = cacheWriteInterval;
		}
		public static long getCacheWritesPerSecond() {
			return cacheWritesPerSecond;
		}
		public static void setCacheWritesPerSecond(long cacheWritesPerSecond) {
			Datastore.cacheWritesPerSecond = cacheWritesPerSecond;
		}
		public static String getMapdbPath() {
			return mapdbPath;
		}
		public static void setMapdbPath(String mapdbPath) {
			Datastore.mapdbPath = mapdbPath;
		}
		
    }
    
	public static class Couchbase {
		private static String bucket;
		private static String password;
		private static List<String> servers;
		public static String getBucket() {
			return bucket;
		}
		public static void setBucket(String bucket) {
			Couchbase.bucket = bucket;
		}
		public static String getPassword() {
			return password;
		}
		public static void setPassword(String password) {
			Couchbase.password = password;
		}
		public static List<String> getServers() {
			return servers;
		}
		public static void setServers(List<String> servers) {
			Couchbase.servers = servers;
		}
	}
	
	
	public static class Jdbc {
		private static String hostname;
		private static int port;
		private static String database;
		private static String ds;
		private static String driver;
		private static String username;
		private static String password;
		
		
		public static String getHostname() {
			return hostname;
		}
		public static void setHostname(String hostname) {
			Jdbc.hostname = hostname;
		}
		public static int getPort() {
			return port;
		}
		public static void setPort(int port) {
			Jdbc.port = port;
		}
		public static String getDatabase() {
			return database;
		}
		public static void setDatabase(String database) {
			Jdbc.database = database;
		}
		public static String getDs() {
			return ds;
		}
		public static void setDs(String ds) {
			Jdbc.ds = ds;
		}
		public static String getDriver() {
			return driver;
		}
		public static void setDriver(String driver) {
			Jdbc.driver = driver;
		}
		public static String getUsername() {
			return username;
		}
		public static void setUsername(String username) {
			Jdbc.username = username;
		}
		public static String getPassword() {
			return password;
		}
		public static void setPassword(String password) {
			Jdbc.password = password;
		}
	}
	
	public static class Gamecloud {
		private static String host;
		private static String user;
		private static String apiKey;
		
		public static String getHost() {
			return host;
		}
		public static void setHost(String host) {
			Gamecloud.host = host;
		}
		public static String getUser() {
			return user;
		}
		public static void setUser(String user) {
			Gamecloud.user = user;
		}
		public static String getApiKey() {
			return apiKey;
		}
		public static void setApiKey(String apiKey) {
			Gamecloud.apiKey = apiKey;
		}
	}
	
	public static class Handlers {
		
		private static String auth;
		private static String team;
		
		
		public static String getAuth() {
			return auth;
		}
		public static void setAuth(String auth) {
			Handlers.auth = auth;
		}
		public static String getTeam() {
			return team;
		}
		public static void setTeam(String team) {
			Handlers.team = team;
		}
	}

	
}
