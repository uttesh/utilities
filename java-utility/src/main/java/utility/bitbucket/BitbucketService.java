package utility.bitbucket;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.vx68k.bitbucket.api.client.BitbucketClient;
import org.vx68k.bitbucket.api.client.BitbucketClientAccount;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import utility.bitbucket.bean.BitbucketSession;

public class BitbucketService {

	private BitbucketSession bitbucketSession = new BitbucketSession();
	ObjectMapper mapper = new ObjectMapper();

	public BitbucketService() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		BitbucketClientAccount bitbucketClientAccount = new BitbucketClientAccount(builder.add("type", "user").build(),
				BitbucketClient.getDefaultInstance());
		BitbucketClient bitbucketClient = bitbucketClientAccount.getBitbucketClient();
		bitbucketClient.setClientId(Constants.Bitbucket.CLIENT_ID);
		bitbucketClient.setClientSecret(Constants.Bitbucket.CLIENT_SECRETE);
		bitbucketClient.login(Constants.Bitbucket.REPO_USER_NAME, Constants.Bitbucket.REPO_PASSWORD);
		bitbucketSession.setAccessToken(bitbucketClient.getAccessToken());
		bitbucketSession.setRefreshToken(bitbucketClient.getRefreshToken());
	}

	public BitbucketSession getBitbucketSession() {
		return bitbucketSession;
	}

	private ResponseEntity<String> getRepoData(String repo, String type) {
		RestTemplate restTemplate = new RestTemplate();
		String repoUrl = Constants.Bitbucket.API_REPO_URL + repo + "/refs/" + type;
		HttpHeaders headers = new HttpHeaders();
		System.out.println("bitbucketSession.getAccessToken() ::: " + bitbucketSession.getAccessToken());
		headers.set("Authorization", "Bearer " + bitbucketSession.getAccessToken());
		HttpEntity<String> entity = new HttpEntity<>("body", headers);
		ResponseEntity<String> response = restTemplate.exchange(repoUrl, HttpMethod.GET, entity, String.class);
		return response;
	}

	private ResponseEntity<String> getfileData(String repo, String path) {
		RestTemplate restTemplate = new RestTemplate();
		String repoUrl = Constants.Bitbucket.API_REPO_URL + repo + path;
		System.out.println("repoUrl :: " + repoUrl);
		System.out.println("bitbucketSession.getAccessToken() :: " + bitbucketSession.getAccessToken());
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + bitbucketSession.getAccessToken());
		HttpEntity<String> entity = new HttpEntity<>("body", headers);
		ResponseEntity<String> response = restTemplate.exchange(repoUrl, HttpMethod.GET, entity, String.class);
		return response;
	}

	public List<String> getAllTags() throws NoSuchFieldException, SecurityException {
		return getNames("", "tags");

	}

	public List<String> getAllBranches() throws NoSuchFieldException, SecurityException {
		return getNames("", "branches");
	}

	private List<String> getNames(String repoName, String type) {
		String data = getRepoData(repoName, type).getBody();
		List<String> tagNames = new ArrayList<String>();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = mapper.readValue(data, new TypeReference<Map<String, Object>>() {
			});
			List<Object> tags = (List<Object>) map.get("values");
			Map<String, Object> _data = new LinkedHashMap<String, Object>();
			for (Object item : tags) {
				tagNames.add(((LinkedHashMap) item).get("name").toString());
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return tagNames;
	}


	public String getPropertyValue(String keyPath, Object object) {
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		StringWriter stringEmp = new StringWriter();
		String value = "";
		try {
			mapper.writeValue(stringEmp, object);
			DocumentContext jsonContext = JsonPath.parse(stringEmp.toString());
			value = jsonContext.read(keyPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}

	public Map<String, Object> getServiceFileList(String path) {
		String data = getfileData("", path).getBody();
		try {
			return mapper.readValue(data, new TypeReference<Map<String, Object>>() {
			});
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<String> getServiceNames(String releaseVersion,String path) {
		    List<String> serviceList = new ArrayList<>();
			Map<String, Object> services = getServiceFileList("/src/" +releaseVersion + path);
			System.out.println("services pagelen:::{} " + services.get("pagelen"));
			System.out.println("services next::: {}" + services.get("next"));
			List<Object> items = (List<Object>) services.get("values");
			items.forEach(item -> {
				String jsonPath = "$['path']";
				String serviceName = getPropertyValue(jsonPath, item);
				System.out.println("============serviceName======== {}" + serviceName);
				serviceList.add(serviceName);
			});
			if (services.get("next") != null && !services.get("next").toString().isEmpty()) {
				String nextPage = services.get("next").toString();
				nextPage = nextPage.substring(nextPage.indexOf("?") + 1, nextPage.length());
				System.out.println("next page " + nextPage);
				getServiceNames(releaseVersion,"/?"+nextPage);
			}
		return serviceList;
	}

	public String getServiceSpec(String releaseVersion) {
		StringBuilder sb = new StringBuilder("[");
		List<String> service = getServiceNames(releaseVersion,"/");
		System.out.println("=========final===serviceList========" + service.size());
		List<String> serviceSpecs = new ArrayList<>();
		for (String serviceName : service) {
			String serviceSpec = getfileData("de-bom", "/src/" + releaseVersion + "/" + serviceName)
					.getBody();
			serviceSpecs.add(serviceSpec);
		}
		String result = serviceSpecs.stream().collect(Collectors.joining(", "));
		String releaseSpec = sb.append(result).append("]").toString();
		System.out.println("final release spec");
		System.err.println(releaseSpec);
		return null;
	}
	
	public void getLatestBuild() {
		RestTemplate restTemplate = new RestTemplate();
		String repoUrl = Constants.Bitbucket.API_REPO_URL +"/commits";
		System.out.println("repoUrl :: " + repoUrl);
		System.out.println("bitbucketSession.getAccessToken() :: " + bitbucketSession.getAccessToken());
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + bitbucketSession.getAccessToken());
		HttpEntity<String> entity = new HttpEntity<>("body", headers);
		ResponseEntity<String> response = restTemplate.exchange(repoUrl, HttpMethod.GET, entity, String.class);
		System.out.println("commites :: "+response.getBody());
	}
}
