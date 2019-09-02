package jtabbedterminal;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;

import com.kodedu.terminalfx.Terminal;
import com.kodedu.terminalfx.TerminalBuilder;
import com.kodedu.terminalfx.TerminalTab;
import com.kodedu.terminalfx.config.TerminalConfig;
import com.pty4j.PtyProcess;

import java.util.logging.Logger;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.TabPane;

public class jTabbedTerminal extends Application {
	private String defaultTerminalCommand = "C:\\cygwin64\\bin\\bash -i -l";
	public String getDefaultTerminalCommand() {
		return defaultTerminalCommand;
	}

	public void setDefaultTerminalCommand(String defaultTerminalCommand) {
		this.defaultTerminalCommand = defaultTerminalCommand;
	}

	public MenuItem getNewTab() {
		return newTab;
	}

	public void setNewTab(MenuItem newTab) {
		this.newTab = newTab;
	}

	public MenuItem getCloseTab() {
		return closeTab;
	}

	public void setCloseTab(MenuItem closeTab) {
		this.closeTab = closeTab;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public Menu getFileMenu() {
		return fileMenu;
	}

	public void setFileMenu(Menu fileMenu) {
		this.fileMenu = fileMenu;
	}

	public MenuItem getExitApp() {
		return exitApp;
	}

	public void setExitApp(MenuItem exitApp) {
		this.exitApp = exitApp;
	}

	public Map<String, JTabbedTerminalPlugin_V1> getPluginMapV1() {
		return pluginMapV1;
	}

	public void setPluginMapV1(Map<String, JTabbedTerminalPlugin_V1> pluginMapV1) {
		this.pluginMapV1 = pluginMapV1;
	}

	private TerminalConfig defaultTerminalConfig = new TerminalConfig();
	private VBox rootBox = new VBox();
	private MenuBar menuBar = new MenuBar();
	private Menu tabMenu = new Menu("Tab");
	private MenuItem newTab = new MenuItem("New Default Terminal");
	private MenuItem closeTab = new MenuItem("Close Tab");
	private TerminalBuilder defaultTerminalBuilder = null;
	private TabPane tabPane = new TabPane();
	private Scene scene = null;
	private Logger logger = java.util.logging.Logger.getLogger(jTabbedTerminal.class.getName());
	private Menu fileMenu = new Menu("File");
	private MenuItem exitApp = new MenuItem("Exit");
	private Map<String, JTabbedTerminalPlugin_V1> pluginMapV1 = new HashMap<>();

	public TerminalConfig getDefaultTerminalConfig() {
		return defaultTerminalConfig;
	}

	public void setDefaultTerminalConfig(TerminalConfig defaultTerminalConfig) {
		this.defaultTerminalConfig = defaultTerminalConfig;
	}

	public VBox getRootBox() {
		return rootBox;
	}

	public void setRootBox(VBox rootBox) {
		this.rootBox = rootBox;
	}

	public MenuBar getMenuBar() {
		return menuBar;
	}

	public void setMenuBar(MenuBar menuBar) {
		this.menuBar = menuBar;
	}

	public Menu getTabMenu() {
		return tabMenu;
	}

	public void setTabMenu(Menu tabMenu) {
		this.tabMenu = tabMenu;
	}

	public TerminalBuilder getDefaultTerminalBuilder() {
		return defaultTerminalBuilder;
	}

	public void setDefaultTerminalBuilder(TerminalBuilder defaultTerminalBuilder) {
		this.defaultTerminalBuilder = defaultTerminalBuilder;
	}

	public TabPane getTabPane() {
		return tabPane;
	}

	public void setTabPane(TabPane tabPane) {
		this.tabPane = tabPane;
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		defaultTerminalConfig.setBackgroundColor(Color.rgb(16, 16, 16));
		defaultTerminalConfig.setForegroundColor(Color.rgb(240, 240, 240));
		defaultTerminalConfig.setCursorColor(Color.rgb(255, 0, 0, 0.5));
		defaultTerminalConfig.setWindowsTerminalStarter(defaultTerminalCommand);
		defaultTerminalConfig.setCopyOnSelect(true);
		defaultTerminalBuilder = new TerminalBuilder(defaultTerminalConfig);
		tabMenu.getItems().add(newTab);
		tabMenu.getItems().add(closeTab);
		fileMenu.getItems().add(exitApp);
		menuBar.getMenus().add(fileMenu);
		menuBar.getMenus().add(tabMenu);
		newTab.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				addTerminalTab();
			}
		});
		closeTab.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				TerminalTab t = (TerminalTab) tabPane.getSelectionModel().getSelectedItem();
				Terminal tm = t.getTerminal();
				PtyProcess proc = tm.getProcess();
				proc.destroy();
				t.closeTerminal(arg0);
				tabPane.getTabs().remove(t);
				
			}
		});
		exitApp.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				ObservableList<Tab> tabs = tabPane.getTabs();
				for (Tab tab : tabs) {
					if (tab instanceof TerminalTab) {
						((TerminalTab) tab).closeTerminal(arg0);
					}
				}
				System.exit(0);
			}
		});
		addTerminalTab();
		rootBox.getChildren().add(menuBar);
		VBox.setVgrow(tabPane, Priority.ALWAYS);
		rootBox.getChildren().add(tabPane);
		//gets a 132x43 character terminal, which is a standard size
		scene = new Scene(rootBox, 1115, 755);
		loadPlugins();
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void addTerminalTab() {
		TerminalTab terminal = defaultTerminalBuilder.newTerminal();
		tabPane.getTabs().add(terminal);
	}

	public void loadPlugins() {
		loadPlugins(System.getProperty("user.home") + File.separator + ".jTabbedTerminal" + File.separator + "plugins"
				+ File.separator);
	}

	public void loadPlugins(String pluginsDir) {
		File pd = new File(pluginsDir);
		if (pd.exists()) {
			if (pd.isDirectory()) {
				String[] jars = pd.list(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.toLowerCase().endsWith(".jar");
					}
				});
				for (String jar : jars) {
					loadAndInitializePlugin(jar);
				}
			} else {
				logger.warning("plugin directory name refers to file, not directory!");
			}
		} else {
			pd.mkdirs();
		}
	}

	private void loadAndInitializePlugin(String jar) {
		try(JarFile jf = new JarFile(jar);) {
			
			Enumeration<JarEntry> pluginEntries = jf.entries();
			URL[] urls = { new URL("jar:file:" + jar + "!/") };
			URLClassLoader jarloader = URLClassLoader.newInstance(urls);
			List<Class<JTabbedTerminalPlugin_V1>> pluginsToInit = new LinkedList<>();
			while (pluginEntries.hasMoreElements()) {
				JarEntry entry = pluginEntries.nextElement();
				if (entry.isDirectory() || !entry.getName().endsWith("class")) {
					continue;
				} else {
					// -6 because ".class".length = 6, so we strip '.class off the end of the
					// classname we want to load
					String className = entry.getName().substring(0, entry.getName().length() - 6);
					className = className.replace('/', '.');// replace separators with dots to construct full class name
					Class<?> c = jarloader.loadClass(className);
					if (c.isAssignableFrom(JTabbedTerminalPlugin_V1.class)) {
						pluginsToInit.add((Class<JTabbedTerminalPlugin_V1>) c);
					}
				}
			}
			for (Class<JTabbedTerminalPlugin_V1> plugin : pluginsToInit) {
				Constructor<JTabbedTerminalPlugin_V1> pluginConstructor = plugin.getConstructor(jTabbedTerminal.class);
				JTabbedTerminalPlugin_V1 plug = pluginConstructor.newInstance(this);
				plug.initalize();
				this.pluginMapV1.put(plug.getPluginName(), plug);

			}
		} catch (IOException | ClassNotFoundException | NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			logger.log(Level.WARNING, "ERROR LOADING JAR PLUGIN", e);
		}
	}
}
