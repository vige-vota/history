package it.vige.labs.gc;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import it.vige.labs.gc.rest.HistoryController;
import it.vige.labs.gc.result.Candidate;
import it.vige.labs.gc.result.Group;
import it.vige.labs.gc.result.Party;
import it.vige.labs.gc.result.Voting;
import it.vige.labs.gc.result.VotingPaper;
import it.vige.labs.gc.votingpapers.VotingPapers;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HistoryTest {

	private Logger logger = LoggerFactory.getLogger(HistoryTest.class);

	@Autowired
	private HistoryController historyController;

	@Test
	public void history() throws IOException {
		clean();
		Date date = new Date();
		Document found = (Document) historyController.execute(database -> {
			MongoCollection<Document> collection = database.getCollection("votingPapers");
			return collection.find().first();
		});
		Assert.assertNull("is all cleaned", found);

		Date savedVoting = historyController.save();
		VotingPapers votingPapers = historyController.getVotingPapers(date);
		Assert.assertNotNull("voting papers is saved", votingPapers);
		Assert.assertEquals("saved voting to the following date", HistoryController.hourFormatter.format(date),
				HistoryController.hourFormatter.format(savedVoting));
		logger.info(votingPapers + "");
		addMock(votingPapers);
		Voting voting = historyController.getVoting(date);
		Assert.assertNotNull("voting for the current date", voting);
	}

	public void clean() {
		historyController.execute(database -> {
			MongoCollection<Document> collection = database.getCollection("voting");
			collection.deleteMany(new BasicDBObject());
			collection = database.getCollection("votingPapers");
			collection.deleteMany(new BasicDBObject());
			return true;
		});
	}

	public void addMock(VotingPapers votingPapers) {
		historyController.execute(database -> {
			Date date = createDate(2000, 5, 10, 3, 25, 45);
			addVotingPaper(database, votingPapers, date, 23, 57, 43, 7299, 5, 67777, 1, 2, 67, 777, 12);
			date = createDate(2001, 5, 20, 4, 25, 45);
			addVotingPaper(database, votingPapers, date, 45, 99, 41, 7899, 5, 57777, 1, 2, 67, 7771, 121);
			date = createDate(2001, 5, 20, 19, 25, 45);
			addVotingPaper(database, votingPapers, date, 22, 44, 46, 7899, 5, 67777, 1, 2, 68, 777, 122);
			date = createDate(2003, 6, 17, 4, 25, 45);
			addVotingPaper(database, votingPapers, date, 33, 55, 65, 7899, 5, 47777, 1, 2, 67, 7772, 12);
			date = createDate(2003, 6, 17, 19, 25, 45);
			addVotingPaper(database, votingPapers, date, 12, 98, 47, 7899, 5, 77777, 1, 2, 69, 777, 123);
			date = createDate(2005, 8, 10, 4, 25, 45);
			addVotingPaper(database, votingPapers, date, 45, 99, 75, 7899, 5, 37777, 1, 2, 70, 7773, 12);
			date = createDate(2005, 8, 10, 19, 25, 45);
			addVotingPaper(database, votingPapers, date, 65, 32, 48, 7899, 5, 87777, 1, 2, 71, 777, 124);
			date = createDate(2007, 10, 13, 4, 25, 45);
			addVotingPaper(database, votingPapers, date, 88, 67, 85, 7899, 5, 27777, 1, 2, 72, 7774, 125);
			date = createDate(2008, 10, 13, 19, 25, 45);
			addVotingPaper(database, votingPapers, date, 76, 3, 48, 7899, 5, 97777, 1, 2, 73, 7777, 126);
			date = createDate(2010, 1, 10, 4, 35, 45);
			addVotingPaper(database, votingPapers, date, 99, 6, 85, 7899, 5, 17777, 1, 2, 74, 777, 127);
			date = createDate(2010, 1, 10, 19, 25, 45);
			addVotingPaper(database, votingPapers, date, 124, 12, 85, 7899, 5, 27777, 1, 2, 75, 7774, 129);
			return true;
		});
	}

	private void addVotingPaper(MongoDatabase database, VotingPapers votingPapers, Date date, int candidate1Electors,
			int candidate2Electors, int partyElectors, int groupElectors, int blankPapers, int votingPaperElectors,
			int candidates1Id, int candidates2Id, int partiesId, int groupsId, int votingPapersId) {

		MongoCollection<Document> collection = database.getCollection("votingPapers");
		Document document = new Document();
		document.put("id", HistoryController.dayFormatter.format(date));
		document.put("votingPaper", votingPapers);
		collection.insertOne(document);

		collection = database.getCollection("voting");
		document = new Document();
		document.put("id", date);
		Voting voting = new Voting();

		Party party = new Party();
		party.setElectors(partyElectors);
		Group group = new Group();
		group.setElectors(groupElectors);
		VotingPaper votingPaper = new VotingPaper();
		votingPaper.setBlankPapers(blankPapers);
		votingPaper.setElectors(votingPaperElectors);
		votingPaper.getMapParties().put(partiesId, party);
		votingPaper.getMapGroups().put(groupsId, group);
		voting.getMapVotingPapers().put(votingPapersId, votingPaper);
		Candidate candidate1 = new Candidate();
		candidate1.setElectors(candidate1Electors);
		party.getMapCandidates().put(candidates1Id, candidate1);
		Candidate candidate2 = new Candidate();
		candidate2.setElectors(candidate2Electors);
		party.getMapCandidates().put(candidates2Id, candidate2);
		document.put("voting", voting);
		collection.insertOne(document);
	}

	private Date createDate(int year, int month, int day, int hour, int minute, int second) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(year, month, day, hour, minute, second);
		Date date = cal.getTime();
		return date;
	}

}
