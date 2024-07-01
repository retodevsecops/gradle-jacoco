<#-- This is a freemarker template that is used to dynamically build the payload object to consume the developer api for document generation. -->
<#-- Variables -->
<#assign 
    current_date_timestamp = .now?long
    amountTotalToPay = offer_data.offer.discount?replace(",", "")?number * offer_data.offer.term
    discount = offer_data.offer.discount?replace(",", "")?number?c
    documentTypeValues = {
        "PHA004": "Número IMSS",
        "ZBDBK": "Número de Cliente Banca Digital",
        "ZBDCB": "Número de Cuenta Banca Digital",
        "ZBDPL": "Número de Plástico Banca Digital",
        "ZCEF": "Credencial de Empleo Federal",
        "ZCIMS": "Credencial del IMSS",
        "ZCPJ": "Credencial de Pensionado y/o jubilado",
        "ZCPRO": "Cédula Profesional",
        "ZCREF": "Clave de Referido",
        "ZCTO": "Clave Centro de trabajo",
        "ZCURP": "Clave Unica de Registro Poblacional",
        "ZFIEL": "Firma Electrónica Avanzada",
        "ZFM1": "FM1",
        "ZFM2": "FM2",
        "ZIDBK": "Número de Cliente",
        "ZIDSFC": "Identificador Salesforce para BP",
        "ZIFE": "Credencial IFE / INE",
        "ZLIC": "Licencia de conducir",
        "ZMATC": "Matrícula Consular",
        "ZNDSS": "Número de Seguridad Social",
        "ZRFC": "Registro Federal de Contribuyentes",
        "ZSIU": "Credencial SIU",
        "ZUAD": "Usuario Active Directory",
        "ZVCB": "Identificador Vector"
    }
    defaultAddress = customer_data.customer.address?filter(dataAddres -> dataAddres.addressType.key == "XXDEFAULT")?first?if_exists
>
<#-- Functions -->
<#function getDocumentTypeValue field>
    <#if field?has_content>
        <#return documentTypeValues[field]>
    <#else>
        <#return "">
    </#if>
