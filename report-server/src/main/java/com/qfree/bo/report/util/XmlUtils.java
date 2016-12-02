package com.qfree.bo.report.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class XmlUtils {

	public static void validate(String xml) throws SAXException, IOException {
		XMLReader parser = XMLReaderFactory.createXMLReader();
		parser.setContentHandler(new DefaultHandler());
		InputSource source = new InputSource(new ByteArrayInputStream(xml.getBytes()));
		parser.parse(source);
	}

}
