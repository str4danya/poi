/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.hssf.usermodel;

import org.apache.poi.ddf.*;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.usermodel.drawing.HSSFShapeType;
import org.apache.poi.ss.usermodel.RichTextString;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a simple shape such as a line, rectangle or oval.
 *
 * @author Glen Stampoultzis (glens at apache.org)
 */
public class HSSFSimpleShape extends HSSFShape
{
    // The commented out ones haven't been tested yet or aren't supported
    // by HSSFSimpleShape.

    public final static short       OBJECT_TYPE_LINE               = 1;
    public final static short       OBJECT_TYPE_RECTANGLE          = 2;
    public final static short       OBJECT_TYPE_OVAL               = 3;
//    public final static short       OBJECT_TYPE_ARC                = 4;
//    public final static short       OBJECT_TYPE_CHART              = 5;
//    public final static short       OBJECT_TYPE_TEXT               = 6;
//    public final static short       OBJECT_TYPE_BUTTON             = 7;
    public final static short       OBJECT_TYPE_PICTURE            = 8;
//    public final static short       OBJECT_TYPE_POLYGON            = 9;
//    public final static short       OBJECT_TYPE_CHECKBOX           = 11;
//    public final static short       OBJECT_TYPE_OPTION_BUTTON      = 12;
//    public final static short       OBJECT_TYPE_EDIT_BOX           = 13;
//    public final static short       OBJECT_TYPE_LABEL              = 14;
//    public final static short       OBJECT_TYPE_DIALOG_BOX         = 15;
//    public final static short       OBJECT_TYPE_SPINNER            = 16;
//    public final static short       OBJECT_TYPE_SCROLL_BAR         = 17;
//    public final static short       OBJECT_TYPE_LIST_BOX           = 18;
//    public final static short       OBJECT_TYPE_GROUP_BOX          = 19;
    public final static short       OBJECT_TYPE_COMBO_BOX          = 20;
    public final static short       OBJECT_TYPE_COMMENT            = 25;
//    public final static short       OBJECT_TYPE_MICROSOFT_OFFICE_DRAWING = 30;

    public final static int WRAP_SQUARE = 0;
    public final static int WRAP_BY_POINTS = 1;
    public final static int WRAP_NONE = 2;

    private static final Map <Short, Short> objTypeToShapeType = new HashMap<Short, Short>();

    private TextObjectRecord _textObjectRecord;

    static {
        objTypeToShapeType.put(OBJECT_TYPE_RECTANGLE, HSSFShapeType.RECTANGLE.getType());
        objTypeToShapeType.put(OBJECT_TYPE_PICTURE, HSSFShapeType.PICTURE.getType());
        objTypeToShapeType.put(OBJECT_TYPE_LINE, HSSFShapeType.LINE.getType());
        objTypeToShapeType.put(OBJECT_TYPE_OVAL, HSSFShapeType.OVAL.getType());
    }

    public HSSFSimpleShape(EscherContainerRecord spContainer, ObjRecord objRecord, TextObjectRecord textObjectRecord) {
        super(spContainer, objRecord);
        this._textObjectRecord = textObjectRecord;
    }

    public HSSFSimpleShape(EscherContainerRecord spContainer, ObjRecord objRecord) {
        super(spContainer, objRecord);
    }

    public HSSFSimpleShape( HSSFShape parent, HSSFAnchor anchor)
    {
        super( parent, anchor );
        _textObjectRecord = createTextObjRecord();
    }

    protected TextObjectRecord getTextObjectRecord() {
        return _textObjectRecord;
    }

    protected TextObjectRecord createTextObjRecord(){
        TextObjectRecord obj = new TextObjectRecord();
        obj.setHorizontalTextAlignment(2);
        obj.setVerticalTextAlignment(2);
        obj.setTextLocked(true);
        obj.setTextOrientation(TextObjectRecord.TEXT_ORIENTATION_NONE);
        obj.setStr(new HSSFRichTextString(""));
        return obj;
    }

