/**
 
    WikiCrimes (http://www.wikicrimes.org) is a project/software that allows posting and accessing criminal occurrences in a digital map.
    The philosophy that drives Wikicrimes is the same as Wikipedia: mass collaboration produces valuable knowledge.
    That is to say, if everybody participates, the criminal mapping will be made collaboratively and everybody
    will leverage crime information digitalized in the map. That is the reason for the slogan "Share crime information. Keep safe!". 
    Wikicrimes is not a project developed by any security institution. 
    In fact it is a project from the citizen to the citizen. 
     
    
    Copyright (C) 2008  Wikinova Solutions (http://www.wikinova.com.br)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/
package org.wikicrimes.servlet;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wikicrimes.model.BaseObject;
import org.wikicrimes.model.Crime;
import org.wikicrimes.model.PontoLatLng;
import org.wikicrimes.service.CrimeService;
import org.wikicrimes.service.ImagemMapaService;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;


/**
 * Gera uma imagem de uma area do mapa com um marcador central e seus adjacentes sem filtro de data.
 * Realize uma requisicaoo no seguinte molde: http://localhost:8080/wikicrimes/ServletScreenShotMapa?chaveCrime=(chave) 
 * onde (chave) e a chave do crime central.
 * Comunica-se com a Google Static Maps API(http://code.google.com/intl/pt-BR/apis/maps/documentation/staticmaps/).
 * @author Andre
 */

public class ServletScreenShotMapa extends HttpServlet {

	private static ImagemMapaService imagemMapaService = null;
	private static CrimeService crimeService = null;
	
	private static final long serialVersionUID = 1L;

	// Razao entre pixel e lat e lng para o zoom 18
	private final int RAZAO_WIDTH_ZOOM_18 = 186413;
	private final int RAZAO_HEIGHT_ZOOM_18 = 186808;

	// Razao entre pixel e lat e lng para o zoom 16
	private final int RAZAO_WIDTH_ZOOM_16 = 46603;
	private final int RAZAO_HEIGHT_ZOOM_16 = 46701;

	// Razao entre pixel e lat e lng para o zoom 15
	private final int RAZAO_WIDTH_ZOOM_15 = 23301;
	private final int RAZAO_HEIGHT_ZOOM_15 = 23351;
	
	// Razao entre pixel e lat e lng escolhida
	private int razaoWidthZoom;
	private int razaoHeightZoom;
	
	//Limites da imagem em latlng
	private double north, south, east, west;
	
	//Tamanho da imagem em pixels
	private double mapImageWidth = 256;
	private double mapImageHeight = 256;