</#function>
<#-- Template -->
{
    "id": "${offer_data.offer.id}",
    "created_at": "${current_date_timestamp?c}",
    "contactInformation": {
        "email": "${customer_data.customer.email?lower_case!''}",
        "phoneAddress": "${defaultAddress?exists?then(defaultAddress.phones?filter(phone -> phone.phoneType == "PRINCIPAL")?first?if_exists.number, '')}",
        "phone": "${defaultAddress?exists?then(defaultAddress.phones?filter(phone -> phone.phoneType == "MOVIL")?first?if_exists.number, '')}"
    },
    "generalData": {
        "interviewResult": "Satisfactoria"
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
        "numeroEmpleado": "${offer_data.offer.employeeNumber?string}"
    },
    "idDocumentData": {
        "ocr": "${customer_data.customer.identificationNumber}",
        "type": "${getDocumentTypeValue(customer_data.customer.identificationType)}"
    },
    "offer": {
        "quoter": {
            "CAT": ${offer_data.offer.cat?replace(",", ".")},
            "agreemen": {
                "branch": {
                    "empresa": {
                        "businessName": "Consupago S.A. de C.V. SOFOM E.R."
                    }
                },
                "convenioId": "${offer_data.offer.agreement.key}"
            },
            "amount": ${amountTotalToPay?c},
            "annualTI": ${offer_data.offer.annualTI?replace(",", ".")},
            "currentDiscount": ${discount},
            "discountAmount": ${discount},
            "frequencyDescription": "${offer_data.offer.frequency}",
            "monthlyTI": ${offer_data.offer.monthlyTI?replace(",", ".")},
            "openingCommissionPercentage": ${offer_data.offer.commissions?replace(",", ".")},
            "plazo": ${offer_data.offer.term},
            "requestedAmount": ${offer_data.offer.amount?c},
            "totalAmount": ${amountTotalToPay?c}
        }
    },
    "person": {
        "address": [
            <#list customer_data.customer.address as residence>
            {
                "addressType": {
                    "key": "${residence.addressType.key! ''}",
                    "description": "${residence.addressType.description! ''}"
                },
                "city": "${residence.city! ''}",
                "country": "${residence.country! ''}",
                "externalNumber": "${residence.externalNumber! ''}",
                "internalNumber": "${residence.internalNumber! ''}",
                "stateDesc": "${residence.stateDesc! ''}",
                "street": "${residence.street! ''}",
                "suburb": "${residence.suburb! ''}",
                "township": "${residence.township! ''}",
                "zipCode": "${residence.zipCode! ''}"
            }<#if residence_has_next>,</#if>
            </#list>
        ],
        "residenceCountry": "MX",
        "countryBirth": {
            "description": "México",
            "key": "MX"
        },
        "curp": "${customer_data.customer.curp}",
        "dateBirth": "${customer_data.customer.dateBirth}",
        "email": "${customer_data.customer.email}",
        "gender": "${customer_data.customer.gender}",
        "name1": "${customer_data.customer.firstName}",
        "name2": "${customer_data.customer.secondName}",
        "lastname1": "${customer_data.customer.lastName}",
        "lastname2": "${customer_data.customer.secondLastName}",
        "levelStudies": {
            "description": "${customer_data.customer.levelStudies.description}",
            "key": "${customer_data.customer.levelStudies.key}"
        },
        "maritalStatus": {
            "description": "${customer_data.customer.maritalStatus.description}",
            "key": "${customer_data.customer.maritalStatus.key}"
        },
        "nationality": {
            "description": "${customer_data.customer.nationality.description}",
            "key": "${customer_data.customer.nationality.key}"
        },
        "occupation": {
            "description": "${customer_data.preApplicationData.applicant.occupation.description}",
            "key": "${customer_data.preApplicationData.applicant.occupation.key}"
        },
        "regimenFiscal": {
            "key": "${customer_data.customer.regimenFiscal.key}",
            "description": "${customer_data.customer.regimenFiscal.description}"
        },
        <#if customer_data.customer.placeBirth?upper_case == "VERACRUZ DE IGNACIO DE LA LLAVE">
            "placeBirth": "VERACRUZ"
        <#else>
            "placeBirth": "${customer_data.customer.placeBirth}"
        </#if>,
        "rfc": "${customer_data.customer.rfc}",
        "spouseLastName1": "${customer_data.preApplicationData.applicant.spouseLastName1! ''}",
        "spouseLastName2": "${customer_data.preApplicationData.applicant.spouseLastName2! ''}",
        "spouseName1": "${customer_data.preApplicationData.applicant.spouseName1! ''}",
        "spouseName2": "${customer_data.preApplicationData.applicant.spouseName2! ''}"
    },
    "privateDeposit": {
        "banco": "${customer_data.preApplicationData.paymentData.bankId! ''}",
        "bancoText": "${customer_data.preApplicationData.paymentData.bankDesc! ''}",
        "cbanc": "${customer_data.preApplicationData.paymentData.clabe! ''}",
        "descriptionPaymentMethod": "${customer_data.preApplicationData.paymentData.paymentMethodDesc! ''}",
        "metodoPago": "${customer_data.preApplicationData.paymentData.paymentMethodId! ''}"
    },
    <#if customer_data.preApplicationData.references??>
    "references": [
        <#list customer_data.preApplicationData.references as reference>
            {
                "apellidoMaterno": "${reference.lastName2! ''}",
                "apellidoPaterno": "${reference.lastName1! ''}",
                "bp": "${reference.bp! ''}",
                "clientId": "${reference.clientId! ''}",
                "nombre": "${reference.name1! ''}",
                "segundoNombre": "${reference.name2! ''}",
                "telefono": "${reference.cellPhone! ''}",
                "telefonoFijo": "${reference.phone! ''}",
                "parentesco": {
                    "description": "${reference.relationship?has_content?then(reference.relationship.description, '')}",
                    "key": "${reference.relationship?has_content?then(reference.relationship.key, '')}"
                }
            }<#if reference_has_next>,</#if>
        </#list>
    ],
    </#if>
    "signatureColor": "#000000",
    "origin": "RENEX"
}