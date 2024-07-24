<#-- Freemarker template used to build the request to consume the create loan application api. -->
<#-- Variables -->
<#assign
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
        "rfc": customer_data.customer.rfc
    }
    current_date_timestamp = .now?long
    promotorCompleteName = promoter_data.name1 + " " + promoter_data.lastname1
    termDesc = offer_data.offer.term + " " + offer_data.offer.frequency
    amountTotalToPay = offer_data.offer.discount?replace(",", "")?number * offer_data.offer.term
>
<#-- Functions -->
<#function getFieldValue(field)>
    <#if field.value?has_content>
        <#return field.value>
    <#elseif fieldValues[field.technicalName]??>
        <#return fieldValues[field.technicalName]>
    <#else>
        <#return "">
    </#if>
</#function>
<#-- Template -->
{
    "createApplicationRequestBO": {
        "applicationId": "CSB-RENEX",
        "aplicationInfo": {
        <#list promoter_data.branches as branch>
            <#if branch.branchID == agreement_configuration_data.branchId>
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
            </#if>
        </#list>
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
                        "id": "071",
                        "url": "gs://csb-venta-digital/renewal/offer/55555/documents/formato-solicitud-gratuita.pdf",
                        "name": "FORMATO UNICO DE SOLICITUD GRATUITA",
                        "visible": false,
                        "fileName": "formato-solicitud-gratuita.pdf",
                        "required": true,
                        "clasification": "D",
                        "technicalName": "formato-solicitud-gratuita"
                    },
                    <#list customer_data.preApplicationData.documents as document>
                        <#assign file_data_matched_sec = files_data?filter(file_data -> file_data.name == document.technicalName) >
                        <#assign file_data_matched = file_data_matched_sec?first?if_exists >
                        <#if file_data_matched.storageRoute??>
                            {
                                "id": "${document.id}",
                                "technicalName": "${document.technicalName}",
                                "fileName": "${document.technicalName + ".pdf"}",
                                "name": "${document.name}",
                                "clasification": "${document.clasification}",
                                "url": <#if file_data_matched.storageRoute??> "${file_data_matched.storageRoute}" <#else> "" </#if>,
                                <#if document.fields??>
                                        "fields": [
                                            <#list document.fields as field>
                                            {
                                                "id": "${field.id}",
                                                "name": "${field.name}",
                                                "technicalName": "${field.technicalName}",
                                                "clasification": "${field.clasification}",
                                                "type": "${field.type}",
                                                "required": ${field.required?c},
                                                "value": "${getFieldValue(field)}"
                                            }<#if field?has_next>,</#if>
                                            </#list>
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
            "rate": ${offer_data.offer.monthlyTI?c},
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