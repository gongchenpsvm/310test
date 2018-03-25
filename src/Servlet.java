

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Servlet
 */
@WebServlet("/Servlet")
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private List<BufferedImage> imagesList;   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Servlet() {
        super();
        imagesList = new LinkedList<>();
        // TODO Auto-generated constructor stub
        
        try {
			search();
		} catch (URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		outputImages();
		BufferedImage concatedImg = concatImages(imagesList);
		try {
			ImageIO.write(concatedImg, "jpg",new File("/Users/gongchen/Desktop/310imagesFolder/imageCONCATED" + ".jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	private void search() throws MalformedURLException, URISyntaxException, IOException{
		  String key = "AIzaSyDFyaeFTiOvijzl7-2OTS3rcPeMYb2S0Ts";
		  String qry = "usc"; // search key word
		  String cx  = "012772727063918838439:2cwicvp-wsk";
		  //String fileType = "png,jpg";
		  //String imgType  = "";
		  String searchType = "image";
		  //int start   = index;
		  //int indexReturn  = index;
		  //println("START INDEX "+indexReturn);
		  //URL url = new URL ("https://www.googleapis.com/customsearch/v1?key=" +key+ "&cx=" +cx+ "&q=" +qry+ "&fileType="+fileType+"&imgType="+imgType+"&searchType="+searchType+"&start="+start+"&num=10&alt=json");
		  //for (int i = 0; i < 3; i++) {
		  int indexResult = 1;
		  int numImagesSaved = 0;
		  while (numImagesSaved < 30) {
			  URL url = new URL ("https://www.googleapis.com/customsearch/v1?key=" +key+ "&cx=" +cx+ "&q=" +qry + "&searchType="+searchType+"&start="+indexResult + "&num=1");//);
			  //GET https://www.googleapis.com/customsearch/v1?key=INSERT_YOUR_API_KEY&cx=017576662512468239146:omuauf_lfve&q=lectures
			  // URL url =  new URL("https://www.googleapis.com/customsearch/v1?q=nebulas&cx=001609494755766729867%3Aez8fjbajppw&key=AIzaSyDoWXkPTvfnzCjmyauvDaRjVyTPpxxYIvM&alt=json");
			  
			  HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			  conn.setRequestMethod("GET");
			  conn.setRequestProperty("Accept", "application/json");
			  BufferedReader br = new BufferedReader(new InputStreamReader ( ( conn.getInputStream() ) ) );
	//		  GResults results = new Gson().fromJson(br, GResults.class);
	//		  System.out.println(results.getUrl());
			  String output;
			  //System.out.println("Output from Server .... \n");
			  while ((output = br.readLine()) != null) {
			        if(output.contains("\"link\": \"")){                
			            String link=output.substring(output.indexOf("\"link\": \"")+("\"link\": \"").length(), output.indexOf("\","));
			            System.out.println(link);       //Will print the google search links
			            URL imageURL = new URL(link);
			            try {
			            		imagesList.add(ImageIO.read(imageURL));
			            		numImagesSaved++;
			            		if (imagesList.get(imagesList.size() - 1) == null) {
			            			
			            		}
			            } catch (Exception InputMismachException) {
			            		System.out.println("Exception catched");    
			            }
			            System.out.println(indexResult);
			            System.out.println(numImagesSaved);
			        }     
			  }
			  indexResult++;
			  conn.disconnect();
		  }
	}
	private void outputImages() {
		if (imagesList.size() == 30) {
			for (int i = 0; i < 30; i++) {
				try {
					if (imagesList.get(i) != null)
					ImageIO.write(imagesList.get(i), "jpg",new File("/Users/gongchen/Desktop/310imagesFolder/image" + i + ".jpg"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	
	private  BufferedImage concatImages(List<BufferedImage> imagesList) {
		int heightTotal = 0;
        for(int j = 0; j < imagesList.size(); j++) {
            heightTotal += imagesList.get(j).getHeight();
        }
        int heightCurr = 0;
        BufferedImage concatImage = new BufferedImage(1000, 800, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = concatImage.createGraphics();
        for(int j = 0; j < imagesList.size(); j++) {
            g2d.drawImage(imagesList.get(j), 0, heightCurr, null);
            heightCurr += imagesList.get(j).getHeight();
        }
        g2d.dispose();
        return concatImage;
	}
}
