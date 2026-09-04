<div style="text-align:right"><img src="https://raw.githubusercontent.com/gematik/gematik.github.io/master/Gematik_Logo_Flag_With_Background.png" width="250" height="47" alt="gematik GmbH Logo"/> <br/> </div> <br/>    

# Release Notes IGS-Gateway

## Release 1.5.0
- upgraded to Spring Boot 4
- optimized custom environment variables handling in helm chart
- updated docker base image to gematik1/osadl-alpine-openjdk25-jre:1.0.7
- Replaced pod anti-affinity with topology spread constraints for pod distribution
- arranged jvm options and resource limits
- fixed handling of falsy custom environment variables (false, 0) in helm chart
- updated spring parent containing the latest notification builder library (9.3.0) which supports igs profile 5.0.1
- added VEX documents to repository
- updated spring-parent 4.1.8

## Release 1.4.0 
- updated base-image and updated from java 21 to java 25
- changed garbage collector to G1GC
- added usage of header forwarding interceptor from service base
- when invoking the IGS service the content-type and accept type have been restricted to application/fhir+json

## Release 1.3.2
- Removed istio helm chart
- Removed feature-flag NEW_API_ENDPOINTS

## Release 1.3.1
- Update spring-parent

## Release 1.3.0
- Support for strict profiles added

## Release 1.2.2
- Invalid input data for host birthday or sex will be handled with a proper status code and error message
- Updated spring-parent

## Release 1.2.1
- Add extra header for new FUTS API endpoints
- Added extra headers for Validation Service requests
- Updated dependencies

## Release 1.2.0
- Update to Spring-Parent 2.12.12
- add support for new FUTS API Endpoints
- change default replica to 2
- implemented centralized message service
- improved error handling for upload CSV
- use x-fhir-profile Header for FUTS Request with new API Endpoints enabled
- Updated dependencies

## Release 1.1.4
- Updated ospo-resources for adding additional notes and disclaimer
- setting new ressources in helm chart
- setting new timeouts and retries in helm chart
- change base chart to istio hostnames
- updating dependencies

## Release 1.1.1
- First official GitHub-Release
