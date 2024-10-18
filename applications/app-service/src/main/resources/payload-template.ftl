<#-- This is a freemarker template that is used to dynamically build the payload object to consume the developer api for document generation. -->
<#-- Variables -->
<#assign
    months = {
        "ENE": "01", "FEB": "02", "MAR": "03", "ABR": "04", "MAY": "05", "JUN": "06",
        "JUL": "07", "AGO": "08", "SEP": "09", "OCT": "10", "NOV": "11", "DIC": "12"
    }
    maritalStatusValues = {
            "1": "S", "2": "C", "3": "V", "4": "D", "5": "S", "6": "U", "7": "Otros"
    }
    current_date_timestamp = .now?long
    amountTotalToPay = (offer_data.offer.discount?replace(",", "")?number * offer_data.offer.term)?replace(",", "")
    discount = offer_data.offer.discount?replace(",", "")?number?c
    defaultAddress = customer_data.customer.address?filter(dataAddres -> dataAddres.addressType.key == "XXDEFAULT")?first?if_exists
    fiscalAddress = customer_data.customer.address?filter(dataAddres -> dataAddres.addressType.key == "ZFISCAL")?first?if_exists
    maritalStatusDescription = (customer_data.customer.maritalStatus.description)!""
    company = agreement_data.company?lower_case
    branch = promoter_data.branches?filter(branch -> branch.branchID == agreement_configuration_data.branchId)?first?if_exists
    folioFiscal = getFolioFiscal(ocr_documents_data)?replace("-", "")
    aggrementKey = offer_data.offer.agreement.key
>
<#-- Functions -->
<#function getPreferredPhone address>
    <#if address?exists && address.phones?exists>
        <#assign mobilePhone = address.phones?filter(phone -> phone.phoneType == "MOVIL")?first?if_exists>
        <#assign principalPhone = address.phones?filter(phone -> phone.phoneType == "PRINCIPAL")?first?if_exists>
        <#if mobilePhone??>
            <#return mobilePhone.number>
        <#elseif principalPhone??>
            <#return principalPhone.number>
        <#else>
            <#return "">
        </#if>
    <#else>
        <#return "">
    </#if>
</#function>
<#function getStringFromBoolean value>
    <#return (value?exists && value == true)?string("Y", "N")>
</#function>
<#function dateToNumberFormat(dateString)>
    <#assign day = dateString?substring(0, 2)>
    <#assign monthName = dateString?substring(3, 6)>
    <#assign year = dateString?substring(7, 11)>
    <#assign monthNumber = months[monthName]>
    <#return year + "/" + monthNumber + "/" + day>
</#function>
<#function getFolioFiscal(ocrDocuments)>
    <#assign latestDate = "1999/12/31"?date("yyyy/MM/dd")>
    <#assign folioFiscal = "">
    <#list ocrDocuments as document>
        <#assign finalPayPeriod = document.data?filter(data -> data.name == "periodo-final-pago")?first.value>
        <#assign finalDate = dateToNumberFormat(finalPayPeriod)?date("yyyy/MM/dd")>
        <#if finalDate gt latestDate>
            <#assign latestDate = finalDate>
            <#assign folioFiscal = document.data?filter(data -> data.name == "folio-fiscal")?first.value>
        <#elseif finalDate == latestDate>
            <#assign possibleFolioFiscal = document.data?filter(data -> data.name == "folio-fiscal")?first.value>
            <#if possibleFolioFiscal?length == 36>
                <#assign folioFiscal = possibleFolioFiscal>
            </#if>
        </#if>
    </#list>
    <#return folioFiscal>
</#function>
<#function getOcrValue(name)>
    <#return (ocr_documents_data[0].data?filter(it -> it.name == name)?first.value!"")>
</#function>
<#function getFieldFromDocument(documentName, fieldName)>
    <#assign document = customer_data.preApplicationData.documents?filter(d -> d.technicalName == documentName)?first>
    <#if document?has_content>
        <#return document.fields?filter(f -> f.technicalName == fieldName)?first.value!"">
    </#if>
    <#return "">
</#function>
<#function getBusinessNameByAggrementKey(aggrementKeyValue)>
    <#if aggrementKeyValue == "10000790">
        <#return "HXTI S.A. de C.V. SOFOM E.N.R.">
    </#if>
    <#return "Consupago S.A. de C.V. SOFOM E.R.">
</#function>
<#function getMaritalStatusValue(value)>
    <#if maritalStatusValues[value]??>
        <#return maritalStatusValues[value]>
    <#else>
        <#return value>
    </#if>
