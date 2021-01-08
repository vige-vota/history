package it.vige.labs.gc;

import static it.vige.labs.gc.bean.votingpapers.State.PREPARE;
import static it.vige.labs.gc.bean.votingpapers.State.VOTE;
import static it.vige.labs.gc.rest.HistoryController.dayFormatter;
import static it.vige.labs.gc.rest.HistoryController.hourFormatter;
import static java.util.Arrays.asList;
import static java.util.Calendar.getInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.keycloak.OAuth2Constants.CLIENT_CREDENTIALS;
import static org.keycloak.OAuth2Constants.GRANT_TYPE;
import static org.keycloak.adapters.KeycloakDeploymentBuilder.build;
import static org.keycloak.adapters.authentication.ClientCredentialsProviderUtils.setClientCredentials;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static org.springframework.web.util.UriComponentsBuilder.newInstance;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.spi.KeycloakAccount;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

import it.vige.labs.gc.bean.result.Candidate;
import it.vige.labs.gc.bean.result.Group;
import it.vige.labs.gc.bean.result.Party;
import it.vige.labs.gc.bean.result.Voting;
import it.vige.labs.gc.bean.result.VotingPaper;
import it.vige.labs.gc.bean.votingpapers.State;
import it.vige.labs.gc.bean.votingpapers.VotingPapers;
import it.vige.labs.gc.messages.Messages;
import it.vige.labs.gc.rest.HistoryController;

@SpringBootTest
@ActiveProfiles("dev")
public class HistoryTest {

	private Logger logger = getLogger(HistoryTest.class);

	private final static DateFormat minuteFormatter = new SimpleDateFormat("dd-MM-yyyy:HH-mm");

	@Autowired
	private HistoryController historyController;

	@Autowired
	private KeycloakRestTemplate restTemplate;

	@Value("${votingpapers.scheme}")
	private String votingpapersScheme;

	@Value("${votingpapers.host}")
	private String votingpapersHost;

	@Value("${votingpapers.port}")
	private int votingpapersPort;

	private static String token;

	private static Principal principal = new Principal() {

		@Override
		public String getName() {
			return "myprincipal";
		}

	};

	private static Set<String> roles = new HashSet<String>(
			asList(new String[] { "admin", "votaoperator", "representative", "citizen" }));

	@BeforeAll
	public static void setAuthentication() throws FileNotFoundException {
		FileInputStream config = new FileInputStream("src/test/resources/keycloak.json");
		KeycloakDeployment deployment = build(config);
		Map<String, String> reqHeaders = new HashMap<>();
		Map<String, String> reqParams = new HashMap<>();
		setClientCredentials(deployment, reqHeaders, reqParams);
		HttpHeaders headers = new HttpHeaders();
		reqHeaders.forEach((x, y) -> {
			headers.add(x, y);
		});
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add(GRANT_TYPE, CLIENT_CREDENTIALS);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		RestTemplate restTemplate = new RestTemplate();
		String url = deployment.getTokenUrl();
		ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(url, POST, request,
				AccessTokenResponse.class, reqParams);
		token = response.getBody().getToken();

		RefreshableKeycloakSecurityContext securityContext = new RefreshableKeycloakSecurityContext(null, null, token,
				null, null, null, null);
		KeycloakAccount account = new SimpleKeycloakAccount(principal, roles, securityContext);
		getContext().setAuthentication(new KeycloakAuthenticationToken(account, true));
	}

	private void setState(State state) {
		UriComponents uriComponents = newInstance().scheme(votingpapersScheme).host(votingpapersHost)
				.port(votingpapersPort).path("/state?state=" + state).buildAndExpand();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		HttpEntity<?> request = new HttpEntity<>(headers);
		restTemplate.exchange(uriComponents.toString(), GET, request, Messages.class);
	}

	@Test
	public void history() throws IOException {
		clean();
		Date date = new Date();
		Document found = (Document) historyController
				.template(mongoTemplate -> mongoTemplate.findOne(new Query(), VotingPapers.class, "votingPapers"));
		assertNull(found, "is all cleaned");

		setState(PREPARE);
		Date savedVoting = historyController.save().getMessages().get(0).getDate();
		assertNull(savedVoting, "PREPARE state denies the save. No votes saved");
		setState(VOTE);
		savedVoting = historyController.save().getMessages().get(0).getDate();
		VotingPapers votingPapers = historyController.getVotingPapers(date);
		assertNotNull(votingPapers, "voting papers is saved");
		assertEquals(minuteFormatter.format(date), minuteFormatter.format(savedVoting),
				"saved voting to the following date");
		logger.info(votingPapers + "");
		addMock(votingPapers);
		Voting voting = historyController.getResult(date).getVotings().get(0);
		assertNotNull(voting, "voting for the current date");
		setState(PREPARE);
	}

