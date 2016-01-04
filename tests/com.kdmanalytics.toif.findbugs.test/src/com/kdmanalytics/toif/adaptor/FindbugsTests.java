package com.kdmanalytics.toif.adaptor;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.kdmanalytics.toif.framework.toolAdaptor.AdaptorOptions;
import com.kdmanalytics.toif.framework.toolAdaptor.Language;

/**
 * Test for findbugs adaptor
 * 
 * @author Adam Nunn
 *
 */
public class FindbugsTests {

	private FindbugsAdaptor fba;

	private static Logger LOG = Logger.getLogger(FindbugsTests.class);

	@Before
	public void setUp() throws Exception {
		fba = new FindbugsAdaptor();
	}

	@Test
	public void testGetLanguage() {
		assertEquals(Language.JAVA, fba.getLanguage());
	}

	@Test
	public void testGetRuntoolName() {
		assertEquals("findbugs", fba.getRuntoolName());
	}

	@Test
	public void testGetAdaptorDescription() {
		assertEquals(
				"Find Bugs in Java Programs with the added functionality of the security plugin.",
				fba.getAdaptorDescription());
	}

	@Test
	public void testGetAdaptorName() {
		assertEquals("Findbugs + Security Plugin", fba.getAdaptorName());
	}

	@Test
	public void testGetAdaptorVendorAddress() {
		assertEquals("1956 Robertson Road, Suite 204, Ottawa ON, K2H 5B9",
				fba.getAdaptorVendorAddress());

	}

	@Test
	public void testGetAdaptorVendorDescription() {
		assertEquals(
				"KDM Analytics is a security assurance company providing products and services for threat risk assessment and management, due diligence assessments, and information and data assurance. Leveraging our decades of experience in static analysis, reverse engineering and formal methods, we have created breakthrough products for the automated and systematic investigation of code, data and networks.",
				fba.getAdaptorVendorDescription());
	}

	@Test
	public void testGetAdaptorVendorEmail() {
		assertEquals("info@kdmanalytics.com", fba.getAdaptorVendorEmail());
	}

	@Test
	public void testGetAdaptorVendorName() {
		assertEquals("KDM Analytics", fba.getAdaptorVendorName());
	}

	@Test
	public void testGetAdaptorVendorPhone() {
		assertEquals("1-613-627-1010", fba.getAdaptorVendorPhone());
	}

	@Test
	public void testGetGeneratorDescription() {
		assertEquals(
				"Static code analysis tool that analyses Java bytecode and detects a wide range of problems.",
				fba.getGeneratorDescription());
	}

	@Test
	public void testGetGeneratorName() {
		assertEquals("Findbugs + Security Plugin", fba.getGeneratorName());
	}

	@Test
	public void testGetGeneratorVendorAddress() {
		assertEquals("http://findbugs.sourceforge.net/",
				fba.getGeneratorVendorAddress());
	}

	@Test
	public void testGetGeneratorVendorDescription() {
		assertEquals("SourceForge is a web-based source code repository.",
				fba.getGeneratorVendorDescription());
	}

	@Test
	public void testGetGeneratorVendorEmail() {
		assertEquals("findbugs@cs.umd.edu", fba.getGeneratorVendorEmail());
	}

	@Test
	public void testGetGeneratorVendorName() {
		assertEquals("sourceforge", fba.getGeneratorVendorName());
	}

	@Test
	public void testGetGeneratorVendorPhone() {
		assertEquals("", fba.getGeneratorVendorPhone());
	}

	@Test
	public void testGetGeneratorVersion() {

		// Partial mock of the findbugs adaptor
		FindbugsAdaptor fbMock = Mockito.spy(new FindbugsAdaptor());

		// the input stream that we will be faking as if it came from findbugs.
		InputStream is = new ByteArrayInputStream("3.0.0".getBytes());

		try {
			// return our own faked input stream when the startProcess is
			// called.
			Mockito.doReturn(is).when(fbMock)
					.startProcess(Mockito.any(ProcessBuilder.class));
		} catch (IOException e) {
			LOG.error("there was a problem when mocking the startProcess method of Findbugs Adaptor.");
			e.printStackTrace();
		}

		// assert that the input stream has been parsed correctly
		assertEquals("3.0.0 (+v1.2.1)", fbMock.getGeneratorVersion());

	}

	@Test
	public void testRunToolCommandsLinux() {

		fba.setOS("Linux");

		AdaptorOptions mockoptions = Mockito.mock(AdaptorOptions.class);

		Mockito.when(mockoptions.getInputFile()).thenReturn(
				new File("Test.class"));

		String additional[] = new String[] { "-Dblah", "-Ifoo" };

		String[] runToolCommands = fba.runToolCommands(mockoptions, additional);

		String expected[] = new String[] { "findbugs", "-xml", "-Dblah",
				"-Ifoo", "Test.class" };

		Assert.assertArrayEquals("Expected:" + Arrays.toString(expected)
				+ " GOT:" + Arrays.toString(runToolCommands), expected,
				runToolCommands);
	}

	public void testRunToolCommandsWindows() {

		fba.setOS("Windows");

		AdaptorOptions mockoptions = Mockito.mock(AdaptorOptions.class);

		Mockito.when(mockoptions.getInputFile()).thenReturn(
				new File("Test.class"));

		String additional[] = new String[] { "-Dblah", "-Ifoo" };

		String[] runToolCommands = fba.runToolCommands(mockoptions, additional);

		String expected[] = null;

		expected = new String[] { "cmd.exe", "/C", "findbugs.bat", "-xml",
				"-Dblah", "-Ifoo", "Test.class" };

		Assert.assertArrayEquals("Expected:" + Arrays.toString(expected)
				+ " GOT:" + Arrays.toString(runToolCommands), expected,
				runToolCommands);
	}

	@Test
	public void testAcceptsDOptions() {
		assertEquals(false, fba.acceptsDOptions());
	}

	@Test
	public void testAcceptsIOptions() {
		assertEquals(false, fba.acceptsIOptions());
	}

}
