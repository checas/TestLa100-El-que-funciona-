package com.TesteoPagina;

import org.testng.annotations.Test;

import org.testng.annotations.BeforeClass;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.AfterClass;

public class TestPaginaNoticias2 {
	private String filepath = "C:\\Users\\yo_ch\\Desktop\\workcloud\\testing\\Test_3.xlsx";
	private int filaActual = 3212;
	private WebDriver driver;
	private ReadExcel readFile;
	private WriteExcel writeFile;
	private By miraTmb = By.cssSelector(".articleContainer div.articleReferenceContainer a");
	private By texto = By.cssSelector(".articleContainer section.paragrath div");
	private By fecha = By.cssSelector(".articleContainer span.articleContainer__header__top__date");
	private By link = By.cssSelector(".articleContainer a");
	private boolean todoOK = true;
	private boolean miraTmbOk = false;
	private boolean textoOk = false;

	@BeforeClass
	public void beforeClass() throws IOException {
		System.setProperty("webdriver.chrome.driver", "./src/test/resources/chromedriver.exe");
		//deja de cargar imagenes
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless", "--disable-gpu", "--blink-settings=imagesEnabled=false");
		driver = new ChromeDriver(options);
		//driver = new ChromeDriver();
		driver.manage().window().maximize();
		readFile = new ReadExcel();
		writeFile = new WriteExcel();
		
		//driver.get("https://la100.cienradios.com/espectaculos/a-puro-escote-sinuoso-y-peligroso-noelia-marzol-encandilo-la-noche/");
		// driver.get("https://la100.cienradios.com/le-dijeron-que-tenia-cancer-le-extirparon-los-pechos-e-hizo-quimioterapia-todo-fue-un-mal-diagnostico/");
		// driver.get("https://la100.cienradios.com/le-dijeron-que-tenia-cancer-le-extirparon-los-pechos-e-hizo-quimioterapia-todo-fue-un-mal-diagnostico/");
	}

