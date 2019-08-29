package it.vige.labs.gc.rest;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import it.vige.labs.gc.result.Voting;
import it.vige.labs.gc.votingpapers.VotingPapers;

@RestController
@CrossOrigin(origins = "*")
public class HistoryController {

	private Logger logger = LoggerFactory.getLogger(HistoryController.class);

	private String DB_HOST = "localhost";
	private int DB_PORT = 27017;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${votingpapers.scheme}")
	private String votingpapersScheme;

	@Value("${votingpapers.host}")
	private String votingpapersHost;

	@Value("${votingpapers.port}")
	private int votingpapersPort;

	@GetMapping(value = "/save")
	public Voting save() {
		Voting voting = new Voting();
		voting.setAffluence(new Date());
		return voting;
	}

	@PostMapping(value = "/configure")
	public Affluences configure(Affluences affluences) {
		execute(database -> {
			MongoCollection<Document> collection = database.getCollection("configuration");
			for (Date date : affluences.getAffluences()) {
				Document document = new Document();
				document.put("affluence", date);
				collection.insertOne(document);
			}
			return "";
		});
		return affluences;
	}

	@GetMapping(value = "/getVotingPapers")
	public VotingPapers getVotingPapers(@RequestParam Date date) {
		@SuppressWarnings("unchecked")
		List<Date> dates = (List<Date>) execute(database -> {
			MongoCollection<Document> collection = database.getCollection("votingPapers");
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("date", date);
			Document found = collection.find(searchQuery).first();
			return found.get("value");
		});
		logger.info(dates + "");
		return new VotingPapers();
	}

	@GetMapping(value = "/getVoting")
	public Voting getVoting(@RequestParam Date date) {
		VotingPapers votingPapers = getVotingPapers();
		logger.info(votingPapers + "");
		return null;
	}

	public Object execute(Function<MongoDatabase, Object> function) {
		try (MongoClient mongoClient = new MongoClient(DB_HOST, DB_PORT)) {
			MongoDatabase database = mongoClient.getDatabase("history");
			return function.apply(database);
		}
	}

	private VotingPapers getVotingPapers() {
		UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme(votingpapersScheme)
				.host(votingpapersHost).port(votingpapersPort).path("/votingPapers").buildAndExpand();

		ResponseEntity<VotingPapers> response = restTemplate.exchange(uriComponents.toString(), HttpMethod.GET, null,
				VotingPapers.class);
		VotingPapers votingPapers = response.getBody();
		return votingPapers;
	}
}