    @Override
    protected EscherContainerRecord createSpContainer() {
        EscherContainerRecord spContainer = new EscherContainerRecord();
        spContainer.setRecordId( EscherContainerRecord.SP_CONTAINER );
        spContainer.setOptions( (short) 0x000F );

        EscherSpRecord sp = new EscherSpRecord();
        sp.setRecordId( EscherSpRecord.RECORD_ID );
        sp.setFlags( EscherSpRecord.FLAG_HAVEANCHOR | EscherSpRecord.FLAG_HASSHAPETYPE );
        sp.setVersion((short) 0x2);

        EscherClientDataRecord clientData = new EscherClientDataRecord();
        clientData.setRecordId( EscherClientDataRecord.RECORD_ID );
        clientData.setOptions( (short) (0x0000) );

        EscherOptRecord optRecord = new EscherOptRecord();
        optRecord.setEscherProperty(new EscherSimpleProperty(EscherProperties.LINESTYLE__LINEDASHING, LINESTYLE_SOLID));
        optRecord.setEscherProperty( new EscherBoolProperty( EscherProperties.LINESTYLE__NOLINEDRAWDASH, 0x00080008));
//        optRecord.setEscherProperty(new EscherSimpleProperty(EscherProperties.LINESTYLE__LINEWIDTH, LINEWIDTH_DEFAULT));
        optRecord.setEscherProperty(new EscherRGBProperty(EscherProperties.FILL__FILLCOLOR, FILL__FILLCOLOR_DEFAULT));
        optRecord.setEscherProperty(new EscherRGBProperty(EscherProperties.LINESTYLE__COLOR, LINESTYLE__COLOR_DEFAULT));
        optRecord.setEscherProperty(new EscherBoolProperty(EscherProperties.FILL__NOFILLHITTEST, NO_FILLHITTEST_FALSE));
        optRecord.setEscherProperty( new EscherBoolProperty( EscherProperties.LINESTYLE__NOLINEDRAWDASH, 0x00080008));

        optRecord.setEscherProperty( new EscherShapePathProperty( EscherProperties.GEOMETRY__SHAPEPATH, EscherShapePathProperty.COMPLEX ) );
        optRecord.setEscherProperty(new EscherBoolProperty( EscherProperties.GROUPSHAPE__PRINT, 0x080000));
        optRecord.setRecordId( EscherOptRecord.RECORD_ID );

        EscherTextboxRecord escherTextbox = new EscherTextboxRecord();
        escherTextbox.setRecordId(EscherTextboxRecord.RECORD_ID);
        escherTextbox.setOptions((short) 0x0000);

        spContainer.addChildRecord(sp);
        spContainer.addChildRecord(optRecord);
        spContainer.addChildRecord(anchor.getEscherAnchor());
        spContainer.addChildRecord(clientData);
        spContainer.addChildRecord(escherTextbox);
        return spContainer;
    }

    @Override
    protected ObjRecord createObjRecord() {
        ObjRecord obj = new ObjRecord();
        CommonObjectDataSubRecord c = new CommonObjectDataSubRecord();
        c.setLocked(true);
        c.setPrintable(true);
        c.setAutofill(true);
        c.setAutoline(true);
        EndSubRecord e = new EndSubRecord();

        obj.addSubRecord(c);
        obj.addSubRecord(e);
        return obj;
    }

    @Override
    protected void afterRemove(HSSFPatriarch patriarch) {
        patriarch._getBoundAggregate().removeShapeToObjRecord(getEscherContainer().getChildById(EscherClientDataRecord.RECORD_ID));
        if (null != getEscherContainer().getChildById(EscherTextboxRecord.RECORD_ID)){
            patriarch._getBoundAggregate().removeShapeToObjRecord(getEscherContainer().getChildById(EscherTextboxRecord.RECORD_ID));
        }
    }

