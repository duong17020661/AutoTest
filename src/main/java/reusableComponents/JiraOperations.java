package reusableComponents;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JiraOperations {
	String jiraURL = PropertiesOperations.getPropertyValueByKey("jiraURL");
	String jiraUserName = PropertiesOperations.getPropertyValueByKey("jiraUserName");
	String jiraAccessKey = PropertiesOperations.getPropertyValueByKey("jiraSecretKey");

	/**
	 * @param ProjectName
	 * @param issueSummary
	 * @param issueDescription
	 * @param label
	 * @param assignee
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws ParseException
	 */
	public String createJiraIssue(String ProjectName, String issueSummary, String issueDescription, String label, String assignee) throws ClientProtocolException, IOException, ParseException {
		
		String issueId = null;
		HttpClient httpClient = HttpClientBuilder.create().build();
		String url = jiraURL+"/rest/api/3/issue";
		HttpPost postRequest = new HttpPost(url);
		postRequest.addHeader("Content-Type", "application/json");

		String encoding = Base64.getEncoder().encodeToString((jiraUserName+":"+jiraAccessKey).getBytes());
		postRequest.setHeader("Authorization", "Basic " + encoding);

		StringEntity params = new StringEntity(createPayloadForCreateJiraIssue(ProjectName, issueSummary, issueDescription, label, assignee));
		params.setContentType("application/json");
		postRequest.setEntity(params);
		HttpResponse response = httpClient.execute(postRequest);

		// Chuyển dữ liệu trả về sang dạng String
		String jsonString = EntityUtils.toString(response.getEntity());

		// Chuyển String sang JSON
		JSONParser parser = new JSONParser();  
		JSONObject json = (JSONObject) parser.parse(jsonString);

		// Lấy ID của Issue
		issueId = (String) json.get("key");
		return issueId;

	}

	/**
	 * @param issueId
	 * @param filePath
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void addAttachmentToJiraIssue(String issueId, String filePath) throws ClientProtocolException, IOException
	{
		String pathname= filePath;
		File fileUpload = new File(pathname);

		HttpClient httpClient = HttpClientBuilder.create().build();
		String url = jiraURL+"/rest/api/3/issue/"+issueId+"/attachments";
		HttpPost postRequest = new HttpPost(url);

		String encoding = Base64.getEncoder().encodeToString((jiraUserName+":"+jiraAccessKey).getBytes());

		postRequest.setHeader("Authorization", "Basic " + encoding);
		postRequest.setHeader("X-Atlassian-Token","nocheck");

		MultipartEntityBuilder entity=MultipartEntityBuilder.create();
		entity.addPart("file", new FileBody(fileUpload));
		postRequest.setEntity( entity.build());
		HttpResponse response = httpClient.execute(postRequest);
		System.out.println(response.getStatusLine());

		if(response.getStatusLine().toString().contains("200 OK")){
			System.out.println("Đã tải tệp đính kèm");
		} else{
			System.out.println("Không thể tải tệp đính kèm");
		}
	}

	/**
	 * @param ProjectName
	 * @param issueSummary
	 * @param issueDescription
	 * @param label
	 * @param assigneeId
	 * @return
	 */
	private static String createPayloadForCreateJiraIssue(String ProjectName, String issueSummary, String issueDescription, String label, String assigneeId) {
		return "{\n" +
				"\"fields\": {\n" +
				"    \"summary\": \"" + ProjectName + ": " + issueSummary+ "\",\n" +
				"    \"issuetype\": {\n" +
				"      \"id\": \"10004\"\n" +
				"    },\n" +
				"    \"project\": {\n" +
				"      \"id\": \"10000\"\n" +
				"    },\n" +
				"    \"description\": {\n" +
				"      \"type\": \"doc\",\n" +
				"      \"version\": 1,\n" +
				"      \"content\": [\n" +
				"        {\n" +
				"          \"type\": \"paragraph\",\n" +
				"          \"content\": [\n" +
				"            {\n" +
				"              \"text\": \"Description\",\n" +
				"              \"type\": \"text\"\n" +
				"            }\n" +
				"          ]\n" +
				"        }\n" +
				"      ]\n" +
				"    },\n" +
				"    \"reporter\": {\n" +
				"      \"id\": \"606a7a842b469c00701afd8d\"\n" +
				"    },\n" +
				"    \"labels\": [\n" +
				"      \""+label+"\"\n" +
				"    ],\n" +
				"\n" +
				"    \"assignee\": {\n" +
				"      \"id\": \""+assigneeId+"\"\n" +
				"    }\n" +
				"  }\n" +
				"}";
	}
}


