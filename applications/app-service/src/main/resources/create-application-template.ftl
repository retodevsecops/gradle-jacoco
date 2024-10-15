<#-- Freemarker template used to build the request to consume the create loan application api. -->
<#-- Variables -->
<#assign
    months = {
        "ENE": "01", "FEB": "02", "MAR": "03", "ABR": "04", "MAY": "05", "JUN": "06",
        "JUL": "07", "AGO": "08", "SEP": "09", "OCT": "10", "NOV": "11", "DIC": "12"
    }
    fieldValues = {
        "monto-a-liquidar": offer_data.offer.amount?c,
        "aplica-mismo-dcsp": "true",
        "nombre1": customer_data.customer.firstName,
        "nombre2": customer_data.customer.secondName,
        "apellido-paterno": customer_data.customer.lastName,
        "apellido-materno": customer_data.customer.secondLastName,
        "clabe": customer_data.preApplicationData.paymentData.clabe,
        "banco": customer_data.preApplicationData.paymentData.bankDesc,
        "numero-empleado": offer_data.offer.employeeNumber,
        "rfc": customer_data.customer.rfc,
        "folio-fiscal": getFolioFiscal(ocr_documents_data)?replace("-", "")
    }
    current_date_timestamp = .now?long
    promotorCompleteName = promoter_data.name1 + " " + promoter_data.lastname1
    termDesc = offer_data.offer.term + " " + offer_data.offer.frequency
    amountTotalToPay = offer_data.offer.discount?replace(",", "")?number * offer_data.offer.term
    fileConsNom151 = files_data?filter(file -> file.name == offer_data.offer.id)?first
    branch = promoter_data.branches?filter(branch -> branch.branchID == agreement_configuration_data.branchId)?first
    doc191 = customer_data.preApplicationData.documents?filter(doc -> doc.technicalName == "pantalla-sistema-externo")?first
    doc191Fields = doc191.fields?filter(field -> field.technicalName == "capacidad-pago" || field.technicalName == "fecha-ingreso" || field.technicalName == "puesto")
>
<#-- Functions -->
<#function getFieldValue(field)>
    <#if fieldValues[field.technicalName]??>
        <#return fieldValues[field.technicalName]>
    <#else>
        <#return field.value>
    </#if>
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
<#-- Macros -->
<#macro renderFields fields>
    <#list fields as field>
    {
        "id": "${field.id}",
        "name": "${field.name}",
        "technicalName": "${field.technicalName}",
        "clasification": "${field.clasification}",
        "type": "${field.type}",
        "required": ${field.required?c},
        "value": "${getFieldValue(field)?replace("\"", "")?replace("\\", "")}"
    }<#if field?has_next>,</#if>
    </#list>
