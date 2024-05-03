<#--
    This is a freemarker template that is used to dynamically build the payload object
    to consume the developer api for document generation.
-->
<#assign
    current_date_timestamp = .now?long
>
{
    "id": "100800009705",
    "created_at": "${current_date_timestamp?c}",
    "contactInformation": {
        "email": "carlosmartinez@gmail.com",
        "phone": "2284767230",
        "phoneAddress": "2222222222",
        "phoneWork": "3333333333"
    },
    "dataSeller": {
        "apellidoPaterno": "${promoter_data.lastname1}",
        "apellidoMaterno": "${promoter_data.lastname2}",
        "bpId": "${promoter_data.bpId}",
        "nombre1": "${promoter_data.name1}",
        "nombre2": "${promoter_data.name2}",
        "rfc": "${promoter_data.rfc}"
    },
    "employmentData": {
        "numeroEmpleado": "111111"
    },
    "idDocumentData": {
        "ocr": "1900091754271",
        "type": "INE O IFE"
    },
    "offer": {
        "quoter": {
            "CAT": 67.9,
            "agreemen": {
                "branch": {
                    "empresa": {
                        "businessName": "La Tenda Mexico S.A. de C.V."
                    },
                    "distribuidor": {
                        "distributorName": "SOLO LO AGREGUE PARA PRUEBAS"
                    }
                },
                "convenioId": "${offer_data.offer.agreement.key}"
            },
            "amount": "${offer_data.offer.amount?string}",
            "annualTI": 51.74999999999999,
            "currentDiscount": 1186.78,
            "discountAmount": 1186.78,
            "frequencyDescription": "${offer_data.offer.frequency}",
            "monthlyTI": 4.312499999999999,
            "openingCommissionPercentage": "1.000",
            "plazo": ${offer_data.offer.term},
            "requestedAmount": 45000,
            "totalAmount": 142413.6
        }
    },
    "person": {
        "address": [
            {
                "addressId": "0025286049",
                "addressType": {
                    "description": "DIRECCIÓN DOMICILIO (PRINCIPAL)",
                    "key": "XXDEFAULT"
                },
                "addressTypeVia": {
                    "description": "",
                    "key": "NOT_FOUND"
                },
                "andStreet": "",
                "betweenStreet": "",
                "city": "XALAPA",
                "country": "MX",
                "externalNumber": "81",
                "internalNumber": "",
                "phones": [
                    {
                        "number": "2284767230",
                        "phoneType": "MOVIL"
                    },
                    {
                        "number": ""
                    }
                ],
                "settlementType": {
                    "description": "",
                    "key": "NOT_FOUND"
                },
                "state": "VER",
                "stateDesc": "VERACRUZ",
                "street": "CHIHUAHUA",
                "suburb": "PROGRESO",
                "town": "",
                "township": "XALAPA",
                "zipCode": "91130"
            }
        ],
        "countryBirth": {
            "description": "México",
            "key": "MX"
        },
        "dateBirth": "1993-11-19",
        "email": "cinosoni@gmail.com",
        "gender": "M",
        "lastname1": "SAN CRISTOBAL",
        "lastname2": "RAMIREZ",
        "levelStudies": {
            "description": "",
            "key": "NOT_FOUND"
        },
        "maritalStatus": {
            "description": "Soltero/a",
            "key": "1"
        },
        "name1": "EMILIO",
        "name2": "",
        "nationality": {
            "description": "MEXICANA",
            "key": "MX"
        },
        "occupation": {
            "description": "Empleado",
            "key": "1008"
        },
        "placeBirth": "VERACRUZ",
        "residenceCountry": "MX",
        "rfc": "SARE931119T80",
        "spouseLastName1": "",
        "spouseLastName2": "",
        "spouseName1": "",
        "spouseName2": ""
    },
    "privateDeposit": {
        "banco": "640",
        "bancoText": "GEM-CONSUBANCO",
        "cbanc": "640180000000454635",
        "descriptionPaymentMethod": "Depósito a Cuenta",
        "metodoPago": "6"
    },
    "references": [
        {
            "apellidoMaterno": "Pardo",
            "apellidoPaterno": "Rey",
            "bp": "0004470746",
            "clientId": "0009181768",
            "nombre": "Diana",
            "segundoNombre": "Pilar",
            "telefono": "2281888881",
            "telefonoFijo": "",
            "parentesco": {
                "description": "ABUELO(A)",
                "key": "ZC01"
            }
        }
    ],
    "signatureColor": "#000000",
    "origin": "ECSB"
}