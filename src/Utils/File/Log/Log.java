package Utils.File.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedChannelException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import Utils.File.FileManager;

public class Log {

	private static int index = 0;
	private static String timeStamp = "";
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("[yyyy-MM-dd] [HH:mm:ss]");
	private static final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();

	public static void init() {
		FileManager.init();
		Thread.ofVirtual().start(() -> {
			while (true) {
				try {
					Thread.sleep(250);
					timeStamp = FORMAT.format(new Date());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		Thread.ofVirtual().start(new LogWriterThread());
	}

	public static void saveQuit(String name) {
		save(Colors.LIGHT_CYAN + name + Colors.GRAY + " disconnected.");
	}

	public static void saveJoin(String name) {
		save(Colors.LIGHT_CYAN + name + Colors.CYAN + " joined.");
	}

	public static void saveDeny(String name, String reason) {
		save(Colors.YELLOW + "Denied " + Colors.LIGHT_CYAN + name + Colors.GRAY + " Reason: " + Colors.WHITE + reason);
	}

	public static void save(String content) {
		try {
			logQueue.put(timeStamp + " " + content + Colors.RESET);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
	}

	public static void saveException(Exception e) {
		if (e instanceof ClosedChannelException || e instanceof AsynchronousCloseException)
			return;
		save(Colors.RED + "[ERROR] " + e + Colors.RESET);
		for (StackTraceElement element : e.getStackTrace()) {
			save(Colors.RED + "[ERROR] " + "\t" + element.toString() + Colors.RESET);
		}
	}

	public static void error(String string) {
		save(Colors.RED + "[ERROR] " + string + Colors.RESET);
	}

	public static void warn(String string) {
		save(Colors.YELLOW + "[WARN] " + string + Colors.RESET);
	}

	public static String getError(Exception e) {
		return e.getClass().getName() + " : " + e.getMessage();
	}

	private static class LogWriterThread extends Thread {
		LogWriterThread() {
			super("Log");
		}

		@Override
		public void run() {
			while (true) {
				try {
					String logEntry = logQueue.take();
					updateLogFile();
					try (FileWriter writer = new FileWriter(FileManager.fileToWrite, true)) {
						System.out.println(Colors.RESET + logEntry);
						logEntry = Colors.removeColor(logEntry);
						writer.write(logEntry + "\r\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (InterruptedException | IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void updateLogFile() throws IOException {
			String currentDate = new SimpleDateFormat("yyyy.MM.dd").format(new Date());
			if (!currentDate.equals(FileManager.curDate)) {
				FileManager.curDate = currentDate;
				index = 0;
				FileManager.dateDir = new File(FileManager.logDir, FileManager.curDate);
				if (!FileManager.dateDir.exists())
					FileManager.dateDir.mkdir();
				String postFix = index == 0 ? "" : " - " + index;
				FileManager.fileToWrite = new File(FileManager.dateDir, currentDate + postFix + ".log");
			}
		}
	}
}
