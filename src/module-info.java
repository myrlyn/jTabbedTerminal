module jtabbedterminal.jTabbedTerminal {
	requires javafx.graphics;
	requires javafx.controls;
	requires com.kodedu.terminalfx;
	requires java.logging;
	requires pty4j;
	exports jtabbedterminal to javafx.graphics;
}