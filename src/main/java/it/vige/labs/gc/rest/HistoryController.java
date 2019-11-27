package it.vige.labs.gc.rest;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.vige.labs.gc.result.Voting;
import it.vige.labs.gc.result.Votings;
import it.vige.labs.gc.votingpapers.VotingPapers;

@RestController
@CrossOrigin(origins = "*")
public class HistoryController {

	@Autowired
	private MongoTemplate mongoTemplate;

	private Logger logger = LoggerFactory.getLogger(HistoryController.class);

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
	public Date save() {
		Date date = new Date();
		Voting voting = getVoting();
		voting.setAffluence(date);
		template(mongoTemplate -> {
			Document document = new Document();
			document.put("id", dayFormatter.format(date));
			document.put("votingPaper", getVotingPapers());
			mongoTemplate.insert(document, "votingPapers");

			document = new Document();
			document.put("id", hourFormatter.format(date));
			document.put("voting", voting);
			mongoTemplate.insert(document, "voting");
			return "";
		});
		return date;
	}

	@GetMapping(value = "/votingPapers/{date}")
	public VotingPapers getVotingPapers(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
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
	public Votings getResult(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
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
						Voting.fill(toAdd);
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
		UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme(votingpapersScheme)
				.host(votingpapersHost).port(votingpapersPort).path("/votingPapers").buildAndExpand();

		ResponseEntity<VotingPapers> response = restTemplate.exchange(uriComponents.toString(), HttpMethod.GET, null,
				VotingPapers.class);
		VotingPapers votingPapers = response.getBody();
		return votingPapers;
	}

	private Voting getVoting() {
		UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme(votingScheme).host(votingHost)
				.port(votingPort).path("/result").buildAndExpand();

		ResponseEntity<Voting> response = restTemplate.exchange(uriComponents.toString(), HttpMethod.GET, null,
				Voting.class);
		Voting voting = response.getBody();
		return voting;
	}
}
