<#--
    This is a freemarker template that is used to dynamically build the payload object
    to consume the developer api for document generation.
-->
<#assign
    current_date_timestamp = .now?long
>
{
    "id": "100800009705", // Este dato lo entrega el front.
    "created_at": "${current_date_timestamp?c}", // Se genera con la fecha actual
    "contactInformation": {
        "email": "carlosmartinez@gmail.com", // api search-interlocutor | api getPreApplicationData | front lo pide al cliente solo si la ine no esta vigente | ¿De donde se obtiene para los casos en que la ine esta vigente?
        "phone": "2284767230", // front lo pide al cliente solo si la ine no esta vigente | ¿De donde se obtiene para los casos en que la ine esta vigente?
        "phoneAddress": "2222222222", // ¿De donde se obtiene?
        "phoneWork": "3333333333" //  ¿De donde se obtiene?
    },
    "dataSeller": {
        "apellidoMaterno": "${promoter.lastname2}", // ya se extrae de la consulta de datos del promotor
        "apellidoPaterno": "${promoter.lastname1}", // ya se extrae de la consulta de datos del promotor
        "bpId": "${promoter.bpId}", // ya se extrae de la consulta de datos del promotor
        "nombre1": "${promoter.name1}", // ya se extrae de la consulta de datos del promotor
        "nombre2": "${promoter.name2}", // ya se extrae de la consulta de datos del promotor
        "rfc": "${promoter.rfc}" // ya se extrae de la consulta de datos del promotor
    },
    "employmentData": {
        "numeroEmpleado": "111111" // ¿De donde se obtiene?
    },
    "exceptionProtocol": {},
    "idDocumentData": {
        "ocr": "1900091754271", // ¿esto es de dock-ia ? ¿De donde se obtiene?
        "type": "INE O IFE" // ¿De donde se obtiene?
    },
    "offer": {
        "quoter": {
            "CAT": 67.9, // ¿De donde se obtiene?
            "agreemen": {
                "branch": {
                    "empresa": {
                        "businessName": "La Tenda México S.A. de C.V." // api getActiveOffering
                    }
                },
                "convenioId": "10000208" // Se obtiene del api getActiveOffering
            },
            "amount": 45000, // api getActiveOffering
            "annualTI": 51.74999999999999, //  ¿De donde se obtiene la tasa de interes anual de la oferta?
            "currentDiscount": 1186.78, // Se obtiene del api getActiveOffering (dato: discount)
            "discountAmount": 1186.78, // Se obtiene del api getActiveOffering (dato: discount)
            "frequencyDescription": "Quincenal", // Se obtiene del api getActiveOffering
            "monthlyTI": 4.312499999999999, // ¿De donde se obtiene la tasa de interes mensual de la oferta?
            "openingCommissionPercentage": "1.000", // api getActiveOffering
            "plazo": 120, // Se obtiene del api getActiveOffering
            "requestedAmount": 45000, // Se obtiene del api getActiveOffering
            "totalAmount": 142413.6 // ¿De donde se obtiene?
        }
    },
    "person": {
        "address": [
            {
                "addressType": {
                    "description": "DIRECCIÓN DOMICILIO (PRINCIPAL)", // api searchAddress
                    "key": "XXDEFAULT"  // Se obtiene del api searchAddress
                },
                "city": "XALAPA",  // Se obtiene del api searchAddress
                "country": "MX",  // Se obtiene del api searchAddress
                "phones": [
                    {
                        "number": "2284767230",  // Se obtiene del api searchAddress
                        "phoneType": "MOVIL"  // Se obtiene del api searchAddress
                    }
                ],
                "stateDesc": "VERACRUZ", // Se obtiene del api searchAddress
                "street": "CHIHUAHUA", // Se obtiene del api searchAddress
                "suburb": "PROGRESO", // Se obtiene del api searchAddress
                "township": "XALAPA", // Se obtiene del api searchAddress
                "zipCode": "91130" // Se obtiene del api searchAddress
            }
        ],
        "countryBirth": {
            "description": "México", // Se obtiene del api getPreApplicationData
            "key": "MX" // Se obtiene del api getPreApplicationData
        },
        "dateBirth": "1993-11-19", // Se obtiene del api getPreApplicationData
        "email": "cinosoni@gmail.com", // Se obtiene del api searchInterlocutor
        "gender": "M",  // Se obtiene del api getPreApplicationData
        "lastname1": "SAN CRISTOBAL", // Se obtiene del api getPreApplicationData
        "lastname2": "RAMIREZ", // Se obtiene del api getPreApplicationData
        "levelStudies": {
            "description": "", // Se obtiene del api getPreApplicationData
            "key": "NOT_FOUND" // Se obtiene del api getPreApplicationData
        },
        "maritalStatus": {
            "description": "Soltero/a", // Se obtiene del api getPreApplicationData
            "key": "1" // Se obtiene del api getPreApplicationData
        },
        "name1": "EMILIO", // Se obtiene del api getPreApplicationData
        "name2": "", // Se obtiene del api getPreApplicationData
        "nationality": {
            "description": "MEXICANA", // Se obtiene del api getPreApplicationData
            "key": "MX" // Se obtiene del api getPreApplicationData
        },
        "occupation": {
            "description": "Empleado", // Se obtiene del api getPreApplicationData
            "key": "1008" // Se obtiene del api getPreApplicationData
        },
        "placeBirth": "VERACRUZ", // Se obtiene del api getPreApplicationData
        "residenceCountry": "MX", // Se obtiene del api getPreApplicationData
        "rfc": "SARE931119T80", // Se obtiene del api getPreApplicationData
        "spouseLastName1": "", // Se obtiene del api getPreApplicationData
        "spouseLastName2": "", // Se obtiene del api getPreApplicationData
        "spouseName1": "", // Se obtiene del api getPreApplicationData
        "spouseName2": "" // Se obtiene del api getPreApplicationData
    },
    "privateDeposit": {
        "banco": "640", // Se obtiene del api getPreApplicationData
        "bancoText": "GEM-CONSUBANCO", // Se obtiene del api getPreApplicationData
        "cbanc": "640180000000454635", // Se obtiene del api getPreApplicationData
        "descriptionPaymentMethod": "Depósito a Cuenta", // Se obtiene del api getPreApplicationData
        "metodoPago": "6" // Se obtiene del pi getPreApplicationData
    },
    "references": [
        {
            "apellidoMaterno": "Pardo", // Se obtiene del api getPreApplicationData
            "apellidoPaterno": "Rey", // Se obtiene del api getPreApplicationData
            "bp": "0004470746", // Se obtiene del api getPreApplicationData
            "clientId": "0009181768", // Se obtiene del api getPreApplicationData
            "nombre": "Diana", // Se obtiene del api getPreApplicationData
            "segundoNombre": "Pilar", // Se obtiene del api getPreApplicationData
            "telefono": "2281888881",  // Se obtiene del api getPreApplicationData
            "telefonoFijo": "", // api Se obtiene del getPreApplicationData
            "parentesco": {
                "description": "ABUELO(A)", // Se obtiene del api getPreApplicationData
                "key": "ZC01" // Se obtiene del api getPreApplicationData
            }
        }
    ],
    "signatureColor": "#000000", // Se obtiene del api agreementGetDetail
    "origin": "ECSB" // Pendiente por definir con promotor que canal debemos enviar
}