import java.net.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import com.google.gson.Gson;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader.*;

public class Server {
	public final static int numImages = 80;
	private List<BufferedImage> imagesList;
	private List<BufferedImage> prevCollageList;
	private BufferedImage prevCollage;
	private static Server instance;
	private String topic;
	public void setTopic(String topic) {
		this.topic = topic;
	}
	private Server() {
		imagesList = new LinkedList<>();
		prevCollageList = new LinkedList<>();
		prevCollage = null;//IMPORTANT to initialize it to null	
	}
	public static Server getInstance() {
		if(instance == null) {
	         instance = new Server();
	    }
	    return instance;
	}
	public void search() throws MalformedURLException, URISyntaxException, IOException{
		  this.topic = "yosemite";
		  //Google api credentials and parameters
		  imagesList.clear();
		  String key = "AIzaSyDFyaeFTiOvijzl7-2OTS3rcPeMYb2S0Ts";
		  String qry = this.topic; // search key word
		  String cx  = "012772727063918838439:2cwicvp-wsk";
		  String searchType = "image";
		  int indexResult = 1;
		  int numImagesSaved = 0;
		  //CONDITION used to make sure we grab exactly 30 images
		  while (numImagesSaved < numImages) {
			  //all parameters are put at the end of the url
			  URL url = new URL ("https://www.googleapis.com/customsearch/v1?key=" +key+ "&cx=" +cx+ "&q=" +qry + "&searchType="+searchType+"&start="+indexResult + "&num=1");//);
			  HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			  conn.setRequestMethod("GET");
			  conn.setRequestProperty("Accept", "application/json");
			  BufferedReader br = new BufferedReader(new InputStreamReader ( ( conn.getInputStream() ) ) );
			  String output;
			  while ((output = br.readLine()) != null) {
			        if(output.contains("\"link\": \"")){   
			        		//get ONE link 
			            String link=output.substring(output.indexOf("\"link\": \"")+("\"link\": \"").length(), output.indexOf("\","));
			            //form ONE url from link
			            URL imageURL = new URL(link);
			            try {
			            		//Add to the image list
			            		imagesList.add(ImageIO.read(imageURL));
			            		//if the image just stored is NOT null, increment the counter
			            		if (imagesList.get(imagesList.size() - 1) != null) {
			            			numImagesSaved++;
			            		}
			            		//if the image just stored is null, remove this null image and do NOT increment the counter
			            		else {
			            			imagesList.remove(imagesList.size() - 1);
			            		}
			            } catch (Exception InputMismachException) {
			            		System.out.println("Exception catched");    
			            }
			        }     
			  }
			  //after 
			  indexResult++;
			  conn.disconnect();
		  }
	}

