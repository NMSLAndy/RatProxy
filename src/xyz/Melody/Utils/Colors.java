package xyz.Melody.Utils;

public enum Colors {

	RESET("\u001B[0m"), BLACK("\u001B[30m"), RED("\u001B[31m"), GREEN("\u001B[32m"), YELLOW("\u001B[33m"),
	BLUE("\u001B[34m"), PURPLE("\u001B[35m"), CYAN("\u001B[36m"), WHITE("\u001B[37m"), GRAY("\u001B[90m"),
	LIGHT_RED("\u001B[91m"), LIGHT_GREEN("\u001B[92m"), LIGHT_YELLOW("\u001B[93m"), LIGHT_BLUE("\u001B[94m"),
	LIGHT_PURPLE("\u001B[95m"), LIGHT_CYAN("\u001B[96m"), LIGHT_WHITE("\u001B[97m"),

	BG_BLACK("\u001B[40m"), BG_RED("\u001B[41m"), BG_GREEN("\u001B[42m"), BG_YELLOW("\u001B[43m"),
	BG_BLUE("\u001B[44m"), BG_PURPLE("\u001B[45m"), BG_CYAN("\u001B[46m"), BG_WHITE("\u001B[47m"),
	BG_GRAY("\u001B[100m"), BG_LIGHT_RED("\u001B[101m"), BG_LIGHT_GREEN("\u001B[102m"), BG_LIGHT_YELLOW("\u001B[103m"),
	BG_LIGHT_BLUE("\u001B[104m"), BG_LIGHT_PURPLE("\u001B[105m"), BG_LIGHT_CYAN("\u001B[106m"),
	BG_LIGHT_WHITE("\u001B[107m");

	private String code;

	private Colors(String code) {
		this.code = code;
	}

	private static final String ANSI_COLOR_REGEX = "\u001B\\[[;\\d]*m";

	public static String removeColor(String input) {
		if (input == null)
			return null;
		return input.replaceAll(ANSI_COLOR_REGEX, "");
	}

	@Override
	public String toString() {
		return code;
	}
}
