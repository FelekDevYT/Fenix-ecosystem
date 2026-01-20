package me.felek.fenix.disk;

import me.felek.fenix.utils.Exit;

import java.util.Arrays;

public class Sector {
    public static final int SIZE = 512;

    private int[] sector;

    public Sector() {
        sector = new int[SIZE];
    }

    public int[] getRaw() {
        return sector;
    }

    public void write(int pos, int value) {
        checkSegmentationFault(pos);

        sector[pos] = value;
    }

    public int read(int pos) {
        checkSegmentationFault(pos);

        return sector[pos];
    }

    public void checkSegmentationFault(int address) {
        if (address > sector.length || address < 0) {
            System.err.println("SEGMENTATION FAULT AT " + address);
            Exit.DISK_SEGMENTATION_ERROR.exit();
        }
    }
}
