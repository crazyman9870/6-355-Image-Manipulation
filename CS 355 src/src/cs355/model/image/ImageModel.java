package cs355.model.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.util.Arrays;

public class ImageModel extends CS355Image {
	
	private BufferedImage buffer = null;
	private int[][] updatedPixels = null;

	public ImageModel()	{
		super();
	}
	
	private void setupUpdatedPixels() {
		if(updatedPixels == null)
			updatedPixels = new int[super.getWidth() * super.getHeight()][3];
	}
	
	private void updatePixels() {
		int height = super.getHeight();
		int width = super.getWidth();
		
		for (int y = 0; y < height; ++y)
			for (int x = 0; x < width; ++x)
				setPixel(x, y, updatedPixels[width * y + x]); // Set the pixel.
	}

	@Override
	public BufferedImage getImage() {
		if(buffer != null)
			return buffer;
		
		int w = super.getWidth();
		int h = super.getHeight();
		buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		WritableRaster wr = buffer.getRaster();
        
		int[] rgb = new int[3];
		
		for (int y = 0; y < h; ++y)
			for (int x = 0; x < w; ++x)
				wr.setPixel(x, y, super.getPixel(x, y, rgb));
		
		buffer.setData(wr);
		
        return buffer;
	}

	@Override
	public void edgeDetection() {
		
		setupUpdatedPixels();
		
		int[] rgb = new int[3];
		float[] hsb = new float[3];
		
		int[] xPos = new int[9];
		int[] yPos = new int[9];
		
		int[] sX = {-1, 0, 1, -2, 0, 2, -1, 0, 1};
		int[] sY = {-1, -2, -1, 0, 0, 0, 1, 2, 1};
		
		int height = super.getHeight();
		int width = super.getWidth();
		
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x)	{
				
				int prevx = x > 0 ? x-1 : 0; //previous x, or 0
				int prevy = y > 0 ? y-1 : 0; //previous y, or 0
				int nextx = x < width-1 ? x+1 : x; //next x, or x if edge
				int nexty = y < height-1 ? y+1 : y; //next y, or y if edge
				
				xPos[0] = prevx; 	yPos[0] = prevy;
				xPos[1] = x; 		yPos[1] = prevy;
				xPos[2] = nextx; 	yPos[2] = prevy;
				xPos[3] = prevx; 	yPos[3] = y;
				xPos[4] = x; 		yPos[4] = y;
				xPos[5] = nextx; 	yPos[5] = y;
				xPos[6] = prevx; 	yPos[6] = nexty;
				xPos[7] = x; 		yPos[7] = nexty;
				xPos[8] = nextx; 	yPos[8] = nexty;
				
				double xTotal = 0;
				double yTotal = 0;
				
				for(int i = 0; i < 9; i++) {
					rgb = super.getPixel(xPos[i], yPos[i], rgb);
					hsb = Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsb);
					
					xTotal += (sX[i]*hsb[2]);
					yTotal += (sY[i]*hsb[2]);
				}
				
				xTotal /= 8;
				yTotal /= 8;
				
				double magnitude = Math.sqrt(xTotal*xTotal + yTotal*yTotal);
				int colorValue = Math.min((int) ( magnitude * 255) + 128, 255);
				
