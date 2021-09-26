package fr.ecam.recoimage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Classe illustrant l'utilisation du client HTTP intégré à Java 11
 *  et de clarifai.com pour faire de la classification d'image
 *
 * @author pat 
 */
public class RecoImage {

	/**
	 * 
	 * @param url- URL de l'image à classifier
	 * @return flux JSON retourné par clarifai
	 */
	public String classifyImage(String url) throws Exception {
		String res = null;

		try {		
			String MODEL_ID= "aaa03c23b3724a16a56b629203edc62c"; //modèle de classification d'images "général"
			String CLARIFAI_API_KEY = "3f11c47ba665437692273b01e4e97af5";
			// URL du service à appeler
			String service_url = String.format("https://api.clarifai.com/v2/models/%s/outputs",MODEL_ID);		

			//contruction manuelle de JSON, envisageable pour un petit flux, sinon on passe plutôt par l'API de manipulation de JSON
			String payload = "{\"inputs\": [{\"data\": {\"image\": {\"url\": \""+url+"\"}}}]}";

			//TODO PARTIE 2 du TP: utiliser le Jackson TreeModel pour construire le flux payload au lieu de passer par une chaine de caractères
			
			
			//Note: ne fonctionne pas pour l'appel du service, mais on peut l'activer en phase de debug 
			//pour une lecture plus facile quand on construit le flux via le Jackson TreeModel
			//mapper.enable(SerializationFeature.INDENT_OUTPUT);
			
			//récupère la payload JSON sous forme d'une chaine de caractères
			//String payload = mapper.writeValueAsString(root);

			//construit une requête via l'API builder, avec tous les en-têtes et la payload:
			//noté le mot-clé var apparu avec Java11 qui permet de ne pas utiliser le type explicite de la variable
			var request = HttpRequest.newBuilder()
					.uri(URI.create(service_url))
					.header("Content-Type", "application/json")
					.header("Authorization", String.format("Key %s",CLARIFAI_API_KEY))
					.POST(HttpRequest.BodyPublishers.ofString(payload))
					.build();
			
			System.out.println("Envoi requete vers: " + service_url);
			System.out.println("avec la payload: " + payload);
			
			// Exécution requete
			var client = HttpClient.newHttpClient();
			
			// Lecture réponse au format String
			var response = client.send(request, HttpResponse.BodyHandlers.ofString());

			System.out.println("----------------------------------------");
			System.out.println(response.statusCode());

			res = response.body();
		} finally {
		}
		return res;
	}

	/**
	 * Affiche un tableau des concepts et de leur probabilité
	 * @param data flux json retourné par clarifai
	 */
	public void displayConcepts(String data) {
		System.out.println("Concepts détectés:");
		System.out.println("flux JSON brut retourné: "+data);
		
		//TODO: utiliser Jackson TreeModel pour récupérer et afficher les concepts retournés
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(data);
			
			//TODO: compléter le code
			//note: vous pouvez utiliser ce site pour formatter un flux json pour une facilité de lecture: https://jsonformatter.org/
			//copier/coller y le flux JSON brut affiché sur la console 
			//pour vous aider à naviguer dans l'arbre JSON avec l'API Jackson
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
	
	/**
	 * Point d'Entree
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) {
		RecoImage ri = new RecoImage();
		
		//URL de l'image à classifier, à adapter à votre nom de VM.
		String urlImage = "http://vps284011.ovh.net/ecam/ecam-1.jpg";
		String res;
		try {
			res = ri.classifyImage(urlImage);
			ri.displayConcepts(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
