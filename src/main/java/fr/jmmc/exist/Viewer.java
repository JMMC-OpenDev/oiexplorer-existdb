/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.exist;

import fr.jmmc.oitools.OIFitsViewer;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import org.exist.Namespaces;
import org.exist.dom.QName;
import org.exist.memtree.DocumentImpl;
import org.exist.memtree.SAXAdapter;
import org.exist.xquery.BasicFunction;
import org.exist.xquery.Cardinality;
import org.exist.xquery.ErrorCodes;
import org.exist.xquery.FunctionSignature;
import org.exist.xquery.XPathException;
import org.exist.xquery.XQueryContext;
import org.exist.xquery.value.FunctionParameterSequenceType;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.SequenceType;
import org.exist.xquery.value.Type;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * OIExplorer existdb extension code implementation for viewer.
 *
 * @author Patrick Bernaud, Guillaume Mella
 */
public class Viewer extends BasicFunction {

    /** declare some xquery functions
     */
    public final static FunctionSignature signatures[] = {
        /* oi:to-xml(filename as xs:string) as node()? */
        new FunctionSignature(
            new QName("to-xml", OIExplorerModule.NAMESPACE_URI, OIExplorerModule.PREFIX), "",
            new SequenceType[]{
                new FunctionParameterSequenceType("filename", Type.STRING, Cardinality.EXACTLY_ONE, "")
            },
            new SequenceType(Type.DOCUMENT, Cardinality.ZERO_OR_ONE)),
    };

    /** Logger (existdb extensions uses log4j) */
    protected static final Logger logger = Logger.getLogger(Viewer.class);

    /* members */
    private final SAXParserFactory factory;

    /**
     * Constructor of Viewer to provide the extension code.
     *
     * @param context
     * @param signature
     */
    public Viewer(XQueryContext context, FunctionSignature signature) {
        super(context, signature);

        factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
    }

    /**
     * eval implementation.
     *
     * @param args input sequence, mainly one location
     * @param contextSequence context
     * @return a sequence with xml representation of given location oifits
     * @throws XPathException
     */
    @Override
    public Sequence eval(Sequence[] args, Sequence contextSequence) throws XPathException {
        if (args[0].getItemCount() == 0) {
            return Sequence.EMPTY_SEQUENCE;
        }

        // Get location from input args
        // TODO can we extend to multiple locations at once
        // TODO complete oitools to handle inputstream instead of File only access...
        final String filename = args[0].itemAt(0).getStringValue();
        if (filename.length() == 0) {
            return Sequence.EMPTY_SEQUENCE;
        }

        // Get our viewer reference
        final OIFitsViewer v = new OIFitsViewer(true, true);

        String output = null;
        try {
            output = v.process(filename);
        } catch (final Exception e) {
            throw new XPathException(this, "Can't read oifits properly: " + e.getMessage(), e);
        }

        // Parse given xml string to provide a document object
        final SAXAdapter adapter = new SAXAdapter(context);
        try {
            final InputSource src = new InputSource(new StringReader(output));

            XMLReader xr = factory.newSAXParser().getXMLReader();
            xr.setContentHandler(adapter);
            xr.setProperty(Namespaces.SAX_LEXICAL_HANDLER, adapter);
            xr.parse(src);
        } catch (final ParserConfigurationException e) {
            throw new XPathException(this, ErrorCodes.EXXQDY0002, "Error while constructing XML parser: " + e.getMessage(), args[0], e);
        } catch (final SAXException e) {
            throw new XPathException(this, ErrorCodes.EXXQDY0002, "Error while parsing XML: " + e.getMessage(), args[0], e);
        } catch (final IOException e) {
            throw new XPathException(this, ErrorCodes.EXXQDY0002, "Error while parsing XML: " + e.getMessage(), args[0], e);
        }

        return (DocumentImpl) adapter.getDocument();
    }
}
