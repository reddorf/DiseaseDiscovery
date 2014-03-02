package diseaseDiscovery

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.xml.parsers.SAXParserFactory

import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler

import com.Disease
import com.Symptom
import com.SymptomDisease

class MedicalInformationService {
	/* TODO: Put these in external config file */
	private def RAMEDIS_ROOT_DIAGNOSIS_URL = "https://www.imbio.de/stable/php/ramedis/htdocs/dm-guest/"
	private def RAMEDIS_ROOT_URL = "https://www.imbio.de/stable/php/ramedis/htdocs/searchv2/"
	private def RAMEDIS_FIRST_JUMP = "search_diagnosis_overview.php"
	private def RAMEDIS_DISEASE_TOKEN = "href=\"search_diagnosis_ok"
	private def RAMEDIS_NEXT_PAGE_TOKEN = "search_diagnosis_overview"
	private def RAMEDIS_CASE_MAIN = "../dm-guest/case_main.php"
	private def RAMEDIS_DIAGNOSIS_TOKEN = "Diagnosis</td>"
	private def RAMEDIS_SYMPTOMS = "case_symptoms.php"
	
	private def ORPHANET_ROOT_URL = "http://www.orphadata.org/data/xml/en_product4.xml"
	
    def populateDB() {
		println "Populating DB"
		
//		populateFromRamedis() // <- Currently not working because of the website
		populateFromOrphanet()
		
		println "Finished DB population"
    }
	
	
	/**********************************/
	/******* ORPHANET FUNCTIONS *******/
	/**********************************/
	// Private SAX model class
	private class SAXModelBuilder extends DefaultHandler{
		def diseases = [:]
		def symp, disease, name, freq
		
		def diseaseName
		def symptoms = [:]
		def symptomName
		def symptomFreq = -1
		
		@Override
		public void startElement(String namespace, String localname, String qname, Attributes atts)
				throws SAXException {
			try {
				if (qname.equals("Disorder")) disease = true;
				else if (qname.equals("DisorderSign")) symp = true;
				else if (qname.equals("Name")) name = true;
				else if (qname.equals("SignFreq")) freq = true;
			} catch (Exception e) { 
				System.err.println(e) 
			}
		}
		
		@Override
		public void characters(char[] ch, int start, int len) {
			def text = new String(ch, start, len);
			if (symp && name && !freq) {
				symptomName = text
			}
			else if (disease && name && !freq) {
				diseaseName = text
			}
			else if (symp && freq && name) {
				symptomFreq = text
			}
		}
		
		@Override
		public void endElement(String namespace, String localname, String qname)
				throws SAXException {
			if (qname.equals("DisorderSign")) {
				symp = false
				symptoms.put("${symptomName}", symptomFreq)
				symptomName = ""
				symptomFreq = -1
			}
			else if (qname.equals("Name")) {
				name = false
			}
			else if (qname.equals("Disorder")) {
				disease = false
				diseases.put(diseaseName, symptoms)
				diseaseName = ""
				symptoms = [:]
			}
			else if (qname.equals("SignFreq")) {
				freq = false
			}
		}
		
		public def getModel() {
			return diseases
		}
	}
	
	private def populateFromOrphanet(){
		def factory = SAXParserFactory.newInstance()
		def saxParser = factory.newSAXParser()
		def parser = saxParser.getXMLReader()
		def mb = new SAXModelBuilder()
		
		parser.setContentHandler(mb)
		parser.parse(ORPHANET_ROOT_URL)
		
		mb.getModel().each { diseaseName, symptoms -> 	
			println "[${diseaseName} : ${symptoms}]"
			diseaseName = diseaseName.replaceAll("[\"]+", "'")
			def newDisease = Disease.findByName(diseaseName)
			
			
			if(!newDisease) {
				newDisease = new Disease(name:diseaseName).save()
			}
			
			symptoms.each { name, freq ->
				def symptomName = name.replaceAll("[\"]+", "'")
				def newSymptom = Symptom.findByName(symptomName)
				
				if (!newSymptom) {
					newSymptom = new Symptom(name: symptomName).save()
				}
				
				newDisease.addSymptom(newSymptom, freq == "Occasional" ? 0.3333f : freq == "Frequent" ? 0.6666f : 1.0000f)
				
				//new SymptomDisease(disease: newDisease, symptom: newSymptom, symptomFreq: freq).save()
			}
		}
	}
	
	
	
	
	
	
	
