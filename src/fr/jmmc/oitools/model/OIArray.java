/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: OIArray.java,v 1.7 2010-06-18 15:42:36 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.6  2010/06/17 15:02:27  bourgesl
 * added getter / setter methods for keywords and columns
 *
 * Revision 1.5  2010/05/31 15:51:19  bourgesl
 * restored units in keywords used in xml output
 *
 * Revision 1.4  2010/05/28 14:56:18  bourgesl
 * removed arrayXYZ unit which is now in description
 *
 * Revision 1.3  2010/05/27 16:13:29  bourgesl
 * javadoc + small refactoring to expose getters/setters for keywords and getters for columns
 *
 * Revision 1.2  2010/04/29 15:47:02  bourgesl
 * use OIFitsChecker instead of CheckLogger / Handler to make OIFits validation
 *
 * Revision 1.1  2010/04/28 14:47:38  bourgesl
 * refactored OIValidator classes to represent the OIFits data model
 *
 * Revision 1.11  2009/09/15 12:00:15  mella
 * add more informations for oi_array
 *
 * Revision 1.10  2009/09/08 16:10:42  mella
 * add same block for all oitable after optionnal specific data
 *
 * Revision 1.9  2008/10/28 07:47:47  mella
 * Add javadoc
 *
 * Revision 1.8  2008/04/08 14:22:16  mella
 * Include Evelyne comments
 *
 * Revision 1.7  2008/03/28 08:58:19  mella
 * add acceptedStaIndex handling
 *
 * Revision 1.6  2008/03/20 14:25:06  mella
 * First semantic step
 *
 * Revision 1.5  2008/03/19 09:02:05  mella
 * fix right choice for FRAME keyword
 *
 * Revision 1.4  2008/03/14 13:55:13  mella
 * Set right keywords
 *
 * Revision 1.3  2008/03/13 07:25:48  mella
 * General commit after first keywords and columns definitions
 *
 * Revision 1.2  2008/03/11 14:48:52  mella
 * commit when evening is comming
 *
 * Revision 1.1  2008/02/28 08:10:40  mella
 * First revision
 *
 ******************************************************************************/
package fr.jmmc.oitools.model;

import fr.jmmc.oitools.OIFitsConstants;
import fr.jmmc.oitools.meta.ColumnMeta;
import fr.jmmc.oitools.meta.KeywordMeta;
import fr.jmmc.oitools.meta.Types;
import fr.jmmc.oitools.meta.Units;

/**
 * Class for OI_ARRAY table.
 */
public final class OIArray extends OITable {

  /* constants */

  /* static descriptors */
  /** ARRNAME keyword descriptor */
  private final static KeywordMeta KEYWORD_ARRNAME = new KeywordMeta(OIFitsConstants.KEYWORD_ARRNAME,
          "array name for cross-referencing", Types.TYPE_CHAR);
  /** FRAME   keyword descriptor */
  private final static KeywordMeta KEYWORD_FRAME = new KeywordMeta(OIFitsConstants.KEYWORD_FRAME,
          "coordinate frame", Types.TYPE_CHAR, new String[]{OIFitsConstants.KEYWORD_FRAME_GEOCENTRIC});
  /** ARRAYX  keyword descriptor */
  private final static KeywordMeta KEYWORD_ARRAY_X = new KeywordMeta(OIFitsConstants.KEYWORD_ARRAY_X,
          "[m] array center X-coordinate", Types.TYPE_DBL, Units.UNIT_METER);
  /** ARRAYY  keyword descriptor */
  private final static KeywordMeta KEYWORD_ARRAY_Y = new KeywordMeta(OIFitsConstants.KEYWORD_ARRAY_Y,
          "[m] array center Y-coordinate", Types.TYPE_DBL, Units.UNIT_METER);
  /** ARRAYZ  keyword descriptor */
  private final static KeywordMeta KEYWORD_ARRAY_Z = new KeywordMeta(OIFitsConstants.KEYWORD_ARRAY_Z,
          "[m] array center Z-coordinate", Types.TYPE_DBL, Units.UNIT_METER);
  /** TEL_NAME column descriptor */
  private final static ColumnMeta COLUMN_TEL_NAME = new ColumnMeta(OIFitsConstants.COLUMN_TEL_NAME,
          "telescope name", Types.TYPE_CHAR, 16);
  /** STA_NAME column descriptor */
  private final static ColumnMeta COLUMN_STA_NAME = new ColumnMeta(OIFitsConstants.COLUMN_STA_NAME,
          "station name", Types.TYPE_CHAR, 16);
  /** STA_INDEX column descriptor */
  private final static ColumnMeta COLUMN_STA_INDEX = new ColumnMeta(OIFitsConstants.COLUMN_STA_INDEX,
          "station index", Types.TYPE_INT);
  /** DIAMETER column descriptor */
  private final static ColumnMeta COLUMN_DIAMETER = new ColumnMeta(OIFitsConstants.COLUMN_DIAMETER,
          "element diameter", Types.TYPE_REAL, Units.UNIT_METER);
  /** STAXYZ column descriptor */
  private final static ColumnMeta COLUMN_STA_XYZ = new ColumnMeta(OIFitsConstants.COLUMN_STA_XYZ,
          "station coordinates relative to array center", Types.TYPE_DBL, 3, Units.UNIT_METER);

