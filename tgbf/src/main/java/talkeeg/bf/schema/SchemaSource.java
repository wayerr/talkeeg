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

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import org.w3c.dom.*;
import org.xml.sax.*;
import talkeeg.bf.EntryType;
import talkeeg.bf.MetaTypes;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.TreeMap;
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
    public static final String ATTR_FIELD_NAME = "fieldName";
    public static final String ATTR_STORE_AS = "storeAs";

    private static final class LoadContext {
        private final Map<Integer, Struct.Builder> structMap = new TreeMap<>();

        private Struct.Builder getStruct(int structId) {
            final Struct.Builder struct = structMap.get(structId);
            if(struct == null) {
                throw new RuntimeException("can not find previously defined struct with id: " + structId);
            }
            return struct;
        }

        private void addStruct(Struct.Builder struct) {
            final int structId = struct.getId();
            final Struct.Builder oldStruct = structMap.put(structId, struct);
            if(oldStruct != null) {
                throw new RuntimeException("find two struct with equal id: " + structId);
            }
        }
    }

    private static final String NS = "http://talkeeg/ns/tgbf";

    public static final String NAME_MESSAGE = "message";
    public static final String NAME_STRUCT = "struct";
    public static final String NAME_PRIMITIVE = "primitive";
    public static final String NAME_LIST = "list";
    public static final String NAME_MAP = "map";
    public static final String NAME_UNION = "union";
    public static final String NAME_INTEGER = MetaTypes.INTEGER;
    public static final String NAME_BOOLEAN = MetaTypes.BOOLEAN;
    public static final String NAME_FLOAT = MetaTypes.FLOAT;
    public static final String NAME_DATETIME = MetaTypes.DATETIME;
    public static final String NAME_BLOB = MetaTypes.BLOB;
    public static final String NAME_STRING = MetaTypes.STRING;
    public static final String NAME_ID = MetaTypes.ID;
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
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        try {
            final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            final javax.xml.validation.Schema xmlschema = schemaFactory.newSchema(Resources.getResource("tgbf.xsd"));
            factory.setSchema(xmlschema);
        } catch(java.lang.IllegalArgumentException e) {
            // android does not contains SchemaFactory
            // see https://code.google.com/p/android/issues/detail?id=7395
        }

        final DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        documentBuilder.setErrorHandler(ERROR_HANDLER);

        final Document doc = documentBuilder.parse(is);

        final Schema.Builder builder = Schema.builder();
        final Element rootElement = doc.getDocumentElement();

        loadByteOrder(builder, rootElement);

        final LoadContext context = new LoadContext();
        final NodeList messages = rootElement.getChildNodes();
        final int length = messages.getLength();
        for(int i = 0; i < length; ++i) {
            final Node node = messages.item(i);
            if(node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final String name = node.getLocalName();
            if(!NAME_MESSAGE.equals(name) && !NAME_STRUCT.equals(name)) {
                throw new RuntimeException("expected 'message' or 'struct' node, but found " + name);
            }
            final Struct.Builder structBuilder = loadStruct(context, node);
            final Struct struct = structBuilder.build();
            builder.putMessage(struct);
        }

        return builder.build();
    }

    private static void loadByteOrder(Schema.Builder builder, Element rootElement) {
        final String byteOrderName = getAttributeValue(rootElement.getAttributes(), "byteOrder", true);
        builder.setByteOrder("BIG_ENDIAN".equals(byteOrderName)? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
    }

    private static EntryType getEntryType(NamedNodeMap attributes, String attrName) {
        final String stringValue = getAttributeValue(attributes, attrName, false);
        if(stringValue == null) {
            return null;
        }
        EntryType entryType = ENTRY_TYPE_MAP.get(stringValue);
        if(entryType == null) {
            throw new RuntimeException("Unknown entry type :" + stringValue + " known: " + ENTRY_TYPE_MAP.keySet());
        }
        return entryType;
    }

    private static Struct.Builder loadStruct(LoadContext context, Node node) {
        final NamedNodeMap attrs = node.getAttributes();
        final int structId = Integer.parseInt(getAttributeValue(attrs, "structId", true));
        final NodeList fields = node.getChildNodes();
        final int length = fields.getLength();
        if(length == 0) {
            //empty struct interpreted as reference to a previously defined struct
            return context.getStruct(structId);
        }
        Struct.Builder b = Struct.builder();
        b.setId(structId);
        for(int i = 0; i < length; ++i) {
            final Node field = fields.item(i);
            final SchemaEntry entry = loadEntry(context, field);
            if(entry != null) {
                b.addField(entry);
            }
        }
        context.addStruct(b);
        return b;
    }

    private static SchemaEntryBuilder loadPrimitive(Node node, Class<?> clazz) {
        return loadPrimitive(node, clazz, null);
    }

    private static SchemaEntryBuilder loadPrimitive(Node node, Class<?> clazz, final EntryType type) {
        final PrimitiveEntry.Builder builder = PrimitiveEntry.builder();
        final NamedNodeMap attributes = node.getAttributes();
        EntryType entryType = type;
        if(entryType == null) {
            entryType = getEntryType(attributes, ATTR_STORE_AS);
            if(entryType == null) {
                throw new RuntimeException("Need specify 'storeAs' attribute in " + node);
            }
        }
        builder.setType(entryType);
        builder.setJavaType(clazz);
        builder.setMetaType(node.getLocalName());
        return builder;
    }

    private static ListEntry.Builder loadList(LoadContext context, Node node) {
        final ListEntry.Builder b = new ListEntry.Builder();
        final NodeList childNodes = node.getChildNodes();
        final int length = childNodes.getLength();
        SchemaEntry itemEntry = null;
        for(int i = 0; i < length; ++i) {
            Node item = childNodes.item(i);
            itemEntry = loadEntry(context, item);
            if(itemEntry != null) {
                break;
            }
        }
        b.setItemEntry(itemEntry);
        return b;
    }

    private static MapEntry.Builder loadMap(LoadContext context, Node node) {
        final MapEntry.Builder b = new MapEntry.Builder();
        final NodeList childNodes = node.getChildNodes();
        final int length = childNodes.getLength();
        int attrIndex = 0;
        for(int i = 0; i < length; ++i) {
            Node item = childNodes.item(i);
            SchemaEntry itemEntry = loadEntry(context, item);
            if(itemEntry != null) {
                if(attrIndex == 0) {
                    b.setKeyEntry(itemEntry);
                } else if(attrIndex == 1) {
                    b.setValueEntry(itemEntry);
                } else {
                    throw new RuntimeException("to many entries in map node: " + attrIndex);
                }
                attrIndex++;
            }
        }
        return b;
    }

    private static SchemaEntry loadEntry(LoadContext context, Node node) {
        if(node.getNodeType() != Node.ELEMENT_NODE) {
            return null;
        }
        final String name = node.getLocalName();
        final SchemaEntryBuilder entry;
        switch (name) {
            case NAME_MESSAGE:
            case NAME_STRUCT:
                entry = loadStruct(context, node);
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
            case NAME_BOOLEAN:
                entry = loadPrimitive(node, Boolean.class, EntryType.HALF);
                break;
            case NAME_PRIMITIVE:
                entry = loadPrimitive(node, Object.class);
                break;
            case NAME_ID:
                entry = loadPrimitive(node, byte[].class, EntryType.BYTE_16);
                break;
            case NAME_BLOB:
                entry = loadPrimitive(node, byte[].class, EntryType.BYTES);
                break;
            case NAME_STRING:
                entry = loadPrimitive(node, String.class, EntryType.BYTES);
                break;
            case NAME_LIST:
                entry = loadList(context, node);
                break;
            case NAME_MAP:
                entry = loadMap(context, node);
                break;
            case NAME_UNION:
                entry = loadUnion(context, node);
                break;
            default:
                throw new RuntimeException("unsupported node type: " + name);
        }

        boolean fieldNameRequired = false;
        final Node parentNode = node.getParentNode();
        if(parentNode != null) {
            final String localName = parentNode.getLocalName();
            fieldNameRequired = localName.equals(NAME_MESSAGE) || localName.equals(NAME_STRUCT);
        }
        entry.setFieldName(getAttributeValue(node.getAttributes(), ATTR_FIELD_NAME, fieldNameRequired));
        return entry.build();
    }

    private static UnionEntry.Builder loadUnion(LoadContext context, Node node) {
        UnionEntry.Builder b = UnionEntry.builder();
        final NodeList fields = node.getChildNodes();
        final int length = fields.getLength();
        for(int i = 0; i < length; ++i) {
            final Node field = fields.item(i);
            if(field.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final SchemaEntry entry = loadEntry(context, field);
            if(entry != null) {
                b.addEntry(entry);
            }
        }
        return b;
    }

    private static String getAttributeValue(NamedNodeMap attrs, String name, boolean required) {
        final Node node = attrs.getNamedItemNS(null, name);
        if(node == null) {
            if(required) {
                //this situation is possible when the validation is disabled or error in the description of the scheme
                throw new RuntimeException("need attribute: " + name);
            } else {
                return null;
            }
        }
        return node.getNodeValue();
    }
}
