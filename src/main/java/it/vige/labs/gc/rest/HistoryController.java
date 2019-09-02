package it.vige.labs.gc.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Function;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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

	private String DB_HOST = "localhost";
	private int DB_PORT = 27017;

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
		Voting voting = new Voting();
		voting.setAffluence(date);
		execute(database -> {
			MongoCollection<Document> collection = database.getCollection("votingPapers");
			Document document = new Document();
			document.put("id", dayFormatter.format(date));
			document.put("votingPaper", getVotingPapers());
			collection.insertOne(document);

			collection = database.getCollection("voting");
			document = new Document();
			document.put("id", hourFormatter.format(date));
			document.put("voting", getVoting());
			collection.insertOne(document);
			return "";
		});
		return date;
	}

	@GetMapping(value = "/getVotingPapers")
	public VotingPapers getVotingPapers(@RequestParam Date date) {
		Document votingPapers = (Document)execute(database -> {
			MongoCollection<Document> collection = database.getCollection("votingPapers");
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("id", dayFormatter.format(date));
			Document found = collection.find(searchQuery).first();
			if (found != null)
				return found.get("votingPaper");
			else
				return null;
		});
		return new VotingPapers(votingPapers);
	}

	@GetMapping(value = "/getVoting")
	public Voting getVoting(@RequestParam Date date) {
		Document voting = (Document) execute(database -> {
			MongoCollection<Document> collection = database.getCollection("voting");
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("id", hourFormatter.format(date));
			Document found = collection.find(searchQuery).first();
			if (found != null)
				return found.get("voting");
			else
				return null;
		});
		return new Voting(voting);
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

	private Voting getVoting() {
		UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme(votingScheme).host(votingHost)
				.port(votingPort).path("/result").buildAndExpand();

		ResponseEntity<Voting> response = restTemplate.exchange(uriComponents.toString(), HttpMethod.GET, null,
				Voting.class);
		Voting voting = response.getBody();
		return voting;
	}
}
