/*
 * Copyright 2006-2012 the original author or authors.
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

package com.consol.citrus.mvn.plugin;

import com.consol.citrus.generate.*;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * @author Christoph Deppisch
 */
public class CreateTestMojoTest {

    private Prompter prompter = Mockito.mock(Prompter.class);
    
    private XmlTestGenerator testGenerator = Mockito.mock(XmlTestGenerator.class);
    private XsdXmlTestGenerator xsdXmlTestGenerator = Mockito.mock(XsdXmlTestGenerator.class);
    private WsdlXmlTestGenerator wsdlXmlTestGenerator = Mockito.mock(WsdlXmlTestGenerator.class);

    private CreateTestMojo mojo;
    
    @BeforeMethod
    public void setup() {
        mojo = new CreateTestMojo(testGenerator, xsdXmlTestGenerator, wsdlXmlTestGenerator);
        mojo.setPrompter(prompter);
    }
    
    @Test
    public void testCreate() throws PrompterException, MojoExecutionException, MojoFailureException {
        reset(prompter, testGenerator);

        when(prompter.prompt(contains("test name"))).thenReturn("FooTest");
        when(prompter.prompt(contains("author"), nullable(String.class))).thenReturn("UnknownAuthor");
        when(prompter.prompt(contains("description"), nullable(String.class))).thenReturn("TODO");
        when(prompter.prompt(contains("package"), nullable(String.class))).thenReturn("com.consol.citrus.foo");
        when(prompter.prompt(contains("framework"), any(List.class), nullable(String.class))).thenReturn("testng");
        when(prompter.prompt(contains("Create test with XML schema"), any(List.class), eq("n"))).thenReturn("n");
        when(prompter.prompt(contains("Create test with WSDL"), any(List.class), eq("n"))).thenReturn("n");
        when(prompter.prompt(contains("Confirm"), any(List.class), eq("y"))).thenReturn("y");

        when(testGenerator.withFramework(UnitFramework.TESTNG)).thenReturn(testGenerator);
        when(testGenerator.withAuthor("UnknownAuthor")).thenReturn(testGenerator);
        when(testGenerator.withDescription("TODO")).thenReturn(testGenerator);
        when(testGenerator.usePackage("com.consol.citrus.foo")).thenReturn(testGenerator);
        when(testGenerator.withName("FooTest")).thenReturn(testGenerator);
        
        mojo.execute();

        verify(testGenerator).create();
    }

    @Test
    public void testAbort() throws PrompterException, MojoExecutionException, MojoFailureException {
        reset(prompter, testGenerator);

        when(prompter.prompt(contains("test name"))).thenReturn("FooTest");
        when(prompter.prompt(contains("author"), nullable(String.class))).thenReturn("UnknownAuthor");
        when(prompter.prompt(contains("description"), nullable(String.class))).thenReturn("TODO");
        when(prompter.prompt(contains("package"), nullable(String.class))).thenReturn("com.consol.citrus.foo");
        when(prompter.prompt(contains("framework"), any(List.class), nullable(String.class))).thenReturn("testng");
        when(prompter.prompt(contains("Create test with XML schema"), any(List.class), eq("n"))).thenReturn("n");
        when(prompter.prompt(contains("Create test with WSDL"), any(List.class), eq("n"))).thenReturn("n");
        when(prompter.prompt(contains("Confirm"), any(List.class), eq("y"))).thenReturn("n");

        mojo.execute();

        verify(testGenerator, times(0)).create();
    }

