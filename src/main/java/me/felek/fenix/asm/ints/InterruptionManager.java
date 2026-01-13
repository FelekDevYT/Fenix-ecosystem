package me.felek.fenix.asm.ints;

import me.felek.fenix.asm.FenixAssembler;
import me.felek.fenix.asm.registers.RegisterUtils;
import me.felek.fenix.asm.registers.Registers;
import me.felek.fenix.mem.MemoryBlock;
import me.felek.fenix.utils.Exit;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class InterruptionManager {
    private PrintStream out;
    private BlockingQueue<Integer> keyboardBuffer = new ArrayBlockingQueue<>(256);
    private FenixAssembler vm;

    public InterruptionManager(FenixAssembler vm, PrintStream stream) {
        out = stream;
        this.vm = vm;
    }

    public void clearKeyboardBuffer() {
        keyboardBuffer.clear();
    }

    public void pushKeyToBuffer(int keyCode) {
        try {
            keyboardBuffer.put(keyCode);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void run(int code) {
        MemoryBlock mem = vm.getMemory();

        switch (code) {
            case 0:
                //R6 - type (0) - print (1) - println (2) - one symbol print
                //R7 - address
                if (RegisterUtils.getRegisterValue(6) == 2) {
                    //r7 is just symbol char number xd
                    out.print((char) RegisterUtils.getRegisterValue(7));
                    return;
                }
                int address = RegisterUtils.getRegisterValue(7);
                StringBuilder sb = new StringBuilder();
                while (mem.get(address) != 0) {
                    sb.append((char) mem.get(address));

                    address++;
                }
                if (RegisterUtils.getRegisterValue(6) == 1) {
                    out.println(sb.toString());
                } else {
                    out.print(sb.toString());
                }
                break;
            case 1:
                try {
                    int key = keyboardBuffer.take();
                    RegisterUtils.setRegisterValue(5, key);
                } catch (InterruptedException exc) {
                    RegisterUtils.setRegisterValue(5, 0);
                    Thread.currentThread().interrupt();
                }
                break;
            case 2:
                //r6 - type (0) - shutdown (1) - reboot
                int operation = RegisterUtils.getRegisterValue(6);
                if (operation == 0) {
                    System.exit(0);
                }
                break;
            case 3://DISK MANAGER
                //r6 - type (0) - read (1) - write
                //r7 - SECTOR NUMBER
                //r8 - address from sector to r/w
                //r9 - address from RAM to WRITE

                int type = RegisterUtils.getRegisterValue(6);
                int sector = RegisterUtils.getRegisterValue(7);
                int sectorAddress = RegisterUtils.getRegisterValue(8);
                int addressForWrite = RegisterUtils.getRegisterValue(9);

                if (type == 0) {
                    int read = vm.getCurrentDisk().read(sector, sectorAddress);

                    RegisterUtils.setRegisterValue(5, read);
                } else if (type == 1) {
                    vm.getCurrentDisk().write(sector, sectorAddress, mem.get(addressForWrite));//sector, position, value
                }
                break;
        }
    }
}
