Creates a single spring boot rest controller with 2 endpoints

/headless-broadcaster/{service}:{port}/**

This broadcasts requests to every single pod behind the headless service named in the first part of the URL. Thus /headless-broadcaster/petclinicheadless:8080/actuator/shutdown would send the same request to the endpoint /actuator/shutdown to every pod belonging to the petclinicheadless headless service

/dns-check/{service}:{port}/**

This will return the list of internal IPs registered to the headless service just for debugging purposes