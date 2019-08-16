package it.vige.labs.gc;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import it.vige.labs.gc.history.VotingPaper;
import it.vige.labs.gc.history.VotingPapers;
import it.vige.labs.gc.rest.HistoryController;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VotingPaperTest {

	private Logger logger = LoggerFactory.getLogger(VotingPaperTest.class);

	@Autowired
	private HistoryController votingPaperController;

	@Test
	public void votingPaper() throws IOException {
		VotingPapers votingPapers = votingPaperController.getVotingPapers();
		List<VotingPaper> list = votingPapers.getVotingPapers();
		Assert.assertEquals("is admin", true, votingPapers.isAdmin());
		Assert.assertEquals("size ok", 4, list.size());
		logger.info(list + "");
	}

}
