package me.felek.fenix.VM;

import me.felek.fenix.asm.FenixAssembler;
import me.felek.fenix.asm.ints.InterruptionManager;
import me.felek.fenix.compiler.Interpreter;
import me.felek.fenix.compiler.Lexer;
import me.felek.fenix.compiler.Preprocessor;
import me.felek.fenix.compiler.Token;
import me.felek.fenix.disk.Disk;
import me.felek.fenix.utils.PParser;

import javax.swing.*;
import java.io.*;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.raylib.Colors.DARKGRAY;
import static com.raylib.Colors.WHITE;
import static com.raylib.Raylib.*;

public class VMMain {
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;
    public static final int PANEL_HEIGHT = 30;
    public static final int CONSOLE_COLUMNS = 80;
    public static final int CONSOLE_ROWS = 28;

    public static void main(String[] args) throws IOException {
        //STAGE 1 - preload
        File settings = new File("vm/settings.properties");
        String mainFile;
        boolean doShowTokens = true;
        if (settings.exists()) {
            mainFile = PParser.getString(settings.getPath(), "main");
            doShowTokens = Boolean.parseBoolean(PParser.getString(settings.getPath(), "doShowTokens"));
        } else {
            //generate
            new File("vm").mkdirs();
            settings.createNewFile();

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(Paths.get(settings.getPath()).toFile()))) {
                bw.write("main=main.fnx");
                bw.write("doShowTokens=true");
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("main.fnx")))) {
                bw.write("db hey, \"Hello, world!\"\n" +
                        "mov r6, 1\n" +
                        "load r7, hey\n" +
                        "int 0");
            }
            mainFile = "main.fnx";
        }

        InitWindow(SCREEN_WIDTH, SCREEN_HEIGHT, "FenixVM - VM for Fenix ecosystem");
        SetTargetFPS(60);

        Rectangle panelArea = new Rectangle().x(0).y(0).width(SCREEN_WIDTH).height(PANEL_HEIGHT);
        Rectangle terminalArea = new Rectangle().x(0).y(PANEL_HEIGHT).width(SCREEN_WIDTH).height(SCREEN_HEIGHT - PANEL_HEIGHT);

        Terminal terminal = new Terminal(CONSOLE_COLUMNS, CONSOLE_ROWS);
        terminal.calculateSize((int)terminalArea.width(), (int)terminalArea.height());
        Font f = LoadFontEx("arial.ttf", terminal.getFontSize(), (IntBuffer) null, 0);
        RenderTexture terminalTexture = LoadRenderTexture((int)terminalArea.width(), (int)terminalArea.height());
        PrintStream terminalOut = new PrintStream(terminal);

        FenixAssembler fenixVM = new FenixAssembler(65536);
        InterruptionManager im = new InterruptionManager(fenixVM, terminalOut);
        fenixVM.registerInterruptionManager(im);

        Thread vmThread = null;

        int[] bytecode = new int[0];
        try {
            Preprocessor preprocessor = new Preprocessor();

            String sourceCode = Files.readString(Path.of(mainFile));

            sourceCode = preprocessor.preprocess(sourceCode);

            List<Token> tokens = Lexer.tokenize(sourceCode);
            if (doShowTokens) {
                System.out.println(tokens);
            }
            bytecode = Interpreter.compile(tokens);

            int[] finalBytecode = bytecode;
            vmThread = new Thread(() -> fenixVM.start(finalBytecode));
            vmThread.start();
        } catch (IOException e) {
            terminalOut.println("ERROR: Could not load 'program.fnx'. " + e.getMessage());
            bytecode = new int[0];
        }

        while (!WindowShouldClose()) {
            int key = GetCharPressed();
            while (key > 0) {
                im.pushKeyToBuffer(key);
                key = GetCharPressed();
            }
            if (IsKeyPressed(KEY_ENTER)) im.pushKeyToBuffer('\n');
            if (IsKeyPressed(KEY_BACKSPACE)) im.pushKeyToBuffer('\b');

            BeginTextureMode(terminalTexture);
            ClearBackground(new Color().r((byte)20).g((byte)20).b((byte)30).a((byte)255));
            terminal.draw(f);
            EndTextureMode();

            BeginDrawing();
            ClearBackground(DARKGRAY);

            GuiPanel(panelArea, "Control Panel");
            Rectangle resetButtonRec = new Rectangle()
                    .x(panelArea.x() + 5)
                    .y(panelArea.y() + 5)
                    .width(80)
                    .height(20);

            if (GuiButton(resetButtonRec, "RESET VM") == 1) {
                terminal.clear(' ');
                im.clearKeyboardBuffer();
                if (vmThread != null) {
                    vmThread.interrupt();
                }

                UnloadFont(f);
                UnloadRenderTexture(terminalTexture);
                CloseWindow();
                VMMain.main(args);
            }

            Rectangle infoButtonRec = new Rectangle()
                    .x(panelArea.x() + 90)
                    .y(panelArea.y() + 5)
                    .width(80)
                    .height(20);

            if (GuiButton(infoButtonRec, "Info") == 1) {
                new InfoFrame(fenixVM.getMemory());
            }

            Rectangle sourceRec = new Rectangle()
                    .x(0)
                    .y(0)
                    .width(terminalTexture.texture().width())
                    .height(-terminalTexture.texture().height());

            DrawTextureRec(terminalTexture.texture(), sourceRec, new Vector2().x(terminalArea.x()).y(terminalArea.y()), WHITE);
            EndDrawing();
        }

        if (vmThread != null) {
            vmThread.interrupt();
        }

        UnloadFont(f);
        UnloadRenderTexture(terminalTexture);
        CloseWindow();
    }
}