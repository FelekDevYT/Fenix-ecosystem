package me.felek.fenix.asm.flags;

public enum Flags {
    EQ(false),
    ZERO(false),
    GT(false),
    LT(false);

    public boolean isEnabled;

    Flags(boolean e) {
        isEnabled = e;
    }
}
