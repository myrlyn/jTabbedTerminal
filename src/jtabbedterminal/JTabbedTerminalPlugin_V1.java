package jtabbedterminal;

public abstract class JTabbedTerminalPlugin_V1 {
	private jTabbedTerminal TerminalWindow;
	public JTabbedTerminalPlugin_V1(jTabbedTerminal jtt) {
		this.TerminalWindow = jtt;
	}
	public abstract void initalize();
	public abstract String getPluginName();
}
