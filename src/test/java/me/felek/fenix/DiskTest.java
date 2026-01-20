package me.felek.fenix;

import me.felek.fenix.disk.Disk;
import me.felek.fenix.disk.Sector;
import org.junit.Test;

import static org.junit.Assert.*;

public class DiskTest {
    @Test
    public void sectorWriting() {
        Sector sector = new Sector();
        sector.write(50, 50);
        assertEquals(50, sector.read(50));
    }

    @Test
    public void sectorInDiskWriting() {
        Disk disk = new Disk(65536);
        disk.write(0, 0, 50);
        assertEquals(50, disk.read(0, 0));
    }
}
