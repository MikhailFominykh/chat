package test.chat.server;

public class StringUtils {
	public static String getFirstWord(String s) {
		String trimmed = s.trim();
		int i = trimmed.indexOf(" ");
		if (i > 0) {
			return trimmed.substring(0, i);
		} else {
			return trimmed;
		}
	}
}