	/*********************************/
	/******* RAMEDIS FUNCTIONS *******/
	/*********************************/
	private def populateFromRamedis() {
		def ramedisManager = initializeRamedis()
		def uDisease
		def buff
		
		def disease_list = getRelUrls()
		
		for (String iUrl : disease_list) {
			uDisease = new URL(RAMEDIS_ROOT_URL + iUrl)
			buff = new BufferedReader(new InputStreamReader(uDisease.openStream()))

			def line
			while ((line = buff.readLine()) != null){
				if (line.contains(RAMEDIS_CASE_MAIN)){
					getTheDiagnosis(new URL(RAMEDIS_ROOT_DIAGNOSIS_URL + getDiagnosisURL(line))) 
					break
				}
			}
		}
		
		buff.close();
	}
	
	private def initializeRamedis() {
		def trustAllCerts = [
			new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkClientTrusted (java.security.cert.X509Certificate[] certs, String authType) {}
				public void checkServerTrusted (java.security.cert.X509Certificate[] certs, String authType) {}
			}
		] as TrustManager[]
		
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		
		def manager = new CookieManager();
		manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(manager);
		
		return manager
	}
	
	private def getRelUrls(){
		def nextJump = RAMEDIS_FIRST_JUMP
		def fromInetURL, line, uDisease, buff
		
		def diseaseList = []
		
		while (nextJump != null) {
			
			fromInetURL = RAMEDIS_ROOT_URL + nextJump
			uDisease = new URL(fromInetURL)
			if (nextJump != null) buff = new BufferedReader(
				new InputStreamReader(uDisease.openStream()))

			while ((line = buff.readLine()) != null) {
				if (line.contains(RAMEDIS_DISEASE_TOKEN)){
					diseaseList << getDiseaseURL(line)
				}
				else if (line.contains(RAMEDIS_NEXT_PAGE_TOKEN)){
					nextJump = getURLNextList(line)
				}
			}
		}
		
		buff.close()
		
		return diseaseList
	}
	
	private def getDiseaseURL(String s){
        int startIndex = s.indexOf("\"") + 1;
        int lastIndex = s.indexOf("\"", startIndex);
        
        return s.substring(startIndex, lastIndex);
    }
	
	private def getURLNextList(String s){
		int startIndex = s.indexOf("\"", s.indexOf("nbsp;")) + 1;
		int lastIndex = s.indexOf("\"", startIndex + 1);

		if (startIndex == 0 || lastIndex == 0) 
			return null
		else 
			return s.substring(startIndex, lastIndex)
	}
	
	private def getTheDiagnosis(URL url) {
		def buff = new BufferedReader(new InputStreamReader(url.openStream()))

		def line

		while ((line = buff.readLine()) != null){
			if (line.contains(RAMEDIS_DIAGNOSIS_TOKEN)){
				line = buff.readLine()
				def disease = getName(line)

				def sympsAddr = new URL(RAMEDIS_ROOT_DIAGNOSIS_URL + RAMEDIS_SYMPTOMS)

				getSymptoms(sympsAddr).each { sympt ->
					def newSymptom = Symptom.findByName(sympt)
					def newDisease = Disease.findByName(disease)
					
					if (!newSymptom) {
						newSymptom = new Symptom(name: sympt).save()
					}
					
					if (!newDisease){
						newDisease = new Disease(name: disease).save()
					}
					
					newDisease.addSymptom(newSymptom)		
				}
				
				break 
			}
		}
		
		buff.close()
	}
	
	private def getName(String s){
		def startIndex = s.indexOf(">") + 1;
		def lastIndex = s.indexOf("(MIM", startIndex) - 1;

		return s.substring(startIndex, lastIndex);
	}
	
	private def getSymptoms(URL url) {
		
		def line
		def symptomList = []
	
		def buff = new BufferedReader(new InputStreamReader(url.openStream()))

		while ((line = buff.readLine()) != null){
			if (line.contains("<td bgcolor=\"#dddddd\"><b>")) {
				buff.readLine()
				buff.readLine()
			}
			else if (line.contains("no clinical signs or symptoms")) {
				buff.readLine()
				buff.readLine()
			}
			else if (line.contains("td bgcolor=\"#dddddd\"") ||
					line.contains("td bgcolor=\"#eeeeee\"")) {
				def beginIndex = line.indexOf(">") + 1
				def endIndex = line.indexOf("<", beginIndex)
				symptomList << line.substring(beginIndex, endIndex)
				
				buff.readLine()
				buff.readLine()
			}
		}
		
		buff.close()
		
		return symptomList

	}
	
	private def getDiagnosisURL(String s){
		def startIndex = s.indexOf("../dm-guest/") + 12;
		def lastIndex = s.indexOf("\"", startIndex);

		return s.substring(startIndex, lastIndex);
	}
}
