<#--
This is a freemarker template that is used to dynamically build the request object
to consume the api for create application. ${}
-->
<#assign
current_date_timestamp = .now?long
>
{
  "createApplicationRequestBO": {
    "applicationId": "${offer_data.offer.id}",
    "aplicationInfo": {
      "probankNumber": "string",
      "folioApplication": "string",
      "promotorBp": "string",
      "promotorCompleteName": "string",
      "applicant": {
        "bp": "string",
        "clientId": "string",
        "curp": "string",
        "rfc": "${customer_data.customer.rfc}",
        "name1": "${customer_data.customer.firstName}",
        "name2": "${customer_data.customer.secondName}",
        "lastName1": "${customer_data.customer.lastName}",
        "lastName2": "${customer_data.customer.secondLastName}",
        "regimenFiscal": {
          "key": "string",
          "description": "string"
        },
        "credits": [
          {
            "accountId": "string",
            "formalizationDate": "2024-05-15",
            "amount": 0,
            "dueAmount": 0,
            "paymentNumber": "string",
            "totalOfPayments": 0,
            "discountAmount": "string",
            "paymentFrequency": "string",
            "paymentBehavior": "string",
            "paymentHistory": "string",
            "fraudFlag": 0,
            "fraudDate": "2024-05-15",
            "convenioId": "string",
            "convenioName": "string",
            "rfc": "string",
            "name": "string",
            "montoExigible": "string",
            "montoAplicado": 0,
            "fechaPosteo": "2024-05-15",
            "quebranto": true,
            "pagoVoluntario": true,
            "diasRetrazo": 0,
            "idEstatusCredito": 0,
            "totalAplicadoNMeses": 0,
            "ratificado": true,
            "mesesDesdePrimerPago": 0,
            "tipoAmortizacion": 0,
            "letter": {
              "monto": 0,
              "interes": 0,
              "iva": 0,
              "fechaSolicitudLiq": "2024-05-15",
              "montoComision": 0,
              "ivaComision": 0,
              "totalLiquidacion": 0,
              "tipoLiquidacion": 0
            },
            "sistemaOrigen": "string",
            "descripcionEstatus": "string",
            "fechaPrimerPago": "2024-05-15",
            "duenioCartera": "string",
            "capitalBalance": 0,
            "outstandingBalance": 0,
            "operativeBalance": 0,
            "death": true,
            "restructure": true,
            "statusAccount": "string",
            "annualOrdinaryInterestRatePerc": 0,
            "annualMoratoriumInterestRatePerc": 0,
            "amountOfInterestEarned": 0,
            "percentageOfInterestRateEarned": 0,
            "cat": 0
          }
        ]
      },
      "branch": {
        "id": "string",
        "name": "string",
        "bp": "string",
        "acronym": "string",
        "company": {
          "id": "string",
          "name": "string",
          "bp": "string",
          "acronym": "string"
        },
        "distributor": {
          "id": "string",
          "name": "string",
          "bp": "string",
          "acronym": "string"
        },
        "branchNFOFlag": true
      },
      "agreement": {
        "id": "string",
        "name": "string",
        "shortName": "string",
        "group": "string",
        "shortGroup": "string",
        "brmsCode": "string",
        "product": {
          "id": "string",
          "name": "string",
          "shortName": "string",
          "category": "string",
          "brmsCode": "string",
          "paymentFrecuencyId": "string",
          "paymentFrecuencyDesc": "string"
        },
        "documents": [
          {
            "id": "string",
            "name": "aviso-de-privacidad",
            "technicalName": "string",
            "clasification": "string",
            "required": true,
            "visible": true,
            "fields": [
              {
                "id": "string",
                "name": "string",
                "technicalName": "string",
                "clasification": "string",
                "type": "string",
                "required": true,
                "value": "string"
              }
            ],
            "fileName": "string",
            "url": "string"
          }
        ],
        "convenioNFOFlag": true
      },
      "amount": 0,
      "priceGroupId": "string",
      "termDesc": "string",
      "isCNCA": true,
      "sourceChannelApp": "string",
      "biometricTask": {
        "probankNumber": "string",
        "taskCRMId": "string",
        "taskStatusCRM": {
          "key": "string",
          "description": "string"
        },
        "createDate": "2024-05-15"
      },
      "recommender": {
        "bp": "string",
        "clientId": "string",
        "curp": "string",
        "rfc": "string",
        "name1": "string",
        "name2": "string",
        "lastName1": "string",
        "lastName2": "string",
        "regimenFiscal": {
          "key": "string",
          "description": "string"
        }
      },
      "competitors": [
        {
          "serviceOrderId": "string",
          "bp": "string",
          "name": "string",
          "shortName": "string",
          "amount": 0,
          "transitPayment": true,
          "numberTransitPayment": 0,
          "lastDiscountDate": "2024-05-15",
          "reference": "string",
          "concept": "string",
          "discountAmount": 0,
          "approvalDate": "2024-05-15",
          "emmisionDate": "2024-05-15",
          "expirationDate": "2024-05-15",
          "cat": 0,
          "rate": 0,
          "bankId": "string",
          "bankDesc": "string",
          "clabeAggrementAccount": "string",
          "paymentReference": "string",
          "paymentMethodId": "string",
          "paymentMethodDesc": "string"
        }
      ],
      "paymentData": {
        "bankId": "string",
        "bankDesc": "string",
        "clabe": "string",
        "paymentMethodId": "string",
        "paymentMethodDesc": "string"
      },
      "reprocessNumber": 0,
      "incidences": [
        {
          "type": {
            "key": "string",
            "description": "string"
          },
          "resolved": true,
          "cause": {
            "key": "string",
            "description": "string"
          },
          "motive": {
            "key": "string",
            "description": "string"
          },
          "reprocessable": true,
          "externalMessage": "string",
          "internalMessage": "string"
        }
      ],
      "opcOppnpTask": {
        "taskId": "string",
        "sourceChannel": "string"
      },
      "specialBranchDocuments": [
        {
          "id": "string",
          "name": "string",
          "technicalName": "string",
          "clasification": "string",
          "required": true,
          "visible": true,
          "fields": [
            {
              "id": "string",
              "name": "string",
              "technicalName": "string",
              "clasification": "string",
              "type": "string",
              "required": true,
              "value": "string"
            }
          ],
          "fileName": "string",
          "base64": "string",
          "url": "string"
        }
      ],
      "references": [
        {
          "bp": "string",
          "clientId": "string",
          "curp": "string",
          "rfc": "string",
          "name1": "string",
          "name2": "string",
          "lastName1": "string",
          "lastName2": "string",
          "regimenFiscal": {
            "key": "string",
            "description": "string"
          },
          "relationship": {
            "key": "string",
            "description": "string"
          },
          "cellPhone": "string"
        }
      ],
      "videoTask": {
        "id": "string",
        "idFolio": "string",
        "documents": [
          {
            "id": "string",
            "name": "string",
            "technicalName": "string",
            "clasification": "string",
            "required": true,
            "visible": true,
            "fields": [
              {
                "id": "string",
                "name": "string",
                "technicalName": "string",
                "clasification": "string",
                "type": "string",
                "required": true,
                "value": "string"
              }
            ],
            "fileName": "string",
            "base64": "string",
            "url": "string"
          }
        ]
      },
      "cat": 0,
      "rate": 0,
      "iva": 0,
      "discountamount": 0,
      "amountTotalToPay": 0,
      "dependence": {
        "key": "string",
        "startDate": "2024-05-15",
        "endDate": "2024-05-15",
        "name": "string"
      },
      "promotorNFOFlag": true
    }
  }
}