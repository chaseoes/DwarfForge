package com.chaseoes.dwarfforge;
/**
 *
 * PluginName: DwarfForge
 * Class: Config
 * User: DonRedhorse
 * Date: 07.12.11
 * Time: 21:36
 *
 */

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;


/**
 * The Config Class allows you to write a custom config file for craftbukkit plugins incl. comments.
 * It allows autoupdating config changes, checking for plugin updates and writing back the configuration.
 * Please note that writing to the config file will overwrite any manual changes.<p>
 * You NEED to fix all ToDos, otherwise the class will NOT work!<p>
 *
 * @author Don Redhorse
 */
public class Config {

	/**
	 * Instance of the Configuration Class
	 */
	private static Config instance = null;

// Nothing to change from here to ==>>>
	/**
	 * Object to handle the configuration
	 *
	 * @see org.bukkit.configuration.file.FileConfiguration
	 */
	private FileConfiguration config;
	/**
	 * Object to handle the plugin
	 */
	private Plugin plugin;
	/**
	 * Configuration File Name
	 */
	private static String configFile = "config.yml";
	/**
	 * Is the configuration available or did we have problems?
	 */
	private boolean configAvailable = false;
// Default plugin configuration
	/**
	 * Enables logging to server console. Warning and Severe will still be logged.
	 */
	private boolean errorLogEnabled = true;
	/**
	 * Enable more logging.. could be messy!
	 */
	private boolean debugLogEnabled = false;
	/**
	 * Check if there is a new version of the plugin out.
	 */
	private boolean checkForUpdate = true;
	/**
	 * AutoUpdate the config file if necessary. This will overwrite any changes outside the configuration parameters!
	 */
	private boolean autoUpdateConfig = false;
	/**
	 * Enable saving of the config file
	 */
	private boolean saveConfig = false;
	/**
	 * Contains the plugin name
	 */
	private String pluginName;
	/**
	 * Contains the plugin version
	 */
	private String pluginVersion;
	/**
	 * Do we require a config update?
	 */
	private boolean configRequiresUpdate = false;

// <<<<=== here..


// Stuff with minor changes


	/**
	 * Link to the location of the plugin website
	 */
	private final String pluginSlug = "http://dev.bukkit.org/server-mods/dwarfforge";
	/**
	 * Link to the location of the recent version number, the file should be a text with just the number
	 */
	private final String versionURL = "https://raw.github.com/DragonRealms/DwarfForge/master/etc/dwarfforge.ver";
	// The org.simiancage.bukkit.DwarfForge.LoggerClass should be renamed to the name of the class you did change the original org.simiancage.bukkit.DwarfForge.LoggerClass too.
	/**
	 * Reference of the org.simiancage.bukkit.DwarfForge.LoggerClass, needs to be renamed to correct name.
	 *
	 * @see Log
	 */

	// ToDo Change the configCurrent if the config changes!
	/**
	 * This is the internal config version
	 */
	private final String configCurrent = "2.5";
	/**
	 * This is the DEFAULT for the config file version, should be the same as configCurrent. Will afterwards be changed
	 */
	private String configVer = configCurrent;


// and now the real stuff


// ********************************************************************************************************************

	private final static String KEY_COOK_TIME = "cooking-time";
	private final static double MAX_COOK_TIME = 9.25;
	private static double cookTime;

	private final static String KEY_REQUIRE_FUEL = "require-fuel";
	private static boolean requireFuel;

	private final static String KEY_ALLOW_CRAFTED_FUEL = "allow-crafted-items";
	private static boolean allowCraftedFuel;

	private final static String KEY_MAX_STACK_HORIZONTAL = "stack-limit-horizontal";
	private static int maxStackHorizontal;

	private final static String KEY_MAX_STACK_VERTICAL = "stack-limit-vertical";
	private static int maxStackVertical;

