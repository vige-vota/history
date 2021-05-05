package it.vige.labs.gc;

import static it.vige.labs.gc.bean.votingpapers.State.PREPARE;
import static it.vige.labs.gc.bean.votingpapers.State.VOTE;
import static it.vige.labs.gc.rest.HistoryController.dayFormatter;
import static it.vige.labs.gc.rest.HistoryController.hourFormatter;
import static java.util.Calendar.getInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.util.UriComponentsBuilder.newInstance;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.vige.labs.gc.bean.result.Candidate;
import it.vige.labs.gc.bean.result.Group;
import it.vige.labs.gc.bean.result.Party;
import it.vige.labs.gc.bean.result.Voting;
import it.vige.labs.gc.bean.result.VotingPaper;
import it.vige.labs.gc.bean.votingpapers.VotingPapers;
import it.vige.labs.gc.rest.HistoryController;

@SpringBootTest
@ActiveProfiles("dev")
public class HistoryTest {

	private Logger logger = getLogger(HistoryTest.class);

	private final static DateFormat minuteFormatter = new SimpleDateFormat("dd-MM-yyyy:HH-mm");

	private Date date;

	@Autowired
	private HistoryController historyController;

	@Mock
	private RestTemplate restTemplate;

	@Value("${votingpapers.scheme}")
	private String votingpapersScheme;

	@Value("${votingpapers.host}")
	private String votingpapersHost;

	@Value("${votingpapers.port}")
	private int votingpapersPort;

	@Value("${voting.scheme}")
	private String votingScheme;

	@Value("${voting.host}")
	private String votingHost;

	@Value("${voting.port}")
	private int votingPort;

	@Mock
	private MongoTemplate mongoTemplate;

	private it.vige.labs.gc.bean.votingpapers.VotingPapers votingPapers;

	@BeforeEach
	public void init() throws Exception {
		mock();
	}

	@Test
	public void history() throws IOException {
		votingPapers.setState(PREPARE);
		Date savedVoting = historyController.save().getMessages().get(0).getDate();
		assertNull(savedVoting, "PREPARE state denies the save. No votes saved");
		votingPapers.setState(VOTE);
		savedVoting = historyController.save().getMessages().get(0).getDate();
		VotingPapers votingPapers = historyController.getVotingPapers(date);
		assertNotNull(votingPapers, "voting papers is saved");
		assertEquals(minuteFormatter.format(date), minuteFormatter.format(savedVoting),
				"saved voting to the following date");
		logger.info(votingPapers + "");
		addMock(votingPapers);
		Voting voting = historyController.getResult(date).getVotings().get(0);
		assertNotNull(voting, "voting for the current date");
		votingPapers.setState(PREPARE);
	}

	public void addMock(VotingPapers votingPapers) {
		historyController.template(mongoTemplate -> {
			Date date = createDate(2000, 6, 10, 3, 25, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 23, 57, 43, 7299, 5, 67777, 14, 15, 13, 12, 11, 2523228);
			date = createDate(2001, 6, 20, 4, 25, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 45, 99, 41, 7899, 5, 57777, 14, 15, 13, 12, 11, 2523228);
			date = createDate(2001, 6, 20, 19, 25, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 22, 44, 46, 7899, 5, 67777, 14, 15, 13, 12, 11, 2523228);
			date = createDate(2003, 7, 17, 4, 25, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 33, 55, 65, 7899, 5, 47777, 17, 18, 13, 12, 11, 2523228);
			date = createDate(2003, 7, 17, 19, 25, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 12, 98, 47, 7899, 5, 77777, 17, 18, 13, 12, 11, 2523228);
			date = createDate(2005, 9, 10, 4, 25, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 45, 99, 75, 7899, 5, 37777, 17, 18, 13, 12, 11, 2523228);
			date = createDate(2005, 9, 10, 19, 25, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 65, 32, 48, 7899, 5, 87777, 132, 133, 123, -1, 121, -1);
			date = createDate(2007, 11, 13, 4, 25, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 88, 67, 85, 7899, 5, 27777, 132, 133, 123, -1, 121, -1);
			date = createDate(2008, 11, 13, 19, 25, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 76, 3, 48, 7899, 5, 97777, 132, 133, 123, -1, 121, -1);
			date = createDate(2010, 2, 10, 4, 35, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 99, 6, 85, 7899, 5, 17777, 132, 133, 123, -1, 121, -1);
			date = createDate(2010, 2, 10, 19, 25, 45);
			addVotingPaper(mongoTemplate, votingPapers, date, 124, 12, 85, 7899, 5, 27777, 134, 135, 123, -1, 121, -1);
			return true;
		});
	}

	private void addVotingPaper(MongoTemplate template, VotingPapers votingPapers, Date date, int candidate1Electors,
			int candidate2Electors, int partyElectors, int groupElectors, int blankPapers, int votingPaperElectors,
			int candidate1Id, int candidate2Id, int partyId, int groupId, int votingPaperId, int zone) {

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

	private void mock() throws Exception {
		date = new Date();
		ObjectMapper objectMapper = new ObjectMapper();
		InputStream jsonStream = new FileInputStream("src/test/resources/mock/votingpapers.json");
		votingPapers = objectMapper.readValue(jsonStream, it.vige.labs.gc.bean.votingpapers.VotingPapers.class);
		jsonStream = new FileInputStream("src/test/resources/mock/voting.json");
		Voting voting = objectMapper.readValue(jsonStream, Voting.class);

		String urlVotingPapers = newInstance().scheme(votingpapersScheme).host(votingpapersHost).port(votingpapersPort)
				.path("/votingPapers").buildAndExpand().toString();
		String votingUrl = newInstance().scheme(votingScheme).host(votingHost).port(votingPort).path("/result")
				.buildAndExpand().toString();

		when(restTemplate.exchange(urlVotingPapers, GET, null, it.vige.labs.gc.bean.votingpapers.VotingPapers.class))
				.thenReturn(new ResponseEntity<it.vige.labs.gc.bean.votingpapers.VotingPapers>(votingPapers, OK));
		when(restTemplate.exchange(votingUrl, GET, null, Voting.class))
				.thenReturn(new ResponseEntity<Voting>(voting, OK));
		Query searchQueryVotingPapers = new Query();
		Query searchQueryVotings = new Query();
		String formattedDate = dayFormatter.format(date);
		String formattedHour = hourFormatter.format(date);
		searchQueryVotingPapers.addCriteria(Criteria.where("id").is(formattedDate));
		searchQueryVotings.addCriteria(Criteria.where("id").regex(formattedDate));
		Document documentVotingPapers = new Document();
		documentVotingPapers.put("id", formattedDate);
		documentVotingPapers.put("votingPaper", new Document());
		when(mongoTemplate.findOne(searchQueryVotingPapers, Document.class, "votingPapers"))
				.thenReturn(documentVotingPapers);
		Document documentVotings = new Document();
		documentVotings.put("id", formattedHour);
		Document documentVoting = new Document();
		documentVotings.put("voting", documentVoting);
		List<Document> votings = Arrays.<Document>asList(new Document());
		documentVoting.put("votings", votings);
		when(mongoTemplate.find(searchQueryVotings, Document.class, "voting"))
				.thenReturn(Arrays.<Document>asList(documentVotings));
		historyController.setRestTemplate(restTemplate);
		historyController.setMongoTemplate(mongoTemplate);
	}

}
