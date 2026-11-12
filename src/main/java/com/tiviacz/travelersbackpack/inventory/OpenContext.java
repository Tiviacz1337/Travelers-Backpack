package com.tiviacz.travelersbackpack.inventory;

public enum OpenContext {
    ITEM(0),
    BLOCK_ENTITY(1),
    EQUIPPED(2);

    private final int id;

    OpenContext(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static OpenContext fromId(int id) {
        return switch (id) {
            case 1 -> BLOCK_ENTITY;
            case 2 -> EQUIPPED;
            default -> ITEM;
        };
    }

    public boolean isItem() {
        return this == ITEM;
    }

    public boolean isBlockEntity() {
        return this == BLOCK_ENTITY;
    }

    public boolean isEquipped() {
        return this == EQUIPPED;
    }
}