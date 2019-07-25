package com.github.o5h.skynet.md;

import java.util.ArrayList;

public class Block {

    private final Block parent;
    private final int level;
    private final String title;
    private final ArrayList<Section> sections = new ArrayList<Section>();
    private final ArrayList<Block> blocks = new ArrayList<Block>();

    public Block() {
        this.parent = null;
        this.level = 0;
        this.title = "";
    }

    protected Block(Block parent, int level, String title) {
        this.parent = parent;
        this.level = level;
        this.title = title;
    }

    public Block getParent() {
        return parent;
    }

    public int getLevel() {
        return level;
    }

    public String getTitle() {
        return title;
    }

    public int getSectionNumber() {
        return this.sections.size();
    }

    public Section getFirstSection(Section.Type type) {
        for (int i = 0; i < this.sections.size(); i++) {
            Section section = this.sections.get(i);
            if (section.getType() == type) {
                return section;
            }
        }
        return null;
    }

    public Section getLastSection(Section.Type type) {
        for (int i = this.sections.size(); i-- > 0; ) {
            Section section = this.sections.get(i);
            if (section.getType() == type) {
                return section;
            }
        }
        return null;
    }

    public Section addSection(Section.Type type) {
        Section section = new Section(type);
        this.sections.add(section);
        return section;
    }

    public Block getBlock(String title) {
        for (Block block : blocks) {
            if (block.getTitle().equals(title)) {
                return block;
            }
        }
        return null;
    }

    public Block addBlock(String title) {
        Block subBlock = new Block(this, level + 1, title);
        blocks.add(subBlock);
        return subBlock;
    }

    public Block[] getBlocks() {
        return blocks.toArray(new Block[]{});
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (level > 0) {
            for (int i = 0; i < level; i++) {
                sb.append("#");
            }
            sb.append(" ");
            sb.append(title);
            sb.append("\n");
        }
        sb.append("\n");
        for (Section s : sections) {
            if (s.getType() == Section.Type.CODE) {
                sb.append("```\n");
            }
            sb.append(s.toString());
            if (s.getType() == Section.Type.CODE) {
                sb.append("```\n");
            }
            sb.append("\n");
        }
        for (Block b : blocks) {
            sb.append(b.toString());
        }
        return sb.toString();
    }
}
