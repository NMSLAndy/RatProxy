package Utils.Config;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CFGPattern {
	@SerializedName("srcPort")
	protected Integer srcPort;

	@SerializedName("targetHost")
	protected String targetHost;

	@SerializedName("targetPort")
	protected Integer targetPort;

	@SerializedName("rewriteHost")
	protected Boolean rewriteHost;

	@SerializedName("rewrittenHost")
	protected String rewrittenHost;

	@SerializedName("rewrittenPort")
	protected Integer rewrittenPort;

	@SerializedName("maxPlayers")
	protected Integer maxPlayers;

	@SerializedName("whiteListEnabled")
	protected Boolean whiteListEnabled;

	@SerializedName("online")
	protected boolean online = true;

	@SerializedName("doubleLogin")
	protected Boolean doubleLogin;

	@SerializedName("motds")
	protected List<String> motds;

	@SerializedName("list")
	protected List<String> list;
}
