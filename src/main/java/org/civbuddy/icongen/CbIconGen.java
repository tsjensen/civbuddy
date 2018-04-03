/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2010 Thomas Jensen
 * $Id$
 * Date created: 2010-12-25
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.civbuddy.icongen;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.civbuddy.client.model.CbGroup;
import org.civbuddy.client.model.CbState;


/**
 * Creates PNG icons used in the app.
 * The resulting files are processed by GWT and combined into an image bundle.
 * 
 * @author tsjensen
 */
public final class CbIconGen
{
    /** one hundred, as in 100 percent */
    private static final int ONEHUNDRED = 100;



    private CbIconGen()
    {
        super();
    }



    @SuppressWarnings("unused")
    private static RenderedImage createBarImage(final int pStepPercent,
        final int pBarHeightPx, final int pPixelsPerPercent)
    {
        if (pStepPercent < 1 || pStepPercent > ONEHUNDRED || (ONEHUNDRED % pStepPercent > 0)) {
            throw new IllegalArgumentException(
                "Parameter pStep must be a divisor of " + ONEHUNDRED); //$NON-NLS-1$
        }
        final int pad = 1;   // 1 pixel padding between sprites
        final int numBars = ONEHUNDRED / pStepPercent;  // number of bars per color
        final Color[] colors = new Color[]{Color.BLACK, Color.GREEN, Color.LIGHT_GRAY};
        final int imgWidth = ((numBars - 1) * pad)
            + (((numBars * (numBars + 1)) / 2) * pStepPercent * pPixelsPerPercent);
        final int imgHeight = (colors.length - 1) * pad + (colors.length * pBarHeightPx);

        BufferedImage bufferedImage = new BufferedImage(imgWidth, imgHeight,
            BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setBackground(Color.WHITE);
        g2d.clearRect(0, 0, imgWidth, imgHeight);
        g2d.setBackground(Color.BLACK);

        int y = 0;
        for (Color color : colors) {
            g2d.setColor(color);
            for (int i = 0; i < numBars; i++) {
                int x = i * pad + ((i * (i + 1)) / 2) * pStepPercent * pPixelsPerPercent;
                int barWidthPx = (i + 1) * pStepPercent * pPixelsPerPercent;
                Paint gp = new GradientPaint(x, y, Color.WHITE,
                    x, y + pBarHeightPx - 1, color);
                g2d.setPaint(gp);
                g2d.fill(new Rectangle(x + 1, y + 1, barWidthPx - 2, pBarHeightPx - 2));
                g2d.setPaint(color);
                g2d.drawRect(x, y, barWidthPx - 1, pBarHeightPx - 1);
            }
            y += pad + pBarHeightPx;
        }

        g2d.dispose();
        return bufferedImage;
    }



    private static RenderedImage createGroupIcon(final CbGroup pGroup,
        final int pGroupIconSizePx)
    {
        BufferedImage bufferedImage = new BufferedImage(pGroupIconSizePx, pGroupIconSizePx,
            BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color transp = Color.BLACK;
        Color color = null;
        g2d.setBackground(transp);
        g2d.clearRect(0, 0, pGroupIconSizePx, pGroupIconSizePx);

        switch (pGroup) {
            case Arts:
                final String colorArts = "0x1067ea"; //$NON-NLS-1$
                color = Color.decode(colorArts);
                g2d.setColor(color);
                g2d.fillPolygon(new int[]{pGroupIconSizePx / 2, 2, pGroupIconSizePx - 1},
                    new int[]{2, pGroupIconSizePx - 2, pGroupIconSizePx - 2}, 3);
                break;
            case Crafts:
                final String colorCrafts = "0xF8A700"; //$NON-NLS-1$
                color = Color.decode(colorCrafts);
                g2d.setColor(color);
                g2d.fillRect(2, 2, pGroupIconSizePx - 4, pGroupIconSizePx - 4);
                break;
            case Sciences:
                final String colorSciences = "0x24C824"; //$NON-NLS-1$
                color = Color.decode(colorSciences);
                g2d.setColor(color);
                g2d.fillOval(2, 2, pGroupIconSizePx - 4, pGroupIconSizePx - 4);
                break;
            case Civics:
                final String colorCivics = "0xDD38AA"; //$NON-NLS-1$
                color = Color.decode(colorCivics);
                g2d.setColor(color);
                g2d.fillPolygon(hexagon(pGroupIconSizePx));
                break;
            case Religion:
                final String colorReligion = "0xF4F400"; //$NON-NLS-1$
                color = Color.decode(colorReligion);
                g2d.setColor(color);
                g2d.fillPolygon(pentagram(pGroupIconSizePx));
                fillCenter(bufferedImage, pGroupIconSizePx, transp, color);
                break;
            default:
                throw new IllegalArgumentException("unknown group"); //$NON-NLS-1$
        }
        
        g2d.dispose();
        bufferedImage = makeColorTransparent(bufferedImage, color, transp);
        return bufferedImage;
    }



    private static Color copperBarRow(final Color pColor1, final Color pColor2,
        final int pRowNum, final int pRowCount)
    {
        int r1 = pColor1.getRed();
        int g1 = pColor1.getGreen();
        int b1 = pColor1.getBlue();
        int r2 = pColor2.getRed();
        int g2 = pColor2.getGreen();
        int b2 = pColor2.getBlue();

        // sine curve for better roundedness
        final float halfCircleDegs = 180.0f;
        float f = (float) Math.sin(Math.toRadians(halfCircleDegs * pRowNum / (pRowCount - 1)));
        System.out.println("f = " //$NON-NLS-1$
            + String.format("%5.3f", Float.valueOf(f))); //$NON-NLS-1$
        Color result = new Color(
            Math.max(0, r1 + Math.round(f * (r2 - r1))),
            Math.max(0, g1 + Math.round(f * (g2 - g1))),
            Math.max(0, b1 + Math.round(f * (b2 - b1))));
        return result;
    }



    private static double brightness(final int pRgb)
    {
        final int r = (pRgb & 0x00FF0000) >> 16;
        final int g = (pRgb & 0x0000FF00) >> 8;
        final int b = (pRgb & 0x000000FF);
        return Math.sqrt(
              r * r * 0.241f
            + g * g * 0.691f
            + b * b * 0.068f);
    }



    private static BufferedImage makeColorTransparent(final BufferedImage pImg,
        final Color pColor, final Color pTransp)
    {
        BufferedImage result = new BufferedImage(pImg.getWidth(), pImg.getHeight(),
            BufferedImage.TYPE_INT_ARGB);  
        Graphics2D g2d = result.createGraphics();  
        g2d.setComposite(AlphaComposite.Src);  
        g2d.drawImage(pImg, null, 0, 0);  
        
        final double bMax = brightness(pColor.getRGB());
        for (int y = 0; y < result.getHeight(); y++) {  
            for (int x = 0; x < result.getWidth(); x++) {
                int rgb = result.getRGB(x, y);
                if (rgb == pTransp.getRGB()) {  
                    result.setRGB(x, y, 0x8F1C1C);  
                } else if (rgb != pColor.getRGB()) {
                    double b = brightness(rgb);
                    int alpha = (int) Math.round(b / bMax * 255d);
                    Color c = new Color(pColor.getRed(), pColor.getGreen(),
                        pColor.getBlue(), alpha);
                    g2d.setColor(c);
                    g2d.fillRect(x, y, 1, 1);
                }
            }  
        }  
        g2d.dispose();  
        return result; 
    }



    private static void fillCenter(final BufferedImage pImg, final int pGroupIconSizePx,
        final Color pTransp, final Color pFillColor)
    {
        final int x = pGroupIconSizePx / 2;
        final int startY = pGroupIconSizePx / 2;
        int y = startY;

        while (pImg.getRGB(x, y) == pTransp.getRGB() || pImg.getRGB(x - 1, y) == pTransp.getRGB()) {
            fillCenterLine(y, pImg, pGroupIconSizePx, pTransp, pFillColor);
            y++;
        }
        y = startY - 1;
        while (pImg.getRGB(x, y) == pTransp.getRGB() || pImg.getRGB(x - 1, y) == pTransp.getRGB()) {
            fillCenterLine(y, pImg, pGroupIconSizePx, pTransp, pFillColor);
            y--;
        }
    }

    private static void fillCenterLine(final int pY, final BufferedImage pImg,
        final int pGroupIconSizePx, final Color pTransp, final Color pFillColor)
    {
        final int startX = pGroupIconSizePx / 2;
        int x = startX;
        while (pImg.getRGB(x, pY) == pTransp.getRGB()) {
            pImg.setRGB(x, pY, pFillColor.getRGB());
            x++;
        }
        x = startX - 1;
        while (pImg.getRGB(x, pY) == pTransp.getRGB()) {
            pImg.setRGB(x, pY, pFillColor.getRGB());
            x--;
        }
    }


    private static Polygon hexagon(final int pGroupIconSizePx)
    {
        final int s = pGroupIconSizePx / 2 - 1;
        final int h = s / 2;
        @SuppressWarnings("cast")
        final int v = (int) Math.round(Math.sin(Math.toRadians(60)) * ((float) s));

        Polygon result = new Polygon();
        final int vMiddle = pGroupIconSizePx / 2;
        result.addPoint(1, vMiddle);                    // middle left
        result.addPoint(2 + h, vMiddle - v);            // top left
        result.addPoint(1 + h + s, vMiddle - v);        // top right
        result.addPoint(pGroupIconSizePx - 1, vMiddle); // middle right
        result.addPoint(1 + h + s, vMiddle + v);        // bottom right
        result.addPoint(2 + h, vMiddle + v);            // bottom left
        return result;
    }



    private static Polygon pentagram(final int pGroupIconSizePx) 
    {
        final int numPoints = 5;  // 5 points to a pentagram
        final int x0 = pGroupIconSizePx / 2;
        final int y0 = pGroupIconSizePx / 2 + 1;
        final double angle = 2 * Math.PI / numPoints;
        final int r = pGroupIconSizePx / 2;

        int[] x = new int[numPoints];
        int[] y = new int[numPoints];
        for (int i = 0; i < numPoints; i++) {
            double v = i * angle - Math.toRadians(90);
            x[i] = x0 + (int) Math.round(r * Math.cos(v));
            y[i] = y0 + (int) Math.round(r * Math.sin(v));
            if (y[i] == 1) {
                y[i] = 0;
            } else if (Math.abs(y[i] - x0) < 3) {
                y[i] -= 1;
            }
        }

        Polygon result = new Polygon();
        final int[] order = new int[]{0, 2, 4, 1, 3};
        for (int idx : order) {
            result.addPoint(x[idx], y[idx]);
        }
        return result;
    }



    private static RenderedImage createBarIcon(final int pBarHeightPx,
        final Color pDark, final Color pBright) throws IllegalAccessException
    {
        final int imgWidthPx = 300;

        BufferedImage bufferedImage = new BufferedImage(imgWidthPx, pBarHeightPx,
            BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        for (int r = 0; r < pBarHeightPx; r++) {
            g2d.setColor(copperBarRow(pDark, pBright, r, pBarHeightPx));
            g2d.fillRect(0, r, imgWidthPx, 1);
        }
        g2d.dispose();
        return bufferedImage;
    }



    private static void saveAsPng(final RenderedImage pImage, final String pFileName)
        throws IOException
    {
        File file = new File(pFileName);
        ImageIO.write(pImage, "png", file);  //$NON-NLS-1$
    }



    /**
     * main.
     * @param pArgs unused
     * @throws Exception error
     */
    public static void main(final String[] pArgs) throws Exception
    {
        // Bars for the credit indicator
        final int barHeightPx = 11;
        // final int steppingPercent = 4;
        // final int xPxPerPercent = 2;
        //// Generate all bars of all lengths
        // RenderedImage rendImage = createBarImage(steppingPercent, barHeightPx, xPxPerPercent);
        // saveAsPng(rendImage, "war/static/bars_" + steppingPercent + "_"
        //     + barHeightPx + "_" + xPxPerPercent + ".png");

        // Generate long, single bars
        RenderedImage rendImage = createBarIcon(barHeightPx,
            Color.BLACK, Color.GRAY);
        saveAsPng(rendImage, "src/com/tj/civ/client/bar_"  //$NON-NLS-1$
            + CbState.Owned + ".png");  //$NON-NLS-1$
        rendImage = createBarIcon(barHeightPx,
            new Color(73, 102, 145), Color.decode("0xb0bccd")); //$NON-NLS-1$
        saveAsPng(rendImage, "src/com/tj/civ/client/bar_"  //$NON-NLS-1$
            + CbState.Planned + ".png");  //$NON-NLS-1$
        rendImage = createBarIcon(barHeightPx,
            Color.LIGHT_GRAY, Color.decode("0xe0e0e0")); //$NON-NLS-1$
        saveAsPng(rendImage, "src/com/tj/civ/client/bar_"  //$NON-NLS-1$
            + CbState.Absent + ".png");  //$NON-NLS-1$

        // Card group icons
        final int groupIconSizePx = 16;  // should be an even value
        for (CbGroup group : CbGroup.values()) {
            if (group != CbGroup.Religion) {
                System.out.println("\nCreating icon for group '" //$NON-NLS-1$
                    + group + "' ..."); //$NON-NLS-1$
                RenderedImage rImage = createGroupIcon(group, groupIconSizePx);
                saveAsPng(rImage, "src/com/tj/civ/client/grp_" //$NON-NLS-1$
                    + group + ".png"); //$NON-NLS-1$
            }
        }
    }
}