package nl.topicus.onderwijs.dashboard.datatypes.hudson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChangeSet implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<ChangeItem> items = new ArrayList<ChangeItem>();
	private String kind;
	private List<ChangeRevision> revisions = new ArrayList<ChangeRevision>();

	public List<ChangeItem> getItems() {
		return items;
	}

	public void setItems(List<ChangeItem> items) {
		this.items = items;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public List<ChangeRevision> getRevisions() {
		return revisions;
	}

	public void setRevisions(List<ChangeRevision> revisions) {
		this.revisions = revisions;
	}
}
