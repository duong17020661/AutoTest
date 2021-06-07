package reusableComponents;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import com.aventstack.extentreports.Status;

import testBase.DriverFactory;
import testBase.ExtentFactory;

public class ActionEngine {

	/**
	 * @param element
	 * @param fieldName
	 * @param valueToBeSent
	 */
	public void sendKeys_custom(WebElement element, String fieldName, String valueToBeSent) {
		try {
			element.sendKeys(valueToBeSent);
			ExtentFactory.getInstance().getExtent().log(Status.PASS, fieldName+"==> Nhập giá trị: "+valueToBeSent);
		} catch (Exception e) {
			ExtentFactory.getInstance().getExtent().log(Status.FAIL, "Nhập giá trị : "+fieldName + " không thành công với lỗi : "+e);
		}
	}

	/**
	 * @param element
	 * @param fieldName
	 * @param valueToBeSent
	 */
	public void uploadFile_custom(WebElement element, String fieldName, String valueToBeSent) {
		try {
			element.sendKeys(valueToBeSent);
			ExtentFactory.getInstance().getExtent().log(Status.PASS, fieldName+"==> Tải tệp tin : "+valueToBeSent);
		} catch (Exception e) {
			ExtentFactory.getInstance().getExtent().log(Status.FAIL, "ải tệp tin : "+fieldName + " không thành công với lỗi : "+e);
		}
	}

	/**
	 * @param element
	 * @param checkElement
	 * @param fieldName
	 * @param value
	 */
	public void selectedCheckbox_custom(WebElement element, WebElement checkElement, String fieldName, int value) {
		try {
			if (element.isSelected() && value == 0) {
				checkElement.click();
			}
			else if (element.isSelected() && value == 1) {
//				checkElement.click();
			}
			else if (!element.isSelected() && value == 0) {
//				checkElement.click();
			}
			else if (!element.isSelected() && value == 1) {
				checkElement.click();
			}
			ExtentFactory.getInstance().getExtent().log(Status.PASS, fieldName+"==> CHọn giá trị thành công ");
		} catch (Exception e) {
			ExtentFactory.getInstance().getExtent().log(Status.FAIL, "Không thể chọn giá trị " +fieldName +", có lỗi xảy ra: "+e);
		}
	}

	/**
	 * @param element
	 * @param fieldName
	 */
	public void click_custom(WebElement element, String fieldName) {
		try {
			element.click();
			ExtentFactory.getInstance().getExtent().log(Status.PASS, fieldName+"==> CHọn giá trị thành công ");
		} catch (Exception e) {
			ExtentFactory.getInstance().getExtent().log(Status.FAIL, "Không thể chọn giá trị " +fieldName +", có lỗi xảy ra: "+e);
		}
	}


	/**
	 * @param element
	 * @param fieldName
	 */
	public void moveToElement_custom(WebElement element,String fieldName){
		try{
			JavascriptExecutor executor = (JavascriptExecutor) DriverFactory.getInstance().getDriver();
			executor.executeScript("arguments[0].scrollIntoView(true);", element);
			Actions actions = new Actions(DriverFactory.getInstance().getDriver());		
			actions.moveToElement(element).build().perform();
			ExtentFactory.getInstance().getExtent().log(Status.PASS, fieldName+"==> Hover thành công ");
			Thread.sleep(1000);
		}catch(Exception e){
			ExtentFactory.getInstance().getExtent().log(Status.FAIL, "Không thể hover: " +fieldName +", có lỗi xảy ra: "+e);

		}
	}


	/**
	 * @param element
	 * @param fieldName
	 * @param ddVisibleText
	 * @throws Throwable
	 */
	public void selectDropDownByVisibleText_custom(WebElement element, String fieldName, String ddVisibleText) throws Throwable {
		try {
			Select s = new Select(element);
			s.selectByVisibleText(ddVisibleText);
			ExtentFactory.getInstance().getExtent().log(Status.PASS, fieldName+"==> Giá trị: "+ ddVisibleText + " đã được chọn");
		} catch (Exception e) {
			ExtentFactory.getInstance().getExtent().log(Status.FAIL, "Không thể chọn giá trị " +fieldName +"  , có lỗi xảy ra: "+e);
		}
	}


	/**
	 * @param expvalue
	 * @param actualValue
	 * @param locatorName
	 * @throws Throwable
	 */
	public void assertEqualsString_custom(String expvalue, String actualValue, String locatorName) throws Throwable {
		try {
			if(expvalue.equals(actualValue) == true) {
				ExtentFactory.getInstance().getExtent().log(Status.PASS, "String Assertion is successful on field "+ locatorName + " Expected value was: "+ expvalue + " actual value is: "+actualValue);
			}else {
				ExtentFactory.getInstance().getExtent().log(Status.FAIL, "String Assertion FAILED on field "+ locatorName + " Expected value was: "+ expvalue + " actual value is: "+actualValue);
				Assert.assertTrue(false);
			}
		} catch (Exception e) {
			Assert.assertTrue(false, e.toString());
		}
	}

}
