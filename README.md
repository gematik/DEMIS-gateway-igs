<img align="right" width="250" height="47" src="media/Gematik_Logo_Flag.png"/> <br/>


# Integrierte Genomic Surveillance Service Gateway (IGS-Gateway)

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
       <ul>
        <li><a href="#quality-gate">Quality Gate</a></li>
        <li><a href="#release-notes">Release Notes</a></li>
      </ul>
	</li>
    <li>
      <a href="#getting-started">Getting Started</a>
    </li>
    <li>
      <a href="#usage">Usage</a>
      <ul>
        <li><a href="#endpoints">Endpoints</a></li>
      </ul>
    </li>
    <li><a href="#security-policy">Security Policy</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

## About The Project

The IGS-Gateway serves as a backend-for-frontend (BFF), fulfilling the purpose of keeping the frontend code leaner and
simpler by handling complex processing tasks on the backend.

It is a Spring Boot application designed for processing and validating CSV data related to Integrated Genomic 
Surveillance (IGS). It features comprehensive CSV processing, validation, and error handling capabilities. Additionally,
the service generates the necessary FHIR resources (such as DocumentReference) that are later utilized by the 
IGS-Service. It also transmits sequence bundles to DEMIS Core.

The project is built using Java, Spring Boot, and Maven and is licensed under the EUPL v1.2.

### Quality Gate
[![Quality Gate Status](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Aigs-gateway&metric=alert_status&token=sqb_7f3c47939f9bb3c7c62b06de9d277bb05431294c)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Aigs-gateway)
[![Vulnerabilities](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Aigs-gateway&metric=vulnerabilities&token=sqb_7f3c47939f9bb3c7c62b06de9d277bb05431294c)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Aigs-gateway)
[![Bugs](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Aigs-gateway&metric=bugs&token=sqb_7f3c47939f9bb3c7c62b06de9d277bb05431294c)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Aigs-gateway)
[![Code Smells](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Aigs-gateway&metric=code_smells&token=sqb_7f3c47939f9bb3c7c62b06de9d277bb05431294c)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Aigs-gateway)
[![Lines of Code](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Aigs-gateway&metric=ncloc&token=sqb_7f3c47939f9bb3c7c62b06de9d277bb05431294c)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Aigs-gateway)
[![Coverage](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Aigs-gateway&metric=coverage&token=sqb_7f3c47939f9bb3c7c62b06de9d277bb05431294c)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Aigs-gateway)

[![Quality gate](https://sonar.prod.ccs.gematik.solutions/api/project_badges/quality_gate?project=de.gematik.demis%3Aigs-gateway&token=sqb_7f3c47939f9bb3c7c62b06de9d277bb05431294c)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Aigs-gateway)

### Release Notes
See [ReleaseNotes](ReleaseNotes.md) for all information regarding the (newest) releases.

## Getting Started
The application can be built from a mvn command file or as a Docker Image.
```sh
mvn clean verify
```

The docker image can be built with the following command:
```docker
docker build -t igs-gateway:latest .
```

The image can alternatively also be built with maven:
```sh
mvn -e clean install -Pdocker
```

## Usage
The application can be started as Docker container with the following commands:
```shell
docker run --rm --name igs-gateway -p 8080:8080 igs-gateway:latest
```

### Endpoints
| HTTP Method | Endpoint                                      | Description                                                                                                                   |
|-------------|-----------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------|
| POST        | `/csv/upload`                                 | This endpoint is used to upload a CSV file, process its content, and return a structured response containing the parsed data. |
| POST        | `/document-reference`                         | Create a FHIR resource and sends it to the IGS Service                                                                        |
| POST        | `/notification-sequence/$process-notification-sequence` | Sends a sequence notification to DEMIS core                                                                                   |

## Security Policy
If you want to see the security policy, please check our [SECURITY.md](.github/SECURITY.md).

## Contributing
If you want to contribute, please check our [CONTRIBUTING.md](.github/CONTRIBUTING.md).

## License
EUROPEAN UNION PUBLIC LICENCE v. 1.2

EUPL © the European Union 2007, 2016

Copyright (c) 2025 gematik GmbH

See [LICENSE](LICENSE.md).

## Contact
E-Mail to [DEMIS Entwicklung](mailto:demis-entwicklung@gematik.de?subject=[GitHub]%20IGS-Service)
