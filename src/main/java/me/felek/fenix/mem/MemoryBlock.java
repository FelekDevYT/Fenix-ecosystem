package me.felek.fenix.mem;

import me.felek.fenix.utils.Exit;

public class MemoryBlock {
    private int[] memory;

    public MemoryBlock(int size) {
        memory = new int[size];
    }

    public int getSize() {
        return memory.length;
    }

    public MemoryBlock() {
        memory = new int[65536];
    }

    public void set(int address, int value) {
        checkSegmentationFault(address);

        memory[address] = value;
    }

    public int get(int address) {
        checkSegmentationFault(address);

        return memory[address];
    }

    public void setFromEnd(int addressOffset, int value) {
        checkSegmentationFault(memory.length - addressOffset);

        memory[memory.length - addressOffset - 1] = value;
    }

    public int getFromEnd(int addressOffset) {
        checkSegmentationFault(memory.length - 1 - addressOffset);
        return memory[memory.length - 1 - addressOffset];
    }

    public void dump(int startIndex, int length) {
        if (startIndex + length > memory.length || startIndex < 0) {
            System.err.println("EXTERNAL OUT OF LENGTH WHILE DUMPING");
            Exit.MEMORY_DUMP_OUT_OF_LENGTH.exit();
        }
        System.out.println("Dump of memory:");

        for (int i = startIndex; i < startIndex + length; i++) {
            System.out.println(String.format("%d: %d", i, memory[i]));
        }
    }

    public void checkSegmentationFault(int address) {
        if (address > memory.length || address < 0) {
            System.err.println("SEGMENTATION FAULT AT " + address);
            Exit.MEMORY_SEGMENTATION_ERROR.exit();
        }
    }
}