	//Zoom usado para criar a iamgem
	private int zoom = 16;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response);
	}

	protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String dominio = request.getParameter("dominio");

		//Trata de forma diferentes requisicoes feitas pelo wikimapps ou wikicrimes
		if (dominio.equalsIgnoreCase("wikicrimes"))
			doRequestWikiCrimes(request, response);
		else if (dominio.equalsIgnoreCase("wikimapps"))
			doRequestWikiMapps(request, response);
	}
	
	
	/*	----	INICIO WIKICRIMES	----	*/
	/**
	 * Trata requisicoes feitas pelo wikicrimes
	 */
	protected void doRequestWikiCrimes(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		razaoWidthZoom = RAZAO_WIDTH_ZOOM_18;
		razaoHeightZoom = RAZAO_HEIGHT_ZOOM_18;
		
		//Recupera a chave do crime central
		String chaveCrime = request.getParameter("chaveCrime");
		
		imagemMapaService = getService();
		crimeService = getCrimeService();
		
		//Recupera o crime central atrav�s da chave
		Crime crimeCentral = crimeService.getCrime(chaveCrime);
		
		URL url = constroiUrlGSM(crimeCentral.getLatitude(), crimeCentral.getLongitude(), false);
		BufferedImage imagemMapa = toBufferedImage(requestImage(url));
		
		//Calcula os valores limite da imagem em latlng
		north = crimeCentral.getLatitude() + (mapImageHeight/2)/razaoHeightZoom;
		south = crimeCentral.getLatitude() - (mapImageHeight/2)/razaoHeightZoom;
		east = crimeCentral.getLongitude() + (mapImageWidth/2)/razaoWidthZoom;
		west = crimeCentral.getLongitude() - (mapImageWidth/2)/razaoWidthZoom;
		
		//Pinta os marcadores na imagem
		pintaCrimes(imagemMapa, crimeCentral);
		
		response.setContentType("image/png");
		OutputStream out = response.getOutputStream();
		ImageIO.write(imagemMapa, "PNG", out);
	}
	
	/**
	 * @param centro - Ponto central do mapa
	 * @return URL preparada para requisitar imagem do Google Static Maps
	 * @throws MalformedURLException
	 */
	/**
	 * @param latitude - Latitude do centro
	 * @param longitude - Longitude do centro
	 * @param satellite - booleano que diz se o mapa deve aparecer como imagem de satelite
	 * @return
	 * @throws MalformedURLException
	 */
	private URL constroiUrlGSM(double latitude, double longitude, boolean satellite)
			throws MalformedURLException {
		/*
		 * Para mais informacoes sobre a URL ver:
		 * http://code.google.com/intl/pt-BR/apis/maps/documentation/staticmaps/
		 */
		
		String urlMapaLimpo = "http://maps.google.com/maps/api/staticmap?center="
				+ latitude
				+ ","
				+ longitude
				+ "&zoom="
				+ zoom
				+ "&size="
				+ (int) mapImageWidth
				+ "x"
				+ (int) mapImageHeight
				+ "&sensor=false";

		if(satellite) 
			urlMapaLimpo += "&maptype=satellite";
		
		return new URL(urlMapaLimpo);
	}
	
	
	/**
	 * Metodo que retorna uma imagem a partir de uma URL.
	 * 
	 * @param url - URL da requisicao
	 * @param width - width que a imagem tera
	 * @param height - height que a imagem tera
	 * @return imagem de reposta
	 */
	private Image requestImage(URL url, int width, int height) {
		Image img = requestImage(url); 
		return img.getScaledInstance(width, height, Image.SCALE_SMOOTH); 
	}
	
	/**
	 * Metodo que retorna uma imagem a partir de uma URL.
	 * 
	 * @param url - URL da requisicao
	 * @return imagem de resposta
	 */
	private Image requestImage(URL url) {
		BufferedImage imagem = null;
		HttpURLConnection con = null;
		try {
			// Acessa a url
			con = (HttpURLConnection) url.openConnection();
			con.connect();

			// Recebe a resposta
			ImageInputStream input = new MemoryCacheImageInputStream(con
					.getInputStream());
			imagem = ImageIO.read(input);

		} catch (IOException e) {
			e.printStackTrace();
			throw new AssertionError("Imposs�vel recuperar a imagem.\n"
					+ e.getMessage());
		} finally {
			con.disconnect();
		}

		return imagem;
	}
	
	/**
	 * Pinta na imagem os marcadores do tipo crime que se encontram nos limites da imagem.
	 * @param imagem - Imagem que que tera os marcadores pintados.
	 * @throws IOException
	 */
	private void pintaCrimes(Image imagem, Crime crimeCentral) throws IOException{
		List<BaseObject> crimes = getCrimesNaArea();
		Graphics2D g = (Graphics2D)imagem.getGraphics();
		
		for(BaseObject o : crimes)
			if(o instanceof Crime){
				Crime c = (Crime)o;
				String tipo = c.getTipoCrime().getNome();
				File marcadorFile = null;

				if(c.getChave().equals(crimeCentral.getChave())) {
					if(tipo.equals("tipocrime.roubo") || tipo.equals("tipocrime.tentativaderoubo"))
						marcadorFile = new File("webapps/wikicrimes/images/baloes/vermelhoDestaque.png");
					else if(tipo.equals("tipocrime.furto") || tipo.equals("tipocrime.tentativadefurto"))
						marcadorFile = new File("webapps/wikicrimes/images/baloes/azulDestaque.png");
					else
						marcadorFile = new File("webapps/wikicrimes/images/baloes/laranjaDestaque.png");
				} else {
					if(tipo.equals("tipocrime.roubo") || tipo.equals("tipocrime.tentativaderoubo"))
						marcadorFile = new File("webapps/wikicrimes/images/baloes/vermelho.png");
					else if(tipo.equals("tipocrime.furto") || tipo.equals("tipocrime.tentativadefurto"))
						marcadorFile = new File("webapps/wikicrimes/images/baloes/novoMarcadorAzul.png");
					else
						marcadorFile = new File("webapps/wikicrimes/images/baloes/novoMarcadorLaranja.png");
				}
				
				Image marcador = requestImage(new URL("http://wikimapps.com/images/icons/mm_293.png"));
				
				int height = marcador.getHeight(null);
				PontoLatLng latlng = new PontoLatLng(c.getLatitude(), c.getLongitude());
				Point p = toPixel(latlng, razaoWidthZoom, razaoHeightZoom);
				if(c.getChave().equals(crimeCentral.getChave()))
					g.drawImage(marcador, p.x - 13, p.y - (height - 2), null);
				else 
					g.drawImage(marcador, p.x, p.y - height, null);
			}
	}
	
	/**
	 * Transforma um ponto latlong em pixel
	 * @param pontoLatLong
	 * @return Ponto em pixel equivalente
	 */
	private Point toPixel(PontoLatLng pontoLatLong, double razaoWidth, double razaoHeight){
		int x = (int)((pontoLatLong.getLongitude() - west) * razaoWidth);
		int y = (int)((north - pontoLatLong.getLatitude()) * razaoHeight);
		
		return new Point(x,y);
	}
	
	/**
	 * @param ponto1 - Limite superior esquerdo
	 * @param ponto2 - Limite inferior direito
	 * @return Crimes dentro da area
	 */
	@SuppressWarnings("unchecked")
	private List<BaseObject> getCrimesNaArea() {
		Map parameters = new HashMap();
		
		parameters.put("norte", north);
		parameters.put("sul", south);
		parameters.put("oeste", west);
		parameters.put("leste", east);
		
		List<BaseObject> result = crimeService.filter(parameters);
		
		return result;
	}
	
	private ImagemMapaService getService(){
		if(imagemMapaService == null){
			ApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
			imagemMapaService = (ImagemMapaService)springContext.getBean("imagemMapaService");
		}
		return imagemMapaService;
	}
	
	private CrimeService getCrimeService(){
		if(crimeService == null){
			ApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
			crimeService = (CrimeService)springContext.getBean("crimeService");
		}
		return crimeService;
	}
	/*	----	FIM WIKICRIMES	----	*/
	
	
	/*	----	INICIO WIKIMAPPS	----	*/
	/**
	 * Trata requisicoes feitas pelo wikimapps
	 */
	protected void doRequestWikiMapps(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			razaoWidthZoom = RAZAO_WIDTH_ZOOM_16;
			razaoHeightZoom = RAZAO_HEIGHT_ZOOM_16;
			
			// Chave do marcador
			String id = request.getParameter("id"); 
			// latitude do marcador
			double latitude = Double.parseDouble(request.getParameter("lat")); 
			// longitude do marcador
			double longitude = Double.parseDouble(request.getParameter("lng"));
			// applicacao wikimapps
			String application = request.getParameter("app");
			
			// Calcula os valores limite da imagem em latlng
			north = latitude + (mapImageHeight / 2) / razaoHeightZoom;
			south = latitude - (mapImageHeight / 2) / razaoHeightZoom;
			east = longitude + (mapImageWidth / 2) / razaoWidthZoom;
			west = longitude - (mapImageWidth / 2) / razaoWidthZoom;

			// xml com os marcadores da area
			String xml = requestWikiMappsXML(application, north, south, east, west);
			
			// Recebe uma img no formato Image e transforma em BufferedImage
			
			URL url;
			if(application.equalsIgnoreCase("unifor"))
				url = constroiUrlGSM(latitude, longitude, true);
			else
				url = constroiUrlGSM(latitude, longitude, false);
			
			Image imageAux = requestImage(url);
			BufferedImage mapImage = toBufferedImage(imageAux);

			// Recebe todos os marcadores da �rea
			List<Marker> markers = readMappsXml(xml);
			HashMap<String, Image> hashMapImages = new HashMap<String, Image>();

			// Variavel que quardara o marcador principal da imagem
			Marker centralMarker = null;

			for (Marker marker : markers) {
				String imageUrlSrting = marker.getWikiMappsUrlImage();

				// Caso n�o consiga recuperar a imagem
				if (imageUrlSrting == null) {
					System.out.println("Erro 400");
					response.sendError(400);
					return;
				}

				// Usa o conceito de singleton para as imagens
				Image markerImage = hashMapImages.get(imageUrlSrting);
				if (marker.chave.equalsIgnoreCase(id)) {
					centralMarker = marker;
					continue;
				} else if (markerImage == null) {
					// Diminue os marcadores coadjuvantes para dar maior destaque ao central
					markerImage = requestImage(new URL(imageUrlSrting),	(int)(marker.iw * 0.8), (int)(marker.ih * 0.8));

					hashMapImages.put(imageUrlSrting, markerImage);
				}

				//Transforma o lat lng do marcador para equivalentes em pixel na imagem do mapa
				Point p = toPixel(marker, north, west, razaoWidthZoom, razaoHeightZoom);

				mapImage.getGraphics().drawImage(markerImage, p.x, p.y, null);
			}

			// Plota o marcador central
			URL urlCentralMarkerImage = new URL(centralMarker.getWikiMappsUrlImage());
			Image markerImage = requestImage(
					urlCentralMarkerImage, 
					(int) (centralMarker.iw * 1.3), 
					(int) (centralMarker.ih * 1.3)
					);
			
			Point p = toPixel(centralMarker, north, west, razaoWidthZoom, razaoHeightZoom);
			mapImage.getGraphics().drawImage(markerImage, p.x, p.y, null);

			// Configura a resposta do servlet para ser uma imagem PNG
			response.setContentType("image/png");
			//Manda a imagem como resposta
			OutputStream out = response.getOutputStream();
			ImageIO.write(mapImage, "PNG", out);

		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(400);
		}
	}
	
	/**
	 * Recebe um Marker com latitude(x) e longitude(y) e retorna um ponto com o
	 * equivalente em pixels. Esse metodo � dependente do zoom predefinido na
	 * classe Servlet.
	 * 
	 * @param marker - Marker com latitude(x) e longitude(y) a serem convertidos
	 * @param north - lat do limite superior da imagem
	 * @param west - lng do limite esquerdo da imagem
	 * @param razaoWidth - Raz�o uasada para calcular o novo width (Cada zoom possui uma raz�o)
	 * @param razaoHeight - Raz�o uasada para calcular o novo height (Cada zoom possui uma raz�o)
	 * @return
	 */
	private Point toPixel(Marker marker, double north, double west,	double razaoWidth, double razaoHeight) {
		int x = (int) ((marker.lng - west) * razaoWidth) - (marker.iw / 2);
		int y = (int) ((north - marker.lat) * razaoHeight) - marker.ih;

		return new Point(x, y);
	}
	
	/**
	 * Retorna uma String com o XML de todos os marcadores dentro da area
	 * delimitada
	 * @param north - Limite superior(lat)
	 * @param south - Limite infeior(lat)
	 * @param east - Limite direito(lng)
	 * @param west - Limite esquerdo(lng)
	 * @return
	 */
	private String requestWikiMappsXML(String app, double north, double south, double east, double west) {
		HttpURLConnection con = null;

		String urlString = 
			"http://wikimapps.com/index.php/a/" + app + 
			"/markers?q=ALL&n=" + north + "&s=" + south + 
			"&l=" + east + 
			"&o=" + west + 
			"&tm=";
		try {
			URL url = new URL(urlString);
				
			// Acessa a url
			con = (HttpURLConnection) url.openConnection();
			con.connect();
	
			// Recebe a resposta
			StringBuilder sb = new StringBuilder();
			String line;

			InputStreamReader inputStreamReader = new InputStreamReader(con.getInputStream(), "UTF-8");
			BufferedReader reader = new BufferedReader(inputStreamReader);
			
			while ((line = reader.readLine()) != null) 
				sb.append(line).append("\n");
		
			return sb.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.disconnect();
		}
		
		return null;
	}
	
	/**
	 * Recebe uma XML padr�o do WikiMapps e retorna os marcadores nela contidos.
	 * 
	 * @param xml
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 */
	private List<Marker> readMappsXml(String xml) throws SAXException, IOException {
		DOMParser parser = new DOMParser();
		parser.parse(new InputSource(new StringReader(xml)));
		Document document = parser.getDocument();

		NodeList nodes = document.getChildNodes(); // Pega o no <markers>
		nodes = nodes.item(0).getChildNodes(); // Pega os nos <m>

		ArrayList<Marker> markers = new ArrayList<Marker>();

		// Percorre todos os elementos <m> do XML
		for (int index = 0; index < nodes.getLength(); index++) {
			if (!(nodes.item(index) instanceof Element))
				continue;

			Element element = (Element) nodes.item(index);

			Marker m = new Marker();

			m.chave = element.getAttribute("id");
			m.lat = Double.parseDouble(element.getAttribute("lt"));
			m.lng = Double.parseDouble(element.getAttribute("lg"));
			m.mid = element.getAttribute("mid");
			m.i = element.getAttribute("i");
			m.iw = Integer.parseInt(element.getAttribute("iw"));
			m.ih = Integer.parseInt(element.getAttribute("ih"));

			markers.add(m);
		}

		if (markers.size() != 0)
			return markers;
		else
			return null;
	}
	
	public BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}

		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();

		// Determine if the image has transparent pixels
		boolean hasAlpha = hasAlpha(image);

		// Create a buffered image with a format that's compatible with the
		// screen
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		try {
			// Determine the type of transparency of the new buffered image
			int transparency = Transparency.OPAQUE;
			if (hasAlpha == true) {
				transparency = Transparency.BITMASK;
			}

			// Create the buffered image
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image
					.getHeight(null), transparency);
		} catch (HeadlessException e) {
		} // No screen

		if (bimage == null) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			if (hasAlpha == true) {
				type = BufferedImage.TYPE_INT_ARGB;
			}
			bimage = new BufferedImage(image.getWidth(null), image
					.getHeight(null), type);
		}

		// Copy image to buffered image
		Graphics g = bimage.createGraphics();

		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}
	
	private boolean hasAlpha(Image image) {
		// If buffered image, the color model is readily available
		if (image instanceof BufferedImage) {
			return ((BufferedImage) image).getColorModel().hasAlpha();
		}

		// Use a pixel grabber to retrieve the image's color model;
		// grabbing a single pixel is usually sufficient
		PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
		}

		// Get the image's color model
		return pg.getColorModel().hasAlpha();
	}
	
	/*
	 * Classes utilitarias
	 */

	private static class Marker {
		private String chave;
		private String mid;
		private String i;
		private double lat;
		private double lng;
		private int ih;
		private int iw;

		/**
		 * Retorna a URL para a imagem no wikimapps e null caso os dados do
		 * wikimapps n�o estejam setados(ou seja, o marker seja do wikimapps)
		 * 
		 * @return
		 */
		public String getWikiMappsUrlImage() {
			if (i == null)
				return null;
			if (i.equals("0"))
				return "http://wikimapps.com/images/icons/mm_" + mid + ".png";
			else
				return "http://wikimapps.com/images/mm_" + i + ".png";
		}

	}
	/*	----	FIM WIKIMAPPS	----	*/
}
	