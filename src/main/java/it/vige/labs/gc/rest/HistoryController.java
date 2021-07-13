package it.vige.labs.gc.rest;

import static it.vige.labs.gc.bean.result.Voting.fill;
import static it.vige.labs.gc.bean.votingpapers.State.VOTE;
import static it.vige.labs.gc.messages.Severity.message;
import static it.vige.labs.gc.rest.Validator.errorMessage;
import static it.vige.labs.gc.rest.Validator.ok;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.web.util.UriComponentsBuilder.newInstance;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import org.bson.Document;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiParam;
import it.vige.labs.gc.bean.result.Voting;
import it.vige.labs.gc.bean.result.Votings;
import it.vige.labs.gc.bean.votingpapers.VotingPapers;
import it.vige.labs.gc.messages.Message;
import it.vige.labs.gc.messages.Messages;

@RestController
@CrossOrigin(origins = "*")
public class HistoryController {

	@Autowired
	private MongoTemplate mongoTemplate;

	private Logger logger = getLogger(HistoryController.class);

	public final static DateFormat dayFormatter = new SimpleDateFormat("dd-MM-yyyy");

	public final static DateFormat hourFormatter = new SimpleDateFormat("dd-MM-yyyy:HH-mm-ss");

	@Autowired
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

	@GetMapping(value = "/save")
	public Messages save() {
		Date date = new Date();
		VotingPapers votingPapers = getVotingPapers();
		if (votingPapers.getState() == VOTE) {
			Voting voting = getVoting();
			voting.setAffluence(date);
			template(mongoTemplate -> {
				Document document = new Document();
				document.put("id", dayFormatter.format(date));
				document.put("votingPaper", votingPapers);
				mongoTemplate.insert(document, "votingPapers");

				document = new Document();
				document.put("id", hourFormatter.format(date));
				document.put("voting", voting);
				mongoTemplate.insert(document, "voting");
				return "";
			});
		} else
			return errorMessage;
		return new Messages(true, Arrays.asList(new Message[] { new Message(message, ok, "all is ok", date) }));
	}

	@GetMapping(value = "/votingPapers/{date}")
	public VotingPapers getVotingPapers(
			@ApiParam(value = "yyyy-MM-dd", required = true) @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		VotingPapers votingPapers = (VotingPapers) template(mongoTemplate -> {
			Query searchQuery = new Query();
			searchQuery.addCriteria(Criteria.where("id").is(dayFormatter.format(date)));
			Document found = mongoTemplate.findOne(searchQuery, Document.class, "votingPapers");
			if (found != null) {
				Document document = (Document) found.get("votingPaper");
				ObjectMapper mapper = new ObjectMapper();
				VotingPapers result = null;
				try {
					result = mapper.readValue(document.toJson(), VotingPapers.class);
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
				return result;
			} else
				return null;
		});
		return votingPapers;
	}

	@GetMapping(value = "/result/{date}")
	public Votings getResult(
			@ApiParam(value = "yyyy-MM-dd", required = true) @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		Votings voting = (Votings) template(mongoTemplate -> {
			Query searchQuery = new Query();
			searchQuery.addCriteria(Criteria.where("id").regex(dayFormatter.format(date)));
			List<Document> found = mongoTemplate.find(searchQuery, Document.class, "voting");
			if (found != null && !found.isEmpty()) {
				Votings result = new Votings();
				for (Document document : found) {
					ObjectMapper mapper = new ObjectMapper();
					try {
						@SuppressWarnings("unchecked")
						Document toAdd = ((List<Document>) ((Document) document.get("voting")).get("votings")).get(0);
						fill(toAdd);
						Voting votingToAdd = mapper.readValue(toAdd.toJson(), Voting.class);
						votingToAdd.setAffluence((Date) ((Document) document.get("voting")).get("affluence"));
						result.getVotings().add(votingToAdd);
					} catch (IOException e) {
						logger.error(e.getMessage());
					}
				}
				return result;
			} else
				return null;
		});
		return voting;
	}

	public Object template(Function<MongoTemplate, Object> function) {
		return function.apply(mongoTemplate);
	}

	private VotingPapers getVotingPapers() {
		UriComponents uriComponents = newInstance().scheme(votingpapersScheme).host(votingpapersHost)
				.port(votingpapersPort).path("/votingPapers?all=ok").buildAndExpand();

		ResponseEntity<VotingPapers> response = restTemplate.exchange(uriComponents.toString(), GET, null,
				VotingPapers.class);
		VotingPapers votingPapers = response.getBody();
		return votingPapers;
	}

	private Voting getVoting() {
		UriComponents uriComponents = newInstance().scheme(votingScheme).host(votingHost).port(votingPort)
				.path("/result").buildAndExpand();

		ResponseEntity<Voting> response = restTemplate.exchange(uriComponents.toString(), GET, null, Voting.class);
		Voting voting = response.getBody();
		return voting;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
}