	private final static String KEY_ALLOW_LAVA_EXPLOIT = "allow-lava-exploit";
	private static boolean allowLavaExploit;

// *******************************************************************************************************************


/*  Here comes the custom config, the default config is later on in the class
Keep in mind that you need to create your config file in a way which is
afterwards parsable again from the configuration class of bukkit
*/

// First we have the default part..
// Which is devided in setting up some variables first

	/**
	 * Method to setup the config variables with default values
	 */

	private void setupCustomDefaultVariables() {
		cookTime = MAX_COOK_TIME;
		requireFuel = false;
		allowCraftedFuel = false;
		maxStackHorizontal = 1;
		maxStackVertical = 3;
		allowLavaExploit = false;

	}

// And than we add the defaults

	/**
	 * Method to add the config variables to the default configuration
	 */

	private void customDefaultConfig() {

		config.addDefault(KEY_COOK_TIME, cookTime);
		config.addDefault(KEY_REQUIRE_FUEL, requireFuel);
		config.addDefault(KEY_ALLOW_CRAFTED_FUEL, allowCraftedFuel);
		config.addDefault(KEY_MAX_STACK_HORIZONTAL, maxStackHorizontal);
		config.addDefault(KEY_MAX_STACK_VERTICAL, maxStackVertical);
		config.addDefault(KEY_ALLOW_LAVA_EXPLOIT, allowLavaExploit);
	}


// Than we load it....

	/**
	 * Method to load the configuration into the config variables
	 */

	private void loadCustomConfig() {

		cookTime = config.getDouble(KEY_COOK_TIME);
		requireFuel = config.getBoolean(KEY_REQUIRE_FUEL);
		allowCraftedFuel = config.getBoolean(KEY_ALLOW_CRAFTED_FUEL);
		maxStackHorizontal = config.getInt(KEY_MAX_STACK_HORIZONTAL);
		maxStackVertical = config.getInt(KEY_MAX_STACK_VERTICAL);
		allowLavaExploit = config.getBoolean(KEY_ALLOW_LAVA_EXPLOIT);

		// Some limits...
		if (maxStackVertical < 0) {
			maxStackVertical = 0;
		}
		if (maxStackHorizontal < 0) {
			maxStackHorizontal = 0;
		}

		if (cookTime < 0) {
			cookTime = 0;
		}
		if (cookTime > MAX_COOK_TIME) {
			cookTime = MAX_COOK_TIME;
		}


	}

// And than we write it....


	/**
	 * Method to write the custom config variables into the config file
	 *
	 * @param stream will be handed over by  writeConfig
	 */

	private void writeCustomConfig(PrintWriter stream) {
//Start here writing your config variables into the config file inkl. all comments


		stream.println("#-------- Plugin Configuration");
		stream.println();
		stream.println("# The time to cook/smelt an item in seconds. 9.25 secs is the Minecraft default.");
		stream.println(KEY_COOK_TIME + ": " + cookTime);
		stream.println();
		stream.println("# Set to true to require fuel (coal, wood, etc.) in the input chest.");
		stream.println("# The forge will continue to automate un/loading of goods and fuel.");
		stream.println(KEY_REQUIRE_FUEL + ": " + requireFuel);
		stream.println();
		stream.println("# When set to false, only the following fuels are burned: coal/charcoal, wood, saplings,");
		stream.println("# sticks, logs, lava buckets. When true, these additional items are counted as fuel:");
		stream.println("# fence, wood stairs, trap doors, chests, note blocks, jukeboxes and bookshelves.");
		stream.println(KEY_ALLOW_CRAFTED_FUEL + ": " + allowCraftedFuel);
		stream.println();
		stream.println("# How far to the left or right a forge may be to access an input/output chest (through other forges).");
		stream.println("# Horizontally \"stacked\" forges still require lava underneath to function.");
		stream.println(KEY_MAX_STACK_HORIZONTAL + ": " + maxStackHorizontal);
		stream.println();
		stream.println("# How far above the lava (through other forges) a furnace may be and still be considered a forge.");
		stream.println(KEY_MAX_STACK_VERTICAL + ": " + maxStackVertical);
		stream.println();
		stream.println("# Allow for flowing lava to be used under forge. This will fix weird behaviour of forges getting");
		stream.println("# stuck, not toggling correctly and some other stuff.");
		stream.println();
		stream.println("# Note: This can be exploited by users not having the right to build forges.");
		stream.println(KEY_ALLOW_LAVA_EXPLOIT + ": " + allowLavaExploit);

	}


// *******************************************************************************************************

// And now you need to create the getters and setters if needed for your config variables


// The plugin specific getters start here!


