class Gpw {
		static final String SETUP_FILE_PATH = "/home/miro/Dokumenty/FINANSE/setup.txt" 
		static final String ALARMS_HISTORY ="/home/miro/Dokumenty/Ustawienia/sync/gpw_history.txt"
		static void main(String [] args){
			// FOREX
			def forex = new ForexYahoo();
			forex.writeInstruments();
			forex.readSetup();

			//GPW
			def gpw = new Securities();
			GpwContent [] inwestment = new GpwContent[1]
				inwestment[0]=gpw;

			for( inw in inwestment){
				inw.updateInstruments()
					inw.writeInstruments()
					inw.readSetup()
			}
		}
}
abstract class GpwContent {
	def updateInstruments() {
		for(instrument in mapOfInstruments){
			def key = instrument.key

				def link = instrument.value[0]
				mapOfInstruments.put(key, [
						link,
						getDescription(link),
						getPrice(link).trim().toDouble()
				])
		}
	}
	def writeInstruments(){
		for(instrument in mapOfInstruments){
			println instrument.value[1]
		}
	}


	abstract    def getDescription(String link )
	abstract    def getPrice(String link );

	def readSetup(){
		String file = new File(Gpw.SETUP_FILE_PATH).text
			def words = file.split("\n")
			for(word in words){
				def firstChar
					if(!word.isEmpty()){
						firstChar = word.charAt(0)
					} else {
						continue
					}
				if(firstChar=='#'){
					continue
				}

				def parameters = word.split(";");
				parameters = parameters*.trim();

				if(mapOfInstruments.containsKey(parameters[0].toLowerCase())){
					alert(parameters[0].toLowerCase(), parameters[1], parameters[2].toDouble(), parameters[3]);
				}
			}
	}
	def alert(String instrument, String sign, Double myPrice, String comment){
		Double stockPrice = mapOfInstruments[instrument][2];
		File file = new File(Gpw.ALARMS_HISTORY);
		def dateTime = new Date()
			String date = dateTime.format("yyy-MM-dd HH:mm", TimeZone.getTimeZone('Europe/Warsaw'));
		switch(sign){
			case "gt" : if(stockPrice > myPrice){
							alarm();
							println "------! "+ instrument.toUpperCase() + " cena "+stockPrice+" większa niż "+myPrice + " → "+ comment +" !-----\n" ;
							file.append(date+" "+instrument+ " > " + myPrice + " → "+ comment+ "\n");
						}
						break;
			case "lt" : if(stockPrice < myPrice){
							alarm();
							println "-----! "+ instrument.toUpperCase() + " cena mniejsza niż "+myPrice+ " → "+ comment+ " !-----\n";
							file.append(date+" "+instrument+ " < " + myPrice + " → "+ comment+ "\n");

						}
						break;
			default : println "zły znak"
		}
	}

	def alarm(){
		def com = "mocp -p"
			// def com = "mplayer ~/Dokumenty/Ustawienia/skrypty/moje/gpw.mp3"
			com.execute()
	}

}

class Securities extends GpwContent {
	def  mapOfInstruments = [
		cdr : [
			"http://www.biznesradar.pl/notowania/CD-PROJEKT#1d_lin_lin",
		"description",
		0.0
		],
		cig : [
			"http://www.biznesradar.pl/notowania/CI-GAMES#1d_lin_lin",
		"desc",
		0.0
		],
		acp : [
			"http://www.biznesradar.pl/notowania/ASSECO-POLAND#1d_candle_lin",
		"description",
		0.0
		],
		play : [
			"http://www.biznesradar.pl/notowania/PLY#at#1d_candle_lin",
		"description",
		0.0
		],
		quantum : [
			"http://www.biznesradar.pl/notowania/QUANTUM-SOFTWARE#1d_candle_lin",
		"description",
		0.0
		]
		];


		def getDescription(String link ){
			def com = "w3m -dump $link".execute() |  'sed -n 4p'.execute()

				com.waitFor()
				def lines = com.text
				com = "w3m -dump $link".execute() |  'sed -n 6p'.execute()
				com.waitFor()
				lines += com.text
				com = "w3m -dump $link".execute() |  'sed -n 58,59p'.execute()
				com.waitFor()
				lines += com.text
				return lines
		}

		def getPrice(String link){

			return exeLinuxCmd(link)

		}
		def exeLinuxCmd(String link){
			String output = new StringWriter().with { out ->
				Process proc =  "w3m -dump $link".execute() | 'sed -n 56p'.execute() | "grep -o [0-9]*\\.[0-9]*".execute()
					proc.consumeProcessOutput( out, System.err )
					proc.waitFor()
					out.toString()
			}
			if(output.isEmpty()){
				println "exeLinuxCmd: output- null pointer"
					return "empty price"
			} else {
				return output
			}
		}
}

class ForexYahoo extends GpwContent {
	//   EURUSD, EURGBP, GBPUSD, USDCHF, USDCAD, USDJPY, AUDUSD, NZDUSD, EURPLN
	//   EURUSD, USDJPY, AUDUSD, USDCAD, GBPUSD, EURGBP, EURJPY, w ostateczności NZDUSD, USDPLN
	def feedUrl = 'http://download.finance.yahoo.com/d/quotes.csv?s=EURUSD=X+EURGBP=X+EURJPY=X+GBPUSD=X+USDCHF=X+USDCAD=X+USDJPY=X+AUDUSD=X+NZDUSD=X+USDPLN=X+BTCUSD=X+EURPLN=X&f=nab';
	def csv = new URL(feedUrl)
		def  mapOfInstruments = [:]

		ForexYahoo(){
			String[] lines = csv.text.split('\n')
				for(l in lines) {
					def rows = l.split(',');

					def instrument = [
						"link",
						rows[0].replace('/','')+" "+ rows[1],
						cutQuotationMark(rows[1]).toDouble()
					]
					def stockKey =  cutQuotationMark(rows[0].replace('/','').toLowerCase())
					mapOfInstruments.put(stockKey, instrument )
				}

		}
	private String cutQuotationMark(string){
		return string.replaceAll('"', '')
	}


	def getDescription(String link ){
	}

	def getPrice(String link ){

	}

}

class Sources extends GpwContent {
	def  mapOfInstruments = [
		cdr : [
			"http://www.biznesradar.pl/notowania/CD-PROJEKT#1d_lin_lin",
		"description",
		0.0]
			,
		cig : [
			"http://www.biznesradar.pl/notowania/CI-GAMES#1d_lin_lin",
		"desc",
		0.0]
	];

	def getDescription(String link ){
		def com = "w3m -dump $link".execute() |  'sed -n 4p'.execute()

			com.waitFor()
			def lines = com.text
			com = "w3m -dump $link".execute() |  'sed -n 6p'.execute()
			com.waitFor()
			lines += com.text
			com = "w3m -dump $link".execute() |  'sed -n 34,35p'.execute()
			com.waitFor()
			lines += com.text
			return lines
	}

	def getPrice(String link ){
		def com = "w3m -dump $link".execute() | 'sed -n 32p'.execute()  | "grep -o [0-9]*\\.[0-9]*".execute()
			com.waitFor()
			return com.text
	}

}
