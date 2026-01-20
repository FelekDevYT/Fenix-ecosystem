package me.felek.fenix.asm;

import me.felek.fenix.VM.Terminal;
import me.felek.fenix.VM.VideoMode;
import me.felek.fenix.asm.flags.Flags;
import me.felek.fenix.asm.ints.InterruptionManager;
import me.felek.fenix.asm.registers.RegisterUtils;
import me.felek.fenix.disk.Disk;
import me.felek.fenix.mem.MemoryBlock;
import me.felek.fenix.utils.Exit;
import me.felek.fenix.utils.StackUtils;

import java.util.HashMap;
import java.util.Map;

public class FenixAssembler {
    public Map<Integer, Integer> labels = new HashMap<>();
    private InterruptionManager im;
    private MemoryBlock memory;
    private Terminal terminal;
    private int[] opcodes;

    private Disk currentDisk;

    private int IP = 0;
    private final int SP = 15;

    public FenixAssembler(int memSize, Terminal terminal) {
        memory = new MemoryBlock(memSize);
        this.terminal = terminal;

        currentDisk = new Disk(65536);
    }
//
//    public void loadProgram(int[] bytecode) {
//        this.opcodes = bytecode;
//        this.IP = 0;
//        System.out.println("Parsing labels...");
//        for (int i = 0; i < opcodes.length; i++) {
//            if (opcodes[i] == 0x40) {
//                labels.put(opcodes[i + 1], i);
//                i += 1;
//            }
//        }
//    }

    public InterruptionManager getInterruptionManager() {
        return im;
    }

    public void registerInterruptionManager(InterruptionManager im) {
        this.im = im;
    }

    public MemoryBlock getMemory() {
        return memory;
    }

