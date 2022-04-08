package org.dgonzalo.headlessbroadcaster;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class HeadlessBroadcasterController {

	private static final String QUERY_STRING_SEPARATOR = "?";

	@RequestMapping(value = { "/headless-broadcaster/{service}:{port}/**",
			"/headless-broadcaster/{service}/**" }, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> forward(ProxyExchange<String> proxy, @PathVariable String service,
			@PathVariable(required = false) String port, HttpServletRequest request) throws Exception {

		int thirdSlash = ordinalIndexOf(request.getRequestURI(), "/", 3);
		String restOfUrl = thirdSlash == -1 ? ""
				: request.getRequestURI().substring(thirdSlash)
						+ (request.getQueryString().isEmpty() ? "" : QUERY_STRING_SEPARATOR + request.getQueryString());
		Map<String, Object> result = new HashMap<>();
		ResponseEntity<String> latestResponse = null;
		for (InetAddress iNetAddress : InetAddress.getAllByName(service)) {
			String destinationUrl = "http://" + iNetAddress.getHostAddress() + ":" + (port == null ? "80" : port)
					+ restOfUrl;
			log.info("Forwarding to " + destinationUrl);
			try {
				latestResponse = proxy.uri(destinationUrl).get();
				log.info("Got return code " + latestResponse.getStatusCodeValue());
				result.put(destinationUrl, latestResponse.getBody());
			} catch (Exception e) {
				log.error("Error forwarding request: " + e.toString());
				result.put(destinationUrl, e.toString());
			}
		}

		return result;
	}

	@GetMapping(value = { "/dns-check/{service}:{port}/**",
			"/dns-check/{service}/**" }, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> check(ProxyExchange<Object> proxy, @PathVariable String service,
			@PathVariable(required = false) String port) throws Exception {

		log.info("Getting DNS info for service " + service);
		List<String> addresses = new ArrayList<>();
		Arrays.stream(InetAddress.getAllByName(service)).forEach(a -> addresses.add(a.getHostAddress() + " "));
		log.info("DNS info for service " + service + " is : " + addresses);
		Map<String, Object> result = new HashMap<>(1);
		result.put(service, addresses);
		return result;
	}

	@GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
	public String hello() {
		return ("Well, hello there!");
	}

	private int ordinalIndexOf(String str, String substr, int n) {
		int pos = str.indexOf(substr);
		while (--n > 0 && pos != -1)
			pos = str.indexOf(substr, pos + 1);
		return pos;
	}

}