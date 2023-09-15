package fr.ecam.recoimage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Classe illustrant l'utilisation du client HTTP intégré à Java 11+ et de
 * clarifai.com pour faire de la classification d'image
 *
 * @author pat
 */
public class RecoImage {

	/**
	 * 
	 * @param url - URL de l'image à classifier
	 * @return flux JSON retourné par clarifai
	 */
	public String classifyImage(String url) throws Exception {
		String res = null;

		try {
			String MODEL_ID = "aaa03c23b3724a16a56b629203edc62c"; // modèle de classification d'images "général"
			String CLARIFAI_API_KEY = "3f11c47ba665437692273b01e4e97af5";
			// URL du service à  appeler
			String service_url = String.format("https://api.clarifai.com/v2/models/%s/outputs", MODEL_ID);

			// construction manuelle de JSON, envisageable pour un petit flux, sinon on
			// passe plutà´t par l'API de manipulation de JSON
			String payload = "{\"inputs\": [{\"data\": {\"image\": {\"url\": \"" + url + "\"}}}]}";

			// TODO PARTIE 2 du TP: utiliser le Jackson TreeModel pour construire le flux
			// payload au lieu de passer par une chaine de caractères

			// Note: ne fonctionne pas pour l'appel du service, mais on peut l'activer en
			// phase de debug
			// pour une lecture plus facile quand on construit le flux via le Jackson
			// TreeModel
			// mapper.enable(SerializationFeature.INDENT_OUTPUT);

			// récupère la payload JSON sous forme d'une chaine de caractères
			// String payload = mapper.writeValueAsString(root);

			// construit une requète via l'API builder, avec tous les en-têtes et la
			// payload:
			// noter le mot-clé var apparu avec Java11 qui permet de ne pas utiliser le type
			// explicite de la variable
			var request = HttpRequest.newBuilder().uri(URI.create(service_url))
					.header("Content-Type", "application/json")
					.header("Authorization", String.format("Key %s", CLARIFAI_API_KEY))
					.POST(HttpRequest.BodyPublishers.ofString(payload)).build();

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
	 * 
	 * @param data flux json retourné par clarifai
	 */
	public void displayConcepts(String data) {
		System.out.println("Concepts détectés:");
		System.out.println("flux JSON brut retourné: " + data);

		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(data);

			// TODO: utiliser Jackson TreeModel pour récupérer et afficher les concepts
			// retournés pour l'image spécifiée ainsi que
			// le score de probabilité associé
			// afficher ces deux infos sur la console

			// attention le flux de retour contient des infos relatives à  des images
			// d'exemple.
			// il faut retourner les concepts associés à  l'image provenant du serveur
			// vps284011.ovh.net
			// exemple:
			/*
			 * "input": { "id": "223872e2ddf1444489327a034d121c7d", "data": { "image": {
			 * "url": "http://vps284011.ovh.net/ecam/image.jpg" } } }, "data": { "concepts":
			 * [ { "id": "ai_c9n7SB25", "name": "furniture", "value": 0.9966653, "app_id":
			 * "main" }....
			 */

			// TODO: compléter le code
			// note: vous pouvez utiliser ce site pour formatter un flux json pour une
			// facilité de lecture: https://jsonformatter.org/
			// copier/coller y le flux JSON brut affiché sur la console
			// pour vous aider à  naviguer dans l'arbre JSON avec l'API Jackson

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Affiche l'horaire de prochain passage du bus de la ligne spécifiée à  l'arràªt
	 * spécifié
	 * 
	 * @param nomCourtLigne - nom court de la ligne: 57 pour celle desservant Ker
	 *                      Lann
	 * @param idArret       - identifnant de l'arràªt: 2311 pour celui devant l'ECAM
	 */
	public void afficheHoraireBus(String nomCourtLigne, int idArret) {

		String service_url = String.format(
				"https://data.rennesmetropole.fr/api/records/1.0/search/?dataset=prochains-passages-des-lignes-de-bus-du-reseau-star-en-temps-reel&q=&facet=idligne&facet=nomcourtligne&facet=sens&facet=destination&facet=numerobuskr&facet=precision&facet=visibilite&facet=idarret&refine.nomcourtligne=%s&refine.idarret=%s",
				nomCourtLigne, idArret);
		System.out.println("Appel de: " + service_url);

		// construit une requàªte via l'API builder
		// noter le mot-clé var apparu avec Java11 qui permet de ne pas utiliser le type
		// explicite de la variable
		var request = HttpRequest.newBuilder().uri(URI.create(service_url)).GET().build();

		// Exécution requete
		var client = HttpClient.newHttpClient();

		// Lecture réponse au format String
		try {
			var response = client.send(request, HttpResponse.BodyHandlers.ofString());
			System.out.println("Réponse Brute:" + response.body());

			// TODO: Parser le flux avec le Jackson TreeModel: instancier un ObjectMapper et
			// parser via mapper.readTree(responde.body())

	
			//TODO:
			// prendre le dernier élément de records[]
			// Attention records[] peut àªtre vide si aucun bus n'est prévu à  cet arràªt
			// détecter ce cas (via la méthode isEmpty(null) de JsonNode et afficher un
			// message dans ce cas)

			// il n'y a pas de moyen simple d'aller au dernier élément du tableau de records
			// => on récupà¨re un Iterator<JsonNode> sur le tableau via
			// recordsNode.iterator() et on le parcourt
			// en entier en stockant le JsonNode retourné par la méthode next() de
			// l'iterator dans une variable locale, utilisée par la suite

			// en sortie de la boucle while on on a l'heure de passage estimée dans l'attribut arriveetheorique de l'attribut fields du dernier record
			// aide: on aura un truc du genre lastRecord.path("fields").path("arriveetheorique").asText() pour récupérer l'heure de passage
			
	
		} catch (IOException | InterruptedException e) {
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

		// URL de l'image à  classifier, à  adapter à  votre nom de VM.
		String urlImage = "http://vps284011.ovh.net/ecam/image-1.jpg";

		// commencez par exécuter le code en changeant d'image dans le code (10 images
		// disponibles).
		// ensuite, faites en sorte de lire le nom de l'image à  utiliser depuis la
		// console (voir le cours sur les Entrées/Sorties)

		String res;
		try {
			//Partie 1: traitement du flux de retour du service de classification d'images de ClarifAI:
			 res = ri.classifyImage(urlImage);
			 ri.displayConcepts(res);
			 
			 //Partie 2: traitement du flux de retour du service d'horaires de passage de bus de la ville de Rennes:
			//ri.afficheHoraireBus("57", 2311);
			// ri.afficheHoraireBus("C1", 1007);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