    @Test
    public void testSuiteFromXsd() throws MojoExecutionException, PrompterException, MojoFailureException {
        reset(prompter, xsdXmlTestGenerator);

        when(prompter.prompt(contains("test name"))).thenReturn("BookStore");
        when(prompter.prompt(contains("path"))).thenReturn("classpath:xsd/BookStore.xsd");
        when(prompter.prompt(contains("request"))).thenReturn("BookRequest");
        when(prompter.prompt(contains("response"), nullable(String.class))).thenReturn("BookResponse");
        when(prompter.prompt(contains("author"), nullable(String.class))).thenReturn("UnknownAuthor");
        when(prompter.prompt(contains("description"), nullable(String.class))).thenReturn("TODO");
        when(prompter.prompt(contains("package"), nullable(String.class))).thenReturn("com.consol.citrus.xsd");
        when(prompter.prompt(contains("framework"), any(List.class), nullable(String.class))).thenReturn("testng");
        when(prompter.prompt(contains("Create test with XML schema"), any(List.class), eq("n"))).thenReturn("y");
        when(prompter.prompt(contains("Create test with WSDL"), any(List.class), eq("n"))).thenReturn("n");
        when(prompter.prompt(contains("Confirm"), any(List.class), eq("y"))).thenReturn("y");

        when(xsdXmlTestGenerator.withFramework(UnitFramework.TESTNG)).thenReturn(xsdXmlTestGenerator);
        when(xsdXmlTestGenerator.withAuthor("UnknownAuthor")).thenReturn(xsdXmlTestGenerator);
        when(xsdXmlTestGenerator.withDescription("TODO")).thenReturn(xsdXmlTestGenerator);
        when(xsdXmlTestGenerator.usePackage("com.consol.citrus.wsdl")).thenReturn(xsdXmlTestGenerator);

        when(xsdXmlTestGenerator.withXsd("classpath:xsd/BookStore.xsd")).thenReturn(xsdXmlTestGenerator);

        when(xsdXmlTestGenerator.withName("BookStore")).thenReturn(xsdXmlTestGenerator);

        mojo.execute();

        verify(xsdXmlTestGenerator).create();
        verify(xsdXmlTestGenerator).withXsd("classpath:xsd/BookStore.xsd");
        verify(xsdXmlTestGenerator).withRequestMessage("BookRequest");
        verify(xsdXmlTestGenerator).withResponseMessage("BookResponse");
    }

    @Test
    public void testSuiteFromXsdAbort() throws MojoExecutionException, PrompterException, MojoFailureException {
        reset(prompter, xsdXmlTestGenerator);

        when(prompter.prompt(contains("test name"))).thenReturn("BookStore");
        when(prompter.prompt(contains("path"))).thenReturn("classpath:wsdl/BookStore.wsdl");
        when(prompter.prompt(contains("request"))).thenReturn("BookRequest");
        when(prompter.prompt(contains("response"), nullable(String.class))).thenReturn("BookResponse");
        when(prompter.prompt(contains("author"), nullable(String.class))).thenReturn("UnknownAuthor");
        when(prompter.prompt(contains("description"), nullable(String.class))).thenReturn("TODO");
        when(prompter.prompt(contains("package"), nullable(String.class))).thenReturn("com.consol.citrus.wsdl");
        when(prompter.prompt(contains("framework"), any(List.class), nullable(String.class))).thenReturn("testng");
        when(prompter.prompt(contains("Create test with XML schema"), any(List.class), eq("n"))).thenReturn("y");
        when(prompter.prompt(contains("Create test with WSDL"), any(List.class), eq("n"))).thenReturn("n");
        when(prompter.prompt(contains("Confirm"), any(List.class), eq("y"))).thenReturn("n");

        when(xsdXmlTestGenerator.withFramework(UnitFramework.TESTNG)).thenReturn(xsdXmlTestGenerator);
        when(xsdXmlTestGenerator.withAuthor("UnknownAuthor")).thenReturn(xsdXmlTestGenerator);
        when(xsdXmlTestGenerator.withDescription("TODO")).thenReturn(xsdXmlTestGenerator);
        when(xsdXmlTestGenerator.usePackage("com.consol.citrus.wsdl")).thenReturn(xsdXmlTestGenerator);

        when(xsdXmlTestGenerator.withName("BookStore")).thenReturn(xsdXmlTestGenerator);

        mojo.execute();

        verify(xsdXmlTestGenerator, times(0)).create();
    }
    