				updatedPixels[width * y + x][0] = colorValue;
				updatedPixels[width * y + x][1] = colorValue;
				updatedPixels[width * y + x][2] = colorValue;
			}
		}
		
		updatePixels();
		
		buffer = null; //reset buffered image
	}

	@Override
	public void sharpen() {
		
		setupUpdatedPixels();
		
		int[] rgb = new int[3];
		
		int height = super.getHeight();
		int width = super.getWidth();
		
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x)	{
				
				int prevx = x > 0 ? x-1 : 0; //previous x, or 0
				int prevy = y > 0 ? y-1 : 0; //previous y, or 0
				int nextx = x < width-1 ? x+1 : x; //next x, or x if edge
				int nexty = y < height-1 ? y+1 : y; //next y, or y if edge
				
				rgb[0] =	(-super.getRed(x, prevy)+
							-super.getRed(prevx, y)+
							(6*super.getRed(x, y))+
							-super.getRed(nextx, y)+
							-super.getRed(x, nexty))/2;
				
				rgb[1] =	(-super.getGreen(x, prevy)+
							-super.getGreen(prevx, y)+
							(6*super.getGreen(x, y))+
							-super.getGreen(nextx, y)+
							-super.getGreen(x, nexty))/2;
				
				rgb[2] =	(-super.getBlue(x, prevy)+
							-super.getBlue(prevx, y)+
							(6*super.getBlue(x, y))+
							-super.getBlue(nextx, y)+
							-super.getBlue(x, nexty))/2; 
				
				//ensure values are in bounds (0 <= x <= 255
				updatedPixels[width * y + x][0] = Math.max(Math.min(rgb[0], 255),0);
				updatedPixels[width * y + x][1] = Math.max(Math.min(rgb[1], 255),0);
				updatedPixels[width * y + x][2] = Math.max(Math.min(rgb[2], 255),0);
			}
		}
		
		updatePixels();
		
		buffer = null; //reset buffered image
	}

	@Override
	public void medianBlur() {
		
		setupUpdatedPixels();
		
		int height = super.getHeight();
		int width = super.getWidth();
		
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x)	{
				
				int prevx = x > 0 ? x-1 : 0; //previous x, or 0
				int prevy = y > 0 ? y-1 : 0; //previous y, or 0
				int nextx = x < width-1 ? x+1 : x; //next x, or x if edge
				int nexty = y < height-1 ? y+1 : y; //next y, or y if edge
				
				int[] red =	{super.getRed(prevx, prevy),
							super.getRed(x, prevy),
							super.getRed(nextx, prevy),
							super.getRed(prevx, y),
							super.getRed(x, y),
							super.getRed(nextx, y),
							super.getRed(prevx, nexty),
							super.getRed(x, nexty),
							super.getRed(nextx, nexty)};
				
				int[] green={super.getBlue(prevx, prevy),
							super.getBlue(x, prevy),
							super.getBlue(nextx, prevy),
							super.getBlue(prevx, y),
							super.getBlue(x, y),
							super.getBlue(nextx, y),
							super.getBlue(prevx, nexty),
							super.getBlue(x, nexty),
							super.getBlue(nextx, nexty)};
				
				int[] blue ={super.getGreen(prevx, prevy),
							super.getGreen(x, prevy),
							super.getGreen(nextx, prevy),
							super.getGreen(prevx, y),
							super.getGreen(x, y),
							super.getGreen(nextx, y),
							super.getGreen(prevx, nexty),
							super.getGreen(x, nexty),
							super.getGreen(nextx, nexty)};
				
				//order colors
				Arrays.sort(red);
				Arrays.sort(green);
				Arrays.sort(blue);
				
				//grab median color
				updatedPixels[width * y + x][0] = red[4];
				updatedPixels[width * y + x][1] = green[4];
				updatedPixels[width * y + x][2] = blue[4];
			}
		}
		
		updatePixels();
		
		buffer = null; //reset buffered image
	}

	@Override
	public void uniformBlur() {

		setupUpdatedPixels();
		
		int height = super.getHeight();
		int width = super.getWidth();
		
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x)	{
				
				int prevx = x > 0 ? x-1 : 0; //previous x, or 0
				int prevy = y > 0 ? y-1 : 0; //previous y, or 0
				int nextx = x < width-1 ? x+1 : x; //next x, or x if edge
				int nexty = y < height-1 ? y+1 : y; //next y, or y if edge
				
				//average red
				updatedPixels[width * y + x][0] =
							(super.getRed(prevx, prevy)+
							super.getRed(x, prevy)+
							super.getRed(nextx, prevy)+
							super.getRed(prevx, y)+
							super.getRed(x, y)+
							super.getRed(nextx, y)+
							super.getRed(prevx, nexty)+
							super.getRed(x, nexty)+
							super.getRed(nextx, nexty))/9;
				
				//average green
				updatedPixels[width * y + x][1] =
							(super.getBlue(prevx, prevy)+
							super.getBlue(x, prevy)+
							super.getBlue(nextx, prevy)+
							super.getBlue(prevx, y)+
							super.getBlue(x, y)+
							super.getBlue(nextx, y)+
							super.getBlue(prevx, nexty)+
							super.getBlue(x, nexty)+
							super.getBlue(nextx, nexty))/9;
				
				//average blue
				updatedPixels[width * y + x][2] =
							(super.getGreen(prevx, prevy)+
							super.getGreen(x, prevy)+
							super.getGreen(nextx, prevy)+
							super.getGreen(prevx, y)+
							super.getGreen(x, y)+
							super.getGreen(nextx, y)+
							super.getGreen(prevx, nexty)+
							super.getGreen(x, nexty)+
							super.getGreen(nextx, nexty))/9;
			}
		}
		
		updatePixels();
		
		buffer = null; //reset buffered image
	}

	@Override
	public void grayscale() {
		
		int[] rgb = new int[3];
		float[] hsb = new float[3];
		
		for (int y = 0; y < super.getHeight(); ++y) {
			for (int x = 0; x < super.getWidth(); ++x) {
				
				rgb = super.getPixel(x, y, rgb);
				
				hsb = Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsb);
				
				hsb[1] = 0; //set saturation to 0
				
				Color c = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
				rgb[0] = c.getRed();
				rgb[1] = c.getGreen();
				rgb[2] = c.getBlue();

				setPixel(x, y, rgb); // Set the pixel.
			}
		}
		
		buffer = null; //reset buffered image
	}

	@Override
	public void contrast(int amount) {

		float scalar = (float) Math.pow(((amount+100.0f)/100.0f), 4.0f);
        
		int[] rgb = new int[3];
		float[] hsb = new float[3];
		
		for (int y = 0; y < super.getHeight(); ++y) {
			for (int x = 0; x < super.getWidth(); ++x) {
				
				rgb = super.getPixel(x, y, rgb);
				
				hsb = Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsb);
				
				hsb[2] = scalar*(hsb[2]-0.5f)+0.5f; //adjust brightness
				
				hsb[2] = Math.max(Math.min(hsb[2], 1.0f), 0.0f); //keep in range
				
				Color c = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
				
				rgb[0] = c.getRed();
				rgb[1] = c.getGreen();
				rgb[2] = c.getBlue();

				setPixel(x, y, rgb); // Set the pixel
			}
		}
		
		buffer = null; //reset buffered image
	}

	@Override
	public void brightness(int amount) {
		
		float adjustedAmount = amount/100.0f;
        
		int[] rgb = new int[3];
		float[] hsb = new float[3];
		
		for (int y = 0; y < super.getHeight(); ++y) {
			for (int x = 0; x < super.getWidth(); ++x) {
				
				rgb = super.getPixel(x, y, rgb);
				
				hsb = Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsb);
				
				hsb[2] += adjustedAmount; //adjust brightness
				
				hsb[2] = Math.max(Math.min(hsb[2], 1.0f), 0.0f); //keep in range
				
				Color c = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
				
				rgb[0] = c.getRed();
				rgb[1] = c.getGreen();
				rgb[2] = c.getBlue();

				setPixel(x, y, rgb); // Set the pixel
			}
		}
		
		buffer = null; //reset buffered image
	}

}
