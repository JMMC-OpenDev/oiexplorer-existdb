/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.oitools.model;

import fr.jmmc.oitools.OIFitsConstants;
import fr.jmmc.oitools.meta.ArrayColumnMeta;
import fr.jmmc.oitools.meta.ColumnMeta;
import fr.jmmc.oitools.meta.KeywordMeta;
import fr.jmmc.oitools.meta.Types;
import fr.jmmc.oitools.meta.Units;
import fr.jmmc.oitools.meta.WaveColumnMeta;

/**
 *
 * @author kempsc
 */
public final class OIInspol extends OIAbstractData {

    /* static descriptors */
    /** NPOL keyword descriptor */
    private final static KeywordMeta KEYWORD_NPOL = new KeywordMeta(OIFitsConstants.KEYWORD_NPOL,
            "Number of polarisation type int this table", Types.TYPE_INT);
    /** ORIENT keyword descriptor */
    private final static KeywordMeta KEYWORD_ORIENT = new KeywordMeta(OIFitsConstants.KEYWORD_ORIENT,
            "Orientation of Jones matrix, could be 'NORTH' " + "(for on-sky orientation), or 'LABORATORY", Types.TYPE_CHAR,
            new String[]{OIFitsConstants.KEYWORD_ORIENT_NORTH, OIFitsConstants.KEYWORD_ORIENT_LABORATORY});
    /** MODEL keyword descriptor */
    private final static KeywordMeta KEYWORD_MODEL = new KeywordMeta(OIFitsConstants.KEYWORD_MODEL,
            "A string keyword that describe the way the Jones matrix is estimated", Types.TYPE_CHAR);

    /** MJD_OBS  column descriptor */
    private final static ColumnMeta COLUMN_MJD_OBS = new ColumnMeta(OIFitsConstants.COLUMN_MJD_OBS,
            "Modified Julian day, start of time lapse", Types.TYPE_DBL, Units.UNIT_MJD);
    /** MJD_END  column descriptor */
    private final static ColumnMeta COLUMN_MJD_END = new ColumnMeta(OIFitsConstants.COLUMN_MJD_END,
            "Modified Julian day, end of time lapse", Types.TYPE_DBL, Units.UNIT_MJD);

    /* Numbers */
    private int nWaves;

    /**
     * Public OICorr class constructor
     * @param oifitsFile main OifitsFile
     */
    public OIInspol(final OIFitsFile oifitsFile) {
        super(oifitsFile);

        // NPOL  keyword definition
        addKeywordMeta(KEYWORD_NPOL);
        // ORIENT  keyword definition
        addKeywordMeta(KEYWORD_ORIENT);
        // MODEL  keyword definition
        addKeywordMeta(KEYWORD_MODEL);

        // INSNAME  column definition
        addColumnMeta(new ColumnMeta(OIFitsConstants.KEYWORD_INSNAME, "name of corresponding detector",
                Types.TYPE_CHAR, 70) {
            @Override
            public String[] getStringAcceptedValues() {
                return getOIFitsFile().getAcceptedInsNames();
            }
        });
        // MJD_OBS  keyword definition
        addColumnMeta(COLUMN_MJD_OBS);
        // MJD_END  keyword definition
        addColumnMeta(COLUMN_MJD_END);
        // JXX  keyword definition
        addColumnMeta(new WaveColumnMeta(OIFitsConstants.COLUMN_JXX,
                "Complex Jones matrix component along X axis", Types.TYPE_COMPLEX, this));
        // JYY  keyword definition
        addColumnMeta(new WaveColumnMeta(OIFitsConstants.COLUMN_JYY,
                "Complex Jones matrix component along Y axis", Types.TYPE_COMPLEX, this));
        // JXY  keyword definition
        addColumnMeta(new WaveColumnMeta(OIFitsConstants.COLUMN_JXY,
                "Complex Jones matrix component between X and Y axis", Types.TYPE_COMPLEX, this));
        // JYX  keyword definition
        addColumnMeta(new WaveColumnMeta(OIFitsConstants.COLUMN_JYX,
                "Complex Jones matrix component between Y and X axis", Types.TYPE_COMPLEX, this));

        // STA_INDEX  column definition
        addColumnMeta(new ArrayColumnMeta(OIFitsConstants.COLUMN_STA_INDEX, "station number contributing to the data",
                Types.TYPE_SHORT, 1, false) {
            @Override
            public short[] getIntAcceptedValues() {
                return getAcceptedStaIndexes();
            }
        });
        // TODO: how to set nWaves as columns are loaded after the constructor
        this.nWaves = 0; // cyclic-dependency or chicken-egg problem !

        // Positive side-effect: it works well except the validation:
        /*
        SEVERE	Can't check repeat for column 'JXX'
        SEVERE	Can't check repeat for column 'JYY'
        SEVERE	Can't check repeat for column 'JXY'
        SEVERE	Can't check repeat for column 'JYX'        
         */
    }

