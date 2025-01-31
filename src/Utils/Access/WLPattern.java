package Utils.Access;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WLPattern {

	@SerializedName("names")
	protected List<String> names;

	@SerializedName("uuids")
	protected List<String> uuids;
}