	public void clean() {
		historyController.template(mongoTemplate -> {
			mongoTemplate.remove(new Query(), Voting.class, "voting");
			mongoTemplate.remove(new Query(), VotingPapers.class, "votingPapers");
			return true;
		});
	}

	public void addMock(VotingPapers votingPapers) {
		historyController.template(mongoTemplate -> {
			Date date = createDate(2000, 6, 10, 3, 25, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 23, 57, 43, 7299, 5, 67777, 14, 15, 13, 12, 11);
			date = createDate(2001, 6, 20, 4, 25, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 45, 99, 41, 7899, 5, 57777, 14, 15, 13, 12, 11);
			date = createDate(2001, 6, 20, 19, 25, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 22, 44, 46, 7899, 5, 67777, 14, 15, 13, 12, 11);
			date = createDate(2003, 7, 17, 4, 25, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 33, 55, 65, 7899, 5, 47777, 17, 18, 13, 12, 11);
			date = createDate(2003, 7, 17, 19, 25, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 12, 98, 47, 7899, 5, 77777, 17, 18, 13, 12, 11);
			date = createDate(2005, 9, 10, 4, 25, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 45, 99, 75, 7899, 5, 37777, 17, 18, 13, 12, 11);
			date = createDate(2005, 9, 10, 19, 25, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 65, 32, 48, 7899, 5, 87777, 132, 133, 123, -1, 121);
			date = createDate(2007, 11, 13, 4, 25, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 88, 67, 85, 7899, 5, 27777, 132, 133, 123, -1, 121);
			date = createDate(2008, 11, 13, 19, 25, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 76, 3, 48, 7899, 5, 97777, 132, 133, 123, -1, 121);
			date = createDate(2010, 2, 10, 4, 35, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 99, 6, 85, 7899, 5, 17777, 132, 133, 123, -1, 121);
			date = createDate(2010, 2, 10, 19, 25, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 124, 12, 85, 7899, 5, 27777, 134, 135, 123, -1, 121);
			return true;
		});
	}

	private void addVotingPaper(MongoTemplate template, VotingPapers votingPapers, Date date, int candidate1Electors,
			int candidate2Electors, int partyElectors, int groupElectors, int blankPapers, int votingPaperElectors,
			int candidate1Id, int candidate2Id, int partyId, int groupId, int votingPaperId) {

		Document document = new Document();
		document.put("id", dayFormatter.format(date));
		document.put("votingPaper", votingPapers);
		template.insert(document, "votingPapers");

		document = new Document();
		document.put("id", hourFormatter.format(date));
		Voting voting = new Voting();

		Party party = new Party();
		party.setId(partyId);
		party.setElectors(partyElectors);
		Group group = new Group();
		group.setId(groupId);
		group.setElectors(groupElectors);
		group.getMapParties().put(partyId + "", party);
		VotingPaper votingPaper = new VotingPaper();
		votingPaper.setId(votingPaperId);
		votingPaper.setBlankPapers(blankPapers);
		votingPaper.setElectors(votingPaperElectors);
		if (groupId == -1)
			votingPaper.getMapParties().put(partyId + "", party);
		if (groupId != -1)
			votingPaper.getMapGroups().put(groupId + "", group);
		voting.getMapVotingPapers().put(votingPaperId + "", votingPaper);
		voting.setAffluence(date);
		Candidate candidate1 = new Candidate();
		candidate1.setId(candidate1Id);
		candidate1.setElectors(candidate1Electors);
		party.getMapCandidates().put(candidate1Id + "", candidate1);
		Candidate candidate2 = new Candidate();
		candidate2.setId(candidate2Id);
		candidate2.setElectors(candidate2Electors);
		party.getMapCandidates().put(candidate2Id + "", candidate2);
		document.put("voting", voting);
		template.insert(document, "voting");
	}

	private Date createDate(int year, int month, int day, int hour, int minute, int second) {
		Calendar cal = getInstance();
		cal.setTimeInMillis(0);
		cal.set(year, month - 1, day, hour, minute, second);
		Date date = cal.getTime();
		return date;
	}

}
