package nl.topicus.onderwijs.dashboard.datatypes;

import java.io.IOException;
import java.io.Serializable;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class Dot implements Serializable {
	private static final long serialVersionUID = 1L;
	private DotColor color;
	private String character;

	public Dot() {
	}

	public Dot(DotColor color, String character) {
		this.color = color;
		this.character = character;
	}

	public DotColor getColor() {
		return color;
	}

	public void setColor(DotColor color) {
		this.color = color;
	}

	public String getCharacter() {
		return character;
	}

	public void setCharacter(String character) {
		this.character = character;
	}

	@Override
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonGenerationException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
