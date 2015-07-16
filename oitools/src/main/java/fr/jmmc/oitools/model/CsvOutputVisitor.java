/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.oitools.model;

import fr.jmmc.oitools.OIFitsViewer;

/**
 * This visitor implementation produces an CSV output of the OIFits file structure
 * @author bourgesl, mella
 */
public final class CsvOutputVisitor implements ModelVisitor {

    /* constants */
    private final static String sep = "\t";

    /* members */
    /** flag to enable/disable the number formatter */
    private boolean format;
    /** flag to enable/disable the verbose output */
    private boolean verbose;
    /** internal buffer */
    private StringBuilder buffer;
    /** checker used to store checking messages */
    private final OIFitsChecker checker;

    /**
     * Return one CSV string with complete OIFitsFile information
     * @param oiFitsFile OIFitsFile model to process
     * @param verbose if true the result will contain the table content
     * @return the CSV description
     */
    public static String getCsvDesc(final OIFitsFile oiFitsFile, final boolean verbose) {
        return getCsvDesc(oiFitsFile, false, verbose);
    }

    /**
     * Return one CSV string with complete OIFitsFile information
     * @param oiFitsFile OIFitsFile model to process
     * @param format flag to represent data with less accuracy but a better string representation
     * @param verbose if true the result will contain the table content
     * @return the CSV description
     */
    public static String getCsvDesc(final OIFitsFile oiFitsFile, final boolean format, final boolean verbose) {
        final CsvOutputVisitor csvSerializer = new CsvOutputVisitor(format, verbose);
        oiFitsFile.accept(csvSerializer);
        return csvSerializer.toString();
    }

    /**
     * Return one CSV string with OITable information
     * @param oiTable OITable model to process
     * @param verbose if true the result will contain the table content
     * @return the CSV description
     */
    public static String getCsvDesc(final OITable oiTable, final boolean verbose) {
        return getCsvDesc(oiTable, false, verbose);
    }

    /**
     * Return one CSV string with OITable information
     * @param oiTable OITable model to process
     * @param format flag to represent data with less accuracy but a better string representation
     * @param verbose if true the result will contain the table content
     * @return the CSV description
     */
    public static String getCsvDesc(final OITable oiTable, final boolean format, final boolean verbose) {
        final CsvOutputVisitor csvSerializer = new CsvOutputVisitor(format, verbose);
        oiTable.accept(csvSerializer);
        return csvSerializer.toString();
    }


    /**
     * Create a new CsvOutputVisitor using default options (not verbose and no formatter used)
     */
    public CsvOutputVisitor() {
        this(false, false);
    }

    /**
     * Create a new CsvOutputVisitor with verbose output i.e. with table data (no formatter used)
     * @param verbose if true the result will contain the table content
     */
    public CsvOutputVisitor(final boolean verbose) {
        this(false, verbose);
    }

    /**
     * Create a new CsvOutputVisitor with verbose output i.e. with table data (no formatter used)
     * @param format flag to represent data with less accuracy but a better string representation
     * @param verbose if true the result will contain the table content
     */
    public CsvOutputVisitor(final boolean format, final boolean verbose) {
        this(format, verbose, null);
    }

    /**
     * Create a new CsvOutputVisitor with verbose output i.e. with table data (no formatter used)
     * @param format flag to represent data with less accuracy but a better string representation
     * @param verbose if true the result will contain the table content
     * @param checker optional OIFitsChecker to dump its report
     */
    public CsvOutputVisitor(final boolean format, final boolean verbose, final OIFitsChecker checker) {
        this.format = format;
        this.verbose = verbose;
        this.checker = checker;

        // allocate buffer size (32K or 128K):
        this.buffer = new StringBuilder(((verbose) ? 128 : 32) * 1024);
    }

    /**
     * Return the flag to enable/disable the number formatter
     * @return flag to enable/disable the number formatter
     */
    public boolean isFormat() {
        return format;
    }

    /**
     * Define the flag to enable/disable the number formatter
     * @param format flag to enable/disable the number formatter
     */
    public void setFormat(final boolean format) {
        this.format = format;
    }

