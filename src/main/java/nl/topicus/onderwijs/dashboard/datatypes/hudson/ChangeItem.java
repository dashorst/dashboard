package nl.topicus.onderwijs.dashboard.datatypes.hudson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChangeItem implements Serializable {
	private static final long serialVersionUID = 1L;
	private Date date;
	private String msg;
	private List<ChangePath> paths = new ArrayList<ChangePath>();
	private int revision;
	private String user;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<ChangePath> getPaths() {
		return paths;
	}

	public void setPaths(List<ChangePath> paths) {
		this.paths = paths;
	}

	public int getRevision() {
		return revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}
