package me.felek.fenix.utils;

public class ColorUtils {
    private static final int[] BIOS_TO_ANSI_FG = {
            30, 34, 32, 36, 31, 35, 33, 37, 90, 94, 92, 96, 91, 95, 93, 97
    };

    private static final int[] BIOS_TO_ANSI_BG = {
            40, 44, 42, 46, 41, 45, 43, 47, 100, 104, 102, 106, 101, 105, 103, 107
    };

    public static void printcol(int col, String text) {
        int bgIndex = (col >> 4) & 0x0F;
        int fgIndex = col & 0x0F;

        String escapeCode = String.format("\u001B[%d;%dm",
                BIOS_TO_ANSI_FG[fgIndex],
                BIOS_TO_ANSI_BG[bgIndex]
        );

        System.out.print(escapeCode + text + "\u001B[0m");
    }
}
