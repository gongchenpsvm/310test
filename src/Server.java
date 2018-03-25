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
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader.*;

public class Server {
	
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
		  while (numImagesSaved < 60) {
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
	public BufferedImage buildCollage() {
		//BufferedImage collage = new BufferedImage(1800, 900,BufferedImage.TYPE_INT_RGB);
		BufferedImage collage = new BufferedImage(1800, 900, BufferedImage.TYPE_INT_RGB);
		//Create transformation for the scaled down images
		AffineTransform at = new AffineTransform();
		double locationX0 = 1800 / 2;//find center of an image
		double locationY0 = 900 / 2;
		at.rotate(Math.toRadians (-45 + Math.random()*90), locationX0, locationY0);
		//pause here
		Graphics2D g = collage.createGraphics();
		
		/*project 2*/
//		FontRenderContext frc = g.getFontRenderContext();
//		Font f = new Font("Helvetica", 1, 320);
//		String s = new String("Yosemite");
//		TextLayout textTl = new TextLayout(s, f, frc);
//		AffineTransform transform = new AffineTransform();
//		Shape outline = textTl.getOutline(null);
//		Rectangle rect = outline.getBounds();
//		transform = g.getTransform();
//		transform.translate(1800/2-(rect.width/2), 900/2+(rect.height/2));
//		g.transform(transform);
//		g.setColor(Color.blue);
//		//g.draw(outline);   
//		g.setClip(outline);
//		g.drawImage(this.imagesList.get(0), rect.x, rect.y, rect.width, rect.height, null);


		//Make the first image background of the collage
		//g.drawImage(this.imagesList.get(0), 0, 0, 1800, 900, 0, 0, this.imagesList.get(0).getWidth(), this.imagesList.get(0).getHeight(), null);
		for (int i = 0; i < 60; i++) {
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
			tx.translate(Math.random()*1800, Math.random()* 900);//Move the small images away from the origin
			tx.rotate(Math.toRadians (-45 + Math.random()*90), locationX, locationY);//rotate around the center
			g.drawImage(smallImage, tx, null);//draw with transformation 
		}
		//for project 2
//		int alpha = 0; //
//		Color textColor = new Color(0, 0, 0, alpha);
//		Color bgColor = Color.pink;
//		g.setColor(textColor);
//		g.setFont(new Font("Serif", Font.BOLD, 50));
//		String s = "FUCLA";
//		
//		FontMetrics fm = g.getFontMetrics();
//        Rectangle2D rect = fm.getStringBounds(s, g);
//
//        g.setColor(bgColor);
//        g.fillRect(900,
//                   450 - fm.getAscent(),
//                   (int) rect.getWidth(),
//                   (int) rect.getHeight());
//
//        g.setColor(textColor);
		
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
		if (imagesList.size() == 60) {
			for (int i = 0; i < 60; i++) {
				try {
					ImageIO.write(imagesList.get(i), "jpg",new File("/Users/gongchen/Desktop/310imagesFolder/image" + i + ".jpg"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	public BufferedImage generateTextImage(String s) {
		        int w = 1800;
		        int h = 900;
		        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		        Graphics2D g2d = img.createGraphics();
		        g2d.setPaint(Color.black);
		        g2d.setFont(new Font("Serif", Font.BOLD, 600));
		        FontMetrics fm = g2d.getFontMetrics();
		        int x = img.getWidth() - fm.stringWidth(s);
		        int y = fm.getHeight();
		        g2d.drawString(s, x, y);
		        g2d.dispose();
		        return img;
    }
	//IMPORTANT after each joinBufferedImage() called, getPrevCollageList() should be called immediately to update the previous collages
	public List<BufferedImage> getPrevCollageList() {
		return prevCollageList;
	}
	
	//Grabbed from stackoverflow
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
		try {
			ImageIO.write(textEffect(s0.buildCollage(), s0.generateTextImage("a")), "png",new File("/Users/gongchen/Desktop/310imagesFolder/letterShaped" + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

