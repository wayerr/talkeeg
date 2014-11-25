/*
 * Copyright (c) 2014, wayerr (radiofun@ya.ru).
 *
 *      This file is part of talkeeg-parent.
 *
 *      talkeeg-parent is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      talkeeg-parent is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with talkeeg-parent.  If not, see <http://www.gnu.org/licenses/>.
 */

package talkeeg.bf.schema;

import com.google.common.base.Enums;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import com.sun.org.apache.xml.internal.resolver.tools.CatalogResolver;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.ext.EntityResolver2;
import talkeeg.bf.EntryType;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * tool for loading schema <p/>
 *
 * Created by wayerr on 21.11.14.
 */
public final class SchemaSource {

    private static final Logger log = Logger.getLogger(SchemaSource.class.getName());
    public static final ErrorHandler ERROR_HANDLER = new ErrorHandler() {

        @Override
        public void warning(SAXParseException exception) throws SAXException {
            log.log(Level.WARNING, "", exception);
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            throw exception;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            throw exception;
        }
    };

    private static final String NS = "http://talkeeg/ns/tgbf";

    public static final String NAME_MESSAGE = "message";
    public static final String NAME_STRUCT = "struct";
    public static final String NAME_PRIMITIVE = "primitive";
    public static final String NAME_INTEGER = "integer";
    public static final String NAME_FLOAT = "float";
    public static final String NAME_DATETIME = "datetime";
    public static final String NAME_BLOB = "blob";
    public static final String NAME_STRING = "string";
    public static final String NAME_LIST = "list";
    public static final Map<String, EntryType> ENTRY_TYPE_MAP = ImmutableMap.<String, EntryType>builder()
            .put("NULL", EntryType.NULL)
            .put("HALF", EntryType.HALF)
            .put("01_BYTE", EntryType.BYTE_1)
            .put("02_BYTE", EntryType.BYTE_2)
            .put("04_BYTE", EntryType.BYTE_4)
            .put("08_BYTE", EntryType.BYTE_8)
            .put("16_BYTE", EntryType.BYTE_16)
            .put("BLOB", EntryType.BYTES)
            .put("STRUCT", EntryType.STRUCT)
            .put("LIST", EntryType.LIST)
            .build();

    public static Schema fromResource(String resourceUri) throws Exception {
        final URL url = Resources.getResource(resourceUri);
        final ByteSource bs = Resources.asByteSource(url);
        try(InputStream is = bs.openStream()) {
            return fromInputStream(is);
        }
    }

    protected static Schema fromInputStream(InputStream is) throws Exception {

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final javax.xml.validation.Schema xmlschema = schemaFactory.newSchema(Resources.getResource("tgbf.xsd"));
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        factory.setSchema(xmlschema);

        final DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        documentBuilder.setErrorHandler(ERROR_HANDLER);

        final Document doc = documentBuilder.parse(is);

        final Schema.Builder builder = Schema.builder();
        final Element rootElement = doc.getDocumentElement();

        loadByteOrder(builder, rootElement);

        final NodeList messages = rootElement.getChildNodes();
        final int length = messages.getLength();
        for(int i = 0; i < length; ++i) {
            final Node node = messages.item(i);
            if(node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final String name = node.getLocalName();
            if(!"message".equals(name)) {
                throw new RuntimeException("expected 'message' node, but found " + name);
            }
            final Struct struct = loadStruct(node);
            builder.putMessage(struct);
        }

        return builder.build();
    }

    private static void loadByteOrder(Schema.Builder builder, Element rootElement) {
        final String byteOrderName = getAttributeValue(rootElement.getAttributes(), "byteOrder");
        builder.setByteOrder("BIG_ENDIAN".equals(byteOrderName)? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
    }

    private static EntryType getEntryType(NamedNodeMap attributes, String attrName) {
        final String stringValue = getAttributeValue(attributes, attrName);
        if(stringValue == null) {
            return null;
        }
        EntryType entryType = ENTRY_TYPE_MAP.get(stringValue);
        if(entryType == null) {
            throw new RuntimeException("Unknown entry type :" + stringValue + " known: " + ENTRY_TYPE_MAP.keySet());
        }
        return entryType;
    }

    private static Struct loadStruct(Node node) {
        final NamedNodeMap attrs = node.getAttributes();
        Struct.Builder b = Struct.builder();
        b.setId(Integer.parseInt(getAttributeValue(attrs, "structId")));
        final NodeList fields = node.getChildNodes();
        final int length = fields.getLength();
        for(int i = 0; i < length; ++i) {
            final Node field = fields.item(i);
            if(field.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final SchemaEntry entry = loadEntry(field);
            if(entry != null) {
                b.addField(entry);
            }
        }
        return b.build();
    }

    private static SchemaEntry loadPrimitive(Node node, Class<?> clazz) {
        return loadPrimitive(node, clazz, null);
    }

    private static SchemaEntry loadPrimitive(Node node, Class<?> clazz, final EntryType type) {
        final PrimitiveEntry.Builder builder = PrimitiveEntry.builder();
        final NamedNodeMap attributes = node.getAttributes();
        EntryType entryType = type;
        if(entryType == null) {
            entryType = getEntryType(attributes, "storeAs");
            if(entryType == null) {
                throw new RuntimeException("Need specify 'storeAs' attribute in " + node);
            }
        }
        builder.setType(entryType);
        builder.setJavaType(clazz);
        builder.setMetaType(node.getLocalName());
        builder.setFieldName(getAttributeValue(attributes, "fieldName"));
        return builder.build();
    }

    private static SchemaEntry loadList(Node node) {

        return null;
    }

    private static SchemaEntry loadEntry(Node node) {
        final String name = node.getLocalName();
        final SchemaEntry entry;
        switch (name) {
            case NAME_MESSAGE:
            case NAME_STRUCT:
                entry = loadStruct(node);
                break;
            case NAME_DATETIME:
                entry = loadPrimitive(node, Long.class, EntryType.BYTE_8);
                break;
            case NAME_FLOAT:
                entry = loadPrimitive(node, Double.class);
                break;
            case NAME_INTEGER:
                entry = loadPrimitive(node, Long.class);
                break;
            case NAME_PRIMITIVE:
                entry = loadPrimitive(node, Object.class);
                break;
            case NAME_BLOB:
                entry = loadPrimitive(node, byte[].class, EntryType.BYTES);
                break;
            case NAME_STRING:
                entry = loadPrimitive(node, String.class, EntryType.BYTES);
                break;
            case NAME_LIST:
                entry = loadList(node);
                break;
            default:
                throw new RuntimeException("unsupported node type: " + name);
        }
        return entry;
    }

    private static String getAttributeValue(NamedNodeMap attrs, String name) {
        final Node node = attrs.getNamedItemNS(null, name);
        if(node == null) {
            //this situation is possible when the validation is disabled or error in the description of the scheme
            throw new RuntimeException("need attribute: " + name);
        }
        return node.getNodeValue();
    }
}
