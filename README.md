# Postable Remittance

_The Da Vinci Postable Remittance Reference Implementation Microservice_

Continuous development build URL: https://build.fhir.org/ig/HL7/davinci-pr/index.html
Official URL: http://hl7.org/fhir/us/davinci-pr/ImplementationGuide/hl7.fhir.us.davinci-pr

---

## Local Setup

### Requirements

- Java version: 17
- Maven version: 3.9.7 (3.6.3+)
- Docker/Docker Desktop (optional but helpful)

### Create docker containers

Create docker containers for postgresql and microservice using docker-compose.yaml file

- Navigate to the docker-compose directory
- Run `sh ./up.sh` to create the container
    - Note you may need to run `chmod 777 *.sh` to make the scripts executable.
    - This only needs to be done the first time to create it
    - You may use Docker Desktop to manage it afterward.
    - You should see containers named `davinci-pr-postgres` and `davinci-pr-service` running.
    - If your container for service is running old jar, run `sh ./build-up.sh` file to update image and run.

#### Other handy commands

- `sh ./build-up.sh` will rebuild the docker image and run if you update any file
- `sh ./stop.sh` will stop the containers
- `sh ./down.sh` will delete the containers (wipes data!!!)
- `sh ./db-up.sh` will start the database
- `sh ./db-stop.sh` will stop the database

#### To delete and recreate the database (and wipe all data)

- `sh ./down.sh` to delete the containers
- `sh ./up.sh` to create the containers

### Create environment file

Create an .env file to store environmental variables used by the service.

- Run `touch .env`
- Copy and paste the data into the file below

```properties
DB_HOST=localhost
DB_PORT=5432
DB_NAME=postgres
DB_USERNAME=postgres
DB_PASSWORD=postgres
DB_ADMIN_USERNAME=postgres
DB_ADMIN_PASSWORD=postgres
LOGICAL_ENV_NAME=local
SERVICE_NAME=postable-remittance
```

### Sample SQL file with data

Add data to postgres using example queries [sample_data.sql](sample_data.sql)

## Getting Started

To boot the service locally, use the helper script which loads the environmental variables from your `.env` file.

- Start the service `sh ./run.sh`
    - Note you may need to run `chmod 777 run.sh` to make the scripts executable.
- Stop the service with `^c`

### Use IntelliJ IDE to debug and test the microservice

- Run microservice in debug mode with breakpoints if required.

### Access the microservice

```shell
curl http://localhost:8080/hello
```

### Swagger API

To access the local Swagger API: http://localhost:8080/swagger-ui/index.html

## Service Endpoints

| Service             | Methods | Description                                               |
|---------------------|---------|-----------------------------------------------------------|
| `/$searchByClaim`   | `POST`  | This endpoint returns search results based on parameters. |
| `/$searchByPatient` | `POST`  | This endpoint returns search results based on parameters. |
| `/$searchByPayment` | `POST`  | This endpoint returns search results based on parameters. |

### Request for the `/$searchByClaim` Endpoint

<details>
<summary>Sample request. Click to expand.</summary>

```shell
curl --location 'http://localhost:8080/$searchByClaim' \
--header 'Content-Type: application/json' \
--data '{
    "resourceType": "Parameters",
    "parameter": [
        {
            "name": "TIN",
            "valueString": "123456789"
        },
        {
            "name": "DateOfService",
            "valuePeriod": {
                "start": "2024-08-01T13:28:16-05:00",
                "end": "2024-08-12T13:28:17-05:00"
            }
        },
        {
            "name": "PatientID",
            "valueString": "1"
        },
        {
            "name": "Claim",
            "part": [
                {
                    "name": "ProviderClaimID",
                    "valueString": "998899"
                },
                {
                    "name": "ProviderID",
                    "valueString": "1234567890"
                },
                {
                    "name": "PayerClaimID",
                    "valueString": "1"
                },
                {
                    "name": "ClaimChargeAmount",
                    "valueString": "100"
                }
            ]
        },
        {
            "name": "PayerID",
            "valueString": "1010101"
        },
        {
            "name": "PayerName",
            "valueString": "payer_random1"
        }
    ]
}'
```

</details>

### Request for the `/$searchByPatient` Endpoint

<details>
<summary>Sample request. Click to expand.</summary>

```shell
curl --location 'http://localhost:8080/$searchByPatient' \
--header 'Content-Type: application/json' \
--data '{
    "resourceType": "Parameters",
    "parameter": [
        {
            "name": "TIN",
            "valueString": "123456789"
        },
        {
            "name": "DateOfService",
            "valuePeriod": {
                "start": "2024-08-01T13:28:16-05:00",
                "end": "2024-08-12T13:28:17-05:00"
            }
        },
        {
            "name": "Patient",
            "part": [
                {
                    "name": "PatientID",
                    "valueString": "1"
                },
                {
                    "name": "DateOfBirth",
                    "valueDate": "1990-04-03"
                },
                {
                    "name": "PatientFirstName",
                    "valueString": "John"
                },
                {
                    "name": "PatientLastName",
                    "valueString": "Doe"
                }
            ]
        },
        {
            "name": "PayerID",
            "valueString": "1010101"
        },
        {
            "name": "PayerName",
            "valueString": "payer_random1"
        }
    ]
}'
```

</details>

### Response of the `/$searchByClaim` and `/$searchByPatient` Endpoints

<details>
<summary>Sample response. Click to expand.</summary>

