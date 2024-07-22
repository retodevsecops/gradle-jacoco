<#-- This is a freemarker template that is used to dynamically build the payload object to consume the developer api for document generation. -->
<#-- Variables -->
<#assign 
    current_date_timestamp = .now?long
    amountTotalToPay = (offer_data.offer.discount?replace(",", "")?number * offer_data.offer.term)?replace(",", "")
    discount = offer_data.offer.discount?replace(",", "")?number?c
    defaultAddress = customer_data.customer.address?filter(dataAddres -> dataAddres.addressType.key == "XXDEFAULT")?first?if_exists
    maritalStatusDescription = (customer_data.customer.maritalStatus.description)!""
>
<#-- Functions -->
<#-- Template -->
{
    "id": "${offer_data.offer.id}",
    "created_at": "${current_date_timestamp?c}",
    "contactInformation": {
        "email": "${customer_data.customer.email?lower_case!''}",
        "phoneAddress": "${(defaultAddress?exists && defaultAddress.phones?exists)?then((defaultAddress.phones?filter(phone -> phone.phoneType == "PRINCIPAL" || phone.phoneType == "MOVIL"))?first?if_exists.number, '')}",
        "phone": "${(defaultAddress?exists && defaultAddress.phones?exists)?then((defaultAddress.phones?filter(phone -> phone.phoneType == "MOVIL" || phone.phoneType == "PRINCIPAL"))?first?if_exists.number, '')}"
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
       "ocr": "${(customer_data.customer.credentialData.ocr)!''}",
       "type": "IFE / INE"
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
            "amount": ${offer_data.offer.amount?c},
            "annualTI": ${offer_data.offer.annualTI?replace(",", ".")},
            "currentDiscount": ${discount},
            "discountAmount": ${discount},
            "frequencyDescription": "${offer_data.offer.frequency}",
            "monthlyTI": ${offer_data.offer.monthlyTI?replace(",", ".")},
            "openingCommissionPercentage": ${offer_data.offer.commissions?replace(",", ".")},
            "plazo": ${offer_data.offer.term},
            "requestedAmount": ${offer_data.offer.amount?c},
            "totalAmount": ${amountTotalToPay}
        }
    },
    "person": {
        "lastFolioFiscal": "${FunctionsUtil.getFolioFiscal(ocr_documents_data)}",
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
                <#if residence.phones??>
                ,
                "phones": [
                    <#list residence.phones as phone>
                    {
                        "phoneType": "${phone.phoneType! ''}",
                        "number": "${phone.number! ''}"
                    }<#if phone_has_next>,</#if>
                    </#list>
                ]
                </#if>
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
            "description": "${maritalStatusDescription?has_content?then(maritalStatusDescription, 'SOLTERO/A')}",
            "key": "${(customer_data.customer.maritalStatus.key)!'1'}"
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
    "signature": [
        {
            "name": "cliente",
            "file_url": "https://storage.googleapis.com/csb_puc_statics_prod/unsigned.png"
        },
        {
            "name": "promotor",
            "file_url": "https://storage.googleapis.com/csb_puc_statics_prod/unsigned.png"
        }
    ],
    "signatureColor": "#000000",
    "origin": "RENEX"
}