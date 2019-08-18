package it.vige.labs.gc.rest;

import java.util.Date;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.vige.labs.gc.result.Voting;
import it.vige.labs.gc.votingpapers.VotingPapers;

@RestController
@CrossOrigin(origins = "*")
public class HistoryController {

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
		return null;
	}

	@PostMapping(value = "/configure")
	public Affluences configure(Affluences affluences) {
		return affluences;
	}

	@GetMapping(value = "/getVotingPapers")
	public VotingPapers getVotingPapers(@RequestParam Date date) {
		return null;
	}

	@GetMapping(value = "/getVoting")
	public Voting getVoting(@RequestParam Date date) {
		return null;
	}
}