	public static boolean isAllowLavaExploit() {
		return allowLavaExploit;
	}

	public static double getCookTime() {
		return cookTime;
	}

	public static boolean isRequireFuel() {
		return requireFuel;
	}

	public static boolean isAllowCraftedFuel() {
		return allowCraftedFuel;
	}

	public static int getMaxStackHorizontal() {
		return maxStackHorizontal;
	}

	public static int getMaxStackVertical() {
		return maxStackVertical;
	}

	public static short cookTime() {
		// Furnace.setCookTime sets time elapsed, NOT time remaining.
		// The config file specifies time remaining, so adjust here.
		return (short) (Utils.SECS * (MAX_COOK_TIME - cookTime));
	}


// Last change coming up... choosing the right ClassName for the Logger..

	/**
	 * Method to get the Instance of the Class, if the class hasn't been initialized yet it will.
	 *
	 * @return instance of class
	 */

	public static Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}

		return instance;
	}

	/**
	 * Method to get the Instance of the Class and pass over a different name for the config file, if the class
	 * hasn't been initialized yet it will.
	 *
	 * @param configuratonFile name of the config file
	 *
	 * @return instance of class
	 */
	public static Config getInstance(String configuratonFile) {
		if (instance == null) {
			instance = new Config();
		}

		configFile = configuratonFile;
		return instance;
	}


// Well that's it.... at least in this class... thanks for reading...


// NOTHING TO CHANGE NORMALLY BELOW!!!


// *******************************************************************************************************************
// Other Methods no change normally necessary


// The class stuff first


	private Config() {

	}


// than the getters

	/**
	 * Method to return the PluginName
	 *
	 * @return PluginName
	 */

	public String pluginName() {
		return pluginName;
	}

	/**
	 * Method to return the PluginVersion
	 *
	 * @return PluginVersion
	 */
	public String pluginVersion() {
		return pluginVersion;
	}

	/**
	 * Method to return the Config File Version
	 *
	 * @return configVer  Config File Version
	 */
	public String configVer() {
		return configVer;
	}

	/**
	 * Method to return if Error Logging is enabled
	 *
	 * @return errorLogEnabled
	 */

	public boolean isErrorLogEnabled() {
		return errorLogEnabled;
	}

	/**
	 * Method to return if Debug Loggin is enabled
	 *
	 * @return debugLogEnabled
	 */
	public boolean isDebugLogEnabled() {
		return debugLogEnabled;
	}

	/**
	 * Method to return if we are checking for updates
	 *
	 * @return checkForUpdate
	 */
	public boolean isCheckForUpdate() {
		return checkForUpdate;
	}

	/**
	 * Method to return if we are AutoUpdating the Config File
	 *
	 * @return autoUpdateConfig
	 */
	public boolean isAutoUpdateConfig() {
		return autoUpdateConfig;
	}

	/**
	 * Method to return if we are saving the config automatically
	 *
	 * @return saveConfig
	 */
	public boolean isSaveConfig() {
		return saveConfig;
	}

	/**
	 * Method to return if we need to update the config
	 *
	 * @return configRequiresUpdate
	 */
	public boolean isConfigRequiresUpdate() {
		return configRequiresUpdate;
	}

// And the rest

