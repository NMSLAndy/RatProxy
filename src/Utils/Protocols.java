package Utils;

import java.util.HashMap;
import java.util.Map;

public class Protocols {

	private static Map<Integer, String> protocolMap = new HashMap<Integer, String>();

	static {
		protocolMap.put(4, "1.7.2");
		protocolMap.put(5, "1.7.6");
		protocolMap.put(47, "1.8.x");
		protocolMap.put(107, "1.9");
		protocolMap.put(108, "1.9.1");
		protocolMap.put(109, "1.9.2");
		protocolMap.put(110, "1.9.3");
		protocolMap.put(210, "1.10");
		protocolMap.put(315, "1.11");
		protocolMap.put(316, "1.11.1");
		protocolMap.put(335, "1.12");
		protocolMap.put(338, "1.12.1");
		protocolMap.put(340, "1.12.2");
		protocolMap.put(393, "1.13");
		protocolMap.put(401, "1.13.1");
		protocolMap.put(404, "1.13.2");
		protocolMap.put(477, "1.14");
		protocolMap.put(480, "1.14.1");
		protocolMap.put(485, "1.14.2");
		protocolMap.put(490, "1.14.3");
		protocolMap.put(498, "1.14.4");
		protocolMap.put(573, "1.15");
		protocolMap.put(575, "1.15.1");
		protocolMap.put(578, "1.15.2");
		protocolMap.put(735, "1.16");
		protocolMap.put(736, "1.16.1");
		protocolMap.put(751, "1.16.2");
		protocolMap.put(753, "1.16.3");
		protocolMap.put(754, "1.16.4");
		protocolMap.put(755, "1.17");
		protocolMap.put(756, "1.17.1");
		protocolMap.put(757, "1.18");
		protocolMap.put(758, "1.18.2");
		protocolMap.put(759, "1.19");
		protocolMap.put(760, "1.19.1");
		protocolMap.put(761, "1.19.3");
		protocolMap.put(762, "1.19.4");
		protocolMap.put(763, "1.20");
		protocolMap.put(763, "1.20.1");
		protocolMap.put(767, "1.21");
		protocolMap.put(768, "1.21.2");
		protocolMap.put(769, "1.21.4");
	}

	public static String getVersion(int protocolVer) {
		return protocolMap.get(protocolVer);
	}
}
