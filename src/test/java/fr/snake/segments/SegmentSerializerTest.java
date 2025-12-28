package fr.snake.segments;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SegmentSerializerTest {

	@Test
	void serialization() {
		Segment segment = new ShieldSegment(132, 657, Math.PI / 2) ;
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Segment.class, new SegmentSerializer())
				.create() ;
		String json = gson.toJson(segment, Segment.class) ;
		Segment deserialized = gson.fromJson(json, Segment.class) ;
		assertEquals(segment, deserialized) ;
	}
}