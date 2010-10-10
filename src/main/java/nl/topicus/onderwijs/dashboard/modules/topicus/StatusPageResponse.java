package nl.topicus.onderwijs.dashboard.modules.topicus;

public class StatusPageResponse {
	private final int httpStatusCode;
	private final String pageContent;

	public StatusPageResponse(int httpStatusCode, String pageContent) {
		this.httpStatusCode = httpStatusCode;
		this.pageContent = pageContent;
	}

	public int getHttpStatusCode() {
		return httpStatusCode;
	}

	public String getPageContent() {
		return pageContent;
	}

	public boolean isOk() {
		return httpStatusCode == 200;
	}

	public boolean isOffline() {
		return httpStatusCode == 503;
	}
}