    /**
     * Public OIInspol class constructor to create a new table
     * @param oifitsFile main OifitsFile
     * @param arrname value of ARRNAME keyword
     * @param nbRows number of rows i.e. the Fits NAXIS2 keyword value
     * @param nWaves number of nwaves
     */
    public OIInspol(final OIFitsFile oifitsFile, final String arrname, final int nbRows, final int nWaves) {
        this(oifitsFile);

        this.nWaves = nWaves;
        this.initializeTable(nbRows);
    }

    /* --- keywords --- */
    /**
     * Get the value of NPOL keyword
     * @return the value of NPOL keyword
     */
    public int getNPol() {
        return getKeywordInt(OIFitsConstants.KEYWORD_NPOL);
    }

    /**
     * Define the NPOL keyword value
     * @param nPol value of NPOL keyword
     */
    public void setNPol(final int nPol) {
        setKeywordInt(OIFitsConstants.KEYWORD_NPOL, nPol);
    }

    /**
     * Get the value of ORIENT keyword
     * @return the value of ORIENT keyword
     */
    public String getOrient() {
        return getKeyword(OIFitsConstants.KEYWORD_ORIENT);
    }

    /**
     * Define the ORIENT keyword value
     * @param orient value of ORIENT keyword
     */
    public void setOrient(final String orient) {
        setKeyword(OIFitsConstants.KEYWORD_ORIENT, orient);
    }

    /**
     * Get the value of MODEL keyword
     * @return the value of MODEL keyword
     */
    public String getModel() {
        return getKeyword(OIFitsConstants.KEYWORD_MODEL);
    }

    /**
     * Define the MODEL keyword value
     * @param model value of MODEL keyword
     */
    public void setModel(final String model) {
        setKeyword(OIFitsConstants.KEYWORD_MODEL, model);
    }


    /* --- columns --- */
    /**
     * Get the INSNAME column.
     * @return the INSNAME column
     */
    public String[] getInsNames() {
        return this.getColumnString(OIFitsConstants.KEYWORD_INSNAME);
    }

    /**
     * Return the MJD_OBS column.
     * @return the MJD_OBS column.
     */
    public double[] getMJDObs() {
        return this.getColumnDouble(OIFitsConstants.COLUMN_MJD_OBS);
    }

    /**
     * Return the MJD_END column.
     * @return the MJD_END column.
     */
    public double[] getMJDEnd() {
        return this.getColumnDouble(OIFitsConstants.COLUMN_MJD_END);
    }

    /**
     * Return the JXX column.
     * @return the JXX column.
     */
    public float[][][] getJXX() {
        return this.getColumnComplexes(OIFitsConstants.COLUMN_JXX);
    }

    /**
     * Return the JYY column.
     * @return the JYY column.
     */
    public float[][][] getJYY() {
        return this.getColumnComplexes(OIFitsConstants.COLUMN_JYY);
    }

    /**
     * Return the JXY column.
     * @return the JXY column.
     */
    public float[][][] getJXY() {
        return this.getColumnComplexes(OIFitsConstants.COLUMN_JXY);
    }

    /**
     * Return the JYX column.
     * @return the JYX column.
     */
    public float[][][] getJYX() {
        return this.getColumnComplexes(OIFitsConstants.COLUMN_JYX);
    }

    /* --- Utility methods for cross-referencing --- */
    /**
     * Return the number of distinct spectral channels (NWAVE) of the associated OI_WAVELENGTH table(s).
     * @return the number of distinct spectral channels (NWAVE) of the associated OI_WAVELENGTH table(s)
     * or 0 if the OI_WAVELENGTH table(s) are missing !
     * Note: this method is used by WaveColumnMeta.getRepeat() to determine the column dimensions
     */
    @Override
    public int getNWave() {
        return this.nWaves = nWaves;
    }

    /*
     * --- Checker -------------------------------------------------------------
     */
    /**
     * Do syntactical analysis of the table
     *
     * @param checker checker component
     */
    @Override
    public void checkSyntax(final OIFitsChecker checker) {
        super.checkSyntax(checker);

        // check MJD ranges
        // TODO: custom rules
        //rule 1: MJD interval time == MJD in different table
        //rule 2: 1 INSPOL ref exsiting INSNAME (in OI_WAVE), Unique list of INSNAME
        //rule 3: if 1 INSNAME is ref in 1 OI_INSPOL canot be in another OI_INSPOL
    }
}