	@Test(invocationCount = 200)
	public void MiraTmb() throws IOException, InterruptedException {
		
			todoOK = true;
			System.out.println(filaActual);
			
			String searchText = readFile.getCellValue(filepath, "Hoja1", filaActual, 2);
			System.out.println(searchText);
			String[] tokens = searchText.split(",");
			
			String[] salidaExcel = new String[12];
			//writeFile.writeCellValue(filepath, "Hoja1", filaActual, 3, tokens[0]);
			
			salidaExcel[0] = tokens[0];
			salidaExcel[7] = "SI";
			salidaExcel[6] = "SI";
			salidaExcel[5] = "NO";
			
			
			System.out.println(tokens[0]);
			//driver.get("https://la100.cienradios.com/viral-se-casaron-en-medio-de-la-cuarentena-por-el-coronavirus/");
			
			driver.get("https://la100.cienradios.com/" +tokens[1]);
			//El de abajo no va
			//writeFile.writeCellValue(filepath, "Hoja1", filaActual, 4, "https://la100.cienradios.com/" + tokens[1]);
			
			salidaExcel[1] = "https://la100.cienradios.com/" + tokens[1];
			
			//fecha
			System.out.println(driver.findElement(fecha).getText());
			salidaExcel[8] = driver.findElement(fecha).getText();
			// Prueba mira tmb
			int iAux;
			List<WebElement> ListMiraTbm = driver.findElements(miraTmb);
			Iterator<WebElement> itMiraTmb = ListMiraTbm.iterator();
			//boolean pasaMiraTmb = true;
			iAux = 0;
			//int miraRoto = 0;
			System.out.println("Cantidad de mira tmb: " + ListMiraTbm.size());
			//writeFile.writeCellValue(filepath, "Hoja1", filaActual, 5, String.valueOf(ListMiraTbm.size()));
			salidaExcel[2] = String.valueOf(ListMiraTbm.size());
			
			//if(ListMiraTbm.size() == 0) {
			List<WebElement> ListaParrafos = driver.findElements(texto);
			Iterator<WebElement> itParrafo = ListaParrafos.iterator();
			iAux = 0;
			boolean sinPlano = true;
			boolean pasaTexto = true;
			int linkTextoWpContent = 0;
			int imagenTexto = 0;
			System.out.println("Cantidad de parrafos: " + ListaParrafos.size());
			while (itParrafo.hasNext()) {
				iAux++;
				pasaTexto = true;
				WebElement parrafoAux = itParrafo.next();
				System.out.println("Texto: " + parrafoAux.getText());
				String textoARevisar = parrafoAux.getText();
				int linkPlano = textoARevisar.indexOf("http");
				System.out.println("indice http: " + linkPlano);
				int fuente1 = textoARevisar.indexOf("Fuente:http");
				System.out.println("indice 'uente:http : " + fuente1);
				int fuente2 = textoARevisar.indexOf("Fuente: http");
				System.out.println("indice 'uente: http : " + fuente2);
				System.out.println("indice http: " + linkPlano);
				if ((linkPlano == -1 || fuente1 == 0 || fuente2 == 0 )) {
					pasaTexto = true;
					System.out.println("Texto OK!");
				} else {
					pasaTexto = false;
					System.out.println("Error Texto");
					sinPlano = false;
					todoOK = false;
					salidaExcel[7] = "NO";
					salidaExcel[6] = "NO";
					salidaExcel[5] = "SI";
					if(textoARevisar.indexOf("wp-content") != -1) {
						System.out.println("Error link con wp-content como texto");
						linkTextoWpContent++;
					}
					if((textoARevisar.indexOf(".gif") != -1) || textoARevisar.indexOf(".jpg") != -1) {
						System.out.println("Error imagen como texto");
						imagenTexto++;
					}
				}
			}
			if(linkTextoWpContent > 0) {
				salidaExcel[11] = "SI";
			} else {
				salidaExcel[11] = "NO";
			}
			if(imagenTexto > 0) {
				salidaExcel[10] = "SI";
			} else {
				salidaExcel[10] = "NO";
			}
			
			/* Parte del if si MiraTmb size >0
			} else {
				todoOK = true;
			}
			*/
			
			List<WebElement> ListaLinks = driver.findElements(link);
			Iterator<WebElement> itLink = ListaLinks.iterator();
			boolean pasaLink = true;
			int linkViejo = 0;
			int posWsComponent = 0;
			String urlLink = "";
			iAux = 0;
			System.out.println("Cantidad de links: " + ListaLinks.size());
			while (itLink.hasNext()) {
				iAux++;
				WebElement linkAux = itLink.next();
				urlLink = linkAux.getAttribute("href");
				posWsComponent = urlLink.indexOf("wp-content");
				if (posWsComponent != -1) {
					System.out.println("Src: " + urlLink);
					pasaLink = false;
					linkViejo++;
					todoOK = false;
				}
			}
			if(linkViejo >0 ) {
				salidaExcel[9] = "SI";
			} else {
				salidaExcel[9] = "NO";
			}
			
			
			
			writeFile.writeTestValue(filepath, "Hoja1", filaActual, salidaExcel);
			filaActual++;
			assertTrue(todoOK, "Fallo en alguna prueba");
			
	}

	@AfterClass
	public void afterClass() {
		driver.quit();
	}

	public boolean revisarLinks(String url) {
		boolean estado = false;
		if (url != null) {
			HttpURLConnection httpConection = null;
			int responseCode = 200;
			try {
				httpConection = (HttpURLConnection) (new URL(url).openConnection());
				httpConection.setRequestMethod("HEAD");
				httpConection.connect();
				responseCode = httpConection.getResponseCode();
				if (responseCode > 400) {
					if (responseCode != 405) {
						System.out.println("ERROR BROKEN LINK: " + responseCode + "-- " + url);
						estado = false;
					}
				} else {
					System.out.println("VALID LINK: -- " + url);
					estado = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Link vacio");
			estado = false;
		}
		return estado;
	}
}
