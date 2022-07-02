package it.vige.labs.gc.rest;

import static it.vige.labs.gc.rest.HistoryController.hourFormatter;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.vige.labs.gc.bean.result.Voting;
import it.vige.labs.gc.bean.votingpapers.VotingPapers;

@Component
public class InitData {

	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	@Lazy
	private HistoryController historyController;

	private void save(String date, String hour, String votingJsonFile) throws Exception {
		String monthDate = date.substring(3, date.length());
		Date formattedDate = hourFormatter.parse(date + ":" + hour);
		if (historyController.getResult(formattedDate, hourFormatter) == null) {
			InputStream votingPapersStream = new FileInputStream(
					"src/test/resources/mock/history/" + monthDate + "/votingpapers.json");
			VotingPapers votingPapersFromJson = objectMapper.readValue(votingPapersStream, VotingPapers.class);
			InputStream votingStream = new FileInputStream(
					"src/test/resources/mock/history/" + monthDate + "/" + date + "/" + votingJsonFile);
			Voting votingFromJson = objectMapper.readValue(votingStream, Voting.class);
			historyController.save(formattedDate, votingPapersFromJson, votingFromJson);
		}
	}

	void start() throws Exception {

		save("11-05-1999", "11-06-12", "voting_1.json");
		save("11-05-1999", "14-01-12", "voting_2.json");
		save("11-05-1999", "18-06-12", "voting_3.json");
		save("11-05-1999", "20-03-17", "voting_4.json");
		save("12-05-1999", "11-06-12", "voting_1.json");
		save("12-05-1999", "14-00-00", "voting_2.json");

		save("13-04-2001", "11-06-12", "voting_1.json");
		save("13-04-2001", "14-01-12", "voting_2.json");
		save("13-04-2001", "18-06-12", "voting_3.json");
		save("13-04-2001", "20-03-17", "voting_4.json");

		save("18-12-2010", "12-05-12", "voting_1.json");
		save("18-12-2010", "15-01-12", "voting_2.json");
		save("18-12-2010", "18-09-12", "voting_3.json");
		save("18-12-2010", "21-03-17", "voting_4.json");
		save("19-12-2010", "11-00-12", "voting_1.json");
		save("19-12-2010", "13-00-00", "voting_2.json");

		save("19-10-2021", "11-46-12", "voting_1.json");
		save("19-10-2021", "14-41-12", "voting_2.json");
		save("19-10-2021", "18-02-12", "voting_3.json");
		save("19-10-2021", "21-03-17", "voting_4.json");
		save("19-10-2021", "12-02-12", "voting_1.json");
		save("19-10-2021", "15-00-17", "voting_2.json");
	}
}
