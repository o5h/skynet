package com.github.o5h.skynet.md.table;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.Reader;
import java.util.Locale;

public class HTMLParser {
    private Table table;

    public Table parse(Reader reader) throws IOException, SAXException, ParserConfigurationException {
        this.table = new Table();
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = parserFactory.newSAXParser();
        InputSource inputSource = new InputSource(reader);
        inputSource.setEncoding("UTF-8");
        saxParser.parse(inputSource, new Handler());
        return table;
    }

    class Handler extends DefaultHandler {

        private Row row;
        private Cell cell;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            String name = qName.toLowerCase(Locale.ROOT);
            if ("tr".equals(name)) {
                this.row = new Row();
                table.add(row);
            } else if ("td".equals(name)) {
                this.cell = new Cell();
                String colspan = attributes.getValue("colspan");

                if (colspan != null) {
                    cell.setColspan(Integer.valueOf(colspan));
                }
                row.add(cell);
            } else if ("input".equals(name)) {
                String id = attributes.getValue("name");
                String type = attributes.getValue("type");
                Input input = new Input(id, type);
                cell.addInput(input);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            String name = qName.toLowerCase(Locale.ROOT);
            if ("tr".equals(name)) {
                this.row = null;
            } else if ("td".equals(name)) {
                this.cell = null;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String text = new String(ch, start, length);
            if (this.cell != null) {
                this.cell.addText(text);
            }
        }
    }
}
