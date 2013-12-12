package oflaherty.xml

import groovy.xml.MarkupBuilder
import org.apache.tools.ant.taskdefs.condition.Matches;

class BettingOddsXmlParser {
		
	static final String TARGET_FILE = "C:\\FileUpload\\formattedXml\\fmt_"
	static final String PREMIER_LEAGUE = "English Premier League"
	static final String CHAMPIONSHIP = "English Championship"
	static final String LEAGUE_1 = "English League 1"
	static final String LEAGUE_2 = "English League 2"

	public boolean processBettingOdds(String path) {
		
		boolean success = false
		
		try {
			def matchesEl = new XmlParser().parse(path)
			def writer = new StringWriter()
			def xml = new MarkupBuilder(writer)
			
			buildXmlContent(xml, matchesEl)
			
			def fileDate = new Date().format("ddMMyyyy");
			
			File file = new File(TARGET_FILE + fileDate + ".xml")
			file.write(writer.toString())

			success = true;
			
		} catch (FileNotFoundException e) {
			log.info 'File Not Found, success has been set to false'
		}

		return success
	}

	private buildXmlContent(MarkupBuilder xml, Node matchesEl) {
		xml.odds() {

			def prefixEl = matchesEl.response.williamhill.class.type

			def leagues = prefixEl.findAll { it.@name == PREMIER_LEAGUE ||
				it.@name == CHAMPIONSHIP || it.@name == LEAGUE_1 || it.@name == LEAGUE_2}

			leagues.each{ leagueEl ->

				league(name: leagueEl.'@name') {

					leagueEl.market.each{ matchEl ->

						if(matchEl.'@name'.endsWith("Match Betting")) {

							match(name: matchEl.'@name', date: matchEl.'@date') {

								for(element in matchEl.participant) {
									team(name: element.'@name', odds: element.'@odds')
								}

							}
						}
					}
				}
			}
		}
	}
		
}
