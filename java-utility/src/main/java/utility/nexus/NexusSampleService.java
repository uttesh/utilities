package utility.nexus;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NexusSampleService {

	private static Logger logger = Logger.getLogger(NexusSampleService.class.getSimpleName());
	private static String username = "builduser";
	private static String password = "dewelcome720";
	private static String url = "http://repo.dev.decisionengines.ai/repository/de-docker/v2/de-ai-service/manifests/latest";
	// private static String encodedAuth = "YnVpbGR1c2VyOmRld2VsY29tZTcyMA==";

	public static void main(String[] args) {
		ObjectMapper mapper = new ObjectMapper();
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> entity = new HttpEntity<>("body", createHeaders(username, password));
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		// System.out.println("response : " + response.getBody());

		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> itemMap = new HashMap<String, Object>();
		try {
			map = mapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {
			});
			boolean flag = map.get("history") instanceof List;
			List<Object> history = (List<Object>) map.get("history");
			Map<String, Object> _data = new LinkedHashMap<String, Object>();
			if (history != null && history.size() > 0) {
				Object item = history.get(0);
				String v1Compatibility = ((LinkedHashMap) item).get("v1Compatibility").toString();
				itemMap = mapper.readValue(v1Compatibility, new TypeReference<Map<String, Object>>() {
				});
				System.out.println("itemMap :: "+itemMap);
				String createdDate = itemMap.get("created").toString();
				System.out.println("createdTime :::::::::::: " + createdDate.split("T"));
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				Date date = dateFormat.parse(createdDate);
				long millis = date.getTime();
				System.out.println("::::::::::millis::::::::::" + millis);
				System.out.println("::::::::::millis to date::::::::::" + new Date(millis));
			}
		} catch (JsonProcessingException | ParseException e) {
			e.printStackTrace();
		}
	}

	public static HttpHeaders createHeaders(String username, String password) {
		return new HttpHeaders() {
			{
				String auth = username + ":" + password;
				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodedAuth);
				set("Authorization", authHeader);
			}
		};
	}
}
