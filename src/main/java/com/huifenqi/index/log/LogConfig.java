
package com.huifenqi.index.log;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * 日志配置
 * @author majianchun
 *
 */
public class LogConfig {

	public static void config(String configFilePath) {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(lc);
		lc.reset();
		try {
			configurator.doConfigure(configFilePath);
			System.out.println("config log config:"+configFilePath);
		}
		catch (JoranException e) {
			e.printStackTrace();
			System.out.println("Fatal: Init LogConfig Error.");
			System.exit(1);
		}
	}
}
