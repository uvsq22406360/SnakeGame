package fr.snake.segments;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Cette classe permet de sérialiser et dé sérialiser les différents types de segments en JSON.
 * Cette classe est nécessaire de par le fait que nativement, GSON ne sait pas transformer une classe abstraite
 * en JSON, car lors de la désérialisation, il ne saura pas quelle classe instancier.
 * Cette classe permet d'encoder le nom de classe du sous-type (DefaultSegment, ShieldSegment, ...) afin de pouvoir
 * le dé sérialiser par la suite.
 */
public class SegmentSerializer implements JsonSerializer<Segment>, JsonDeserializer<Segment> {

	@Override
	public Segment deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		String className = jsonElement.getAsJsonObject().get("type").getAsString() ;
		Class<? extends Segment> clazz;
		try {
			clazz = (Class<? extends Segment>) Class.forName(className) ;
		} catch (ClassNotFoundException e) {
			throw new JsonParseException(e) ;
		}
		Segment segment = jsonDeserializationContext.deserialize(jsonElement, clazz) ;
		return segment;
	}

	@Override
	public JsonElement serialize(Segment segment, Type type, JsonSerializationContext jsonSerializationContext) {
		JsonElement jsonElement = jsonSerializationContext.serialize(segment) ;
		jsonElement.getAsJsonObject().addProperty("type", segment.getClass().getName()) ;
		return jsonElement;
	}
}
