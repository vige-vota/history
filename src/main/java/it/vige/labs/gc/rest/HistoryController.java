package it.vige.labs.gc.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

	@GetMapping(value = "/prepare")
	public boolean prepare(@RequestParam boolean start) {
		return start;
	}

	@PostMapping(value = "/vote")
	public StartVote vote(@RequestBody StartVote startVote) {
		return startVote;
	}

	@GetMapping(value = "/save")
	public Voting save() {
		Voting voting = new Voting();
		voting.setAffluence(new Date());
		return voting;
	}

	@PostMapping(value = "/configure")
	public Affluences configure(Affluences affluences) {
		execute(database -> {
			MongoCollection<Document> collection = database.getCollection("affluences");
			for (Date date : affluences.getAffluences()) {
				Document document = new Document();
				document.put("date", date);
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
			MongoCollection<Document> collection = database.getCollection("affluences");
			List<Date> list = new ArrayList<Date>();
			Iterator<Document> found = collection.find().iterator();
			while (found.hasNext()) {
				list.add((Date) found.next().get("date"));
			}
			return list;
		});
		logger.info(dates + "");
		return new VotingPapers();
	}

	@GetMapping(value = "/getVoting")
	public Voting getVoting(@RequestParam Date date) {
		return null;
	}

	private Object execute(Function<MongoDatabase, Object> function) {
		try (MongoClient mongoClient = new MongoClient(DB_HOST, DB_PORT)) {
			MongoDatabase database = mongoClient.getDatabase("history");
			return function.apply(database);
		}
	}
}
