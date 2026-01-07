package me.felek.fenix.disk;

import me.felek.fenix.utils.Exit;

public class Disk {
    public int DISK_SIZE;
    public Sector[] sectors;

    public Disk(int diskSize) {
        if (diskSize%Sector.SIZE != 0) {
            System.err.println("SEGMENTATION FAULT WHILE SECTORING DISK.");
            Exit.DISK_SEGMENTATION_ERROR.exit();
        }

        sectors = new Sector[diskSize/Sector.SIZE];
        DISK_SIZE = diskSize;

        for (int sector = 0; sector < sectors.length; sector++) {
            sectors[sector] = new Sector();
            for (int pos = 0; pos < Sector.SIZE; pos++) {
                sectors[sector].write(pos, 0);
            }
        }
    }

    public int[] readSector(int sector) {
        checkSegmentationFault(sector);

        return sectors[sector].getRaw();
    }

    public void writeSector(int sectorID, int[] sector) {
        checkSegmentationFault(sectorID);

        if (sector.length != Sector.SIZE) {
            System.err.println("DISK SEGMENTATION FAULT: Attempted to access sector " + sectorID +
                    ", but valid range is 0 to " + (sectors.length - 1) + ".");
            Exit.DISK_SEGMENTATION_ERROR.exit();
        }

        sectors[sectorID] = new Sector();
    }

    public void write(int sector, int pos, int value) {
        checkSegmentationFault(sector);

        sectors[sector].write(pos, value);
    }

    public int read(int sector, int pos) {
        checkSegmentationFault(sector);

        return sectors[sector].read(pos);
    }

    public void checkSegmentationFault(int sectorIndex) {
        if (sectorIndex >= sectors.length || sectorIndex < 0) {
            System.err.println("DISK SEGMENTATION FAULT: Attempted to access sector " + sectorIndex +
                    ", but valid range is 0 to " + (sectors.length - 1) + ".");
            Exit.DISK_SEGMENTATION_ERROR.exit();
        }
    }
}
