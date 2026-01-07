package me.felek.fenix.VM;

import me.felek.fenix.asm.registers.RegisterUtils;
import me.felek.fenix.disk.Disk;
import me.felek.fenix.disk.Sector;
import me.felek.fenix.mem.MemoryBlock;

import javax.swing.*;

public class InfoFrame {
    private int WIDTH = 1280;
    private int HEIGHT = 720;

    public InfoFrame(MemoryBlock memoryBlock) {
        JFrame frame = new JFrame();
        frame.setTitle("FenixVM - INFO");
        frame.setSize(WIDTH, HEIGHT);

        JTabbedPane pane = new JTabbedPane();

            JPanel registers = getRegistersPanel();
            JPanel memory = getMemoryPanel(memoryBlock);

        pane.addTab("Registers", registers);
        pane.addTab("Memory dump", memory);

        frame.add(pane);

        frame.setVisible(true);
    }

    public JPanel getMemoryPanel(MemoryBlock memoryBlock) {
        JPanel mem = new JPanel(null);
        String[][] data = new String[memoryBlock.getSize()][3];

        for (int i = 0; i < memoryBlock.getSize(); i++) {
            data[i][0] = String.valueOf(i);
            data[i][1] = String.valueOf(memoryBlock.get(i));
            data[i][2] = String.valueOf((char) memoryBlock.get(i));
        }

        String[] columns = new String[]{"Field", "Value", "Character"};

        JTable table = new JTable(data, columns);
        table.setCellSelectionEnabled(false);

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(0, 0, WIDTH-50, HEIGHT-70);

        mem.add(sp);

        return mem;
    }

    public JPanel getRegistersPanel() {
        JPanel reg = new JPanel(null);
        String[][] data = new String[16][2];

        for (int i = 0; i < 16; i++) {
            data[i][0] = String.valueOf(i);
            data[i][1] = String.valueOf(RegisterUtils.getRegisterValue(i));
        }

        String[] columns = new String[]{"Register", "Value"};

        JTable table = new JTable(data, columns);
        table.setCellSelectionEnabled(false);
        table.setBounds(0, 0, WIDTH-50, HEIGHT-70);

        reg.add(table);
        return reg;
    }
}
