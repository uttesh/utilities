package utility.bitbucket;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.vx68k.bitbucket.api.BitbucketAccount;
import org.vx68k.bitbucket.api.BitbucketUser;
import org.vx68k.bitbucket.api.client.BitbucketClient;
import org.vx68k.bitbucket.api.client.BitbucketClientAccount;
import org.vx68k.bitbucket.api.client.BitbucketClientRepository;


public class BitbucketClientSample {

	public static void main(String[] args) {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		BitbucketClientAccount bitbucketClientAccount = new BitbucketClientAccount(builder.add("type", "user").build(), BitbucketClient.getDefaultInstance());
		BitbucketClient bitbucketClient = bitbucketClientAccount.getBitbucketClient();
		bitbucketClient.setClientId("nNL5QecVJRnQ9he8cb");
		bitbucketClient.setClientSecret("nVCuyjffBsEwfq2HaytjC8CPrTmpW66W");
		bitbucketClient.login("uttesh@rivetsys.com", "utt1234$");
		
		System.out.println("bitbucketClient ::: getAccessToken :: "+bitbucketClient.getAccessToken());
		System.out.println("bitbucketClient ::: getRefreshToken :: "+bitbucketClient.getRefreshToken());
		
		
		BitbucketClientRepository bitbucketClientRepository = new BitbucketClientRepository(builder.add("type", "repository").build(), BitbucketClient.getDefaultInstance());
		
		 System.out.println("Got repo " + bitbucketClientRepository.getRepository().getSize()); 
		
		BitbucketUser user = (BitbucketUser) bitbucketClient.getUser("uhosadurga");
        System.out.println("Got user " + user);
        System.out.println("--------------------");
        
        
        BitbucketAccount team = bitbucketClient.getTeam("decisionengines");
        System.out.println("Got team" + team);
        System.out.println("--------------------");
        System.out.println("Got repositories" + bitbucketClient.getRepository("uhosadurga","de-ai-service"));
        
        RestTemplate restTemplate = new RestTemplate();	
        String fooResourceUrl = "https://api.bitbucket.org/2.0/teams/decisionengines/repositories";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+ bitbucketClient.getAccessToken());
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<String> response  =  restTemplate.exchange(fooResourceUrl, HttpMethod.GET, entity, String.class);
        //System.out.println("response data  :: "+response.getBody());
        
        System.out.println("tags :: "+getRepoMeta(bitbucketClient.getAccessToken(), "de-bom", "tags").getBody());
        
//		BitbucketClient client = BitbucketClient.builder()
//				.endPoint("https://bitbucket.org") // Optional and can be sourced from system/env. Falls back to http://127.0.0.1:7990
//				.credentials("srinigowda@rivetsys.com:rivetlabs1234$") // Optional and can be sourced from system/env and can be Base64 encoded.
//				.build();
//	    Version version = client.api().systemApi().version();
//		System.out.println("version ::: "+version.displayName());
		
	}
	
	public static ResponseEntity<String> getRepoMeta(String token,String repo,String type) {
		    RestTemplate restTemplate = new RestTemplate();	
	        String fooResourceUrl = "https://api.bitbucket.org/2.0/repositories/decisionengines/"+repo+"/refs/"+type;
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Authorization", "Bearer "+ token);
	        HttpEntity<String> entity = new HttpEntity<>("body", headers);
	        ResponseEntity<String> response  =  restTemplate.exchange(fooResourceUrl, HttpMethod.GET, entity, String.class);
		return response;
	}
}
