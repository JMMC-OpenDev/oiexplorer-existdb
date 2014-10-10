/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.exist;

import java.util.List;
import java.util.Map;
import org.exist.xquery.AbstractInternalModule;
import org.exist.xquery.FunctionDef;
import org.exist.xquery.XPathException;

/**
 * OIExplorer existdb extension
 * @author Patrick Bernaud, Guillaume Mella
 */
public class OIExplorerModule extends AbstractInternalModule {

    public final static String NAMESPACE_URI = "http://exist.jmmc.fr/extension/oiexplorer";

    public final static String PREFIX = "oi";

    private final static FunctionDef[] functions = {
        new FunctionDef(Viewer.signatures[0], Viewer.class),
        new FunctionDef(Viewer.signatures[1], Viewer.class),
    };

    public OIExplorerModule(Map<String, List<? extends Object>> parameters) throws XPathException {
        super(functions, parameters);
    }

    @Override
    public String getNamespaceURI() {
        return NAMESPACE_URI;
    }

    @Override
    public String getDefaultPrefix() {
        return PREFIX;
    }

    @Override
    public String getDescription() {
        return "description";
    }

    @Override
    public String getReleaseVersion() {
        return "";
    }
}