    @Test
    public void testSuiteFromWsdl() throws MojoExecutionException, PrompterException, MojoFailureException {
        reset(prompter, wsdlXmlTestGenerator);

        when(prompter.prompt(contains("test name"))).thenReturn("BookStore");
        when(prompter.prompt(contains("path"))).thenReturn("classpath:wsdl/BookStore.wsdl");
        when(prompter.prompt(contains("prefix"), nullable(String.class))).thenReturn("BookStore_");
        when(prompter.prompt(contains("suffix"), nullable(String.class))).thenReturn("_Test");
        when(prompter.prompt(contains("author"), nullable(String.class))).thenReturn("UnknownAuthor");
        when(prompter.prompt(contains("description"), nullable(String.class))).thenReturn("TODO");
        when(prompter.prompt(contains("package"), nullable(String.class))).thenReturn("com.consol.citrus.wsdl");
        when(prompter.prompt(contains("actor"), any(List.class), nullable(String.class))).thenReturn("client");
        when(prompter.prompt(contains("framework"), any(List.class), nullable(String.class))).thenReturn("testng");
        when(prompter.prompt(contains("operation"), nullable(String.class))).thenReturn("all");
        when(prompter.prompt(contains("Create test with XML schema"), any(List.class), eq("n"))).thenReturn("n");
        when(prompter.prompt(contains("Create test with WSDL"), any(List.class), eq("n"))).thenReturn("y");
        when(prompter.prompt(contains("Confirm"), any(List.class), eq("y"))).thenReturn("y");

        when(wsdlXmlTestGenerator.withFramework(UnitFramework.TESTNG)).thenReturn(wsdlXmlTestGenerator);
        when(wsdlXmlTestGenerator.withAuthor("UnknownAuthor")).thenReturn(wsdlXmlTestGenerator);
        when(wsdlXmlTestGenerator.withDescription("TODO")).thenReturn(wsdlXmlTestGenerator);
        when(wsdlXmlTestGenerator.usePackage("com.consol.citrus.wsdl")).thenReturn(wsdlXmlTestGenerator);

        when(wsdlXmlTestGenerator.withWsdl("classpath:wsdl/BookStore.wsdl")).thenReturn(wsdlXmlTestGenerator);
        when(wsdlXmlTestGenerator.withNameSuffix("_Test")).thenReturn(wsdlXmlTestGenerator);

        when(wsdlXmlTestGenerator.withName("BookStore")).thenReturn(wsdlXmlTestGenerator);

        mojo.execute();

        verify(wsdlXmlTestGenerator).create();
        verify(wsdlXmlTestGenerator).withWsdl("classpath:wsdl/BookStore.wsdl");
        verify(wsdlXmlTestGenerator).withNameSuffix("_Test");
    }

    @Test
    public void testSuiteFromWsdlAbort() throws MojoExecutionException, PrompterException, MojoFailureException {
        reset(prompter, wsdlXmlTestGenerator);

        when(prompter.prompt(contains("test name"))).thenReturn("BookStore");
        when(prompter.prompt(contains("path"))).thenReturn("classpath:wsdl/BookStore.wsdl");
        when(prompter.prompt(contains("prefix"), nullable(String.class))).thenReturn("BookStore_");
        when(prompter.prompt(contains("suffix"), nullable(String.class))).thenReturn("_Test");
        when(prompter.prompt(contains("author"), nullable(String.class))).thenReturn("UnknownAuthor");
        when(prompter.prompt(contains("description"), nullable(String.class))).thenReturn("TODO");
        when(prompter.prompt(contains("package"), nullable(String.class))).thenReturn("com.consol.citrus.wsdl");
        when(prompter.prompt(contains("actor"), any(List.class), nullable(String.class))).thenReturn("client");
        when(prompter.prompt(contains("framework"), any(List.class), nullable(String.class))).thenReturn("testng");
        when(prompter.prompt(contains("operation"), nullable(String.class))).thenReturn("all");
        when(prompter.prompt(contains("Create test with XML schema"), any(List.class), eq("n"))).thenReturn("n");
        when(prompter.prompt(contains("Create test with WSDL"), any(List.class), eq("n"))).thenReturn("y");
        when(prompter.prompt(contains("Confirm"), any(List.class), eq("y"))).thenReturn("n");

        when(wsdlXmlTestGenerator.withFramework(UnitFramework.TESTNG)).thenReturn(wsdlXmlTestGenerator);
        when(wsdlXmlTestGenerator.withAuthor("UnknownAuthor")).thenReturn(wsdlXmlTestGenerator);
        when(wsdlXmlTestGenerator.withDescription("TODO")).thenReturn(wsdlXmlTestGenerator);
        when(wsdlXmlTestGenerator.usePackage("com.consol.citrus.wsdl")).thenReturn(wsdlXmlTestGenerator);

        when(wsdlXmlTestGenerator.withName("BookStore")).thenReturn(wsdlXmlTestGenerator);

        mojo.execute();

        verify(wsdlXmlTestGenerator, times(0)).create();
    }
}
