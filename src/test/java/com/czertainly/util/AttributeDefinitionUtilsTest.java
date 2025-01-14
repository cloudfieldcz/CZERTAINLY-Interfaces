package com.czertainly.util;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.*;
import com.czertainly.api.model.credential.CredentialDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.czertainly.core.util.AttributeDefinitionUtils.*;

public class AttributeDefinitionUtilsTest {

    @Test
    public void testGetAttribute() {
        String attributeName = "testAttribute";
        List<AttributeDefinition> attributes = createAttributes(attributeName, 1234);

        AttributeDefinition attribute = getAttributeDefinition(attributeName, attributes);
        Assertions.assertNotNull(attribute);
        Assertions.assertTrue(containsAttributeDefinition(attributeName, attributes));
        Assertions.assertEquals(attributes.get(0), attribute);
    }

    @Test
    public void testGetAttributeValue() {
        String attribute1Name = "testAttribute1";
        String attribute2Name = "testAttribute2";

        AttributeDefinition attribute1 = new AttributeDefinition();
        attribute1.setName(attribute1Name);
        attribute1.setValue(1234);

        AttributeDefinition attribute2 = new AttributeDefinition();
        attribute2.setName(attribute2Name);
        attribute2.setValue("value");

        List<AttributeDefinition> attributes = List.of(attribute1, attribute2);

        Integer value1 = getAttributeValue(attribute1Name, attributes);
        Assertions.assertNotNull(value1);
        Assertions.assertTrue(containsAttributeDefinition(attribute1Name, attributes));
        Assertions.assertEquals(attribute1.getValue(), value1);

        String value2 = getAttributeValue(attribute2Name, attributes);
        Assertions.assertNotNull(value2);
        Assertions.assertTrue(containsAttributeDefinition(attribute2Name, attributes));
        Assertions.assertEquals(attribute2.getValue(), value2);

        Object value3 = getAttributeValue("wrongName", attributes);
        Assertions.assertNull(value3);
        Assertions.assertFalse(containsAttributeDefinition("wrongName", attributes));
    }

    @Test
    public void testGetAttributeNameAndUuidValue() {
        String attribute1Name = "testAttribute1";

        HashMap<String, Object> attribute1Value = new HashMap<>();
        attribute1Value.put("uuid", UUID.randomUUID().toString());
        attribute1Value.put("name", "testName");

        AttributeDefinition attribute1 = new AttributeDefinition();
        attribute1.setName(attribute1Name);
        attribute1.setValue(attribute1Value);

        List<AttributeDefinition> attributes = List.of(attribute1);

        NameAndUuidDto dto = getNameAndUuidValue(attribute1Name, attributes);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(attribute1Value.get("uuid"), dto.getUuid());
        Assertions.assertEquals(attribute1Value.get("name"), dto.getName());
    }

    @Test
    public void testGetAttributeCredentialValue() {
        String attribute1Name = "testAttribute1";
        List<AttributeDefinition> credentialAttributes = createAttributes("credAttr", 987);

        HashMap<String, Object> attribute1Value = new HashMap<>();
        attribute1Value.put("uuid", UUID.randomUUID().toString());
        attribute1Value.put("name", "testName");
        attribute1Value.put("attributes", credentialAttributes);

        AttributeDefinition attribute1 = new AttributeDefinition();
        attribute1.setName(attribute1Name);
        attribute1.setValue(attribute1Value);

        List<AttributeDefinition> attributes = List.of(attribute1);

        CredentialDto dto = getCredentialValue(attribute1Name, attributes);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(attribute1Value.get("uuid"), dto.getUuid());
        Assertions.assertEquals(attribute1Value.get("name"), dto.getName());
        Assertions.assertEquals(credentialAttributes.get(0).getName(), dto.getAttributes().get(0).getName());
    }

    @Test
    public void testAttributeSerialization() {
        String attrData = "[{ \"name\": \"tokenType\", \"value\": \"PEM\" },{ \"name\": \"description\", \"value\": \"DEMO RA Profile\" },{ \"name\": \"endEntityProfile\", \"value\": { \"id\": 0, \"name\": \"DemoTLSServerEndEntityProfile\" } },{ \"name\": \"certificateProfile\", \"value\": { \"id\": 0, \"name\": \"DemoTLSServerEECertificateProfile\" } },{ \"name\": \"certificationAuthority\", \"value\": { \"id\": 0, \"name\": \"DemoServerSubCA\" } },{ \"name\": \"sendNotifications\", \"value\": false },{ \"name\": \"keyRecoverable\", \"value\": true }]";

        List<AttributeDefinition> attrs = deserialize(attrData);
        Assertions.assertNotNull(attrs);
        Assertions.assertEquals(7, attrs.size());

        NameAndIdDto endEntityProfile = getNameAndIdValue("endEntityProfile", attrs);
        Assertions.assertNotNull(endEntityProfile);
        Assertions.assertEquals(0, endEntityProfile.getId());
        Assertions.assertEquals("DemoTLSServerEndEntityProfile", endEntityProfile.getName());

        String serialized = serialize(attrs);
        Assertions.assertTrue(serialized.matches("^.*\"name\":\"tokenType\".*\"value\":\"PEM\".*$"));
        Assertions.assertTrue(serialized.matches("^.*\"name\":\"keyRecoverable\".*\"value\":true.*$"));
    }

