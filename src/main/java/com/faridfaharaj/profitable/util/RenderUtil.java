package com.faridfaharaj.profitable.util;

import org.bukkit.map.MapCanvas;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 渲染相关的工具方法集合（静态）。
 * 把 MapGraphRenderer 中的静态工具迁移到这里，以便复用而无需创建对象。
 */
public class RenderUtil {

    private static final String ARK_PIXEL_12_PX_MONOSPACED_ZH_CN_TTF = "ark-pixel-12px-monospaced-zh_cn.ttf";
    private static final String FUSION_PIXEL_8_PX_MONOSPACED_ZH_HANS_TTF = "fusion-pixel-8px-monospaced-zh_hans.ttf";

    private static final Font PIXEL_12_PX = getEmbeddedFont(ARK_PIXEL_12_PX_MONOSPACED_ZH_CN_TTF, 12f);
    private static final Font PIXEL_8_PX = getEmbeddedFont(FUSION_PIXEL_8_PX_MONOSPACED_ZH_HANS_TTF, 8f);

    private static Font getEmbeddedFont(String name, float size) {
        try {
            java.io.InputStream is = RenderUtil.class.getResourceAsStream("/" + name);
            if (is == null) {
                throw new Exception("Font resource not found: " + name);
            }
            return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

    // 自动选择字体
    private static Font getFont(int size) {
        switch (size) {
            case 12 -> {
                return PIXEL_12_PX;
            }
            case 8 ->{
                return PIXEL_8_PX;
            }
            default -> {
                return new Font("SansSerif", Font.PLAIN, size);
            }
        }
    }

    public static Image createTextImage(String text) {
        return createTextImage(text, 12);
    }

    public static Image createTextImage(String text, int size) {
        if (text == null) text = "";

        Font font = getFont(size);
        BufferedImage measureImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = measureImg.createGraphics();
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int width = Math.max(1, fm.stringWidth(text));
        int height = Math.max(1, fm.getHeight());
        g2.dispose();

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(font);
        fm = g2.getFontMetrics();

        g2.setColor(Color.DARK_GRAY);
        int x = 0;
        int y = fm.getAscent();

        g2.drawString(text, x, y);
        g2.dispose();

        return img;
    }

    public static void rectangle(MapCanvas canvas, int initX, int initY, int targetX, int targetY, Color color) {
        for (int x = Math.min(initX, targetX); x <= Math.max(initX, targetX); x++) {
            for (int y = Math.min(initY, targetY); y <= Math.max(initY, targetY); y++) {
                canvas.setPixelColor(x, y, color);
            }
        }
    }

    public static void transparentRectangle(MapCanvas canvas, int initX, int initY, int targetX, int targetY, Color color) {
        for (int x = Math.min(initX, targetX); x <= Math.max(initX, targetX); x++) {
            for (int y = Math.min(initY, targetY); y <= Math.max(initY, targetY); y++) {
                canvas.setPixelColor(x, y, blendColors(canvas.getPixelColor(x, y), color));
            }
        }
    }

    public static void shadedRectangle(MapCanvas canvas, int initX, int initY, int targetX, int targetY, Color color) {
        for (int x = Math.min(initX, targetX); x <= Math.max(initX, targetX); x++) {
            for (int y = Math.min(initY, targetY); y <= Math.max(initY, targetY); y++) {
                canvas.setPixelColor(x, y, color);
                canvas.setPixelColor(x + 1, y + 1, color.darker());
            }
        }
    }

    public static void DIERectangle(MapCanvas canvas, int initX, int initY, int targetX, int targetY, Color color) {
        for (int x = Math.min(initX, targetX); x <= Math.max(initX, targetX); x++) {
            for (int y = Math.min(initY, targetY); y <= Math.max(initY, targetY); y++) {
                if (canvas.getPixelColor(x, y) == null) {
                    canvas.setPixelColor(x, y, color);
                }
            }
        }
    }

    public static Color blendColors(Color base, Color overlay) {
        if (base == null) return overlay;

        float alphaOver = overlay.getAlpha() / 255.0f;
        float alphaBase = base.getAlpha() / 255.0f;

        int r = (int) ((overlay.getRed() * alphaOver) + (base.getRed() * (1 - alphaOver)));
        int g = (int) ((overlay.getGreen() * alphaOver) + (base.getGreen() * (1 - alphaOver)));
        int b = (int) ((overlay.getBlue() * alphaOver) + (base.getBlue() * (1 - alphaOver)));

        int alphaOut = (int) ((alphaOver + alphaBase * (1 - alphaOver)) * 255);

        return new Color(r, g, b, alphaOut);
    }

    public static Color blendColors(Color base, Color overlay, float amount) {
        if (base == null) return overlay;

        float alphaOver = amount / 255.0f;
        float alphaBase = base.getAlpha() / 255.0f;

        int r = (int) ((overlay.getRed() * alphaOver) + (base.getRed() * (1 - alphaOver)));
        int g = (int) ((overlay.getGreen() * alphaOver) + (base.getGreen() * (1 - alphaOver)));
        int b = (int) ((overlay.getBlue() * alphaOver) + (base.getBlue() * (1 - alphaOver)));

        int alphaOut = (int) ((alphaOver + alphaBase * (1 - alphaOver)) * 255);

        return new Color(r, g, b, alphaOut);
    }
}

