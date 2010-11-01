package nl.topicus.onderwijs.dashboard.modules;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;

@JsonTypeInfo(include = As.WRAPPER_OBJECT, use = Id.CLASS)
public interface Key extends Serializable {
	String getCode();

	String getName();
}
