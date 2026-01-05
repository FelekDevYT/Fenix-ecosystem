package me.felek.fenix.asm.registers;

import me.felek.fenix.utils.Exit;

public class RegisterUtils {
    public static Registers getRegisterByNumber(int num) {
        try {
            Registers reg = Registers.valueOf("R" + num);

            return reg;
        } catch (IllegalArgumentException exc) {
            System.err.println("NOT FOUND REGISTER R" + num);
            Exit.REGISTER_NOT_FOUND.exit();
        }
        return null;//CANT BE CALLED!!!
    }

    public static void setRegisterValue(int reg, int val) {
        getRegisterByNumber(reg).value = val;
    }

    public static int getRegisterValue(int reg){
        return getRegisterByNumber(reg).value;
    }
}
