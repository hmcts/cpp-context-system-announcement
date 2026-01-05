# cpp-context-system-announcement

**cpp-context-system-announcement** is a Java-based NON-CQRS microservice developed by HMCTS to manage and disseminate system-wide announcements within the Common Platform Programme (CPP). This service enables the creation, retrieval, updating, and deletion of contextual announcements, ensuring effective communication across various components of the CPP ecosystem.

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)
- [Acknowledgements](#acknowledgements)

## Features

- **Create Announcements**: Allows authorized users to create system-wide announcements with specific contexts.
- **Retrieve Announcements**: Provides endpoints to fetch active announcements based on various filters.
- **Update Announcements**: Enables modification of existing announcements to correct or update information.
- **Delete Announcements**: Supports the removal of announcements that are no longer relevant.
- **Scheduled Activation/Deactivation**: Facilitates setting start and end dates for announcements to automate their visibility periods.
- **Scheduled Deletion**: Supports the automated and scheduled removal of the expired announcements.

## Requirements

- **Java 17** or higher
- **Maven 3.8.x** or higher
- **PostgreSQL 13** or compatible database
- **Docker** (for containerized deployment)

## Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/hmcts/cpp-context-system-announcement.git
   cd cpp-context-system-announcement
   ```

2. **Build the Application**:
   ```bash
   mvn clean install
   ```

## Usage

### Running Locally

To run the application locally:

See `buildDeployAndTest` in `runIntegrationTests.sh` for details

The service will start and be accessible at `http://localhost:8080/systemannouncement-service/rest/systemannouncement`.

### Running with Docker

1. **Build the Docker Image**:

See `buildDeployAndTest` in `runIntegrationTests.sh` for details


2. **Run the Docker Container**:
   
See `buildDeployAndTest` in `runIntegrationTests.sh` for details

## API Endpoints

The service exposes RESTful endpoints for managing system announcements. The following are the available endpoints:

Base URI : http://localhost:8080/systemannouncement-service/rest/systemannouncement

```markdown
| Method | Endpoint                | Description                      | Media Type                                                   |
|--------|-------------------------|----------------------------------|--------------------------------------------------------------|
| GET    | /announcements          | List all announcements           | application/vnd.systemannouncement.get-all-announcements+json |
| GET    | /announcements          | Get banner announcements         | application/vnd.systemannouncement.get-banner-announcements+json |
| POST   | /announcement/{id}      | Update specific announcement     | application/vnd.systemannouncement.update-system-announcement+json |
| POST   | /announcement/{id}      | Delete specific announcement     | application/vnd.systemannouncement.delete-system-announcement-by-id+json |
| POST   | /announcement           | Create new announcement          | application/vnd.systemannouncement.create-system-announcement+json |
| POST   | /announcement           | Delete expired announcement      | application/vnd.systemannouncement.delete-expired-system-announcements+json |
```
see `systemannouncement-api.raml` for more details.

## Testing

 ```bash
   mvn clean verify
   ```

To be updated later

Ensure that all tests pass successfully before deploying the application.

## Contributing

Contributions to the **cpp-context-system-announcement** service are welcome. To contribute:

1. Fork the repository.
2. Create a new branch for your feature or bugfix.
3. Commit your changes with clear commit messages.
4. Push your branch and create a pull request against the `main` branch.

Please adhere to the project's coding standards and include appropriate tests with your contributions.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgements

We extend our gratitude to all contributors and the HMCTS development team for their efforts in building and maintaining this service.

---

