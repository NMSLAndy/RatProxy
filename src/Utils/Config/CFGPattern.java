package Utils.Config;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CFGPattern {
	@SerializedName("srcPort")
	protected int srcPort;

	@SerializedName("targetHost")
	protected String targetHost;

	@SerializedName("targetPort")
	protected int targetPort;

	@SerializedName("rewriteHost")
	protected boolean rewriteHost;

	@SerializedName("rewrittenHost")
	protected String rewrittenHost;

	@SerializedName("rewrittenPort")
	protected int rewrittenPort;

	@SerializedName("maxPlayers")
	protected int maxPlayers;

	@SerializedName("whiteListEnabled")
	protected boolean whiteListEnabled;

	@SerializedName("motd")
	protected String motd;

	@SerializedName("list")
	protected List<String> list;

	// Getter 方法
	protected List<String> getList() {
		return list;
	}
}
