package fr.ecam.recoimage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * Classe illustrant l'utilisation du client HTTP intégré à Java 11+ et de
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
			// URL du service à appeler
			String service_url = String.format("https://api.clarifai.com/v2/models/%s/outputs", MODEL_ID);

			// construction manuelle de JSON, envisageable pour un petit flux, sinon on
			// passe plutôt par l'API de manipulation de JSON JSONObject
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

		
		try {
			//parse le flux json passé dans la chaine de caractères data
			JSONObject jo = new JSONObject(data);

			// TODO: utiliser la librairie JSON-Java pour récupérer et afficher les concepts
			// retournés pour l'image spécifiée ainsi que
			// le score de probabilité associé
			// afficher ces deux infos sur la console

			// attention le flux de retour contient des infos relatives à des images
			// d'exemple.
			// il faut retourner les concepts associés à l'image provenant du serveur
			// vps284011.ovh.net
			// extrait de flux de réponse:			
			 /* {
	"status": {
		"code": 10000,
		"description": "Ok",
	},
	"outputs": [
		{
			"id": "63a98eaff84b417287dc33bab7ff2e30",
			"status": {
				"code": 10000,
				"description": "Ok"
			},
			"created_at": "2023-10-30T13:43:21.444252525Z",
			"model": {
				"id": "general-image-recognition",
				"name": "Image Recognition",
				"created_at": "2016-03-09T17:11:39.608845Z",
				...
			},
			"input": {
				"id": "d359a2945d0a4ea38880508185cbee05",
				"data": {
					"image": {
						"url": "http://vps284011.ovh.net/ecam/image-1.jpg"
					}
				}
			},
			"data": {
				"concepts": [
					{
						"id": "ai_c9n7SB25",
						"name": "furniture",
						"value": 0.9966653,
						"app_id": "main"
					},
			 */

			// note: vous pouvez utiliser ce site pour formatter un flux json pour une
			// facilité de lecture: https://jsonformatter.org/
			// copier/coller y le flux JSON brut affiché sur la console
			// pour vous aider à naviguer dans l'arbre JSON avec l'API JSON-OBJECT
			

			//récupère un tableau des concepts détectés, et de leur probabilité
			//on utilise le modèle du flux JSON ci-dessus pour parcourir le flux 
			//on prend le premier élément du tableau "outputs", duquel on prend l'élément "data"
			//duquel on prend le tableau "concepts" sur lequel on itère ensuite
			JSONArray concepts = ((JSONObject) jo.getJSONArray("outputs").get(0)).getJSONObject("data").getJSONArray("concepts");
			System.out.println(concepts);
			
			// TODO: compléter le code pour faire une boucle sur chaque concept du JSONArray concepts et afficher les attributs "name" et "value" de chaque concept
			//Utiliser la doc de JSONArray: https://stleary.github.io/JSON-java/org/json/JSONArray.html 
			//et en particulier la méthode iterator() (iterator() est non-typé, il faudra donc utiliser un opérateur de cast dans la bouche

			//...compléterl le code ici...
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Affiche l'horaire de prochain passage du bus de la ligne spécifiée à l'arrêt
	 * spécifié
	 * 
	 * @param nomCourtLigne - nom court de la ligne: 57 pour celle desservant KerLann
	 * @param idArret       - identifiant de l'arrêt: 2311 pour celui devant l'ECAM
	 */
	public void afficheHoraireBus(String nomCourtLigne, int idArret) {

		String service_url = String.format(
				"https://data.rennesmetropole.fr/api/records/1.0/search/?dataset=prochains-passages-des-lignes-de-bus-du-reseau-star-en-temps-reel&q=&facet=idligne&facet=nomcourtligne&facet=sens&facet=destination&facet=numerobuskr&facet=precision&facet=visibilite&facet=idarret&refine.nomcourtligne=%s&refine.idarret=%s",
				nomCourtLigne, idArret);
		System.out.println("Appel de: " + service_url);

		// construit une requête via l'API builder
		// noter le mot-cle var apparu avec Java11 qui permet de ne pas utiliser le type
		// explicite de la variable
		var request = HttpRequest.newBuilder().uri(URI.create(service_url)).GET().build();

		// Exécution requete
		var client = HttpClient.newHttpClient();

		// Lecture réponse au format String
		try {
			var response = client.send(request, HttpResponse.BodyHandlers.ofString());
			System.out.println("Réponse Brute:" + response.body());

			//parse le flux json reçu
			JSONObject jo = new JSONObject(response.body());
	
			
			
			
			//TODO:
			// prendre le dernier élément du tableau (JSOONArray) records[]
			// Attention records[] peut être vide si aucun bus n'est prévu à cet arrêt
			// détecter ce cas et afficher un message "Aucun bus à cet arrêt", puis quitter par un System.exit(0) )

			//prendre ensuite le dernier élément du tableau records
			//JSONObject lastRecord = records.getJSONObject(records.length()-1);
			//et afficher la valeur de l'attribut "arriveetheorique" qui se trouve lui-même dans un attribut "fields"

			
			System.out.println("Arrivée théorique à l'arrêt à: " + "....");

			
		} catch (JSONException | IOException | InterruptedException e) {
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

		// URL de l'image à classifier
		String urlImage = "http://vps284011.ovh.net/ecam/image-1.jpg";

		// commencez par exécuter le code en changeant d'image dans le code (10 images
		// disponibles).
		// ensuite, faites en sorte de lire le nom de l'image à utiliser depuis la
		// console (voir le cours sur les Entrées/Sorties)

		String res;
		try {
			//Partie 1: traitement du flux de retour du service de classification d'images de ClarifAI:
			 //res = ri.classifyImage(urlImage);
			 //ri.displayConcepts(res);
			 
			 //Partie 2: traitement du flux de retour du service d'horaires de passage de bus de la ville de Rennes:
			 //Arrêt Campus Ker Lann:
			 //ri.afficheHoraireBus("C7", 2317);
			
			 //Arrêt Assomption sur ligne 14:
			 //ri.afficheHoraireBus("14", 1007);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
