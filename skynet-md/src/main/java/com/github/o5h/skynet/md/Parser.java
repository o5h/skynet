package com.github.o5h.skynet.md;

import java.io.Reader;
import java.util.Scanner;

public class Parser {

    private Block currentBlock;
    private Scanner scanner;
    private String line;

    public Block parse(Reader r) {
        Block document = new Block(null, 0, "");
        this.currentBlock = document;
        this.scanner = new Scanner(r);
        while (scanner.hasNext()) {
            this.line = scanner.nextLine();
            int level = headerLevel(line);
            if (level > 0) {
                handleHeader(level);
            } else if (line.startsWith("* ")) {
                handleList();
            } else if (line.startsWith("```")) {
                handleCode();
            } else if (line.startsWith("<")) {
                handleHtml();
            } else if (line.startsWith("|")) {
                handleTable();
            } else if (line.trim().length() != 0) {
                handleText();
            }
        }
        return document;
    }

    private void handleCode() {
        Section code = getLastSectionOrAdd(Section.Type.CODE);
        while (scanner.hasNext()) {
            this.line = scanner.nextLine();
            if (line.startsWith("```")) {
                return;
            }
            code.addLine(line);
        }
    }


    private void handleTable() {
        Section table = getLastSectionOrAdd(Section.Type.TABLE);
        table.addLine(line);
        while (scanner.hasNext()) {
            this.line = scanner.nextLine();
            if (line.trim().length() == 0) {
                return;
            }
            table.addLine(line);
        }
    }

    private void handleHtml() {
        Section html = getLastSectionOrAdd(Section.Type.HTML);
        html.addLine(line);
        while (scanner.hasNext()) {
            this.line = scanner.nextLine();
            if (line.trim().length() == 0) {
                return;
            }
            html.addLine(line);
        }
    }


    private void handleHeader(int level) {
        Block parent = findParentAtLevel(currentBlock, level - 1);
        String title = this.line.substring(level).trim();
        currentBlock = parent.addBlock(title);
    }

    private void handleText() {
        Section text = getLastSectionOrAdd(Section.Type.TEXT);
        text.addLine(line);
    }

    private void handleList() {
        Section list = getLastSectionOrAdd(Section.Type.LIST);
        list.addLine(line);
    }

    private static Block findParentAtLevel(Block block, int targetLevel) {
        if (targetLevel > block.getLevel()) {
            throw new IllegalArgumentException("Target level greater then current level");
        }
        while (block.getLevel() != targetLevel) {
            block = block.getParent();
        }
        return block;
    }


    private Section getLastSectionOrAdd(Section.Type type) {
        Section section = currentBlock.getLastSection(type);
        if (section == null) {
            section = currentBlock.addSection(type);
        }
        return section;
    }


    private static int headerLevel(String line) {
        if (!line.startsWith("#")) {
            return -1;
        }
        String headerPrefix = line.trim().split(" ")[0];
        for (int i = 0; i < headerPrefix.length(); i++) {
            if (headerPrefix.charAt(i) != '#') {
                return -1;
            }
        }
        return headerPrefix.length();
    }


}