</#macro>
<#-- Template -->
{
    "createApplicationRequestBO": {
        "applicationId": "CSB-RENEX",
        "aplicationInfo": {
            "branch": {
                "id": "${branch.branchID}",
                "bp": "${branch.branchBPID}",
                "name": "${branch.branchName}",
                "company": {
                    "id": "${branch.empresa.enterpriseID}",
                    "bp": "${branch.empresa.enterpriseBPID}",
                    "name": "${branch.empresa.enterpriseName}",
                    "acronym": "${branch.empresa.enterpriseSigla}"
                },
                "distributor": {
                    "id": "${branch.distribuidor.distributorID}",
                    "bp": "${branch.distribuidor.distributorBPID}",
                    "name": "${branch.distribuidor.distributorName}",
                    "acronym": "${branch.distribuidor.distributorSigla}"
                },
                "branchNFOFlag": true
            },
            "cat": ${offer_data.offer.cat?c},
            "amount": ${offer_data.offer.amount?c},
            "isCNCA": true,
            "termDesc": "${termDesc}",
            "agreement": {
                "id": "${offer_data.offer.agreement.key}",
                "name": "${offer_data.offer.agreement.description}",
                "group": "${customer_data.preApplicationData.agreement.group}",
                "product": {
                    "id": "${offer_data.offer.product.key}",
                    "name": "${offer_data.offer.product.description}",
                    "category": "${customer_data.preApplicationData.agreement.product.category}",
                    "shortName": "${customer_data.preApplicationData.agreement.product.shortName}",
                    "paymentFrecuencyId": "${customer_data.preApplicationData.agreement.product.paymentFrecuencyId}",
                    "paymentFrecuencyDesc": "${customer_data.preApplicationData.agreement.product.paymentFrecuencyDesc}"
                },
                "brmsCode": "${customer_data.preApplicationData.agreement.brmsCode}",
                "documents": [
                    {
                        "id": "999",
                        "technicalName": "opp-constancia-conservacion-digital",
                        "fileName": "${fileConsNom151.name + ".cons"}",
                        "name": "CONSTANCIA DE CONSERVACION",
                        "clasification": "D",
                        "url": "${fileConsNom151.storageRoute}",
                        "required": true,
                        "visible": false
                    },
                    <#list customer_data.preApplicationData.documents as document>
                        <#assign file_data_matched = files_data?filter(file_data -> file_data.name == document.technicalName)?first!>
                        <#if file_data_matched.storageRoute??>
                            {
                                "id": "${document.id}",
                                "technicalName": "${document.technicalName}",
                                "fileName": "${document.technicalName + ".pdf"}",
                                "name": "${document.name}",
                                "clasification": "${document.clasification}",
                                "url": "${file_data_matched.storageRoute!''}",
                                <#if document.fields??>
                                "fields": [
                                    <@renderFields fields=document.fields/>
                                    <#if document.technicalName == "recibo-nomina">
                                    ,<@renderFields fields=doc191Fields/>
                                    </#if>
                                ],
                                </#if>
                                "required": ${document.required?c},
                                "visible": ${document.visible?c}
                            }<#if document?has_next>,</#if>
                        </#if>
                    </#list>
                  ],
                "shortName": "${customer_data.preApplicationData.agreement.shortName}",
                "shortGroup": "${customer_data.preApplicationData.agreement.shortGroup}",
                "convenioNFOFlag": true
            },
            "applicant": {
                "bp": "${customer_data.customer.bpId}",
                "clientId": "${customer_data.customer.clientId}",
                "curp": "${customer_data.customer.curp}",
                "rfc": "${customer_data.customer.rfc}",
                "name1": "${customer_data.customer.firstName}",
                "name2": "${customer_data.customer.secondName}",
                "lastName1": "${customer_data.customer.lastName}",
                "lastName2": "${customer_data.customer.secondLastName}",
                "credits": [
                    <#list offer_data.offer.creditList as credit>
                    {
                        "amount": ${credit.capital?c},
                        "letter": {
                            "iva": ${credit.iva?c},
                            "monto": ${credit.totalLiquidacion?c},
                            "interes": ${credit.interes?c}
                        },
                        "accountId": "${credit.creditNumber?c}"
                    }<#if credit?has_next>,</#if>
                    </#list>
                ],
                "regimenFiscal": {
                    "key": "${customer_data.customer.regimenFiscal.key}",
                    "description": "${customer_data.customer.regimenFiscal.description}"
                }
            },
            "promotorBp": "${promoter_data.bpId}",
            "priceGroupId": "${offer_data.offer.priceGroupId}",
            <#if biometric_task_data.biometricTaskId?has_content>
            "biometricTask": {
                "taskCRMId": "${biometric_task_data.biometricTaskId}",
                "createDate": "${biometric_task_data.biometricTaskDate}",
                "probankNumber": "${biometric_task_data.probankFolio}",
                "taskStatusCRM": {
                    "key": "E0002",
                    "description": "Completada"
                }
            },
            </#if>
            "paymentData": {
                "bankId": "${customer_data.preApplicationData.paymentData.bankId}",
                "bankDesc": "${customer_data.preApplicationData.paymentData.bankDesc}",
                "clabe": "${customer_data.preApplicationData.paymentData.clabe}",
                "paymentMethodId": "${customer_data.preApplicationData.paymentData.paymentMethodId}",
                "paymentMethodDesc": "${customer_data.preApplicationData.paymentData.paymentMethodDesc}"
            },
            "probankNumber": "${offer_data.offer.id}",
            "rate": ${(offer_data.offer.monthlyTI / 100)?string("0.######")},
            "discountamount": ${offer_data.offer.discount?c},
            "promotorNFOFlag": true,
            "reprocessNumber": 0,
            "amountTotalToPay": ${amountTotalToPay?c},
            "folioApplication": "${offer_data.offer.id}",
            "sourceChannelApp": "RENEX",
            "promotorCompleteName": "${promotorCompleteName}"
        }
    }
}