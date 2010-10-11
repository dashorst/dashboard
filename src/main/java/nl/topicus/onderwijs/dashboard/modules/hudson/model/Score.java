package nl.topicus.onderwijs.dashboard.modules.hudson.model;

import java.io.Serializable;

public class Score implements Serializable {
	private static final long serialVersionUID = 1L;
	private String description;
	private String ruleName;
	private String rulesetName;
	private double value;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getRulesetName() {
		return rulesetName;
	}

	public void setRulesetName(String rulesetName) {
		this.rulesetName = rulesetName;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
