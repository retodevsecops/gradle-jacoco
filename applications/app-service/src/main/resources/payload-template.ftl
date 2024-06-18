<#--
    This is a freemarker template that is used to dynamically build the payload object
    to consume the developer api for document generation.
-->
<#assign
current_date_timestamp = .now?long
amount = offer_data.offer.amount?replace(",", "")?number?c
discount = offer_data.offer.discount?replace(",", "")?number?c
>
{
"id": "${offer_data.offer.id}",
"created_at": "${current_date_timestamp?c}",
"contactInformation": {
"email": "${customer_data.customer.email?lower_case!''}",
"phone": "${customer_data.customer.phone!''}",
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
"numeroEmpleado": "${offer_data.offer.employeeNumber?string}"
},
"idDocumentData": {
"ocr": "${customer_data.preApplicationData.applicant.identificationNumber}",
"type": "${customer_data.preApplicationData.applicant.identificationType}"
},
"offer": {
"quoter": {
"CAT": ${offer_data.offer.cat?replace(",", ".")},
"agreemen": {
"branch": {
"empresa": {
"businessName": "La Tenda Mexico S.A. de C.V."
}
},
"convenioId": "${offer_data.offer.agreement.key}"
},
"amount": ${amount},
"annualTI": ${offer_data.offer.annualTI?replace(",", ".")},
"currentDiscount": ${discount},
"discountAmount": ${discount},
"frequencyDescription": "${offer_data.offer.frequency}",
"monthlyTI": ${offer_data.offer.monthlyTI?replace(",", ".")},
"openingCommissionPercentage": ${offer_data.offer.commissions?replace(",", ".")},
"plazo": ${offer_data.offer.term},
"requestedAmount": ${amount},
"totalAmount": ${amount}
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
"residenceCountry": "MX",
"countryBirth": {
"description": "México",
"key": "MX"
},
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
"placeBirth": "${customer_data.customer.placeBirth}",
"rfc": "${customer_data.customer.rfc}",
"spouseLastName1": "${customer_data.preApplicationData.applicant.spouseLastName1}",
"spouseLastName2": "${customer_data.preApplicationData.applicant.spouseLastName2}",
"spouseName1": "${customer_data.preApplicationData.applicant.spouseName1}",
"spouseName2": "${customer_data.preApplicationData.applicant.spouseName2}"
},
"privateDeposit": {
"banco": "${customer_data.preApplicationData.paymentData.bankId}",
"bancoText": "${customer_data.preApplicationData.paymentData.bankDesc}",
"cbanc": "${customer_data.preApplicationData.paymentData.clabe}",
"descriptionPaymentMethod": "${customer_data.preApplicationData.paymentData.paymentMethodDesc}",
"metodoPago": "${customer_data.preApplicationData.paymentData.paymentMethodId}"
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
"origin": "RENEX"
}