  /**
   * Public OIArray class constructor
   * @param oifitsFile main OifitsFile
   */
  public OIArray(final OIFitsFile oifitsFile) {
    super(oifitsFile);

    // ARRNAME  keyword definition
    addKeywordMeta(KEYWORD_ARRNAME);

    // FRAME  keyword definition
    addKeywordMeta(KEYWORD_FRAME);

    // ARRAYX  keyword definition
    addKeywordMeta(KEYWORD_ARRAY_X);

    // ARRAYY  keyword definition
    addKeywordMeta(KEYWORD_ARRAY_Y);

    // ARRAYZ  keyword definition
    addKeywordMeta(KEYWORD_ARRAY_Z);

    // TEL_NAME  column definition
    addColumnMeta(COLUMN_TEL_NAME);

    // STA_NAME  column definition
    addColumnMeta(COLUMN_STA_NAME);

    // STA_INDEX  column definition
    addColumnMeta(COLUMN_STA_INDEX);

    // DIAMETER  column definition
    addColumnMeta(COLUMN_DIAMETER);

    // STAXYZ  column definition
    addColumnMeta(COLUMN_STA_XYZ);
  }

  /**
   * Public OIArray class constructor to create a new table
   * @param oifitsFile main OifitsFile
   * @param nbRows number of rows i.e. the Fits NAXIS2 keyword value
   */
  public OIArray(final OIFitsFile oifitsFile, final int nbRows) {
    this(oifitsFile);

    this.initializeTable(nbRows);
  }

  /* --- Keywords --- */
  /**
   * Get the value of ARRNAME keyword
   * @return the value of ARRNAME keyword
   */
  public String getArrName() {
    return getKeyword(OIFitsConstants.KEYWORD_ARRNAME);
  }

  /**
   * Define the ARRNAME keyword value
   * @param arrName value of ARRNAME keyword
   */
  public void setArrName(final String arrName) {
    setKeyword(OIFitsConstants.KEYWORD_ARRNAME, arrName);
  }

  /**
   * Get the value of FRAME keyword
   * @return the value of FRAME keyword
   */
  public String getFrame() {
    return getKeyword(OIFitsConstants.KEYWORD_FRAME);
  }

  /**
   * Define the FRAME keyword value
   * @param frame value of FRAME keyword
   */
  public void setFrame(final String frame) {
    setKeyword(OIFitsConstants.KEYWORD_FRAME, frame);
  }

  /**
   * Get the value of ARRAYX, ARRAYY, ARRAYZ keywords
   * @return the value of ARRAYX, ARRAYY, ARRAYZ keywords
   */
  public double[] getArrayXYZ() {
    return new double[]{
              getKeywordDouble(OIFitsConstants.KEYWORD_ARRAY_X),
              getKeywordDouble(OIFitsConstants.KEYWORD_ARRAY_Y),
              getKeywordDouble(OIFitsConstants.KEYWORD_ARRAY_Z)};
  }

  /**
   * Define the value of ARRAYX, ARRAYY, ARRAYZ keywords
   * @param values value of ARRAYX, ARRAYY, ARRAYZ keywords
   */
  public void setArrayXYZ(final double[] values) {
    if (values != null && values.length == 3) {
      setKeywordDouble(OIFitsConstants.KEYWORD_ARRAY_X, values[0]);
      setKeywordDouble(OIFitsConstants.KEYWORD_ARRAY_Y, values[1]);
      setKeywordDouble(OIFitsConstants.KEYWORD_ARRAY_Z, values[2]);
    }
  }

  /* --- Columns --- */
  /**
   * Get TEL_NAME column.
   * @return the array of TEL_NAME.
   */
  public final String[] getTelName() {
    return getColumnString(OIFitsConstants.COLUMN_TEL_NAME);
  }

  /**
   * Get STA_NAME column.
   * @return the array of STA_NAME.
   */
  public final String[] getStaName() {
    return getColumnString(OIFitsConstants.COLUMN_STA_NAME);
  }

  /**
   * Return the STA_INDEX column.
   * @return the STA_INDEX column i.e. an array containing station indexes.
   */
  public short[] getStaIndex() {
    final short[] data = getColumnShort(OIFitsConstants.COLUMN_STA_INDEX);
    if (data == null) {
      return EMPTY_SHORT_ARRAY;
    }
    return data;
  }

  /**
   * Get DIAMETER column.
   * @return the array of DIAMETER.
   */
  public final float[] getDiameter() {
    return getColumnFloat(OIFitsConstants.COLUMN_DIAMETER);
  }

  /**
   * Get STAXYZ column.
   * @return the array of STAXYZ.
   */
  public double[][] getStaXYZ() {
    return this.getColumnDoubles(OIFitsConstants.COLUMN_STA_XYZ);
  }

  /* --- Other methods --- */
  /**
   * Returns a string representation of this table
   * @return a string representation of this table
   */
  @Override
  public String toString() {
    return super.toString() + " [ ARRNAME=" + getArrName() + " | " + getNbRows() + " telescopes ]";
  }

  /**
   * Do syntactical analysis.
   * @param checker checker component
   */
  @Override
  public void checkSyntax(final OIFitsChecker checker) {
    super.checkSyntax(checker);

    if (getArrName() != null && getArrName().length() == 0) {
      checker.severe("ARRNAME identifier has blank value");
    }

    getOIFitsFile().checkCrossRefering(this, checker);
  }

  /**
   * Fill the given buffer with the xml serialisation of the table.
   * @param sb string buffer
   * @param detailled if true the result will contain the table content
   */
  @Override
  public void getXmlDesc(final StringBuilder sb, final boolean detailled) {
    sb.append("<arrname>");
    sb.append("<name>").append(getArrName()).append("</name>");
    sb.append("<positions>").append(getNbRows()).append("</positions>");
    sb.append("</arrname>\n\n");

    super.getXmlDesc(sb, detailled);
  }
}
/*___oOo___*/
