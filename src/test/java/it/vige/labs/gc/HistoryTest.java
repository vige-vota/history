package it.vige.labs.gc;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import it.vige.labs.gc.rest.HistoryController;
import it.vige.labs.gc.votingpapers.VotingPaper;
import it.vige.labs.gc.votingpapers.VotingPapers;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HistoryTest {

	private Logger logger = LoggerFactory.getLogger(HistoryTest.class);

	@Autowired
	private HistoryController historyController;

	@Test
	public void history() throws IOException {
		VotingPapers votingPapers = historyController.getVotingPapers(new Date());
		List<VotingPaper> list = votingPapers.getVotingPapers();
		Assert.assertEquals("is admin", true, votingPapers.isAdmin());
		Assert.assertEquals("size ok", 4, list.size());
		logger.info(list + "");
	}

}