</#function>
<#-- Template -->
<#if company == "csb">
{
    "id": "${offer_data.offer.id}",
    "created_at": "${current_date_timestamp?c}",
    "contactInformation": {
        "email": "${customer_data.customer.email?lower_case!''}",
        "phoneAddress": "${(defaultAddress?exists && defaultAddress.phones?exists)?then((defaultAddress.phones?filter(phone -> phone.phoneType == "PRINCIPAL" || phone.phoneType == "MOVIL"))?first?if_exists.number, '')}",
        "phone": "${getPreferredPhone(defaultAddress)}"
    },
    "generalData": {
        "interviewResult": "Satisfactoria"
    },
    "dataSeller": {
        "apellidoPaterno": "",
        "apellidoMaterno": "",
        "bpId": "",
        "nombre1": "${promoter_data.name1}",
        "nombre2": "",
        "rfc": ""
    },
    "employmentData": {
        "employeeNumber": "${offer_data.offer.employeeNumber?string}",
        "numeroEmpleado": "${offer_data.offer.employeeNumber?string}",
        "publicServerKey": "${offer_data.offer.employeeNumber?string}"
    },
    "idDocumentData": {
       "ocr": "${(customer_data.customer.credentialData.ocr)!''}",
       "type": "IFE / INE"
    },
    "offer": {
        "quoter": {
            "CAT": ${offer_data.offer.cat?replace(",", ".")},
            "agreemen": {
                "dependencies": {
                    "gemDependency": {
                        "descripcion": "${getFieldFromDocument('carta-autorizacion-descuento', 'dependencia')}"
                    }
                },
                "branch": {
                    "empresa": {
                        "businessName": "${getBusinessNameByAggrementKey(aggrementKey)}"
                    }
                },
                "convenioId": "${offer_data.offer.agreement.key}"
            },
            "amount": ${offer_data.offer.amount?c},
            "annualTI": ${offer_data.offer.annualTI?replace(",", ".")},
            "currentDiscount": ${discount},
            "discountAmount": ${discount},
            "frequencyDescription": "${offer_data.offer.frequency}",
            "monthlyTI": "${offer_data.offer.monthlyTI?c}",
            "openingCommissionPercentage": ${offer_data.offer.commissions?replace(",", ".")},
            "plazo": ${offer_data.offer.term},
            "requestedAmount": ${offer_data.offer.amount?c},
            "totalAmount": ${amountTotalToPay},
            "estimatedCommision": ${offer_data.offer.commissions?replace(",", ".")}
        }
    },
    "person": {
        "lastFolioFiscal": "${folioFiscal}",
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
    "geolocation": {
        "latitude": "",
        "longitude": ""
    },
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
<#elseif company == "mn">
<#-- This is a freemarker template for Masnomina aggrements. -->
{
    "applicationId" : "RENEX",
    "origen": "ECSB",
    "destinoCredito": "",
    "claveServidor": "${offer_data.offer.employeeNumber?string}",
    "dependenciaGEM": "${getFieldFromDocument('carta-autorizacion-descuento', 'dependencia')}",
    "cuenta": "",
    "empresa": "${(branch.empresa.enterpriseName)!''}",
    "solicitud": "${offer_data.offer.id}",
    "fechaSolicitud": ${current_date_timestamp?c},
    "banco": "${customer_data.preApplicationData.paymentData.bankDesc! ''}",
    "imssAgrement": true,
    "tipoCredito": "NUEVO",
    "tipoDisposicion": "T",
    "clabe": "${(customer_data.preApplicationData.paymentData.clabe)! ''}",
    "oferta": {
        "montoPago": ${offer_data.offer.discount?c},
        "cat": ${offer_data.offer.cat?replace(",", ".")},
        "tasa": "${offer_data.offer.monthlyTI?c}",
        "montoPrestamo": ${offer_data.offer.amount?c},
        "plazo": ${offer_data.offer.term},
        "frecuencia": "${offer_data.offer.frequency?substring(0, 1)?upper_case}",
        "comisionApertura": ${offer_data.offer.commissions?replace(",", ".")},
        "fechaAnterior": null,
        "montoAnterior": null,
        "montototalLibranza": "",
        "cnca": true,
        "montoTotal": ${amountTotalToPay}
    },
    "convenio": {
        "agreement": "${offer_data.offer.agreement.key}",
        "agreementCRM": "${offer_data.offer.agreement.key}",
        "codigoBaseCalculo": "${agreement_data.calculationBaseCode}",
        "tipoAmortizacion": "${agreement_data.amortizationType}",
        "razonSocial": "${offer_data.offer.agreement.description}",
        "name": "${agreement_data.name}",
        "codigoCsb": "${agreement_data.csbCode}",
        "nombreCsb": "${agreement_data.csbName}",
        "codigoSector": "${agreement_data.sectorCode}",
        "branchName": "${agreement_data.businessName}",
        "distributorName": "${agreement_data.providerCapacity}"
    },
    "cliente": {
       "ingresos": "0",
        "numeroEmpleadoEmp": "${offer_data.offer.employeeNumber?string}",
        "ultimoFolioFiscal": "${folioFiscal}",
        "funcionarioPublico": "${getStringFromBoolean(customer_data.preApplicationData.applicant.pep)}",
        "parienteFuncionarioPublico": "${getStringFromBoolean(customer_data.preApplicationData.applicant.familiarPep)}",
        "codigoPuestoOcupacion": "${(customer_data.preApplicationData.applicant.occupation.key)!''}",
        "ocupacion": "${(customer_data.preApplicationData.applicant.occupation.description)!''}",
        "idDocumentData": {
            "ocr": "${(customer_data.customer.credentialData.ocr)!''}",
            "type": "IFE / INE"
        },
        "tipoRegimenFiscal": {
            "key": "${customer_data.customer.regimenFiscal.key}",
            "description": "${customer_data.customer.regimenFiscal.description}"
        },
        "satisfactorio": "Satisfactoria",
        "apellidoPaterno": "${(customer_data.customer.lastName)!''}",
        "apellidoMaterno": "${(customer_data.customer.secondLastName)!''}",
        "nombre": "${customer_data.customer.firstName!''}",
        "codigoPaisNacimiento": "MX",
        "rfc": "${(customer_data.customer.rfc)!''}",
        "curp": "${(customer_data.customer.curp)!''}",
        "sexo": "${(customer_data.customer.gender)!''}",
        "fechaNacimiento": "${(customer_data.customer.dateBirth)!''}",
        "codigoEstadoNacimiento": "${(customer_data.customer.placeBirth)!''}",
        "nacionalidad": "${(customer_data.customer.nationality.description)!''}",
        "correo": "${(customer_data.customer.email)!''}",
        "estadoCivil": "${getMaritalStatusValue((customer_data.customer.maritalStatus.key)!'')}",
        "telefonos": {
            "movil": "${getPreferredPhone(defaultAddress)}",
            "trabajo": ""
        },
        "domicilio": {
            "calle": "${defaultAddress.street!''}",
            "ciudad": "${defaultAddress.city!''}",
            "codigoEstado": "${defaultAddress.stateDesc!''}",
            "codigoPais": "${defaultAddress.country!''}",
            "colonia": "${defaultAddress.suburb!''}",
            "municipio": "${defaultAddress.township!''}",
            "numeroExterior": "${defaultAddress.externalNumber!''}",
            "numeroInterior": "${defaultAddress.internalNumber!''}",
            "cp": "${defaultAddress.zipCode!''}"
        },
        "domicilioFiscal": {
            "calle": "${fiscalAddress.street!''}",
            "ciudad": "${fiscalAddress.city!''}",
            "codigoEstado": "${fiscalAddress.stateDesc!''}",
            "codigoPais": "${fiscalAddress.country!''}",
            "colonia": "${fiscalAddress.suburb!''}",
            "municipio": "${fiscalAddress.township!''}",
            "numeroExterior": "${fiscalAddress.externalNumber!''}",
            "numeroInterior": "${fiscalAddress.internalNumber!''}",
            "cp": "${fiscalAddress.zipCode!''}"
        }
        <#if customer_data.preApplicationData.references??>
        ,"referencias": {
            <#assign reference = customer_data.preApplicationData.references?first>
            "personal": {
                "apellidoMaterno": "${reference.lastName2!''}",
                "apellidoPaterno": "${reference.lastName1!''}",
                "codigoRelacion": "05",
                "clientId": "${reference.clientId!''}",
                "nombre": "${reference.name1!''}",
                "segundoNombre": "${reference.name2!''}",
                "telefono": "${reference.cellPhone!''}",
                "telefonoFijo": "${reference.phone!''}"
            }
        }
        </#if>
    },
    "vendedor": {
        "oficina": "",
        "nombre": "Canal Digital",
        "persona": "",
        "claveImss": "",
        "rfc": "${promoter_data.name1}"
    },
    "documentPhotos": {},
    "geolocation": {
        "latitude": "",
        "longitude": ""
    },
    "firmas": {
        "cliente": "https://storage.googleapis.com/csb_puc_statics_prod/unsigned.png",
        "promotor": "https://storage.googleapis.com/csb_puc_statics_prod/unsigned.png"
    }
}
<#else>
{
    "message": "There is not a valid company ${company}"
}
</#if>