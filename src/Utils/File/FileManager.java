package Utils.File;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Utils.File.FavIcon.IconManager;

public class FileManager {

	public static String curDate = null;
	public static File mainDir = null;

	// Log的家
	public static File dateDir = null;
	public static File fileToWrite = null;
	public static File logDir = null;

	// icon的家
	public static File iconDir = null;

	public static void init() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
		Date date = new Date();
		curDate = dateFormat.format(date);

		try {
			mainDir = new File(new File("").getCanonicalPath(), "Rat Proxy");
			if (!mainDir.exists())
				mainDir.mkdir();
			checklogDir();
			checkIconDir();
			int append = 0;
			while (true) {
				String postFix = append == 0 ? "" : " - " + append;
				fileToWrite = new File(dateDir, curDate + postFix + ".log");
				if (fileToWrite.exists()) {
					append++;
				} else {
					fileToWrite.createNewFile();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void checklogDir() {
		logDir = new File(mainDir, "Logs");
		if (!logDir.exists())
			logDir.mkdir();
		dateDir = new File(logDir, curDate);
		if (!dateDir.exists())
			dateDir.mkdir();
	}

	private static void checkIconDir() {
		iconDir = new File(mainDir, "FavIcons");
		if (!iconDir.exists())
			iconDir.mkdir();
		IconManager.readIcons();
	}
}
