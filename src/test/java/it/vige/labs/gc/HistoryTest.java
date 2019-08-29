package it.vige.labs.gc;

import java.io.IOException;
import java.util.Arrays;
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

import it.vige.labs.gc.rest.Affluences;
import it.vige.labs.gc.rest.HistoryController;
import it.vige.labs.gc.result.Voting;
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
		Document found = (Document) historyController.execute(database -> {
			MongoCollection<Document> collection = database.getCollection("votingPapers");
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("id", 0);
			return collection.find(searchQuery).first();
		});
		Assert.assertNull("all the dates", found);

		VotingPapers votingPapers = historyController.getVotingPapers(new Date());
		Assert.assertNotNull("voting papers is saved in database after the closing in the prepare service",
				votingPapers);
		logger.info(votingPapers + "");
		Voting voting = historyController.getVoting(new Date());
		Assert.assertNull("no voting for the current date, only previous dates", voting);
		Date currentDate = new Date();
		Voting savedVoting = historyController.save();
		Assert.assertEquals("saved voting to the following date", currentDate, savedVoting.getAffluence());
		Affluences affluences = new Affluences();
		affluences.setAffluences(
				Arrays.asList(new Date[] { addHoursToDate(new Date(), 1), addHoursToDate(new Date(), 2) }));
		Affluences configuredAffluences = historyController.configure(affluences);
		Assert.assertEquals("configured two dates to save in database", affluences, configuredAffluences);
	}

	private Date addHoursToDate(Date date, int hours) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		return calendar.getTime();
	}

	public void clean() {
		historyController.execute(database -> {
			MongoCollection<Document> collection = database.getCollection("configuration");
			collection.deleteMany(new BasicDBObject());
			collection = database.getCollection("votingPapers");
			collection.deleteMany(new BasicDBObject());
			return true;
		});
	}

}
