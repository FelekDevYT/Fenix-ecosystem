package me.felek.fenix.utils;

public enum Exit {
    //INTERNAL - -NUMBER
    //EXTERNAL - +NUMBER
    //INTERNAL
    MEMORY_SEGMENTATION_ERROR(-10),
    REGISTER_NOT_FOUND(-20),
    LABEL_DOES_NOT_EXISTS(-40),
    INTERRUPTION_DOES_NOT_EXISTS(-60),
    UNDEFINED_VARIABLE(-50),
    DISK_SEGMENTATION_ERROR(-80),

    //EXTERNAL
    MEMORY_DUMP_OUT_OF_LENGTH(10);

    private int code;

    Exit(int code) {
        this.code = code;
    }

    public void exit() {
        System.exit(code);
    }
}