    /**
     * @return the rich text string for this textbox.
     */
    public HSSFRichTextString getString() {
        return _textObjectRecord.getStr();
    }

    /**
     * @param string Sets the rich text string used by this object.
     */
    public void setString(RichTextString string) {
        //TODO add other shape types which can not contain text
        if (getShapeType() == 0 || getShapeType() == OBJECT_TYPE_LINE){
            throw new IllegalStateException("Cannot set text for shape type: "+getShapeType());
        }
        HSSFRichTextString rtr = (HSSFRichTextString) string;
        // If font is not set we must set the default one
        if (rtr.numFormattingRuns() == 0) rtr.applyFont((short) 0);
        _textObjectRecord.setStr(rtr);
        if (string.getString() != null){
            setPropertyValue(new EscherSimpleProperty(EscherProperties.TEXT__TEXTID, string.getString().hashCode()));
        }
    }

    @Override
    void afterInsert(HSSFPatriarch patriarch){
        EscherAggregate agg = patriarch._getBoundAggregate();
        agg.associateShapeToObjRecord(getEscherContainer().getChildById(EscherClientDataRecord.RECORD_ID), getObjRecord());

        //used only when clone shapes
        if (null != getTextObjectRecord()){
            agg.associateShapeToObjRecord(getEscherContainer().getChildById(EscherTextboxRecord.RECORD_ID), getTextObjectRecord());
        }
    }

    @Override
    protected HSSFShape cloneShape() {
        TextObjectRecord txo = null;
        EscherContainerRecord spContainer = new EscherContainerRecord();
        byte [] inSp = getEscherContainer().serialize();
        spContainer.fillFields(inSp, 0, new DefaultEscherRecordFactory());
        ObjRecord obj = (ObjRecord) getObjRecord().cloneViaReserialise();
        if (getTextObjectRecord() != null && getString() != null && !"".equals(getString().getString())){
            txo = (TextObjectRecord) getTextObjectRecord().cloneViaReserialise();
        }
        return new HSSFSimpleShape(spContainer, obj, txo);
    }


    /**
     * Gets the shape type.
     * @return  One of the OBJECT_TYPE_* constants.
     *
     * @see #OBJECT_TYPE_LINE
     * @see #OBJECT_TYPE_OVAL
     * @see #OBJECT_TYPE_RECTANGLE
     * @see #OBJECT_TYPE_PICTURE
     * @see #OBJECT_TYPE_COMMENT
     */
    public int getShapeType() {
        CommonObjectDataSubRecord cod = (CommonObjectDataSubRecord) getObjRecord().getSubRecords().get(0);
        return cod.getObjectType();
    }

    /**
     * Sets the shape types.
     *
     * @param shapeType One of the OBJECT_TYPE_* constants.
     *
     * @see #OBJECT_TYPE_LINE
     * @see #OBJECT_TYPE_OVAL
     * @see #OBJECT_TYPE_RECTANGLE
     * @see #OBJECT_TYPE_PICTURE
     * @see #OBJECT_TYPE_COMMENT
     */
    public void setShapeType( int shapeType ){
        CommonObjectDataSubRecord cod = (CommonObjectDataSubRecord) getObjRecord().getSubRecords().get(0);
        cod.setObjectType((short) shapeType);
        EscherSpRecord spRecord = getEscherContainer().getChildById(EscherSpRecord.RECORD_ID);
        if (null == objTypeToShapeType.get((short)shapeType)){
            System.out.println("Unknown shape type: "+shapeType);
            return;
        }
        spRecord.setShapeType(objTypeToShapeType.get((short) shapeType));
    }

    public int getWrapText(){
        EscherSimpleProperty property = getOptRecord().lookup(EscherProperties.TEXT__WRAPTEXT);
        return null == property ? WRAP_SQUARE : property.getPropertyValue();
    }

    public void setWrapText(int value){
        setPropertyValue(new EscherSimpleProperty(EscherProperties.TEXT__WRAPTEXT, false, false, value));
    }
}