    public Disk getCurrentDisk() {
        return currentDisk;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public void start(int[] bytecode) {
        this.opcodes = bytecode;
        this.IP = 0;
        this.labels.clear();
        RegisterUtils.setRegisterValue(SP, 0);

        System.out.println("Parsing labels...");
        for (int i = 0; i < opcodes.length; i++) {
            if (opcodes[i] == 0x40) {
                labels.put(opcodes[i + 1], i);
                i++;
            }
        }
        System.out.println("VM Thread Started. Running code...");

        while (!Thread.currentThread().isInterrupted() && IP < opcodes.length) {
            int opcode = opcodes[IP];

            switch (opcode) {
                case 0x10://MOV REG, VALUE
                    RegisterUtils.setRegisterValue(opcodes[IP+1], opcodes[IP+2]);
                    IP += 3;
                    break;
                case 0x11://MOV REG, REG
                    RegisterUtils.setRegisterValue(opcodes[IP+1], RegisterUtils.getRegisterValue(opcodes[IP+2]));
                    IP += 3;
                    break;
                /*
                    MATH BLOCK
                 */
                case 0x200://ADD REG, REG
                    int summ = RegisterUtils.getRegisterValue(opcodes[IP+1]) + RegisterUtils.getRegisterValue(opcodes[IP+2]);
                    RegisterUtils.setRegisterValue(opcodes[IP+1], summ);
                    IP += 3;
                    break;
                case 0x201://ADD REG, VALUE
                    summ = RegisterUtils.getRegisterValue(opcodes[IP+1]) + opcodes[IP+2];
                    RegisterUtils.setRegisterValue(opcodes[IP+1], summ);
                    IP += 3;
                    break;

                case 0x210://SUB REG, REG
                    int sub = RegisterUtils.getRegisterValue(opcodes[IP+1]) - RegisterUtils.getRegisterValue(opcodes[IP+2]);
                    RegisterUtils.setRegisterValue(opcodes[IP+1], sub);
                    IP += 3;
                    break;
                case 0x211://SUB REG, VALUE
                    sub = RegisterUtils.getRegisterValue(opcodes[IP+1]) - opcodes[IP+2];
                    RegisterUtils.setRegisterValue(opcodes[IP+1], sub);
                    IP += 3;
                    break;

                case 0x220://MUL REG, REG
                    int mul = RegisterUtils.getRegisterValue(opcodes[IP+1]) * RegisterUtils.getRegisterValue(opcodes[IP+2]);
                    RegisterUtils.setRegisterValue(opcodes[IP+1], mul);
                    IP += 3;
                    break;
                case 0x221://MUL REG, VALUE
                    mul = RegisterUtils.getRegisterValue(opcodes[IP+1]) * opcodes[IP+2];
                    RegisterUtils.setRegisterValue(opcodes[IP+1], mul);
                    IP += 3;
                    break;

                case 0x230://DIV REG, REG
                    int div = RegisterUtils.getRegisterValue(opcodes[IP+1]) / RegisterUtils.getRegisterValue(opcodes[IP+2]);
                    RegisterUtils.setRegisterValue(opcodes[IP+1], div);
                    IP += 3;
                    break;
                case 0x231://DIV REG, VALUE
                    div = RegisterUtils.getRegisterValue(opcodes[IP+1]) / opcodes[IP+2];
                    RegisterUtils.setRegisterValue(opcodes[IP+1], div);
                    IP += 3;
                    break;

                /*
                    LOGICAL
                 */
                case 0x300://CMP REG, REG
                    Flags.EQ.isEnabled = RegisterUtils.getRegisterValue(opcodes[IP+1]) == RegisterUtils.getRegisterValue(opcodes[IP+2]);
                    IP += 3;
                    break;
                case 0x301://CMP REG, VALUE
                    Flags.EQ.isEnabled = RegisterUtils.getRegisterValue(opcodes[IP+1]) == opcodes[IP+2];
                    IP += 3;
                    break;
                case 0x302://CMP VALUE, VALUE
                    Flags.EQ.isEnabled = opcodes[IP+1] == opcodes[IP+2];
                    IP += 3;
                    break;

                case 0x310://ZERO VALUE
                    Flags.ZERO.isEnabled = opcodes[IP+1] == 0;
                    IP += 2;
                    break;
                case 0x311://ZERO REG
                    Flags.ZERO.isEnabled = RegisterUtils.getRegisterValue(opcodes[IP+1]) == 0;
                    IP += 2;
                    break;

                case 0x320://GR REG, REG
                    Flags.GT.isEnabled = RegisterUtils.getRegisterValue(opcodes[IP+1]) > RegisterUtils.getRegisterValue(opcodes[IP+2]);
                    IP += 3;
                    break;
                case 0x321://GR REG, VALUE
                    Flags.GT.isEnabled = RegisterUtils.getRegisterValue(opcodes[IP+1]) > opcodes[IP+2];
                    IP += 3;
                    break;
                case 0x322://GR VALUE, VALUE
                    Flags.GT.isEnabled = opcodes[IP+1] > opcodes[IP+2];
                    IP += 3;
                    break;

                case 0x330://LT REG, REG
                    Flags.LT.isEnabled = RegisterUtils.getRegisterValue(opcodes[IP+1]) < RegisterUtils.getRegisterValue(opcodes[IP+2]);
                    IP += 3;
                    break;
                case 0x331://LT REG, VALUE
                    Flags.LT.isEnabled = RegisterUtils.getRegisterValue(opcodes[IP+1]) < opcodes[IP+2];
                    IP += 3;
                    break;
                case 0x332://LT VALUE, VALUE
                    Flags.LT.isEnabled = opcodes[IP+1] < opcodes[IP+2];
                    IP += 3;
                    break;
                    /*
                    FLOW CONTROL
                     */
                case 0x40://LABEL
                    IP += 2;
                    break;//just skip
                case 0x41://JMP LABELNAME(ID)
                    IP = getLabel(opcodes[IP+1]);
                    break;
                case 0x42://JE
                    if (Flags.EQ.isEnabled) {
                        IP = getLabel(opcodes[IP+1]);
                    } else {
                        IP += 2;
                    }
                    break;
                case 0x43://GT
                    if (Flags.GT.isEnabled) {
                        IP = getLabel(opcodes[IP+1]);
                    } else {
                        IP += 2;
                    }
                    break;
                case 0x44://LT
                    if (Flags.LT.isEnabled) {
                        IP = getLabel(opcodes[IP+1]);
                    } else {
                        IP += 2;
                    }
                    break;
                case 0x45://JZ
                    if (Flags.ZERO.isEnabled) {
                        IP = getLabel(opcodes[IP+1]);
                    } else {
                        IP += 2;
                    }
                    break;
                case 0x46://JNE
                    if (!Flags.EQ.isEnabled) {
                        IP = getLabel(opcodes[IP+1]);
                    } else {
                        IP += 2;
                    }
                    break;
                case 0x47://CALL labelName
                    int returnAddress = IP + 2;//next operation
                    StackUtils.push(memory, returnAddress);
                    IP = getLabel(opcodes[IP+1]);
                    break;
                case 0x48://RET
                    IP = StackUtils.pop(memory);
                    break;
                /*
                MEMORY MANAGEMENT
                 */
                case 0x50://STORE ADDR, REG
                    memory.set(opcodes[IP+1], RegisterUtils.getRegisterValue(opcodes[IP+2]));
                    IP += 3;
                    break;
                case 0x51://STORE ADDR, VALUE
                    memory.set(opcodes[IP+1], opcodes[IP+2]);
                    IP += 3;
                    break;
                case 0x53://STORE REG, REG
                    memory.set(RegisterUtils.getRegisterValue(opcodes[IP+1]), RegisterUtils.getRegisterValue(opcodes[IP+2]));
                    IP += 3;
                    break;
                case 0x54://STORE REG, VALUE
                    memory.set(RegisterUtils.getRegisterValue(opcodes[IP+1]), opcodes[IP+2]);
                    IP += 3;
                    break;
                case 0x52://LOAD REG, ADDR
                    RegisterUtils.setRegisterValue(opcodes[IP+1], memory.get(opcodes[IP+2]));
                    IP += 3;
                    break;
                case 0x55:
                    RegisterUtils.setRegisterValue(opcodes[IP + 1], memory.get(RegisterUtils.getRegisterValue(opcodes[IP + 2])));
                    IP += 3;
                    break;
                    /*
                    INTERRUPTIONS
                     */
                case 0x60://int syscall
                    if (im != null) {
                        im.run(opcodes[IP+1]);
                    }
                    IP += 2;
                    break;
                    /*
                    STACK
                     */
                case 0x70://PUSH value + R15++
                    int value = opcodes[IP+1];
//                    memory.setFromEnd(RegisterUtils.getRegisterValue(15), value);
//                    RegisterUtils.setRegisterValue(15, RegisterUtils.getRegisterValue(15) + 1);
                    StackUtils.push(memory, value);
                    IP += 2;
                    break;
                case 0x71://POP REG
                    int reg = opcodes[IP+1];
//                    RegisterUtils.setRegisterValue(reg, memory.getFromEnd(RegisterUtils.getRegisterValue(15)));
//                    RegisterUtils.setRegisterValue(15, RegisterUtils.getRegisterValue(15) - 1);
                    RegisterUtils.setRegisterValue(reg, StackUtils.pop(memory));
                    IP += 2;
                    break;
                case 0x72://PUSH REG
                    StackUtils.push(memory, RegisterUtils.getRegisterValue(opcodes[IP+1]));
                    IP += 2;
                    break;

                default:
                    IP++;
                    break;
            }
        }
        System.out.println("VM Thread Finished.");
    }

    public int getLabel(int lblID) {
        if (!labels.containsKey(lblID)) {
            System.err.println("Label " + lblID + " does not exists");
            Exit.LABEL_DOES_NOT_EXISTS.exit();
        }
        
        return labels.get(lblID);
    }
}
