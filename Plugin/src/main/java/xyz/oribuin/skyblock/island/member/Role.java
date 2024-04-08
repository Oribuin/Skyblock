package xyz.oribuin.skyblock.island.member;

public enum Role {
    OWNER(0),
    ADMIN(1),
    MEMBER(2);

    private final int priority;

    Role(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return this.ordinal();
    }

}
