package nl.topicus.onderwijs.dashboard.datatypes.hudson;

import java.io.Serializable;

public class ChangePath implements Serializable {
	private static final long serialVersionUID = 1L;
	private String editType;
	private String file;

	public String getEditType() {
		return editType;
	}

	public void setEditType(String editType) {
		this.editType = editType;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
}
