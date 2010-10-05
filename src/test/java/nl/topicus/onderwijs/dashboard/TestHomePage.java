package nl.topicus.onderwijs.dashboard;

import junit.framework.TestCase;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;
import nl.topicus.onderwijs.dashboard.web.pages.DashboardPage;

import org.apache.wicket.util.tester.WicketTester;

/**
 * Simple test using the WicketTester
 */
public class TestHomePage extends TestCase
{
	private WicketTester tester;

	@Override
	public void setUp()
	{
		tester = new WicketTester(new WicketApplication());
	}

	public void testRenderMyPage()
	{
		//start and render the test page
		tester.startPage(DashboardPage.class);

		//assert rendered page class
		tester.assertRenderedPage(DashboardPage.class);

		//assert rendered label component
		tester.assertLabel("message", "If you see this message wicket is properly configured and running");
	}
}