// Setting up the config

	/**
	 * Method to setup the configuration.
	 * If the configuration file doesn't exist it will be created by {@link #defaultConfig()}
	 * After that the configuration is loaded {@link #loadConfig()}
	 * We than check if an configuration update is necessary {@link #updateNecessary()}
	 * and if {@link #autoUpdateConfig} is true we update the configuration {@link #updateConfig()}
	 * If {@link #checkForUpdate} is true we check if there is a new version of the plugin {@link #versionCheck()}
	 * and set {@link #configAvailable} to true
	 *
	 * @param config references the config file
	 * @param plugin references the plugin for this configuration
	 *
	 * @see #defaultConfig()
	 * @see #loadConfig()
	 * @see #updateNecessary()
	 * @see #updateConfig()
	 * @see #versionCheck()
	 */

	public void setupConfig(FileConfiguration config, Plugin plugin) {

		this.config = config;
		this.plugin = plugin;
// Checking if config file exists, if not create it
		if (!(new File(plugin.getDataFolder(), configFile)).exists()) {
			defaultConfig();
		}
// Loading the config from file
		loadConfig();

// Checking internal configCurrent and config file configVer

		updateNecessary();
// If config file has new options update it if enabled
		if (autoUpdateConfig) {
			updateConfig();
		}
// Also check for New Version of the plugin
		if (checkForUpdate) {
			versionCheck();
		}
		configAvailable = true;
	}


// Creating the defaults

// Configuring the Default options..

	/**
	 * Method to write and create the default configuration.
	 * The custom configuration variables are added via #setupCustomDefaultVariables()
	 * Than we write the configuration to disk  #writeConfig()
	 * Than we get the config object from disk
	 * We are adding the default configuration for the variables and load the
	 * defaults for the custom variables  #customDefaultConfig()
	 *
	 * @see #setupCustomDefaultVariables()
	 * @see #customDefaultConfig()
	 */

	private void defaultConfig() {
		setupCustomDefaultVariables();
		config = plugin.getConfig();
		config.addDefault("configVer", configVer);
		config.addDefault("errorLogEnabled", errorLogEnabled);
		config.addDefault("DebugLogEnabled", debugLogEnabled);
		config.addDefault("checkForUpdate", checkForUpdate);
		config.addDefault("autoUpdateConfig", autoUpdateConfig);
		config.addDefault("saveConfig", saveConfig);
		customDefaultConfig();
	}


// Loading the configuration

	/**
	 * Method for loading the configuration from disk.
	 * First we get the config object from disk, than we
	 * read in the standard configuration part.
	 * We also log a message if #debugLogEnabled
	 * and we produce some debug logs.
	 * After that we load the custom configuration part #loadCustomConfig()
	 *
	 * @see #loadCustomConfig()
	 */

	private void loadConfig() {
		config = plugin.getConfig();
		// Starting to update the standard configuration
		configVer = config.getString("configVer");
		errorLogEnabled = config.getBoolean("errorLogEnabled");
		debugLogEnabled = config.getBoolean("DebugLogEnabled");
		checkForUpdate = config.getBoolean("checkForUpdate");
		autoUpdateConfig = config.getBoolean("autoUpdateConfig");
		saveConfig = config.getBoolean("saveConfig");

		loadCustomConfig();
	}


