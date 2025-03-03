package com.reports;

import java.util.List;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

//Testng to listen to this extent reports.
public class ExtentTestNGITestListener implements IReporter {
    public ExtentReports extent;
    public ExtentReports logger;
    
    
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
    extent = new ExtentReports("TestNGReports" + File.separator + "AutomationReportsTestNG.html", true);
    
    
    for (ISuite suite : suites) {
            Map<String, ISuiteResult> result = suite.getResults();
            for (ISuiteResult r : result.values()) {
            ITestContext context = r.getTestContext();
            buildTestNodes(context.getPassedTests(), LogStatus.PASS);
            buildTestNodes(context.getFailedTests(), LogStatus.FAIL);
            buildTestNodes(context.getSkippedTests(), LogStatus.SKIP);
            }
    }
    	extent.flush();
        extent.close();  
   }

private void buildTestNodes(IResultMap tests, LogStatus status) {
   ExtentTest test;
   if (tests.size() > 0) {
   for (ITestResult result : tests.getAllResults()) {
   test = extent.startTest(result.getMethod().getMethodName());
   
   if (result.getMethod().getDescription() != null) {
       test.setDescription(result.getMethod().getDescription());
   }
   test.setStartedTime(getTime(result.getStartMillis()));
   test.setEndedTime(getTime(result.getEndMillis()));

   for (String group : result.getMethod().getGroups()) {
       test.assignCategory(group);
   }

   String message = "Test " + status.toString().toLowerCase() + "ed";
   if (result.getThrowable() != null)
   {
   message = result.getThrowable().getMessage();
   }
   test.log(status, message);
   extent.endTest(test);

// Adding additional log info
   test.log(LogStatus.INFO, "Test started at: " + getTime(result.getStartMillis()));
   test.log(LogStatus.INFO, "Test ended at: " + getTime(result.getEndMillis()));

   extent.endTest(test);
   }
   }
}
private Date getTime(long millis) {
   Calendar calendar = Calendar.getInstance();
   calendar.setTimeInMillis(millis);
   return calendar.getTime();        
   }
}