	/*Scale up one image as background. Rest of images are scaled down to display*/
	public BufferedImage buildCollage(int x, int y) {
		//BufferedImage collage = new BufferedImage(1800, 900,BufferedImage.TYPE_INT_RGB);
		BufferedImage collage = new BufferedImage(1800, 900, BufferedImage.TYPE_INT_RGB);
		//Create transformation for the scaled down images
		AffineTransform at = new AffineTransform();
		double locationX0 = 1800 / 2;//find center of an image
		double locationY0 = 900 / 2;
		at.rotate(Math.toRadians (-45 + Math.random()*90), locationX0, locationY0);
		//pause here
		Graphics2D g = collage.createGraphics();


		//Make the first image background of the collage
		//g.drawImage(this.imagesList.get(0), 0, 0, 1800, 900, 0, 0, this.imagesList.get(0).getWidth(), this.imagesList.get(0).getHeight(), null);
		for (int i = 0; i < numImages; i++) {
			//Set up the small image with no image yet
			BufferedImage smallImage = new BufferedImage(241, 125,BufferedImage.TYPE_INT_RGB);
			Graphics2D gToScaleDown = smallImage.createGraphics();
			gToScaleDown.setPaint(new Color ( 255, 255, 255 ) );//make the background white so after an image is on the background the border is white
			gToScaleDown.fillRect ( 0, 0, 241, 125 );
			gToScaleDown.drawImage(this.imagesList.get(i), 3, 3, 238, 122, 0, 0,
					this.imagesList.get(i).getWidth(), this.imagesList.get(i).getHeight(), null);//put an image on background
			gToScaleDown.dispose();
			//Create transformation for the scaled down images
			AffineTransform tx = new AffineTransform();
			double locationX = smallImage.getWidth() / 2;//find center of an image
			double locationY = smallImage.getHeight() / 2;
			//IMPORTANT translate must be before rotate 
			tx.translate(x + Math.random()*1700, Math.random()* 900);//Move the small images away from the origin
			tx.rotate(Math.toRadians (-45 + Math.random()*90), locationX, locationY);//rotate around the center
			g.drawImage(smallImage, tx, null);//draw with transformation 
		}
	
        //g.drawImage(img, r.x, r.y, r.width, r.height, this);
		g.dispose();//Release all resources used by g
		//for local test
		try {
			ImageIO.write(collage, "png",new File("/Users/gongchen/Desktop/310imagesFolder/collage" + ".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//To form previous collage list
	    if (prevCollage != null) {//if prevCollage is null, it's the first search that does not have previous collages
	    		prevCollageList.add(prevCollage);
	    }
	    prevCollage = collage;
		return collage;
	}
	//For local test
	private void outputImages() {
		if (imagesList.size() == numImages) {
			for (int i = 0; i < numImages; i++) {
				try {
					ImageIO.write(imagesList.get(i), "jpg",new File("/Users/gongchen/Desktop/310imagesFolder/image" + i + ".jpg"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	public TextOutputClass generateTextImage(String s) {
		    int w = 1800;
		    int h = 900;
			BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			           
	        Graphics2D g2d = img.createGraphics();
	        g2d.setPaint(Color.black);
	        int size = s.length();
	        if(size >= 3) {
	          g2d.setFont(new Font("Serif", Font.BOLD, h*3/size));
	        }
	        else {
	          g2d.setFont(new Font("Serif", Font.BOLD, h+100));
	        }
	        FontMetrics fm = g2d.getFontMetrics();
	        int x = img.getWidth()/2 - fm.stringWidth(s)/2;
	        int y = img.getHeight()/2 + fm.getAscent()/2 - fm.getDescent()/2;// + fm.getHeight()/4;
	        
	        System.out.println("x: " + x);
	        System.out.println("y: " + y);
	
	        g2d.drawString(s, x, y);
	        g2d.dispose();
	        return new TextOutputClass(img, x, y);
    }
	//IMPORTANT after each joinBufferedImage() called, getPrevCollageList() should be called immediately to update the previous collages
	public List<BufferedImage> getPrevCollageList() {
		return prevCollageList;
	}
	
	//Grabbed from stackoverflow "Cut out image in shape of text"
	public static BufferedImage textEffect(BufferedImage image, BufferedImage text) {
	    if (image.getWidth() != text.getWidth() ||
	        image.getHeight() != text.getHeight())
	    {
	        throw new IllegalArgumentException("Dimensions are not the same!");
	    }
	    BufferedImage img = new BufferedImage(image.getWidth(),
	                                          image.getHeight(),
	                                          BufferedImage.TYPE_INT_ARGB_PRE);

	    for (int y = 0; y < image.getHeight(); ++y) {
	        for (int x = 0; x < image.getWidth(); ++x) {
	           int textPixel = text.getRGB(x, y);
	           int textAlpha = (textPixel & 0xFF000000);
	           int sourceRGB = image.getRGB(x, y);
	           int newAlpha = (int) (((textAlpha >> 24) * (sourceRGB >> 24)) / 255d);
	           int imgPixel = (newAlpha << 24) |  (sourceRGB & 0x00FFFFFF);
	           int rgb = imgPixel | textAlpha;
	           img.setRGB(x, y, rgb);

	        }
	    }
	    return img;
	}
	private BufferedImage grayScale(BufferedImage img) {
		//BufferedImage img = new BufferedImage(imgInput);
		//get image width and height
	    int width = img.getWidth();
	    int height = img.getHeight();

	    //convert to grayscale
	    for(int y = 0; y < height; y++){
	      for(int x = 0; x < width; x++){
	        int p = img.getRGB(x,y);

	        int a = (p>>24)&0xff;
	        int r = (p>>16)&0xff;
	        int g = (p>>8)&0xff;
	        int b = p&0xff;
            
	        //calculate average
	        int avg = (r+g+b)/3;

	        //replace RGB value with avg
	        p = (a<<24) | (avg<<16) | (avg<<8) | avg;

	        img.setRGB(x, y, p);
	      }
	    }
//		BufferedImage gray = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
//
//        // Automatic converstion....
//        ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
//        op.filter(img, gray);
	    try {
			ImageIO.write(img, "png",new File("/Users/gongchen/Desktop/310imagesFolder/grayscaled" + ".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return img;
	}
	
	private BufferedImage sepia(BufferedImage img) {
		//get width and height of the image
        int width = img.getWidth();
        int height = img.getHeight();
        
        //convert to sepia
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int p = img.getRGB(x,y);
                
                int a = (p>>24)&0xff;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;
                
                //calculate tr, tg, tb
                int tr = (int)(0.393*r + 0.769*g + 0.189*b);
                int tg = (int)(0.349*r + 0.686*g + 0.168*b);
                int tb = (int)(0.272*r + 0.534*g + 0.131*b);
                
                //check condition
                if(tr > 255){
                    r = 255;
                }else{
                    r = tr;
                }
                
                if(tg > 255){
                    g = 255;
                }else{
                    g = tg;
                }
                
                if(tb > 255){
                    b = 255;
                }else{
                    b = tb;
                }
                
                //set new RGB value
                p = (a<<24) | (r<<16) | (g<<8) | b;
                
                img.setRGB(x, y, p);
            }
        }
	    try {
			ImageIO.write(img, "png",new File("/Users/gongchen/Desktop/310imagesFolder/sepia" + ".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return img;
	}
	private BufferedImage bw(BufferedImage img) {
		BufferedImage bw = new BufferedImage(img.getWidth(), img.getHeight(),
				BufferedImage.TYPE_BYTE_BINARY);
		Graphics2D g = bw.createGraphics();
        g.drawImage(img, 0, 0, null);
        try {
			ImageIO.write(bw, "png",new File("/Users/gongchen/Desktop/310imagesFolder/bw" + ".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return img;
		
	}
	public static void main(String[] args) {
		Server s0 = new Server();
		try {
			//Grab 30 images
			s0.search();//Parameter is a place holder.
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
		s0.outputImages();
		//s0.buildCollage();
		//output text shape image
//		try {
//			ImageIO.write(s0.generateTextImage("fucla"), "png",new File("/Users/gongchen/Desktop/310imagesFolder/textShape" + ".png"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		//output the letter shape cut image
		TextOutputClass toc = s0.generateTextImage("FUCLA");
		BufferedImage letterShapedCollage = textEffect(s0.buildCollage(toc.getX(), toc.getY()), toc.getBiOutput());
		try {
			ImageIO.write(letterShapedCollage, "png",new File("/Users/gongchen/Desktop/310imagesFolder/letterShaped" + ".png"));
			s0.grayScale(letterShapedCollage);
			s0.sepia(textEffect(s0.buildCollage(toc.getX(), toc.getY()), toc.getBiOutput()));
			s0.bw(textEffect(s0.buildCollage(toc.getX(), toc.getY()), toc.getBiOutput()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//inner class for the return type of generateTextImage
	 class TextOutputClass {
		private BufferedImage biOutput;
		private int x;
		private int y;
		public TextOutputClass(BufferedImage biOutput, int x, int y) {
			super();
			this.biOutput = biOutput;
			this.x = x;
			this.y = y;
		}
		public BufferedImage getBiOutput() {
			return biOutput;
		}
		public int getX() {
			return x;
		}
		public int getY() {
			return y;
		}
		
	}
}