//  Writing the config file

	/**
	 * Method for writing the configuration file.
	 * First we write the standard configuration part, than we
	 * write the custom configuration part via #writeCustomConfig()
	 *
	 * @return true if writing the config was successful
	 *
	 * @see #writeCustomConfig(java.io.PrintWriter)
	 */

	private boolean writeConfig() {
		boolean success = false;
		try {
			PrintWriter stream;
			File folder = plugin.getDataFolder();
			if (folder != null) {
				folder.mkdirs();
			}
			String pluginPath = plugin.getDataFolder() + System.getProperty("file.separator");
			PluginDescriptionFile pdfFile = this.plugin.getDescription();
			pluginName = pdfFile.getName();
			pluginVersion = pdfFile.getVersion();
			stream = new PrintWriter(pluginPath + configFile);
//Let's write our config ;)
			stream.println("# " + pluginName + " " + pdfFile.getVersion() + " by " + pdfFile.getAuthors().toString());
			stream.println("#");
			stream.println("# Configuration File for " + pluginName + ".");
			stream.println("#");
			stream.println("# For detailed assistance please visit: " + pluginSlug);
			stream.println();
			stream.println("#------- Default Configuration");
			stream.println();
			stream.println("# Configuration Version");
			stream.println("configVer: '" + configVer + "'");
			stream.println();
			stream.println("# Error Log Enabled");
			stream.println("# Enable logging to server console");
			stream.println("# Warning and Severe will still be logged.");
			stream.println("errorLogEnabled: " + errorLogEnabled);
			stream.println();
			stream.println("# Debug Log Enabled");
			stream.println("# Enable more logging.. could be messy!");
			stream.println("DebugLogEnabled: " + debugLogEnabled);
			stream.println();
			stream.println("# Check for Update");
			stream.println("# Will check if there is a new version of the plugin out.");
			stream.println("checkForUpdate: " + checkForUpdate);
			stream.println();
			stream.println("# Auto Update Config");
			stream.println("# This will overwrite any changes outside the configuration parameters!");
			stream.println("autoUpdateConfig: " + autoUpdateConfig);
			stream.println();
			stream.println("# Save Config");
			stream.println("# This will overwrite any changes outside the configuration parameters!");
			stream.println("# Only needed if you use ingame commands to change the configuration.");
			stream.println("saveConfig: " + saveConfig);
			stream.println();

// Getting the custom config information from the top of the class
			writeCustomConfig(stream);

			stream.println();

			stream.close();

			success = true;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return success;
	}


// Checking if the configVersions differ

	/**
	 * Method to check if the configuration version are different.
	 * will set #configRequiresUpdate to true if versions are different
	 */
	private void updateNecessary() {
		if (isNewerVersion(configCurrent, configVer)) {
			configRequiresUpdate = true;
		}
	}

// Checking the Current Version via the Web

	/**
	 * Method to check if there is a newer version of the plugin available.
	 */
	private void versionCheck() {
		String thisVersion = plugin.getDescription().getVersion();
		URL url;
		try {
			url = new URL(versionURL);
			BufferedReader in;
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			String newVersion = "";
			String line;
			while ((line = in.readLine()) != null) {
				newVersion += line;
			}
			in.close();
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}


// Updating the config

	/**
	 * Method to update the configuration if it is necessary.
	 */
	private void updateConfig() {
		if (configRequiresUpdate) {
			configVer = configCurrent;
		}
	}

// Reloading the config

	/**
	 * Method to reload the configuration.
	 *
	 * @return msg with the status of the reload
	 */

	public String reloadConfig() {
		String msg;
		if (configAvailable) {
			loadConfig();
			msg = "Config was reloaded";
		} else {
			msg = "Something terrible terrible did go really really wrong, see console log!";
		}
		return msg;
	}
// Saving the config


	/**
	 * Method to save the config to file.
	 *
	 * @return true if the save was successful
	 */
	public boolean saveConfig() {
		boolean saved = false;
		if (saveConfig) {
			saved = writeConfig();
		}
		return saved;
	}
	
// Version comparison functions
	/**
	 * Checks if the first input is a newer version number than the second input
	 * @param version1 the version to check
	 * @param version2 the version to check against
	 * @return true if the first input is a newer version number, false if it isn't
	 */
	private boolean isNewerVersion(String version1, String version2) {
        String s1 = normalisedVersion(version1);
        String s2 = normalisedVersion(version2);
        int cmp = s1.compareTo(s2);
        //String cmpStr = cmp < 0 ? "<" : cmp > 0 ? ">" : "==";
        return cmp>0;
    }
	
	private String normalisedVersion(String version) {
        return normalisedVersion(version, ".", 4);
    }

	private String normalisedVersion(String version, String sep, int maxWidth) {
        String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(String.format("%" + maxWidth + 's', s));
        }
        return sb.toString();
	}

}