```json
{
  "resourceType": "Parameters",
  "id": "51bf20cb-700b-4897-a280-82e6ac09ad47",
  "meta": {
    "profile": [
      "http://hl7.org/fhir/us/davinci-pr/StructureDefinition/searchResultParameters"
    ]
  },
  "parameter": [
    {
      "name": "TIN",
      "valueString": "123456789"
    },
    {
      "name": "Payer",
      "part": [
        {
          "name": "PayerID",
          "valueString": "1010101"
        },
        {
          "name": "PayerName",
          "valueString": "payer_random1"
        }
      ]
    },
    {
      "name": "Claim",
      "part": [
        {
          "name": "ProviderClaimID",
          "valueString": "998899"
        },
        {
          "name": "ClaimReceivedDate",
          "valueDate": "2024-08-10"
        },
        {
          "name": "ProviderID",
          "valueString": "1234567890"
        },
        {
          "name": "PayerClaimID",
          "valueString": "1"
        },
        {
          "name": "PaymentInfo",
          "part": [
            {
              "name": "PaymentDate",
              "valueDate": "2024-08-11"
            },
            {
              "name": "PaymentNumber",
              "valueString": "1234567"
            },
            {
              "name": "PaymentAmount",
              "valueMoney": {
                "value": 100,
                "currency": "USD"
              }
            }
          ]
        }
      ]
    },
    {
      "name": "Patient",
      "part": [
        {
          "name": "DateOfBirth",
          "valueDate": "1990-04-03"
        },
        {
          "name": "PatientID",
          "valueString": "1"
        },
        {
          "name": "PatientFirstName",
          "valueString": "John"
        },
        {
          "name": "PatientLastName",
          "valueString": "Doe"
        }
      ]
    },
    {
      "name": "Remittance",
      "part": [
        {
          "name": "RemittanceAdviceIdentifier",
          "valueString": "adviceID1234"
        },
        {
          "name": "RemittanceAdviceType",
          "valueCode": "835"
        },
        {
          "name": "RemittanceAdviceDate",
          "valueDate": "2024-08-11"
        },
        {
          "name": "RemittanceAdviceFileSize",
          "valueInteger": 500
        }
      ]
    },
    {
      "name": "Remittance",
      "part": [
        {
          "name": "RemittanceAdviceIdentifier",
          "valueString": "adviceID123"
        },
        {
          "name": "RemittanceAdviceType",
          "valueCode": "PDF"
        },
        {
          "name": "RemittanceAdviceDate",
          "valueDate": "2024-08-11"
        },
        {
          "name": "RemittanceAdviceFileSize",
          "valueInteger": 150
        }
      ]
    }
  ]
}
```

</details>

### Request for the `/$searchByPayment` Endpoint

<details>
<summary>Sample request. Click to expand.</summary>

```shell
curl --location 'http://localhost:8080/$searchByPayment' \
--header 'Content-Type: application/json' \
--data '{
    "resourceType": "Parameters",
    "parameter": [
        {
            "name": "TIN",
            "valueString": "123456789"
        },
        {
            "name": "DateOfService",
            "valuePeriod": {
                "start": "2024-08-01T13:28:16-05:00",
                "end": "2024-08-12T13:28:17-05:00"
            }
        },
        {
            "name": "Payment",
            "part": [
                {
                    "name": "PaymentIssueDate",
                    "valuePeriod": {
                        "start": "2024-08-01T13:28:16-05:00",
                        "end": "2024-08-12T13:28:17-05:00"
                    }
                },
                {
                    "name": "PaymentAmount",
                    "part": [
                        {
                            "name": "PaymentAmountLow",
                            "valueMoney": {
                                "value": 100.00,
                                "currency": "USD"
                            }
                        },
                        {
                            "name": "PaymentAmountHigh",
                            "valueMoney": {
                                "value": 150.00,
                                "currency": "USD"
                            }
                        }
                    ]
                },
                {
                    "name": "PaymentNumber",
                    "valueString": "1234567"
                }
            ]
        },
        {
            "name": "PayerID",
            "valueString": "1010101"
        },
        {
            "name": "PayerName",
            "valueString": "payer_random1"
        }
    ]
}'
```

</details>

### Response of the `/$searchByPayment`

<details>
<summary>Sample response. Click to expand.</summary>

```json
{
  "resourceType": "Parameters",
  "id": "SearchResult",
  "meta": {
    "profile": [
      "http://hl7.org/fhir/us/davinci-pr/StructureDefinition/searchByPaymentResultParameters"
    ]
  },
  "parameter": [
    {
      "name": "TIN",
      "valueString": "123456789"
    },
    {
      "name": "Payer",
      "part": [
        {
          "name": "PayerID",
          "valueString": "1010101"
        },
        {
          "name": "PayerName",
          "valueString": "payer_random1"
        }
      ]
    },
    {
      "name": "Payment",
      "part": [
        {
          "name": "PaymentIssueDate",
          "valueDate": "2024-08-11"
        },
        {
          "name": "PaymentNumber",
          "valueString": "1234567"
        },
        {
          "name": "PaymentAmount",
          "valueMoney": {
            "value": 100.0,
            "currency": "USD"
          }
        }
      ]
    },
    {
      "name": "Remittance",
      "part": [
        {
          "name": "RemittanceAdviceIdentifier",
          "valueString": "adviceID1234"
        },
        {
          "name": "RemittanceAdviceType",
          "valueCode": "835"
        },
        {
          "name": "RemittanceAdviceDate",
          "valueDate": "2024-08-11"
        },
        {
          "name": "RemittanceAdviceFileSize",
          "valueInteger": 500
        }
      ]
    },
    {
      "name": "Remittance",
      "part": [
        {
          "name": "RemittanceAdviceIdentifier",
          "valueString": "adviceID123"
        },
        {
          "name": "RemittanceAdviceType",
          "valueCode": "PDF"
        },
        {
          "name": "RemittanceAdviceDate",
          "valueDate": "2024-08-11"
        },
        {
          "name": "RemittanceAdviceFileSize",
          "valueInteger": 150
        }
      ]
    }
  ]
}
```

</details>