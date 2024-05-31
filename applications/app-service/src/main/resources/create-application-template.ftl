<#--
This is a freemarker template that is used to dynamically build the request object
to consume the api for create application. ${}
-->
<#assign
current_date_timestamp = .now?long
promotorCompleteName = promoter_data.name1 + " " + promoter_data.lastname1
termDesc = offer_data.offer.term + " " + offer_data.offer.frequency
>
{
    "createApplicationRequestBO": {
        "applicationId": "CSB-RENEX",  
        "aplicationInfo": {
            "branch": {
                "bp": "0017002824",
                "id": "51187525",
                "name": "Equipo 360° - Digital CSB",
                "company": {
                    "bp": "0017001478",
                    "id": "51168057",
                    "name": "CONSUBANCO",
                    "acronym": "1300"
                },
                "distributor": {
                    "bp": "0017002823",
                    "id": "51187524",
                    "name": "CANAL DIGITAL",
                    "acronym": "CANDIG"
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
                    <#list customer_data.preApplicationData.documents as document>
                        <#assign file_data_matched_sec = files_data?filter(file_data -> file_data.name == document.technicalName) >
                        <#assign file_data_matched = file_data_matched_sec?first?if_exists >
                        <#if file_data_matched.storageRoute??>
                            {
                                "id": "${document.id}",
                                "technicalName": "${document.technicalName}",
                                "fileName": "${document.technicalName}",
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
                                                "value": <#if field.value??> "${field.value}" <#else> "" </#if>
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
                "clientId": "${customer_data.customer.bpId}",
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
                    "key": "string",
                    "description": "string"
                }
            },
            "promotorBp": "${promoter_data.bpId}",
            "priceGroupId": "${offer_data.offer.priceGroupId}",
            "biometricTask": {
                "taskCRMId": "string",
                "createDate": "2024-05-15",
                "probankNumber": "",
                "taskStatusCRM": {
                    "key": "string",
                    "description": "string"
                }
            }, 
            "paymentData": {
                "bankId": "${customer_data.preApplicationData.paymentData.bankId}",
                "bankDesc": "${customer_data.preApplicationData.paymentData.bankDesc}",
                "clabe": "${customer_data.preApplicationData.paymentData.clabe}",
                "paymentMethodId": "${customer_data.preApplicationData.paymentData.paymentMethodId}",
                "paymentMethodDesc": "${customer_data.preApplicationData.paymentData.paymentMethodDesc}"
            },
            "probankNumber": "${offer_data.offer.id}",
            "discountamount": 0,
            "promotorNFOFlag": true,
            "reprocessNumber": 0,
            "amountTotalToPay": 0,
            "folioApplication": "${offer_data.offer.id}",
            "sourceChannelApp": "RENEX", 

            "promotorCompleteName": "${promotorCompleteName}"
     }
    }
}