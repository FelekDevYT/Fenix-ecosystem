package me.felek.fenix.asm.registers;

public enum Registers {
    R0(0, 0),
    R1(1, 0),
    R2(2, 0),
    R3(3, 0),
    R4(4, 0),
    R5(5, 0),
    R6(6, 0),
    R7(7, 0),
    R8(8, 0),
    R9(9, 0),
    R10(10, 0),
    R11(11, 0),
    R12(12, 0),
    R13(13, 0),
    R14(14, 0),
    R15(15, 0);

    public int name, value;

    Registers(int n, int v) {
        this.name = n;
        this.value = v;
    }
}
