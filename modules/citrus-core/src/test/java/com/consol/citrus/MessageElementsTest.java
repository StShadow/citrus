/*
 * Copyright 2006-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus;

import static org.easymock.EasyMock.*;

import java.util.*;

import org.easymock.EasyMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.consol.citrus.actions.ReceiveMessageAction;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.message.MessageReceiver;
import com.consol.citrus.testng.AbstractBaseTest;
import com.consol.citrus.validation.MessageValidator;
import com.consol.citrus.validation.context.ValidationContext;

/**
 * @author Christoph Deppisch
 */
public class MessageElementsTest extends AbstractBaseTest {
    @Autowired
    MessageValidator<ValidationContext> validator;
    
    MessageReceiver messageReceiver = EasyMock.createMock(MessageReceiver.class);
    
    ReceiveMessageAction receiveMessageBean;
    
    @Override
    @BeforeMethod
    public void setup() {
        super.setup();
        
        receiveMessageBean = new ReceiveMessageAction();
        receiveMessageBean.setMessageReceiver(messageReceiver);
        receiveMessageBean.setValidator(validator);        
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testValidateMessageElements() {
        reset(messageReceiver);
        
        Message message = MessageBuilder.withPayload("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'>text-value</sub-elementA>"
                            + "<sub-elementB attribute='B'>text-value</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>").build();
        
        expect(messageReceiver.receive()).andReturn(message);
        replay(messageReceiver);
        
        HashMap<String, String> validateMessageElements = new HashMap<String, String>();
        validateMessageElements.put("//root/element/sub-elementA", "text-value");
        validateMessageElements.put("//sub-elementB", "text-value");
        
        receiveMessageBean.setPathValidationExpressions(validateMessageElements);
        
        receiveMessageBean.execute(context);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testValidateEmptyMessageElements() {
        reset(messageReceiver);
        
        Message message = MessageBuilder.withPayload("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'></sub-elementA>"
                            + "<sub-elementB attribute='B'></sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>").build();
        
        expect(messageReceiver.receive()).andReturn(message);
        replay(messageReceiver);
        
        HashMap<String, String> validateMessageElements = new HashMap<String, String>();
        validateMessageElements.put("//root/element/sub-elementA", "");
        validateMessageElements.put("//sub-elementB", "");
        
        receiveMessageBean.setPathValidationExpressions(validateMessageElements);
        
        receiveMessageBean.execute(context);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testValidateEmptyMessageAttributes() {
        reset(messageReceiver);
        
        Message message = MessageBuilder.withPayload("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute=''>text-value</sub-elementA>"
                            + "<sub-elementB attribute=''>text-value</sub-elementB>"
                            + "<sub-elementC attribute=''>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>").build();
        
        expect(messageReceiver.receive()).andReturn(message);
        replay(messageReceiver);
        
        HashMap<String, String> validateMessageElements = new HashMap<String, String>();
        validateMessageElements.put("//root/element/sub-elementA/@attribute", "");
        validateMessageElements.put("//root/element/sub-elementB/@attribute", "");
        validateMessageElements.put("//root/element/sub-elementC/@attribute", "");
        
        receiveMessageBean.setPathValidationExpressions(validateMessageElements);
        
        receiveMessageBean.execute(context);
    }
    
    @Test(expectedExceptions = {ValidationException.class})
    @SuppressWarnings("unchecked")
    public void testValidateNullElements() {
        reset(messageReceiver);
        
        Message message = MessageBuilder.withPayload("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'></sub-elementA>"
                            + "<sub-elementB attribute='B'></sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>").build();
        
        expect(messageReceiver.receive()).andReturn(message);
        replay(messageReceiver);
        
        HashMap<String, String> validateMessageElements = new HashMap<String, String>();
        validateMessageElements.put("//root/element/sub-elementA", "null");
        validateMessageElements.put("//sub-elementB", "null");
        
        receiveMessageBean.setPathValidationExpressions(validateMessageElements);
        
        receiveMessageBean.execute(context);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testValidateMessageElementAttributes() {
        reset(messageReceiver);
        
        Message message = MessageBuilder.withPayload("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'>text-value</sub-elementA>"
                            + "<sub-elementB attribute='B'>text-value</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>").build();
        
        expect(messageReceiver.receive()).andReturn(message);
        replay(messageReceiver);
        
        HashMap<String, String> validateMessageElements = new HashMap<String, String>();
        validateMessageElements.put("//root/element/sub-elementA/@attribute", "A");
        validateMessageElements.put("//sub-elementB/@attribute", "B");
        
        receiveMessageBean.setPathValidationExpressions(validateMessageElements);
        
        receiveMessageBean.execute(context);
    }
    
    @Test(expectedExceptions = {CitrusRuntimeException.class})
    @SuppressWarnings("unchecked")
    public void testValidateMessageElementsWrongExpectedElement() {
        reset(messageReceiver);
        
        Message message = MessageBuilder.withPayload("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'>text-value</sub-elementA>"
                            + "<sub-elementB attribute='B'>text-value</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>").build();
        
        expect(messageReceiver.receive()).andReturn(message);
        replay(messageReceiver);
        
        HashMap<String, String> validateMessageElements = new HashMap<String, String>();
        validateMessageElements.put("//root/element/sub-element-wrong", "text-value");
        validateMessageElements.put("//sub-element-wrong", "text-value");
        
        receiveMessageBean.setPathValidationExpressions(validateMessageElements);
        
        receiveMessageBean.execute(context);
    }
    
    @Test(expectedExceptions = {ValidationException.class})
    @SuppressWarnings("unchecked")
    public void testValidateMessageElementsWrongExpectedValue() {
        reset(messageReceiver);
        
        Message message = MessageBuilder.withPayload("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'>text-value</sub-elementA>"
                            + "<sub-elementB attribute='B'>text-value</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>").build();
        
        expect(messageReceiver.receive()).andReturn(message);
        replay(messageReceiver);
        
        HashMap<String, String> validateMessageElements = new HashMap<String, String>();
        validateMessageElements.put("//root/element/sub-elementA", "text-value-wrong");
        validateMessageElements.put("//sub-elementB", "text-value-wrong");
        
        receiveMessageBean.setPathValidationExpressions(validateMessageElements);
        
        receiveMessageBean.execute(context);
    }
    
    @Test(expectedExceptions = {ValidationException.class})
    @SuppressWarnings("unchecked")
    public void testValidateMessageElementAttributesWrongExpectedValue() {
        reset(messageReceiver);
        
        Message message = MessageBuilder.withPayload("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'>text-value</sub-elementA>"
                            + "<sub-elementB attribute='B'>text-value</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>").build();
        
        expect(messageReceiver.receive()).andReturn(message);
        replay(messageReceiver);
        
        HashMap<String, String> validateMessageElements = new HashMap<String, String>();
        validateMessageElements.put("//root/element/sub-elementA/@attribute", "wrong-value");
        validateMessageElements.put("//sub-elementB/@attribute", "wrong-value");
        
        receiveMessageBean.setPathValidationExpressions(validateMessageElements);
        
        receiveMessageBean.execute(context);
    }
    
    @Test(expectedExceptions = {CitrusRuntimeException.class})
    @SuppressWarnings("unchecked")
    public void testValidateMessageElementAttributesWrongExpectedAttribute() {
        reset(messageReceiver);
        
        Message message = MessageBuilder.withPayload("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'>text-value</sub-elementA>"
                            + "<sub-elementB attribute='B'>text-value</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>").build();
        
        expect(messageReceiver.receive()).andReturn(message);
        replay(messageReceiver);
        
        HashMap<String, String> validateMessageElements = new HashMap<String, String>();
        validateMessageElements.put("//root/element/sub-elementA/@attribute-wrong", "A");
        validateMessageElements.put("//sub-elementB/@attribute-wrong", "B");
        
        receiveMessageBean.setPathValidationExpressions(validateMessageElements);
        
        receiveMessageBean.execute(context);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testSetMessageElements() {
        reset(messageReceiver);
        
        Message message = MessageBuilder.withPayload("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'>text-value</sub-elementA>"
                            + "<sub-elementB attribute='B'>text-value</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>").build();
        
        expect(messageReceiver.receive()).andReturn(message);
        replay(messageReceiver);
        
        receiveMessageBean.setMessageData("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'>to be overwritten</sub-elementA>"
                            + "<sub-elementB attribute='B'>to be overwritten</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>");
        
        HashMap<String, String> messageElements = new HashMap<String, String>();
        messageElements.put("//root/element/sub-elementA", "text-value");
        messageElements.put("//sub-elementB", "text-value");
        
        receiveMessageBean.setMessageElements(messageElements);
        
        receiveMessageBean.execute(context);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testSetMessageElementsUsingEmptyString() {
        reset(messageReceiver);
        
        Message message = MessageBuilder.withPayload("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'></sub-elementA>"
                            + "<sub-elementB attribute='B'></sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>").build();
        
        expect(messageReceiver.receive()).andReturn(message);
        replay(messageReceiver);
        
        receiveMessageBean.setMessageData("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'>to be overwritten</sub-elementA>"
                            + "<sub-elementB attribute='B'>to be overwritten</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>");
        
        HashMap<String, String> messageElements = new HashMap<String, String>();
        messageElements.put("//root/element/sub-elementA", "");
        messageElements.put("//sub-elementB", "");
        
        receiveMessageBean.setMessageElements(messageElements);
        
        receiveMessageBean.execute(context);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testSetMessageElementsAndValidate() {
        reset(messageReceiver);
        
        Message message = MessageBuilder.withPayload("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'>text-value</sub-elementA>"
                            + "<sub-elementB attribute='B'>text-value</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>").build();
        
        expect(messageReceiver.receive()).andReturn(message);
        replay(messageReceiver);
        
        receiveMessageBean.setMessageData("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'>to be overwritten</sub-elementA>"
                            + "<sub-elementB attribute='B'>to be overwritten</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>");
        
        HashMap<String, String> messageElements = new HashMap<String, String>();
        messageElements.put("//root/element/sub-elementA", "text-value");
        messageElements.put("//sub-elementB", "text-value");
        
        receiveMessageBean.setMessageElements(messageElements);
        
        HashMap<String, String> validateElements = new HashMap<String, String>();
        validateElements.put("//root/element/sub-elementA", "text-value");
        validateElements.put("//sub-elementB", "text-value");
        
        receiveMessageBean.setPathValidationExpressions(validateElements);
        
        receiveMessageBean.execute(context);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testSetMessageElementAttributes() {
        reset(messageReceiver);
        
        Message message = MessageBuilder.withPayload("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'>text-value</sub-elementA>"
                            + "<sub-elementB attribute='B'>text-value</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>").build();
        
        expect(messageReceiver.receive()).andReturn(message);
        replay(messageReceiver);
        
        receiveMessageBean.setMessageData("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='to be overwritten'>text-value</sub-elementA>"
                            + "<sub-elementB attribute='to be overwritten'>text-value</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>");
        
        HashMap<String, String> messageElements = new HashMap<String, String>();
        messageElements.put("//root/element/sub-elementA/@attribute", "A");
        messageElements.put("//sub-elementB/@attribute", "B");
        
        receiveMessageBean.setMessageElements(messageElements);
        
        receiveMessageBean.execute(context);
    }
    
    @Test(expectedExceptions = {CitrusRuntimeException.class})
    @SuppressWarnings("unchecked")
    public void testSetMessageElementsError() {
        reset(messageReceiver);
        
        Message message = MessageBuilder.withPayload("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'>text-value</sub-elementA>"
                            + "<sub-elementB attribute='B'>text-value</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>").build();
        
        expect(messageReceiver.receive()).andReturn(message);
        replay(messageReceiver);
        
        receiveMessageBean.setMessageData("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'>to be overwritten</sub-elementA>"
                            + "<sub-elementB attribute='B'>to be overwritten</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>");
        
        HashMap<String, String> messageElements = new HashMap<String, String>();
        messageElements.put("//root/element/sub-element-wrong", "text-value");
        messageElements.put("//sub-element-wrong", "text-value");
        
        receiveMessageBean.setMessageElements(messageElements);
        
        receiveMessageBean.execute(context);
    }
    
    @Test(expectedExceptions = {CitrusRuntimeException.class})
    @SuppressWarnings("unchecked")
    public void testSetMessageElementAttributesError() {
        reset(messageReceiver);
        
        Message message = MessageBuilder.withPayload("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'>text-value</sub-elementA>"
                            + "<sub-elementB attribute='B'>text-value</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>").build();
        
        expect(messageReceiver.receive()).andReturn(message);
        replay(messageReceiver);
        
        receiveMessageBean.setMessageData("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='to be overwritten'>text-value</sub-elementA>"
                            + "<sub-elementB attribute='to be overwritten'>text-value</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>");
        
        HashMap<String, String> messageElements = new HashMap<String, String>();
        messageElements.put("//root/element/sub-elementA/@attribute-wrong", "A");
        messageElements.put("//sub-elementB/@attribute-wrong", "B");
        
        receiveMessageBean.setMessageElements(messageElements);
        
        receiveMessageBean.execute(context);
    }
    
    @Test(expectedExceptions = {CitrusRuntimeException.class})
    @SuppressWarnings("unchecked")
    public void testSetMessageElementAttributesErrorWrongElement() {
        reset(messageReceiver);
        
        Message message = MessageBuilder.withPayload("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'>text-value</sub-elementA>"
                            + "<sub-elementB attribute='B'>text-value</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>").build();
        
        expect(messageReceiver.receive()).andReturn(message);
        replay(messageReceiver);
        
        receiveMessageBean.setMessageData("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='to be overwritten'>text-value</sub-elementA>"
                            + "<sub-elementB attribute='to be overwritten'>text-value</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>");
        
        HashMap<String, String> messageElements = new HashMap<String, String>();
        messageElements.put("//root/element/sub-elementA-wrong/@attribute", "A");
        messageElements.put("//sub-elementB-wrong/@attribute", "B");
        
        receiveMessageBean.setMessageElements(messageElements);
        
        receiveMessageBean.execute(context);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExtractMessageElements() {
        reset(messageReceiver);
        
        Message message = MessageBuilder.withPayload("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'>text-value</sub-elementA>"
                            + "<sub-elementB attribute='B'>text-value</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>").build();
        
        expect(messageReceiver.receive()).andReturn(message);
        replay(messageReceiver);
        
        receiveMessageBean.setMessageData("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                    + "<sub-elementA attribute='A'>text-value</sub-elementA>"
                    + "<sub-elementB attribute='B'>text-value</sub-elementB>"
                    + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                + "</element>" 
                + "</root>");
        
        HashMap<String, String> extractMessageElements = new HashMap<String, String>();
        extractMessageElements.put("//root/element/sub-elementA", "${valueA}");
        extractMessageElements.put("//root/element/sub-elementB", "${valueB}");
        
        receiveMessageBean.setExtractMessageElements(extractMessageElements);
        
        receiveMessageBean.execute(context);
        
        Assert.assertTrue(context.getVariables().containsKey("valueA"));
        Assert.assertEquals(context.getVariables().get("valueA"), "text-value");
        Assert.assertTrue(context.getVariables().containsKey("valueB"));
        Assert.assertEquals(context.getVariables().get("valueB"), "text-value");
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testExtractMessageAttributes() {
        reset(messageReceiver);
        
        Message message = MessageBuilder.withPayload("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'>text-value</sub-elementA>"
                            + "<sub-elementB attribute='B'>text-value</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>").build();
        
        expect(messageReceiver.receive()).andReturn(message);
        replay(messageReceiver);
        
        receiveMessageBean.setMessageData("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                    + "<sub-elementA attribute='A'>text-value</sub-elementA>"
                    + "<sub-elementB attribute='B'>text-value</sub-elementB>"
                    + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                + "</element>" 
                + "</root>");
        
        HashMap<String, String> extractMessageElements = new HashMap<String, String>();
        extractMessageElements.put("//root/element/sub-elementA/@attribute", "${valueA}");
        extractMessageElements.put("//root/element/sub-elementB/@attribute", "${valueB}");
        
        receiveMessageBean.setExtractMessageElements(extractMessageElements);
        
        receiveMessageBean.execute(context);
        
        Assert.assertTrue(context.getVariables().containsKey("valueA"));
        Assert.assertEquals(context.getVariables().get("valueA"), "A");
        Assert.assertTrue(context.getVariables().containsKey("valueB"));
        Assert.assertEquals(context.getVariables().get("valueB"), "B");
    }
    
    @Test(expectedExceptions = {CitrusRuntimeException.class})
    @SuppressWarnings("unchecked")
    public void testExtractMessageElementsForWrongElement() {
        reset(messageReceiver);
        
        Message message = MessageBuilder.withPayload("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'>text-value</sub-elementA>"
                            + "<sub-elementB attribute='B'>text-value</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>").build();
        
        expect(messageReceiver.receive()).andReturn(message);
        replay(messageReceiver);
        
        receiveMessageBean.setMessageData("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                    + "<sub-elementA attribute='A'>text-value</sub-elementA>"
                    + "<sub-elementB attribute='B'>text-value</sub-elementB>"
                    + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                + "</element>" 
                + "</root>");
        
        HashMap<String, String> extractMessageElements = new HashMap<String, String>();
        extractMessageElements.put("//root/element/sub-element-wrong", "${valueA}");
        extractMessageElements.put("//element/sub-element-wrong", "${valueB}");
        
        receiveMessageBean.setExtractMessageElements(extractMessageElements);
        
        receiveMessageBean.execute(context);
        
        Assert.assertFalse(context.getVariables().containsKey("valueA"));
        Assert.assertFalse(context.getVariables().containsKey("valueB"));
    }
    
    @Test(expectedExceptions = {CitrusRuntimeException.class})
    @SuppressWarnings("unchecked")
    public void testExtractMessageElementsForWrongAtribute() {
        reset(messageReceiver);
        
        Message message = MessageBuilder.withPayload("<root>"
                        + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                            + "<sub-elementA attribute='A'>text-value</sub-elementA>"
                            + "<sub-elementB attribute='B'>text-value</sub-elementB>"
                            + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                        + "</element>" 
                        + "</root>").build();
        
        expect(messageReceiver.receive()).andReturn(message);
        replay(messageReceiver);
        
        receiveMessageBean.setMessageData("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value' >"
                    + "<sub-elementA attribute='A'>text-value</sub-elementA>"
                    + "<sub-elementB attribute='B'>text-value</sub-elementB>"
                    + "<sub-elementC attribute='C'>text-value</sub-elementC>"
                + "</element>" 
                + "</root>");
        
        HashMap<String, String> extractMessageElements = new HashMap<String, String>();
        extractMessageElements.put("//root/element/sub-elementA/@attribute-wrong", "${attributeA}");
        
        receiveMessageBean.setExtractMessageElements(extractMessageElements);
        
        receiveMessageBean.execute(context);
        
        Assert.assertFalse(context.getVariables().containsKey("attributeA"));
    }
}
