package io.github.techtastic.computation.machine;

import com.hypixel.hytale.server.core.Message;
import org.jline.builtins.SyntaxHighlighter;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class BaseMachine {
    private static final long F_FOREGROUND = 0x00000010;
    private static final long F_FOREGROUND_RGB = 0x00004000;
    private static final int FG_COLOR_EXP = 15;
    private static final long FG_COLOR = 0xFFFFFFL << FG_COLOR_EXP;

    private SyntaxHighlighter syntaxHighlighter;

    public BaseMachine(SyntaxHighlighter syntaxHighlighter) {
        init();
        this.syntaxHighlighter = syntaxHighlighter;

        System.out.println("Syntax Highlighter: " + this.syntaxHighlighter.getCurrentTheme());
    }

    protected abstract void init();

    public abstract String getType();

    public Object[] runScript(File script, Consumer<String> printCallback, Consumer<String> errorCallback) throws IOException {
        return runScript(Files.readString(script.toPath()), printCallback, errorCallback);
    }

    public abstract Object[] runScript(String script, Consumer<String> printCallback, Consumer<String> errorCallback);

    public String highlightSyntax(String code) {
        AttributedString formatted = this.syntaxHighlighter.highlight(code);
        return formatted.toAnsi();

        /*List<Message> messages = new ArrayList<>();
        String text = formatted.toString();

        if (text.isEmpty())
            return Message.raw(formatted.toString());

        int start = 0;
        AttributedStyle currentStyle = formatted.styleAt(0);
        for (int i = 1; i < text.length(); i++) {
            AttributedStyle style = formatted.styleAt(i);
            if (!style.equals(currentStyle)) {
                String message = text.substring(start, i);
                messages.add(Message
                        .raw(message)
                        .bold((style.getStyle() & 1L) != 0)
                        .italic((style.getStyle() & 4L) != 0)
                        .color(extractTextColor(style))
                );

                start = i;
                currentStyle = style;
            }
        }

        if (messages.isEmpty())
            return Message
                .raw(text)
                .bold((currentStyle.getStyle() & 1L) != 0)
                .italic((currentStyle.getStyle() & 4L) != 0)
                .color(extractTextColor(currentStyle));
        return Message.join(messages.toArray(new Message[0]));*/
    }

    // Stupid packed long
    private Color extractTextColor(AttributedStyle style) {
        long value = style.getStyle();

        if ((value & F_FOREGROUND) == 0)
            return Color.WHITE;

        int color = (int) ((value & FG_COLOR) >>> FG_COLOR_EXP);

        if ((value & F_FOREGROUND_RGB) != 0)
            return new Color(
                    (color >> 16) & 0xFF,
                    (color >> 8) & 0xFF,
                    color & 0xFF
            );
        return parseAnsi(color & 0xFF);
    }

    private Color parseAnsi(int index) {
        if (index < 16) { // 16 colors
            return switch(index) {
                case 0 -> Color.BLACK;
                case 1 -> new Color(128, 0, 0, 255);
                case 2 -> new Color(0, 128, 0, 255);
                case 3 -> new Color(128, 128, 0, 255);
                case 4 -> new Color(0, 0, 128, 255);
                case 5 -> new Color(128, 0, 128, 255);
                case 6 -> new Color(0, 128, 128, 255);
                case 7 -> new Color(192, 192, 192, 255);
                case 8 -> new Color(128, 128, 128, 255);
                case 9 -> Color.RED;
                case 10 -> Color.GREEN;
                case 11 -> Color.YELLOW;
                case 12 -> Color.BLUE;
                case 13 -> Color.MAGENTA;
                case 14 -> Color.CYAN;
                default -> Color.WHITE;
            };
        } else if (index <= 231) { // 216 Color Cube
            int i = index - 16;
            return new Color(
                    (i / 36) * 51,
                    ((i % 36) / 6) * 51,
                    (i % 6) * 51,
                    255
            );
        } else if (index <= 255) { // Greyscale
            int gray = 8 + (index - 232) * 10;
            return new Color(gray, gray, gray, 255);
        } else {
            return Color.WHITE;
        }
    }
}
