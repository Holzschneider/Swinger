package de.dualuse.commons.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.xml.bind.DatatypeConverter;


public class ImagePatchBorder implements Border {

    private Image borderImage;

    private Insets borderInsets;
    private Insets patchInsets;
    
    private boolean fill;

    public ImagePatchBorder(Image img, Insets borderInsets) {
        this(img, borderInsets, borderInsets, true);
    }
    
    public ImagePatchBorder(Image img, Insets borderInsets, boolean fill) {
        this(img, borderInsets, borderInsets, fill);
    }

    public ImagePatchBorder(Image img, Insets imageInsets, Insets borderInsets) {
        this(img, imageInsets, borderInsets, true);
    }


    public ImagePatchBorder(Image img, Insets imageInsets, Insets borderInsets, boolean fill) {
        this.borderImage = img;
        this.patchInsets = imageInsets;
        this.borderInsets = borderInsets;
        this.fill = fill;
    }

    public boolean isBorderOpaque() {
        return false;
    }

    public Insets getBorderInsets(Component c) {
        return (Insets) borderInsets.clone();
    }
    
    
    public void paintBorder(Component c, Graphics gr, int x, int y, int width, int height) {
        if (borderImage == null) 
            return;

        Graphics2D g = (Graphics2D) gr.create();

        // Set some variables for easy access of insets and image size
        int top = patchInsets.top;
        int left = patchInsets.left;
        int bottom = patchInsets.bottom;
        int right = patchInsets.right;
        int borderWidth = borderImage.getWidth(null);
        int borderHeight = borderImage.getHeight(null);


        if (width < left + right) {
            left = Math.min(left, width / 2); //Math.max(0, left + (width - left - right) / 2);
            right = width - left;
        }
        if (height < top + bottom) {
            top = Math.min(top, height / 2); //Math.max(0, top + (height - top - bottom) / 2);
            bottom = height - top;
        }

        if (top > 0 && left > 0) 
            g.drawImage( borderImage,
                    x, y, x + left, y + top,
                    0, 0, left, top,
                    c);
        
        if (top > 0 && right > 0) 
            g.drawImage(
                    borderImage,
                    x + width - right, y, x + width, y + top,
                    borderWidth - right, 0, borderWidth, top,
                    c);
        
        if (bottom > 0 && left > 0) 
            g.drawImage(
                    borderImage,
                    x, y + height - bottom, x + left, y + height,
                    0, borderHeight - bottom, left, borderHeight,
                    c);
        
        if (bottom > 0 && right > 0) 
            g.drawImage(
                    borderImage,
                    x + width - right, y + height - bottom, x + width, y + height,
                    borderWidth - right, borderHeight - bottom, borderWidth, borderHeight,
                    c);
        
        if (top > 0 && left + right < width && borderWidth > right + left)
            	g.drawImage( borderImage,
            			x+left, y, width-left-right +x+left, top+y,
            			left, 0, left+borderWidth - right - left, 0+top,
            			null);
        
        if (bottom > 0 && left + right < width && borderHeight > bottom && borderWidth > right + left) 
            	g.drawImage(borderImage,
            			x + left, y + height - bottom, x + left+width - left - right, y + height - bottom+bottom,
            			left, borderHeight - bottom, left+borderWidth - right - left, borderHeight - bottom+bottom,
            			c);
        
        if (left > 0 && top + bottom < height && borderHeight > top + bottom)
                g.drawImage(borderImage,
                		x, y + top, x+ left, y+top+height - top - bottom,
                		0, top, 0+left, top+borderHeight - top - bottom,
                		c);
        
        if (right > 0 && top + bottom < height && borderWidth > right + right && borderHeight > top + bottom) 
            	g.drawImage(borderImage, 
            			x + width - right, y + top, x + width - right+right, y + top+height - top - bottom,
            			borderWidth - right, top, borderWidth-right+right, top+borderHeight - top - bottom,
            			c);

		if (fill)
			if (left + right < width && top + bottom < height 
					&& borderWidth - right - left > 0 && borderHeight - top - bottom > 0)
				g.drawImage(borderImage, 
						x + left, y + top, x + left + width - right - left, y + top + height - top - bottom, 
						left, top, left + borderWidth - right - left,
						top + borderHeight - top - bottom, c);                    			

        g.dispose();
    }

    
  
}


