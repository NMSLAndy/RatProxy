package xyz.Melody.Utils.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

public class Message {

	private static Gson gson = new GsonBuilder().registerTypeAdapter(Message.class, new MessageSerializer()).create();

	@SerializedName("text")
	private String text = "";

	@SerializedName("extra")
	private List<Message> extra = new ArrayList<Message>();

	public void setText(String text) {
		this.text = text;
	}

	public String construct() {
		return gson.toJson(this);
	}

	public Message addExtra(String content) {
		Message elemnt = new Message();
		elemnt.setText(content + "\n");
		this.extra.add(elemnt);
		return elemnt;
	}

	static class MessageSerializer implements JsonSerializer<Message> {

		@Override
		public JsonElement serialize(Message msg, Type type, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("text", msg.text);
			List<Message> extra = msg.extra;
			if (extra != null && !extra.isEmpty())
				jsonObject.add("extra", context.serialize(extra));
			return jsonObject;
		}

	}
}
