/**
 * Copyright (C) 2013 Guestful (info@guestful.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.guestful.asciiart;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public class TextToAsciiArt {

    public static class Font {
        public static enum Style {
            PLAIN,
            BOLD,
            ITALIC
        }
    }

    private static final String lineSeparator = System.getProperty("line.separator");

    private String fontName = "Serif";
    private int fontSize = 12;
    private final Collection<Font.Style> fontStyles = Arrays.asList(Font.Style.BOLD);

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public Collection<Font.Style> getFontStyles() {
        return fontStyles;
    }

    public void setFontStyles(Collection<Font.Style> fontStyles) {
        this.fontStyles.clear();
        this.fontStyles.addAll(fontStyles);
    }

    public void setFontStyle(Font.Style fontStyle) {
        this.fontStyles.clear();
        this.fontStyles.add(fontStyle);
    }

    public void writeImage(String text, File out) throws IOException {
        String format = out.getName().substring(out.getName().lastIndexOf('.') + 1);
        BufferedImage image = getImage(text);
        ImageIO.write(image, format.toLowerCase(), out);
    }

    public void writeImage(String text, String format, OutputStream out) throws IOException {
        BufferedImage image = getImage(text);
        ImageIO.write(image, format, out);
    }

    public String getAsciiArt(String text) throws IOException {
        BufferedImage image = getImage(text);
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < image.getHeight(); y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < image.getWidth(); x++) {
                line.append(image.getRGB(x, y) == -16777216 ? " " : image.getRGB(x, y) == -1 ? "#" : "*");
            }
            if (!line.toString().trim().isEmpty()) {
                sb.append(line).append(lineSeparator);
            }
        }
        return sb.toString();
    }

    @SuppressWarnings({"MagicConstant", "Convert2MethodRef"})
    public BufferedImage getImage(String text) {
        Dimension dimension = getTextWidth(text);
        BufferedImage image = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = getGraphics(image);
        graphics.drawString(text, 0, getFontSize());
        return image;
    }

    public Dimension getTextWidth(String text) {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = getGraphics(image);
        FontMetrics metrics = graphics.getFontMetrics();
        return new Dimension(metrics.stringWidth(text), metrics.getHeight());
    }

    private Graphics2D getGraphics(BufferedImage image) {
        java.awt.Font font = new java.awt.Font(
            getFontName(),
            getFontStyles().stream().collect(Collectors.summingInt(s -> s.ordinal())),
            getFontSize());
        Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.TRACKING, TextAttribute.TRACKING_LOOSE);
        font = font.deriveFont(attributes);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setFont(font);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return graphics;
    }

}
