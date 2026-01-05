package me.felek.fenix.utils;

import me.felek.fenix.asm.registers.RegisterUtils;
import me.felek.fenix.mem.MemoryBlock;

public class StackUtils {
    public static void push(MemoryBlock block, int value) {
        block.setFromEnd(RegisterUtils.getRegisterValue(15), value);
        RegisterUtils.setRegisterValue(15, RegisterUtils.getRegisterValue(15) + 1);
    }

    public static int pop(MemoryBlock block) {
//        int ret = block.getFromEnd(RegisterUtils.getRegisterValue(15));
//        RegisterUtils.setRegisterValue(15, RegisterUtils.getRegisterValue(15) - 1);
//        return ret;
        RegisterUtils.setRegisterValue(15, RegisterUtils.getRegisterValue(15) - 1);
        int ret = block.getFromEnd(RegisterUtils.getRegisterValue(15));
        return ret;
    }
}
