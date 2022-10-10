package org.altervista.logisim.Vassembler_Launcher;

import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;

public class Main {

	public static void main(String[] args) {
		Utils.copyJAR();
		// Check if is already satisfied
		if (SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_10)) {
			// Launch Vassembler with system JAVA
			Utils.launchRealJAR(false);
		} else {
			// Check if JDK is in USER_HOME/.Vassembler and download it if not
			Utils.getJDK();
			Utils.launchRealJAR(true);
		}
	}
}
