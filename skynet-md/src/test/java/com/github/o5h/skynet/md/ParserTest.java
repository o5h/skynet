package com.github.o5h.skynet.md;

import com.github.o5h.skynet.md.table.HTMLParser;
import com.github.o5h.skynet.md.table.Table;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

import static junit.framework.TestCase.assertEquals;

public class ParserTest {

    @Test
    public void parse_h1() {
        Parser parser = new Parser();
        Reader reader = open("h1.md");
        Block doc = parser.parse(reader);
        Block block = doc.getBlocks()[0];

        assertEquals(1, block.getLevel());
        assertEquals("Header 1", block.getTitle());
        assertEquals("Text\n", block.getFirstSection(Section.Type.TEXT).toString());
    }

    @Test
    public void parse_popup() {
        Parser parser = new Parser();
        Reader reader = open("popup.md");
        Block doc = parser.parse(reader);
        Block block = doc.getBlocks()[0].getBlocks()[1];
        assertEquals("Function: Test", block.getTitle());
        assertEquals("def test():\nend\n", block.getLastSection(Section.Type.CODE).toString());
    }

    @Test
    public void parse_popup_html() throws ParserConfigurationException, SAXException, IOException {
        Parser parser = new Parser();
        Reader reader = open("popup.md");
        Block doc = parser.parse(reader);
        Section htmlSection = doc
                .getBlocks()[0]
                .getBlocks()[0]
                .getBlock("View")
                .getLastSection(Section.Type.HTML);

        String htmlString = htmlSection.toString();
        Table table = new HTMLParser().parse(new StringReader(htmlString));
        assertEquals("${Message}", table.getRows()[1].getCells()[0].toString());

    }

    private Reader open(String resource) {
        InputStream is = ParserTest.class.getResourceAsStream(resource);
        Reader reader = new InputStreamReader(is);
        return reader;
    }
}