    @Test
    public void testValidateAttributes_success() {
        String attributeName = "testAttribute1";
        String attributeId = "9379ca2c-aa51-42c8-8afd-2a2d16c99c57";
        int attributeValue = 1234;

        AttributeDefinition definition = new AttributeDefinition();
        definition.setName(attributeName);
        definition.setId(attributeId);
        definition.setType(BaseAttributeDefinitionTypes.STRING);
        definition.setRequired(true);

        AttributeDefinition attribute = new AttributeDefinition();
        attribute.setName(attributeName);
        attribute.setId(attributeId);
        attribute.setType(BaseAttributeDefinitionTypes.STRING);
        attribute.setValue(attributeValue);

        validateAttributes(List.of(definition), List.of(attribute));
    }

    @Test
    public void testValidateAttributes_failNoAttribute() {
        String attributeName = "testAttribute1";
        String attributeId = "9379ca2c-aa51-42c8-8afd-2a2d16c99c57";

        AttributeDefinition definition = new AttributeDefinition();
        definition.setName(attributeName);
        definition.setId(attributeId);
        definition.setType(BaseAttributeDefinitionTypes.STRING);
        definition.setRequired(true);

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
            // tested method
            validateAttributes(List.of(definition), List.of())
        );

        Assertions.assertEquals(1, exception.getErrors().size());
    }

    @Test
    public void testValidateAttributes_failNoValue() {
        String attributeName = "testAttribute1";
        String attributeId = "9379ca2c-aa51-42c8-8afd-2a2d16c99c57";

        AttributeDefinition definition = new AttributeDefinition();
        definition.setName(attributeName);
        definition.setId(attributeId);
        definition.setType(BaseAttributeDefinitionTypes.STRING);
        definition.setRequired(true);

        AttributeDefinition attribute = new AttributeDefinition();
        attribute.setName(attributeName);
        attribute.setId(attributeId);
        attribute.setType(BaseAttributeDefinitionTypes.STRING);
        attribute.setValue(null); // cause or failure

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                // tested method
                validateAttributes(List.of(definition), List.of(attribute))
        );

        Assertions.assertEquals(1, exception.getErrors().size());
    }

    @Test
    public void testValidateAttributes_regex() {
        String attributeName = "testAttribute1";
        String attributeId = "9379ca2c-aa51-42c8-8afd-2a2d16c99c57";
        String attributeValue = "1234";
        String validationRegex = "^\\d{4}$";

        Assertions.assertTrue(attributeValue.matches(validationRegex));

        AttributeDefinition definition = new AttributeDefinition();
        definition.setName(attributeName);
        definition.setId(attributeId);
        definition.setType(BaseAttributeDefinitionTypes.STRING);
        definition.setValidationRegex(validationRegex);
        definition.setRequired(true);

        AttributeDefinition attribute = new AttributeDefinition();
        attribute.setName(attributeName);
        attribute.setId(attributeId);
        attribute.setType(BaseAttributeDefinitionTypes.STRING);
        attribute.setValue(attributeValue);

        validateAttributes(List.of(definition), List.of(attribute));
    }

    @Test
    public void testValidateAttributes_regexFail() {
        String attributeName = "testAttribute1";
        String attributeId = "9379ca2c-aa51-42c8-8afd-2a2d16c99c57";
        String attributeValue = "12345";
        String validationRegex = "^\\d{4}$";

        Assertions.assertFalse(attributeValue.matches(validationRegex));

        AttributeDefinition definition = new AttributeDefinition();
        definition.setName(attributeName);
        definition.setId(attributeId);
        definition.setType(BaseAttributeDefinitionTypes.STRING);
        definition.setValidationRegex(validationRegex);
        definition.setRequired(true);

        AttributeDefinition attribute = new AttributeDefinition();
        attribute.setName(attributeName);
        attribute.setId(attributeId);
        attribute.setType(BaseAttributeDefinitionTypes.STRING);
        attribute.setValue(attributeValue);

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                // tested method
                validateAttributes(List.of(definition), List.of(attribute))
        );

        Assertions.assertEquals(1, exception.getErrors().size());
    }

    @Test
    public void testValidateAttributeCallback_success() {
        Set<AttributeCallbackMapping> mappings = new HashSet<>();
        mappings.add(new AttributeCallbackMapping(
                "credentialKind",
                AttributeValueTarget.PATH_VARIABLE,
                "softKeyStore"));

        AttributeCallback callback = new AttributeCallback();
        callback.setCallbackContext("v1/test");
        callback.setCallbackMethod("GET");
        callback.setMappings(mappings);
        callback.setPathVariables(Map.ofEntries(Map.entry("credentialKind", "softKeyStore")));

        validateCallback(callback); // should not throw exception
    }

    @Test
    public void testValidateAttributeCallback_fail() {
        Set<AttributeCallbackMapping> mappings = new HashSet<>();
        mappings.add(new AttributeCallbackMapping(
                "credentialKind",
                AttributeValueTarget.PATH_VARIABLE,
                "softKeyStore"));
        mappings.add(new AttributeCallbackMapping(
                "fromAttribute",
                BaseAttributeDefinitionTypes.CREDENTIAL,
                "toQueryParam",
                AttributeValueTarget.REQUEST_PARAMETER));
        mappings.add(new AttributeCallbackMapping(
                "fromAttribute",
                "toBodyKey",
                AttributeValueTarget.BODY));

        AttributeCallback callback = new AttributeCallback();
        callback.setCallbackContext("core/getCredentials");
        callback.setCallbackMethod("GET");
        callback.setMappings(mappings);
        callback.setPathVariables(Map.ofEntries(Map.entry("credentialKind", "softKeyStore")));
        callback.setQueryParameters(Map.ofEntries(Map.entry("toQueryParam", 1234)));


        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> validateCallback(callback));

        Assertions.assertNotNull(exception.getErrors());
        Assertions.assertFalse(exception.getErrors().isEmpty());
        Assertions.assertEquals(2, exception.getErrors().size());
    }
}
