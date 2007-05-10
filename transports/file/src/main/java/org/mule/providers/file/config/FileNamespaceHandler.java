/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.providers.file.config;

import org.mule.config.spring.parsers.SimpleChildDefinitionParser;
import org.mule.config.spring.parsers.SingleElementDefinitionParser;
import org.mule.providers.file.FileConnector;
import org.mule.providers.file.FilenameParser;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Reigsters a Bean Definition Parser for handling <code><tcp:connector></code> elements.
 *
 */
public class FileNamespaceHandler extends NamespaceHandlerSupport
{
    public void init()
    {
        registerBeanDefinitionParser("connector", new SingleElementDefinitionParser(FileConnector.class, true));
        registerBeanDefinitionParser("filename-parser",
                new SimpleChildDefinitionParser("filenameParser", null, FilenameParser.class));
    }
}