    /**
     * Return the flag to enable/disable the verbose output
     * @return flag to enable/disable the verbose output
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * Define the flag to enable/disable the verbose output
     * @param verbose flag to enable/disable the verbose output
     */
    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Clear the internal buffer for later reuse
     */
    public void reset() {
        // recycle buffer :
        this.buffer.setLength(0);
    }

    /**
     * Return the buffer content as a string
     * @return buffer content
     */
    @Override
    public String toString() {
        final String result = this.buffer.toString();

        // reset the buffer content
        reset();

        return result;
    }

    /**
     * Process the given OIFitsFile element with this visitor implementation :
     * fill the internal buffer with file information
     * @param oiFitsFile OIFitsFile element to visit
     */
    @Override
    public void visit(final OIFitsFile oiFitsFile) {

        enterOIFitsFile(oiFitsFile);

        // targets
        final OITarget oiTarget = oiFitsFile.getOiTarget();
        if (oiTarget != null) {
            oiTarget.accept(this);
            printMetadata(oiFitsFile);
        }

        exitOIFitsFile();
    }

    /**
     * Process the given OITable element with this visitor implementation :
     * csv don't care the content
     * @param oiTable OITable element to visit
     */
    @Override
    public void visit(final OITable oiTable) {
        // no op
    }


    /**
     * Open the oifits tag with OIFitsFile description
     * @param oiFitsFile OIFitsFile to get its description (file name)
     */
    private void enterOIFitsFile(final OIFitsFile oiFitsFile) {
        
        if (isVerbose() && oiFitsFile != null && oiFitsFile.getAbsoluteFilePath() != null) {
            this.buffer.append("# filename       ").append((oiFitsFile.getSourceURI() != null)
                    ? oiFitsFile.getSourceURI().toString() : oiFitsFile.getAbsoluteFilePath()).append("\n");
            this.buffer.append("# local_filename ").append(oiFitsFile.getAbsoluteFilePath()).append("\n");                                    
        }
    }

    /**
     * Close the oifits tag
     */
    private void exitOIFitsFile() {
        // no op
    }

    private void printMetadata(final OIFitsFile oiFitsFile) {
        /* analyze structure of file to browse by target */
        oiFitsFile.analyze();
        appendHeader(this.buffer, this.sep);

        for (int i = 0; i < oiFitsFile.getOiTarget().getNbTargets(); i++) {
            this.buffer.append(OIFitsViewer.targetMetadata(oiFitsFile, i, false));
        }
    }

    public static void appendHeader(StringBuilder buffer, String sep) {
        // respect the same order has the one provided in the appendCsvRecord
        buffer.append("target_name").append(sep)
                .append("s_ra").append(sep)
                .append("s_dec").append(sep)
                .append("t_exptime").append(sep)
                .append("t_min").append(sep)
                .append("t_max").append(sep)
                .append("em_res_power").append(sep)
                .append("em_min").append(sep)
                .append("em_max").append(sep)
                .append("facility_name").append(sep)
                .append("instrument_name").append(sep)
                .append("nb_vis").append(sep)
                .append("nb_vis2").append(sep)
                .append("nb_t3").append(sep)
                .append("nb_channels").append(sep)
                .append("\n");
    }

    public static void appendRecord(final StringBuilder buffer, final String targetName, final double targetRa, final double targetDec, double intTime, double tMin, double tMax, float resPower, float minWavelength, float maxWavelength, String facilityName, final String insName, int nbVis, int nbVis2, int nbT3, int nbChannels) {
        buffer.append(targetName).append(sep)
                .append(targetRa).append(sep)
                .append(targetDec).append(sep)
                .append(intTime).append(sep)
                .append(tMin).append(sep)
                .append(tMax).append(sep)
                .append(resPower).append(sep)
                .append(minWavelength).append(sep)
                .append(maxWavelength).append(sep)
                .append(facilityName).append(sep)
                .append(insName).append(sep)
                .append(nbVis).append(sep)
                .append(nbVis2).append(sep)
                .append(nbT3).append(sep)
                .append(nbChannels).append(sep)
                .append("\n");
    }

}
