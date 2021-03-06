package reusableComponents;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import testBase.DriverFactory;
import testBase.ExtentFactory;
import testBase.ExtentReportNG;
import testBase.TestBase;

public class ListenersImplementation implements ITestListener{
	JiraOperations jiraOps = new JiraOperations();
	TestBase testBase = new TestBase();
	static ExtentReports report;
		   ExtentTest test;

	/**
	 * @param result
	 */
	public void onTestStart(ITestResult result) {
		test = report.createTest(result.getMethod().getMethodName());
		ExtentFactory.getInstance().setExtent(test);
	}

	/**
	 * @param result
	 */
	public void onTestSuccess(ITestResult result) {
		ExtentFactory.getInstance().getExtent().log(Status.PASS, "Ca kiểm thử: "+result.getMethod().getMethodName()+ " thành công.");
		ExtentFactory.getInstance().removeExtentObject();
		testBase.tearDown();
	}

	/**
	 * @param result
	 */
	public void onTestFailure(ITestResult result) {
		ExtentFactory.getInstance().getExtent().log(Status.FAIL, "Ca kiểm thử: "+result.getMethod().getMethodName()+ " thất bại.");
		ExtentFactory.getInstance().getExtent().log(Status.FAIL, result.getThrowable());
		// Chụp ảnh màn hình kiểm thử thất bại
		File src = ((TakesScreenshot)DriverFactory.getInstance().getDriver()).getScreenshotAs(OutputType.FILE);
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy HH-mm-ss");
		Date date = new Date();
		String actualDate = format.format(date);
		String screenshotPath = System.getProperty("user.dir")+
				"/Reports/Screenshots/"+actualDate+".jpeg";
		File dest = new File(screenshotPath);
		
		try {
			FileUtils.copyFile(src, dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			ExtentFactory.getInstance().getExtent().addScreenCaptureFromPath(screenshotPath, "Hình ảnh vị trí kiểm thử thất bại");
			ExtentFactory.getInstance().removeExtentObject();

		} catch (IOException e) {
			e.printStackTrace();
		}
		// Kiểm tra xem có tự động tạo issue khi kiểm thử thất bại hay không
		String automaticJIRAcreation = PropertiesOperations.getPropertyValueByKey("automatic_Issue_Creation_In_JIRA");
		if(automaticJIRAcreation.trim().equalsIgnoreCase("ON")) {
			String issueS = "Kiểm thử thất bại - "+result.getMethod().getMethodName();
			String issueD = "Kiểm thử thành công.";
			String issueNumber = null;
			try {
				issueNumber = jiraOps.createJiraIssue("AutoTest", issueS, issueD, "AUT", "606a7a842b469c00701afd8d");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				jiraOps.addAttachmentToJiraIssue(issueNumber, screenshotPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		testBase.tearDown();
	}

	/**
	 * @param result
	 */
	public void onTestSkipped(ITestResult result) {
		ExtentFactory.getInstance().getExtent().log(Status.SKIP, "Ca kiểm thử: "+result.getMethod().getMethodName()+ " bị bỏ qua.");
		ExtentFactory.getInstance().removeExtentObject();
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
	}

	public void onTestFailedWithTimeout(ITestResult result) {
	}

	/**
	 * @param context
	 */
	public void onStart(ITestContext context) {
		try {
			 report = ExtentReportNG.setupExtentReport();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param context
	 */
	public void onFinish(ITestContext context) {
		report.flush();
	}

}
