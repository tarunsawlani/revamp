package com.amaropticals.common;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amaropticals.model.CreateInvoiceRequest;

public class PDFUtils {
	private final static Logger LOGGER = LoggerFactory.getLogger(PDFUtils.class);

	private static final FopFactory fopFactory = FopFactory.newInstance(new File("./pdf.xconf").toURI());
	private static final TransformerFactory factory = TransformerFactory.newInstance();


	public byte[] convertToPDF(CreateInvoiceRequest model, String invoiceXsl) throws IOException, FOPException, TransformerException {

		JAXBContext jaxbContext;
		ByteArrayOutputStream sw = new ByteArrayOutputStream();

		try {
			jaxbContext = JAXBContext.newInstance(CreateInvoiceRequest.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(model, sw);
			sw.close();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String xmlString = new String(sw.toByteArray(), "UTF-8");
		LOGGER.info("XML Invoice" + xmlString);

		// the XSL FO file

		// the XML file which provides the input
		StringReader s = new StringReader(xmlString);
		StreamSource xmlSource = new StreamSource(s); // create an instance of fop factory
		// a user agent is needed for transformation
		// Setup output
		ByteArrayOutputStream out;
		out = new ByteArrayOutputStream();

		try {
			// Construct fop with desired output format
			Fop fop = fopFactory.newFop("application/pdf", out);

			// Setup XSLT
			LOGGER.info("File path:" + invoiceXsl + "invoice.xsl");
			File xsltFile = new File("/home/amaropti/aj/xsl/invoice.xsl");
			LOGGER.info("File exists:" + xsltFile.exists());

			Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));

			// Resulting SAX events (the generated FO) must be piped through to FOP
			Result res = new SAXResult(fop.getDefaultHandler());

			// Start XSLT transformation and FOP processing
			// That's where the XML is first transformed to XSL-FO and then
			// PDF is created
			transformer.transform(xmlSource, res);
		} finally {
			out.close();
		}

		return out.toByteArray();
	}

}
