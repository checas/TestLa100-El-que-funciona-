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
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.AfterClass;

public class TestPaginaNoticias {
	private String filepath = "C:\\Users\\yo_ch\\Desktop\\workcloud\\testing\\Test_3.xlsx";
	private int filaActual = 406;
	private WebDriver driver;
	private ReadExcel readFile;
	private WriteExcel writeFile;
	private By miraTmb = By.xpath("//div[@class=\"articleContainer\"]//div[@class=\"articleReferenceContainer\"]/a");
	private By texto = By.xpath("//div[@class=\"articleContainer\"]//section[@class=\"paragrath\"]/div");
	private boolean todoOK = true;
	private boolean miraTmbOk = false;
	private boolean textoOk = false;

	@BeforeClass
	public void beforeClass() throws IOException {
		System.setProperty("webdriver.chrome.driver", "./src/test/resources/chromedriver.exe");
		driver = new ChromeDriver();
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
			String[] salidaExcel = new String[8];
			//writeFile.writeCellValue(filepath, "Hoja1", filaActual, 3, tokens[0]);
			salidaExcel[0] = tokens[0];
			//System.out.println(tokens[0]);
			driver.get("https://la100.cienradios.com/" +tokens[1]);
			//writeFile.writeCellValue(filepath, "Hoja1", filaActual, 4, "https://la100.cienradios.com/" + tokens[1]);
			salidaExcel[1] = "https://la100.cienradios.com/" + tokens[1];
			/*for(String t : tokens) {
				System.out.println(t);
			}
			/*
			System.out.println("Lectura excel: " + searchText);
			driver.get(searchText);
			*/
			// Prueba mira tmb
			int iAux;
			List<WebElement> ListMiraTbm = driver.findElements(miraTmb);
			Iterator<WebElement> itMiraTmb = ListMiraTbm.iterator();
			boolean pasaMiraTmb = true;
			iAux = 0;
			int miraRoto = 0;
			System.out.println("Cantidad de mira tmb: " + ListMiraTbm.size());
			//writeFile.writeCellValue(filepath, "Hoja1", filaActual, 5, String.valueOf(ListMiraTbm.size()));
			salidaExcel[2] = String.valueOf(ListMiraTbm.size());
			while (itMiraTmb.hasNext()) {
				iAux++;
				WebElement miraAux = itMiraTmb.next();
				System.out.println("Mira tmb " + iAux + "/" + ListMiraTbm.size());
				System.out.println("Src: " + miraAux.getAttribute("href"));
				if (!revisarLinks(miraAux.getAttribute("href"))) {
					pasaMiraTmb = false;
					miraRoto++;
				}
			}
			//writeFile.writeCellValue(filepath, "Hoja1", filaActual, 6, String.valueOf(miraRoto));
			salidaExcel[3] = String.valueOf(miraRoto);
			if (pasaMiraTmb) {
				//writeFile.writeCellValue(filepath, "Hoja1", filaActual, 7, "SI");
				salidaExcel[4] = "SI";
			} else {
				//writeFile.writeCellValue(filepath, "Hoja1", filaActual, 7, "NO");
				salidaExcel[4] = "NO";
				todoOK = false;
			}
			System.out.println("---------------------------------------------------------");

			// Prueba parrafos
			List<WebElement> ListaParrafos = driver.findElements(texto);
			Iterator<WebElement> itParrafo = ListaParrafos.iterator();
			iAux = 0;
			boolean sinPlano = true;
			boolean pasaTexto = true;
			System.out.println("Cantidad de parrafos: " + ListaParrafos.size());
			while (itParrafo.hasNext()) {
				iAux++;
				pasaTexto = true;
				WebElement parrafoAux = itParrafo.next();
				System.out.println("Mira tmb " + iAux + "/" + ListaParrafos.size());
				System.out.println("Texto: " + parrafoAux.getText());
				//prueba para elementos dentro del parrafo
				List<WebElement> webEleList = parrafoAux.findElements(By.xpath(".//*"));
				System.out.println("Elementos dentro del parrafo:" + webEleList.size());
				Iterator<WebElement>itDentroParrafo = webEleList.iterator();
				/*
				while(itDentroParrafo.hasNext()) {
					WebElement algo = itDentroParrafo.next();
					System.out.println("Contenido tag: " + algo.getTagName());
					System.out.println("Contenido text: " + algo.getText());
				}
				*/
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
				}
			}
			if (sinPlano) {
				//writeFile.writeCellValue(filepath, "Hoja1", filaActual, 8, "NO");
				salidaExcel[5] = "NO";
				//writeFile.writeCellValue(filepath, "Hoja1", filaActual, 9, "SI");
				salidaExcel[6] = "SI";
			} else {
				//writeFile.writeCellValue(filepath, "Hoja1", filaActual, 8, "SI");
				//writeFile.writeCellValue(filepath, "Hoja1", filaActual, 9, "NO");
				salidaExcel[5] = "SI";
				salidaExcel[6] = "NO";
				todoOK = false;
			}

			System.out.println("---------------------------------------------------------");

			if (todoOK) {
				//writeFile.writeCellValue(filepath, "Hoja1", filaActual, 10, "SI");
				salidaExcel[7] = "SI";
			} else {
				//writeFile.writeCellValue(filepath, "Hoja1", filaActual, 10, "NO");
				salidaExcel[7] = "NO";
			}
			/*
			 * tituloOk = tagTitulo.equals("h1"); subTituloOk = tagSubtitulo.equals("h2");
			 */
			// assertTrue(driver.findElement(titulo).getTagName().equals("h1"), "Fallo el
			// tag del titulo o del sub titulo");
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
