package it.vige.labs.gc.rest;

import java.util.Arrays;

import org.springframework.stereotype.Component;

import it.vige.labs.gc.messages.Message;
import it.vige.labs.gc.messages.Messages;
import it.vige.labs.gc.messages.Severity;

@Component
public class Validator {

	public final static String ok = "ok";

	public final static Messages errorMessage = new Messages(false,
			Arrays.asList(new Message[] { new Message(Severity.error, "Generic error", "Validation not ok") }));
}
