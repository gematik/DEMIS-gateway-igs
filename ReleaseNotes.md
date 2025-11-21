<img align="right" alt="gematik" width="250" height="47" src="media/Gematik_Logo_Flag.png"/> <br/>    

# Release Notes IGS-Gateway

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
